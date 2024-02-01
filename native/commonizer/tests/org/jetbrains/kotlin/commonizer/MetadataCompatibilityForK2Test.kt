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
import java.io.File
import kotlin.test.fail
import org.jetbrains.kotlin.konan.file.File as KFile

class MetadataCompatibilityForK2Test {
    @Test
    fun testK2diff() {
        val coroutinesDir = File("/Users/Dmitriy.Dolovov/Downloads/coroutines")
        val atomicFuDir = File("/Users/Dmitriy.Dolovov/Downloads/atomicfu")
        val serializationDir = File("/Users/Dmitriy.Dolovov/Downloads/serialization")

        // IMPORTANT: Preserve order of libraries in `k1Libraries` and `k2Libraries`!
        @Suppress("TestFailedLine", "RedundantSuppression")
        assertLibrariesAreEqual(
            k1LibraryFiles = listOf(
                coroutinesDir.resolve("k1-kotlinx-coroutines-core.klib"),
                coroutinesDir.resolve("k1-kotlinx-coroutines-test.klib"),
                atomicFuDir.resolve("k1-atomicfu.klib"),
                serializationDir.resolve("k1-kotlinx-serialization-core.klib"),
            ),
            k2LibraryFiles = listOf(
                coroutinesDir.resolve("k2-kotlinx-coroutines-core.klib"),
                coroutinesDir.resolve("k2-kotlinx-coroutines-test.klib"),
                atomicFuDir.resolve("k2-atomicfu.klib"),
                serializationDir.resolve("k2-kotlinx-serialization-core.klib"),
            ),
        )
    }
}

@Suppress("SameParameterValue")
private fun assertLibrariesAreEqual(
    k1LibraryFiles: List<File>,
    k2LibraryFiles: List<File>,
) {
    val k1LibraryPaths = k1LibraryFiles.map { it.canonicalPath }
    val k2LibraryPaths = k2LibraryFiles.map { it.canonicalPath }

    check(k1LibraryPaths.size == k2LibraryPaths.size)
    check(k1LibraryPaths.size == k1LibraryPaths.toSet().size)
    check(k2LibraryPaths.size == k2LibraryPaths.toSet().size)
    check((k1LibraryPaths intersect k2LibraryPaths.toSet()).isEmpty())

    val k1Modules = loadKlibModulesMetadata(k1LibraryPaths)
    val k2Modules = loadKlibModulesMetadata(k2LibraryPaths)

    val results = (k1Modules zip k2Modules).map { (k1Module, k2Module) ->
        MetadataDeclarationsComparator.compare(k1Module, k2Module)
    }

    val mismatches = results.flatMap { result ->
        when (result) {
            is Result.Success -> emptyList()
            is Result.Failure -> result.mismatches
        }
    }.sortedBy { it::class.java.simpleName + "_" + it.kind }

    val unexpectedMismatches = MismatchesFilter(k1Resolver = Resolver(k1Modules), k2Resolver = Resolver(k2Modules)).filter(mismatches)
    if (unexpectedMismatches.isNotEmpty()) {
        val failureMessage = buildString {
            appendLine("${unexpectedMismatches.size} mismatches found while comparing K1 (A) module with K2 (B) module:")
            unexpectedMismatches.forEachIndexed { index, mismatch -> appendLine("${(index + 1)}.\n$mismatch") }
        }

        fail(failureMessage)
    }
}

private fun loadKlibModulesMetadata(libraryPaths: List<String>): List<KlibModuleMetadata> = libraryPaths.map { libraryPath ->
    val library = resolveSingleFileKlib(KFile(libraryPath))
    val metadata = loadBinaryMetadata(library)
    KlibModuleMetadata.read(SerializedMetadataLibraryProvider(metadata))
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

private class Resolver(modules: Collection<KlibModuleMetadata>) {
    private val packageFqNames = hashSetOf<String>()
    private val typeAliases = hashMapOf<String, KmTypeAlias>()
    private val classes = hashMapOf<String, KmClass>()

    init {
        modules.forEach { module ->
            module.fragments.forEach fragment@{ fragment ->
                fragment.classes.forEach { clazz ->
                    classes[clazz.name] = clazz
                }

                val pkg = fragment.pkg ?: return@fragment
                val packageFqName = pkg.fqName?.replace('.', '/') ?: return@fragment
                packageFqNames += packageFqName

                pkg.typeAliases.forEach { typeAlias ->
                    val typeAliasFqName = if (packageFqName.isNotEmpty()) packageFqName + "/" + typeAlias.name else typeAlias.name
                    typeAliases[typeAliasFqName] = typeAlias
                }
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
) {
    fun filter(input: List<Mismatch>): List<Mismatch> {
        return input
            /* --- FILTER OUT: MISMATCHES THAT ARE OK --- */
            .filter {
                when {
                    it.isMissingEnumEntryInK2() -> false
                    it.isShortCircuitedTypeRecordedInK2TypeAliasUnderlyingType() -> false
                    it.isMoreExpandedTypeRecordedInK2TypeAliasUnderlyingType() -> false
                    it.isTypeAliasRecordedInK1BothAsClassAndTypeAlias() -> false
                    it.isValueParameterWithNonPropagatedDeclaresDefaultValueFlagInK1() -> false
                    /* no more reproduced */ //it.isHasAnnotationsFlagHasDifferentStateInK1AndK2() -> false
                    it.isMissingAnnotationOnNonVisibleDeclaration() -> false
                    /* see KT-65383 */ it.isFalsePositiveIsOperatorFlagOnInvokeFunctionInK1() -> false
                    it.isNullableKotlinAnyUpperBoundInClassTypeParameterMissing() -> false
                    /* see KT-61138 */ it.isIgnoredHasConstantFlag() -> false
                    it.isCompileTimeValueForConstantMissingInK1() -> false
                    else -> true
                }
            }
            .dropPairedMissingFunctionMismatchesDueToMissingNullableKotlinAnyUpperBound()

            /* --- FILTER OUT: MISMATCHES THAT ARE NOT OK --- */
            /* We know about them. But let's skip them all to make sure there is nothing new. */
            .filter {
                when {
                    /* already fixed in KT-64743 */ //it.isTypeAliasRecordedInK2TypeAsClass() -> false
                    /* TODO: KT-65263 (scheduled to 2.0.0-RC) */ it.isAbbreviatedTypeMissingInK1OrK2Type() -> false
                    /* TODO: KT-63871 (scheduled to 2.1.0) */ it.isNotDefaultPropertyNotMarkedAsNotDefaultInK2() -> false
                    else -> true
                }
            }
    }

    /* --- MISMATCHES THAT ARE OK --- */

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
    @Suppress("unused")
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
            // If entity is invisible outside the module or the annotation is invisible outside the module,
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

    private fun Mismatch.isNullableKotlinAnyUpperBoundInClassTypeParameterMissing(): Boolean {
        if (this is Mismatch.MissingEntity && kind == TypeKind.UPPER_BOUND) {
            val missingUpperBound = existentValue as KmType
            if ((missingUpperBound.classifier as? KmClassifier.Class)?.name == "kotlin/Any"
                && missingUpperBound.arguments.isEmpty()
                && missingUpperBound.isNullable
                && !missingUpperBound.isDefinitelyNonNull
                && missingUpperBound.abbreviatedType == null
                && missingUpperBound.flexibleTypeUpperBound == null
                && missingUpperBound.outerType == null
            ) {
                return true
            }
        }

        return false
    }

    private fun Mismatch.isIgnoredHasConstantFlag(): Boolean {
        return this is Mismatch.DifferentValues && kind is FlagKind && name == "hasConstant"
    }

    private fun Mismatch.isCompileTimeValueForConstantMissingInK1(): Boolean {
        if (this is Mismatch.MissingEntity && kind == EntityKind.CompileTimeValue && missingInA) {
            val compileTimeValue = existentValue as KmAnnotationArgument
            if (compileTimeValue is KmAnnotationArgument.ArrayValue && compileTimeValue.elements.isEmpty()) {
                // Some strange case when empty arrays were not considered as compile time values.
                return true
            }
        }

        return false
    }

    private fun List<Mismatch>.dropPairedMissingFunctionMismatchesDueToMissingNullableKotlinAnyUpperBound(): List<Mismatch> {
        fun groupingKey(mismatch: Mismatch, presentOnlyInK1: Boolean): String? {
            if (mismatch !is Mismatch.MissingEntity || mismatch.kind != EntityKind.Function || presentOnlyInK1 == mismatch.missingInA) return null

            val functionKey = mismatch.toString().substringBefore(" is missing in ", missingDelimiterValue = "")
            if (functionKey.isEmpty()) return null

            return mismatch.path.filter { it !is PathElement.Root }.joinToString(" -> ", postfix = " -> $functionKey")
        }

        val functionsPresentOnlyInK1: Map<String, Mismatch> = mapNotNull { mismatch ->
            val key = groupingKey(mismatch, presentOnlyInK1 = true) ?: return@mapNotNull null
            key to mismatch
        }.toMap()

        val functionsPresentOnlyInK2: Map<String, Mismatch> = mapNotNull { mismatch ->
            val key = groupingKey(mismatch, presentOnlyInK1 = false) ?: return@mapNotNull null
            key to mismatch
        }.toMap()

        val functionsKeysInK2WithoutNullableAnyUppedBound = functionsPresentOnlyInK2.keys.associateWith { key ->
            key.replace(": kotlin/Any?,", ",").replace(": kotlin/Any?>", ">")
        }

        val mismatchesToRemove = hashSetOf<Mismatch>()
        functionsKeysInK2WithoutNullableAnyUppedBound.forEach { (keyForK2, keyForK1) ->
            val functionInK1 = functionsPresentOnlyInK1[keyForK1] ?: return@forEach
            val functionInK2 = functionsPresentOnlyInK2.getValue(keyForK2)

            // self annihilation:
            mismatchesToRemove += functionInK1
            mismatchesToRemove += functionInK2
        }

        return this - mismatchesToRemove
    }

    // 'isOperator' flag is not set for certain functions.
    private fun Mismatch.isFalsePositiveIsOperatorFlagOnInvokeFunctionInK1(): Boolean {
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

    /* --- MISMATCHES THAT ARE NOT OK --- */

    @Suppress("unused")
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
                // value parameters has properly recorded TA in its type.
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
    private fun isTypeAliasRecordedInK2TypeAsClass(typeK1: KmType, typeK2: KmType): Boolean {
        val typeK1Class = typeK1.classifier as? KmClassifier.Class
        val typeK1Abbreviation = typeK1.abbreviatedType?.classifier as? KmClassifier.TypeAlias

        val typeK2Class = typeK2.classifier as? KmClassifier.Class
        val typeK2HasAbbreviation = typeK2.abbreviatedType != null

        @Suppress("RedundantIf", "RedundantSuppression")
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

    // Abbreviated type may be absent at all. No harm at all.
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

    // 'isNotDefault' flag is not set for certain properties.
    private fun Mismatch.isNotDefaultPropertyNotMarkedAsNotDefaultInK2(): Boolean {
        if (this is Mismatch.DifferentValues
            && kind is FlagKind
            && name == "isNotDefault"
        ) {
            val lastPathElement = path.last() as? PathElement.Property
            if (lastPathElement != null) {
                val propertyInK2 = lastPathElement.propertyB

                // This is not a 100% correct check for non-defaultness, but a good approximation:
                // If a property has `kind == DELEGATION` then it is a fake override of a property declared in an interface
                // that appeared in the current class through interface delegation. Such property in interface could only have
                // accessor body (never a backing field).
                val isReallyNotDefaultBecauseOfInterfaceDelegation = propertyInK2.kind == MemberKind.DELEGATION

                val isNotDefaultInK1 = valueA as Boolean
                val isNotDefaultInK2 = valueB as Boolean

                if (isNotDefaultInK1 && !isNotDefaultInK2 && isReallyNotDefaultBecauseOfInterfaceDelegation)
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
