/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.commonizer

import kotlinx.metadata.*
import kotlinx.metadata.klib.KlibModuleMetadata
import kotlinx.metadata.klib.annotations
import kotlinx.metadata.klib.fqName
import org.jetbrains.kotlin.commonizer.metadata.utils.MetadataDeclarationsComparator
import org.jetbrains.kotlin.commonizer.metadata.utils.MetadataDeclarationsComparator.*
import org.jetbrains.kotlin.commonizer.metadata.utils.MetadataDeclarationsComparator.EntityKind.*
import org.jetbrains.kotlin.commonizer.metadata.utils.SerializedMetadataLibraryProvider
import org.jetbrains.kotlin.library.KotlinLibrary
import org.jetbrains.kotlin.library.SerializedMetadata
import org.jetbrains.kotlin.library.metadata.parseModuleHeader
import org.jetbrains.kotlin.library.resolveSingleFileKlib
import org.jetbrains.kotlin.utils.addIfNotNull
import org.junit.Test
import kotlin.test.fail
import org.jetbrains.kotlin.konan.file.File as KFile

class MetadataCompatibilityForK2Test {
    @Test
    fun testK2diff() {
        assertLibrariesAreEqual(
            k1LibraryPath = "/Users/Dmitriy.Dolovov/Downloads/coroutines/k1-kotlinx-coroutines-core.klib",
            k2LibraryPath = "/Users/Dmitriy.Dolovov/Downloads/coroutines/k2-kotlinx-coroutines-core.klib",
        )
    }
}

@Suppress("SameParameterValue")
private fun assertLibrariesAreEqual(
    k1LibraryPath: String,
    k2LibraryPath: String,
) {
    val k1Module = loadKlibModuleMetadata(k1LibraryPath)
    val k2Module = loadKlibModuleMetadata(k2LibraryPath)

    val mismatchesFilter = MismatchesFilter(
        k1Resolver = Resolver(k1Module),
        k2Resolver = Resolver(k2Module)
    )

    when (val result = MetadataDeclarationsComparator.compare(k1Module, k2Module)) {
        is Result.Success -> Unit
        is Result.Failure -> {
            val mismatches = result.mismatches
                .sortedBy { it::class.java.simpleName + "_" + it.kind }
                .filter(mismatchesFilter)

            if (mismatches.isEmpty()) return

            val failureMessage = buildString {
                appendLine("${mismatches.size} mismatches found while comparing K1 (A) module with K2 (B) module:")
                mismatches.forEachIndexed { index, mismatch -> appendLine("${(index + 1)}.\n$mismatch") }
            }

            fail(failureMessage)
        }
    }
}

private fun loadKlibModuleMetadata(libraryPath: String): KlibModuleMetadata {
    val library = resolveSingleFileKlib(KFile(libraryPath))
    val metadata = loadBinaryMetadata(library)
    return KlibModuleMetadata.read(SerializedMetadataLibraryProvider(metadata))
}

private fun loadBinaryMetadata(library: KotlinLibrary): SerializedMetadata {
    val moduleHeader = library.moduleHeaderData
    val fragmentNames = parseModuleHeader(moduleHeader).packageFragmentNameList.toSet()
    val fragments = fragmentNames.map { fragmentName ->
        val partNames = library.packageMetadataParts(fragmentName)
        partNames.map { partName -> library.packageMetadata(fragmentName, partName) }
    }

    return SerializedMetadata(
        module = moduleHeader,
        fragments = fragments,
        fragmentNames = fragmentNames.toList()
    )
}

private class Resolver(module: KlibModuleMetadata) {
    private val packageFqNames = hashSetOf<String>()
    private val typeAliases = hashMapOf<String, KmTypeAlias>()
    private val classes = hashMapOf<String, KmClass>()

    init {
        module.fragments.forEach { fragment ->
            fragment.classes.forEach { clazz ->
                classes[clazz.name] = clazz
            }

            val pkg = fragment.pkg ?: return@forEach
            val packageFqName = pkg.fqName?.replace('.', '/') ?: return@forEach
            packageFqNames += packageFqName

            pkg.typeAliases.forEach { typeAlias ->
                val typeAliasFqName = if (packageFqName.isNotEmpty()) packageFqName + "/" + typeAlias.name else typeAlias.name
                typeAliases[typeAliasFqName] = typeAlias
            }
        }
    }

    fun getClass(classFqName: String): KmClass? = classes[classFqName]
    fun hasClass(classFqName: String): Boolean = getClass(classFqName) != null

    fun getTypeAlias(typeAliasFqName: String): KmTypeAlias? = typeAliases[typeAliasFqName]
    fun hasTypeAlias(typeAliasFqName: String): Boolean = getTypeAlias(typeAliasFqName) != null

    fun isDefinitelyNotResolvedTypeAlias(typeAliasFqName: String, classFqName: String): Boolean =
        when (val resolvedClassFqName = (getTypeAlias(typeAliasFqName)?.expandedType?.classifier as? KmClassifier.Class)?.name) {
            null -> {
                val typeAliasPackageFqName = typeAliasFqName.substringBeforeLast('/')
                typeAliasPackageFqName in packageFqNames
            }
            else -> resolvedClassFqName != classFqName
        }
}

// TODO: add filtering of mismatches consciously and ON DEMAND, don't use the filter that is used in commonizer tests!!!
private class MismatchesFilter(
    private val k1Resolver: Resolver,
    private val k2Resolver: Resolver,
) : (Mismatch) -> Boolean {
    override fun invoke(mismatch: Mismatch): Boolean {
        if (mismatch.isMismatchThatIsOK())
            return false

        if (mismatch.isMismatchThatIsNotOK())
            return false // We know about them. But let's skip them all to make sure there is nothing new.

        return true
    }

    /* --- MISMATCHES THAT ARE OK --- */

    private fun Mismatch.isMismatchThatIsOK(): Boolean =
        isMissingEnumEntryInK2()
                || isShortCircuitedTypeRecordedInK2TypeAliasUnderlyingType()
                || isMoreExpandedTypeRecordedInK2TypeAliasUnderlyingType()
                || isAbbreviatedTypeMissingInK1OrK2Type()
                || isTypeAliasRecordedInK1BothAsClassAndTypeAlias()
                || isValueParameterWithNonPropagatedDeclaresDefaultValueFlagInK1()
                || isHasAnnotationsFlagHasDifferentStateInK1AndK2()
                || isMissingAnnotationOnNonVisibleDeclaration()

    // enum entry classes are not serialized in K2
    private fun Mismatch.isMissingEnumEntryInK2(): Boolean =
        this is Mismatch.MissingEntity
                && kind == EntityKind.Class
                && missingInB
                && (existentValue as KmClass).kind == ClassKind.ENUM_ENTRY

    // That's OK. Types in underlying type of TA are written short-cut. No harm at all.
    private fun Mismatch.isShortCircuitedTypeRecordedInK2TypeAliasUnderlyingType(): Boolean {
        if (this !is Mismatch.DifferentValues || kind != EntityKind.Classifier) return false

        val lastPathElement = path.last()

        if (lastPathElement is PathElement.Type && isRelatedToTypeAliasUnderlyingType()) {
            val typeK1 = lastPathElement.typeA
            val typeK2 = lastPathElement.typeB

            val typeK1TypeAlias = typeK1.classifier as? KmClassifier.TypeAlias
            val typeK1HasAbbreviation = typeK1.abbreviatedType != null

            val typeK2Class = typeK2.classifier as? KmClassifier.Class
            val typeK2Abbreviation = typeK2.abbreviatedType?.classifier as? KmClassifier.TypeAlias

            if (typeK1TypeAlias != null
                && !typeK1HasAbbreviation
                && typeK2Class != null
                && typeK2Abbreviation != null
                && typeK2Abbreviation.name == typeK1TypeAlias.name
            ) {
                return true
            }
        }

        return false
    }

    // That's OK. Types in underlying type of TA are written a bit expanded. No harm at all.
    private fun Mismatch.isMoreExpandedTypeRecordedInK2TypeAliasUnderlyingType(): Boolean {
        if (this is Mismatch.MissingEntity
            && (kind == TypeKind.ABBREVIATED || kind == EntityKind.TypeArgument)
        ) {
            val lastPathElement = path.last()

            if (lastPathElement is PathElement.Type && isRelatedToTypeAliasUnderlyingType()) {
                val typeK1 = lastPathElement.typeA
                val typeK2 = lastPathElement.typeB

                val typeK1TypeAlias = typeK1.classifier as? KmClassifier.TypeAlias
                val typeK1HasAbbreviation = typeK1.abbreviatedType != null

                val typeK2Class = typeK2.classifier as? KmClassifier.Class
                val typeK2Abbreviation = typeK2.abbreviatedType?.classifier as? KmClassifier.TypeAlias

                if (typeK1TypeAlias != null
                    && !typeK1HasAbbreviation
                    && typeK2Class != null
                    && typeK2Abbreviation != null
                    && typeK2Abbreviation.name == typeK1TypeAlias.name
                ) {
                    return true
                }
            }
        }

        return false
    }

    // That's OK. Abbreviated type may be absent at all. No harm at all.
    private fun Mismatch.isAbbreviatedTypeMissingInK1OrK2Type(): Boolean {
        if (this is Mismatch.MissingEntity && kind == TypeKind.ABBREVIATED) {
            val lastPathElement = path.last()

            if (lastPathElement is PathElement.Type && !isRelatedToTypeAliasUnderlyingType()) {
                val typeK1 = lastPathElement.typeA
                val typeK2 = lastPathElement.typeB

                val typeK1Class = typeK1.classifier as? KmClassifier.Class
                val typeK1HasAbbreviation = typeK1.abbreviatedType != null

                val typeK2Class = typeK2.classifier as? KmClassifier.Class
                val typeK2HasAbbreviation = typeK2.abbreviatedType != null

                if (typeK1Class != null
                    && typeK2Class != null
                    && typeK1Class.name == typeK2Class.name
                    && typeK1HasAbbreviation != typeK2HasAbbreviation
                ) {
                    return true
                }
            }
        }

        return false
    }

    // This is OK since in K2 it works correctly.
    private fun Mismatch.isTypeAliasRecordedInK1BothAsClassAndTypeAlias(): Boolean {
        if (this is Mismatch.MissingEntity && kind == EntityKind.Class && missingInB) {
            val classThatIsMissingInK2 = existentValue as KmClass
            val classFqName = classThatIsMissingInK2.name

            val hasSuchClassInK1 = k1Resolver.hasClass(classFqName)
            val hasSuchClassInK2 = k2Resolver.hasClass(classFqName)

            val hasSuchTypeAliasInK1 = k1Resolver.hasTypeAlias(classFqName)
            val hasSuchTypeAliasInK2 = k2Resolver.hasTypeAlias(classFqName)

            if (hasSuchClassInK1 && !hasSuchClassInK2 && hasSuchTypeAliasInK1 && hasSuchTypeAliasInK2 && classThatIsMissingInK2.isExpect)
                return true
        }

        return false
    }

    // This is OK since in K2 it works correctly.
    private fun Mismatch.isValueParameterWithNonPropagatedDeclaresDefaultValueFlagInK1(): Boolean {
        if (this is Mismatch.DifferentValues
            && kind is FlagKind
            && name == "declaresDefaultValue"
            && path.last() is PathElement.ValueParameter
        ) {
            val declaresDefaultValueInK1 = valueA as Boolean
            val declaresDefaultValueInK2 = valueB as Boolean

            if (!declaresDefaultValueInK1 && declaresDefaultValueInK2)
                return true
        }

        return false
    }

    // This issue itself is not a problem. The real problem is that some annotations are missing,
    // and this is addressed by another check in 'MISMATCHES THAT ARE NOT OK' section.
    private fun Mismatch.isHasAnnotationsFlagHasDifferentStateInK1AndK2(): Boolean {
        if (this is Mismatch.DifferentValues && kind is FlagKind && name == "hasAnnotations") {
            val hasAnnotationsInK1 = valueA as Boolean
            val hasAnnotationsInK2 = valueB as Boolean

            val (nonEmptyAnnotationsInK1, nonEmptyAnnotationsInK2) = when (val lastPathElement = path.last()) {
                is PathElement.Property -> {
                    val annotationsInK1 = lastPathElement.propertyA.annotations
                    val annotationsInK2 = lastPathElement.propertyB.annotations

                    annotationsInK1.isNotEmpty() to annotationsInK2.isNotEmpty()
                }
                else -> error("Not yet supported: ${lastPathElement::class.java}")
            }

            if (hasAnnotationsInK1 == nonEmptyAnnotationsInK1 && hasAnnotationsInK2 == nonEmptyAnnotationsInK2)
                return true
        }

        return false
    }

    private fun Mismatch.isMissingAnnotationOnNonVisibleDeclaration(): Boolean {
        val annotationClassFqName = name
        if (annotationClassFqName == "kotlin/Deprecated") return false // This is a very strict exception!

        fun isVisibleClass(classFqName: String): Boolean? {
            return when (k2Resolver.getClass(classFqName)?.visibility) {
                null -> null
                Visibility.PUBLIC, Visibility.PROTECTED, Visibility.INTERNAL -> true
                else -> false
            }
        }

        if (this is Mismatch.MissingEntity && kind is AnnotationKind) {
            // If entity is invisible outside of the module or the annotation is invisible outside the module,
            // treat this as a non-error situation.
            if (isDefinitelyUnderNonVisibleDeclarationInK2())
                return true

            // `null` means unknown visibility because the symbol is somewhere in another module.
            val isVisibleAnnotation: Boolean? = isVisibleClass(annotationClassFqName)
                ?: (k2Resolver.getTypeAlias(annotationClassFqName)?.expandedType?.classifier as? KmClassifier.Class)?.name
                    ?.let(::isVisibleClass)

            if (isVisibleAnnotation == false)
                return true
        }

        return false
    }

    /* --- MISMATCHES THAT ARE NOT OK --- */

    private fun Mismatch.isMismatchThatIsNotOK(): Boolean =
        isTypeAliasRecordedInK2TypeAsClass()
                || isInvokeFunctionWithoutOperatorFlagInK2()
                || isNotDefaultPropertyNotMarkedAsNotDefaultInK2()

    private fun Mismatch.isTypeAliasRecordedInK2TypeAsClass(): Boolean {
        if (this is Mismatch.DifferentValues && kind == EntityKind.Classifier) {
            val lastPathElement = path.last()

            if (lastPathElement is PathElement.Type
                && !isRelatedToTypeAliasUnderlyingType()
                && isTypeAliasRecordedInK2TypeAsClass(typeK1 = lastPathElement.typeA, typeK2 = lastPathElement.typeB)
            ) {
                return true
            }
        }

        if (this is Mismatch.MissingEntity && kind == TypeKind.ABBREVIATED) {
            val lastPathElement = path.last()

            if (lastPathElement is PathElement.Type
                && !isRelatedToTypeAliasUnderlyingType()
                && isTypeAliasRecordedInK2TypeAsClass(typeK1 = lastPathElement.typeA, typeK2 = lastPathElement.typeB)
            ) {
                return true
            }
        }

        if (this is Mismatch.MissingEntity && kind == EntityKind.Function) {
            fun KmFunction.allTypes(): List<KmType> = buildList {
                valueParameters.mapTo(this) { it.type }
                addIfNotNull(receiverParameterType)
            }

            if (missingInA) {
                // Some function is missing in K1. Probably, that's because one of the function's
                // value parameters has TA recorded as Class.
                val functionInK2 = existentValue as KmFunction
                functionInK2.allTypes().forEach { type ->
                    if (type.abbreviatedType != null) return@forEach
                    val typeAliasFqName = (type.classifier as? KmClassifier.Class)?.name ?: return@forEach
                    if (k2Resolver.hasTypeAlias(typeAliasFqName))
                        return true
                }
            } else {
                // Some function is missing in K2. Probably, that's because one of the function's
                // value parameters has properly recorded TA in it's type.
                val functionInK1 = existentValue as KmFunction
                functionInK1.allTypes().forEach { type ->
                    val classFqName = (type.classifier as? KmClassifier.Class)?.name ?: return@forEach
                    val typeAliasFqName = (type.abbreviatedType?.classifier as? KmClassifier.TypeAlias)?.name ?: return@forEach
                    if (!k1Resolver.isDefinitelyNotResolvedTypeAlias(typeAliasFqName, classFqName))
                        return true
                }
            }
        }

        return false
    }

    // Invalid type: type alias is recorded as KmClassifier.Class in metadata!
    // TODO: fix type serialization in FIR
    private fun isTypeAliasRecordedInK2TypeAsClass(typeK1: KmType, typeK2: KmType): Boolean {
        val typeK1Class = typeK1.classifier as? KmClassifier.Class
        val typeK1Abbreviation = typeK1.abbreviatedType?.classifier as? KmClassifier.TypeAlias

        val typeK2Class = typeK2.classifier as? KmClassifier.Class
        val typeK2HasAbbreviation = typeK2.abbreviatedType != null

        if (typeK1Class != null
            && typeK1Abbreviation != null
            && typeK2Class != null
            && !typeK2HasAbbreviation
            && typeK1Abbreviation.name == typeK2Class.name
            && !k1Resolver.isDefinitelyNotResolvedTypeAlias(typeK1Abbreviation.name, typeK1Class.name)
        ) {
            return true
        }

        return false
    }

    // 'isOperator' flag is not set for certain functions.
    // TODO: fix type serialization in FIR
    private fun Mismatch.isInvokeFunctionWithoutOperatorFlagInK2(): Boolean {
        if (this is Mismatch.DifferentValues && kind is FlagKind && name == "isOperator") {
            val lastPathElement = path.last() as? PathElement.Function
            if (lastPathElement?.functionA?.name == "invoke") {
                val isOperatorInK1 = valueA as Boolean
                val isOperatorInK2 = valueB as Boolean

                if (isOperatorInK1 && !isOperatorInK2)
                    return true
            }
        }

        return false
    }

    // 'isNotDefault' flag is not set for certain properties.
    // TODO: fix type serialization in FIR
    private fun Mismatch.isNotDefaultPropertyNotMarkedAsNotDefaultInK2(): Boolean {
        if (this is Mismatch.DifferentValues
            && kind is FlagKind
            && name == "isNotDefault"
        ) {
            val lastPathElement = path.last() as? PathElement.Property
            if (lastPathElement != null) {
                val propertyInK2 = lastPathElement.propertyB
                val isReallyNotDefault = propertyInK2.hasAnnotations || propertyInK2.isDelegated

                val isNotDefaultInK1 = valueA as Boolean
                val isNotDefaultInK2 = valueB as Boolean

                if (isNotDefaultInK1 && !isNotDefaultInK2 && isReallyNotDefault)
                    return true
            }
        }

        return false
    }

    /* --- UTILS --- */

    private fun Mismatch.isRelatedToTypeAliasUnderlyingType(): Boolean =
        path.any { path.any { it is PathElement.Type && it.kind == TypeKind.UNDERLYING } }

    private fun Mismatch.isDefinitelyUnderNonVisibleDeclarationInK2(): Boolean {
        for (pathElement in path) {
            val visibility = when (pathElement) {
                is PathElement.Class -> pathElement.clazzB.visibility
                is PathElement.Constructor -> pathElement.constructorB.visibility
                is PathElement.Function -> pathElement.functionB.visibility
                is PathElement.Property -> pathElement.propertyB.visibility
                else -> continue
            }

            if (visibility != Visibility.PUBLIC && visibility != Visibility.PROTECTED && visibility != Visibility.INTERNAL)
                return true
        }

        return false
    }
}
