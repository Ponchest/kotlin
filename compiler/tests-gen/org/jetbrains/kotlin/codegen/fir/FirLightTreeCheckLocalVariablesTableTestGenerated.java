/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.fir;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.JUnit3RunnerWithInners;
import org.jetbrains.kotlin.test.KotlinTestUtils;
import org.jetbrains.kotlin.test.util.KtTestUtil;
import org.jetbrains.kotlin.test.TargetBackend;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.kotlin.test.generators.GenerateCompilerTestsKt}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("compiler/testData/checkLocalVariablesTable")
@TestDataPath("$PROJECT_ROOT")
@RunWith(JUnit3RunnerWithInners.class)
public class FirLightTreeCheckLocalVariablesTableTestGenerated extends AbstractFirLightTreeCheckLocalVariablesTableTest {
    private void runTest(String testDataFilePath) throws Exception {
        KotlinTestUtils.runTest(this::doTest, TargetBackend.JVM_IR, testDataFilePath);
    }

    public void testAllFilesPresentInCheckLocalVariablesTable() throws Exception {
        KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("compiler/testData/checkLocalVariablesTable"), Pattern.compile("^(.+)\\.kt$"), null, TargetBackend.JVM_IR, true);
    }

    @TestMetadata("destructuringInLambdas.kt")
    public void testDestructuringInLambdas() throws Exception {
        runTest("compiler/testData/checkLocalVariablesTable/destructuringInLambdas.kt");
    }

    @TestMetadata("inlineLambdaWithItParam.kt")
    public void testInlineLambdaWithItParam() throws Exception {
        runTest("compiler/testData/checkLocalVariablesTable/inlineLambdaWithItParam.kt");
    }

    @TestMetadata("inlineLambdaWithParam.kt")
    public void testInlineLambdaWithParam() throws Exception {
        runTest("compiler/testData/checkLocalVariablesTable/inlineLambdaWithParam.kt");
    }

    @TestMetadata("inlineSimple.kt")
    public void testInlineSimple() throws Exception {
        runTest("compiler/testData/checkLocalVariablesTable/inlineSimple.kt");
    }

    @TestMetadata("inlineSimpleChain.kt")
    public void testInlineSimpleChain() throws Exception {
        runTest("compiler/testData/checkLocalVariablesTable/inlineSimpleChain.kt");
    }

    @TestMetadata("itInLambda.kt")
    public void testItInLambda() throws Exception {
        runTest("compiler/testData/checkLocalVariablesTable/itInLambda.kt");
    }

    @TestMetadata("itInReturnedLambda.kt")
    public void testItInReturnedLambda() throws Exception {
        runTest("compiler/testData/checkLocalVariablesTable/itInReturnedLambda.kt");
    }

    @TestMetadata("kt11117.kt")
    public void testKt11117() throws Exception {
        runTest("compiler/testData/checkLocalVariablesTable/kt11117.kt");
    }

    @TestMetadata("lambdaAsVar.kt")
    public void testLambdaAsVar() throws Exception {
        runTest("compiler/testData/checkLocalVariablesTable/lambdaAsVar.kt");
    }

    @TestMetadata("objectInLocalPropertyDelegate.kt")
    public void testObjectInLocalPropertyDelegate() throws Exception {
        runTest("compiler/testData/checkLocalVariablesTable/objectInLocalPropertyDelegate.kt");
    }

    @TestMetadata("suspendFunctionDeadVariables.kt")
    public void testSuspendFunctionDeadVariables() throws Exception {
        runTest("compiler/testData/checkLocalVariablesTable/suspendFunctionDeadVariables.kt");
    }

    @TestMetadata("underscoreNames.kt")
    public void testUnderscoreNames() throws Exception {
        runTest("compiler/testData/checkLocalVariablesTable/underscoreNames.kt");
    }

    @TestMetadata("compiler/testData/checkLocalVariablesTable/parametersInSuspendLambda")
    @TestDataPath("$PROJECT_ROOT")
    @RunWith(JUnit3RunnerWithInners.class)
    public static class ParametersInSuspendLambda extends AbstractFirLightTreeCheckLocalVariablesTableTest {
        private void runTest(String testDataFilePath) throws Exception {
            KotlinTestUtils.runTest(this::doTest, TargetBackend.JVM_IR, testDataFilePath);
        }

        public void testAllFilesPresentInParametersInSuspendLambda() throws Exception {
            KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("compiler/testData/checkLocalVariablesTable/parametersInSuspendLambda"), Pattern.compile("^(.+)\\.kt$"), null, TargetBackend.JVM_IR, true);
        }

        @TestMetadata("dataClass.kt")
        public void testDataClass() throws Exception {
            runTest("compiler/testData/checkLocalVariablesTable/parametersInSuspendLambda/dataClass.kt");
        }

        @TestMetadata("extensionComponents.kt")
        public void testExtensionComponents() throws Exception {
            runTest("compiler/testData/checkLocalVariablesTable/parametersInSuspendLambda/extensionComponents.kt");
        }

        @TestMetadata("generic.kt")
        public void testGeneric() throws Exception {
            runTest("compiler/testData/checkLocalVariablesTable/parametersInSuspendLambda/generic.kt");
        }

        @TestMetadata("inline.kt")
        public void testInline() throws Exception {
            runTest("compiler/testData/checkLocalVariablesTable/parametersInSuspendLambda/inline.kt");
        }

        @TestMetadata("otherParameters.kt")
        public void testOtherParameters() throws Exception {
            runTest("compiler/testData/checkLocalVariablesTable/parametersInSuspendLambda/otherParameters.kt");
        }

        @TestMetadata("parameters.kt")
        public void testParameters() throws Exception {
            runTest("compiler/testData/checkLocalVariablesTable/parametersInSuspendLambda/parameters.kt");
        }
    }
}
