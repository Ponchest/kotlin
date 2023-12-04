/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.incremental

import com.intellij.util.io.KeyDescriptor
import org.jetbrains.kotlin.build.report.DoNothingICReporter
import org.jetbrains.kotlin.build.report.ICReporter
import org.jetbrains.kotlin.incremental.storage.BasicFileToPathConverter
import org.jetbrains.kotlin.incremental.storage.FileToPathConverter
import java.io.File

class IncrementalCompilationContext(
    // The root directories of source files and output files are different, so we need different `FileToPathConverter`s
    val pathConverterForSourceFiles: FileToPathConverter = BasicFileToPathConverter,
    val pathConverterForOutputFiles: FileToPathConverter = BasicFileToPathConverter,
    val storeFullFqNamesInLookupCache: Boolean = false,
    val transaction: CompilationTransaction = NonRecoverableCompilationTransaction(),
    val reporter: ICReporter = DoNothingICReporter,
    /**
     * Controls whether changes in lookup cache should be tracked. Required for the classpath snapshots based IC approach
     */
    val trackChangesInLookupCache: Boolean = false,
    /**
     * Controls whether any changes should be propagated to FS until we decide that the compilation is successful or not
     *
     * Required for optimizing Gradle side outputs backup
     */
    val keepIncrementalCompilationCachesInMemory: Boolean = false,
    /**
     * Holds current mapping from source files to fragments, in the K2 KMP context
     */
    val fragmentContext: FragmentContext? = null
) {
    @Deprecated("This constructor is scheduled to be removed. KSP is using it")
    constructor(
        pathConverter: FileToPathConverter,
        storeFullFqNamesInLookupCache: Boolean = false,
        transaction: CompilationTransaction = NonRecoverableCompilationTransaction(),
        reporter: ICReporter = DoNothingICReporter,
        trackChangesInLookupCache: Boolean = false,
        keepIncrementalCompilationCachesInMemory: Boolean = false,
    ) : this(
        pathConverter,
        pathConverter,
        storeFullFqNamesInLookupCache,
        transaction,
        reporter,
        trackChangesInLookupCache,
        keepIncrementalCompilationCachesInMemory
    )

    val fileDescriptorForSourceFiles: KeyDescriptor<File> = pathConverterForSourceFiles.getFileDescriptor()
    val fileDescriptorForOutputFiles: KeyDescriptor<File> = pathConverterForOutputFiles.getFileDescriptor()
}
