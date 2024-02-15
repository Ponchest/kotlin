/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.analysis.checkers.expression

import org.jetbrains.kotlin.KtFakeSourceElementKind
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirErrors
import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.expressions.FirBlock
import org.jetbrains.kotlin.fir.expressions.FirLoop
import org.jetbrains.kotlin.fir.expressions.FirReturnExpression

object FirForLoopStatementAssignmentChecker : FirLoopExpressionChecker(MppCheckerKind.Common) {
    override fun check(expression: FirLoop, context: CheckerContext, reporter: DiagnosticReporter) {
        if (expression.source?.kind != KtFakeSourceElementKind.DesugaredForLoop) return

        val parent = if (context.containingElements.size >= 3) context.containingElements[context.containingElements.size - 3] else return
        if (parent is FirBlock
            || (parent is FirReturnExpression && parent.source?.kind == KtFakeSourceElementKind.ImplicitReturn.FromLastStatement)
            || (parent is FirProperty && parent.source?.kind == KtFakeSourceElementKind.DesugaredForLoop))
            return

        reporter.reportOn(expression.source, FirErrors.EXPRESSION_EXPECTED, context)
    }
}