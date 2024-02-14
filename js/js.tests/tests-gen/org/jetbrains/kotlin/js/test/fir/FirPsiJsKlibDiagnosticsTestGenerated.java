/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.test.fir;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.util.KtTestUtil;
import org.jetbrains.kotlin.test.TargetBackend;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.kotlin.generators.tests.GenerateJsTestsKt}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("compiler/testData/diagnostics/klibSerializationTests")
@TestDataPath("$PROJECT_ROOT")
public class FirPsiJsKlibDiagnosticsTestGenerated extends AbstractFirPsiJsDiagnosticWithBackendTest {
  @Test
  public void testAllFilesPresentInKlibSerializationTests() {
    KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("compiler/testData/diagnostics/klibSerializationTests"), Pattern.compile("^([^_](.+))\\.kt$"), Pattern.compile("^(.+)\\.fir\\.kts?$"), TargetBackend.JS_IR, true);
  }

  @Test
  @TestMetadata("signatureClashClasses.kt")
  public void testSignatureClashClasses() {
    runTest("compiler/testData/diagnostics/klibSerializationTests/signatureClashClasses.kt");
  }

  @Test
  @TestMetadata("signatureClashFunctions.kt")
  public void testSignatureClashFunctions() {
    runTest("compiler/testData/diagnostics/klibSerializationTests/signatureClashFunctions.kt");
  }

  @Test
  @TestMetadata("signatureClashVariables.kt")
  public void testSignatureClashVariables() {
    runTest("compiler/testData/diagnostics/klibSerializationTests/signatureClashVariables.kt");
  }

  @Test
  @TestMetadata("signatureClash_MPP.kt")
  public void testSignatureClash_MPP() {
    runTest("compiler/testData/diagnostics/klibSerializationTests/signatureClash_MPP.kt");
  }
}
