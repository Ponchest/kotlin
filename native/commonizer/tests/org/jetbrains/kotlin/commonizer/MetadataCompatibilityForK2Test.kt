/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.commonizer

import kotlinx.metadata.ClassKind
import kotlinx.metadata.KmClass
import kotlinx.metadata.kind
import kotlinx.metadata.klib.KlibModuleMetadata
import org.jetbrains.kotlin.commonizer.metadata.utils.MetadataDeclarationsComparator
import org.jetbrains.kotlin.commonizer.metadata.utils.SerializedMetadataLibraryProvider
import org.jetbrains.kotlin.library.KotlinLibrary
import org.jetbrains.kotlin.library.SerializedMetadata
import org.jetbrains.kotlin.library.metadata.parseModuleHeader
import org.jetbrains.kotlin.library.resolveSingleFileKlib
import org.junit.Test
import kotlin.test.fail
import org.jetbrains.kotlin.konan.file.File as KFile

class MetadataCompatibilityForK2Test {
    @Test
    fun testK2diff() {
        assertLibrariesAreEqual(
            k1LibraryPath = "/Users/Dmitriy.Dolovov/temp/000/lib-k1x.klib",
            k2LibraryPath = "/Users/Dmitriy.Dolovov/temp/000/lib-k2x.klib"
        )
    }

}

private fun assertLibrariesAreEqual(
    k1LibraryPath: String,
    k2LibraryPath: String,
) {
    val k1Module = loadKlibModuleMetadata(k1LibraryPath)
    val k2Module = loadKlibModuleMetadata(k2LibraryPath)

    when (val result = MetadataDeclarationsComparator.compare(k1Module, k2Module)) {
        is MetadataDeclarationsComparator.Result.Success -> Unit
        is MetadataDeclarationsComparator.Result.Failure -> {
            val mismatches = result.mismatches
                .filter(KNOWN_MISMATCHES_FILTER)
                .sortedBy { it::class.java.simpleName + "_" + it.kind }

            if (mismatches.isEmpty()) return

            val digitCount = mismatches.size.toString().length

            val failureMessage = buildString {
                appendLine("${mismatches.size} mismatches found while comparing K1 module with K2 module:")
                mismatches.forEachIndexed { index, mismatch ->
                    appendLine((index + 1).toString().padStart(digitCount, ' ') + ". " + mismatch)
                }
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

// TODO: add filtering of mismatches consciously and ON DEMAND, don't use the filter that is used in commonizer tests!!!
private val KNOWN_MISMATCHES_FILTER: (MetadataDeclarationsComparator.Mismatch) -> Boolean = { mismatch ->
    when {
        mismatch is MetadataDeclarationsComparator.Mismatch.MissingEntity
                && mismatch.kind == MetadataDeclarationsComparator.EntityKind.Class
                && mismatch.missingInB
                && (mismatch.existentValue as KmClass).kind == ClassKind.ENUM_ENTRY -> {
            // enum entry classes are not serialized in K2
            false
        }
        else -> true
    }
}
