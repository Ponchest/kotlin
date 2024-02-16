/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.konan.test.blackbox;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.util.KtTestUtil;
import org.jetbrains.kotlin.konan.test.blackbox.support.EnforcedProperty;
import org.jetbrains.kotlin.konan.test.blackbox.support.ClassLevelProperty;
import org.junit.jupiter.api.Tag;
import org.jetbrains.kotlin.konan.test.blackbox.support.group.FirPipeline;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.kotlin.generators.tests.GenerateNativeTestsKt}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("native/native.tests/testData/CExport/InterfaceV1")
@TestDataPath("$PROJECT_ROOT")
@EnforcedProperty(property = ClassLevelProperty.BINARY_LIBRARY_KIND, propertyValue = "STATIC")
@Tag("frontend-fir")
@FirPipeline()
public class FirCExportStaticTestGenerated extends AbstractNativeCExportTest {
    @Test
    public void testAllFilesPresentInInterfaceV1() throws Exception {
        KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("native/native.tests/testData/CExport/InterfaceV1"), Pattern.compile("^([^_](.+))$"), null, false);
    }

    @Test
    @TestMetadata("kt36639")
    public void testKt36639() throws Exception {
        runTest("native/native.tests/testData/CExport/InterfaceV1/kt36639/");
    }

    @Test
    @TestMetadata("smoke0")
    public void testSmoke0() throws Exception {
        runTest("native/native.tests/testData/CExport/InterfaceV1/smoke0/");
    }
}
