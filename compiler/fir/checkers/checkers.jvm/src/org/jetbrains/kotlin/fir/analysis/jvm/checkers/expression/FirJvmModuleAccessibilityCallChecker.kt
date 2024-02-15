/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.analysis.jvm.checkers.expression

import org.jetbrains.kotlin.KtVirtualFileSourceFile
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirQualifiedAccessExpressionChecker
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors
import org.jetbrains.kotlin.fir.containingClassLookupTag
import org.jetbrains.kotlin.fir.declarations.utils.sourceElement
import org.jetbrains.kotlin.fir.expressions.FirQualifiedAccessExpression
import org.jetbrains.kotlin.fir.expressions.toReference
import org.jetbrains.kotlin.fir.java.JavaBinarySourceElement
import org.jetbrains.kotlin.fir.modules.FirJavaModuleResolver
import org.jetbrains.kotlin.fir.packageFqName
import org.jetbrains.kotlin.fir.references.toResolvedCallableSymbol
import org.jetbrains.kotlin.fir.resolve.toFirRegularClassSymbol
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.resolve.jvm.modules.JavaModuleResolver

object FirJvmModuleAccessibilityCallChecker : FirQualifiedAccessExpressionChecker(MppCheckerKind.Common) {
    override fun check(expression: FirQualifiedAccessExpression, context: CheckerContext, reporter: DiagnosticReporter) {
        val callableSymbol = expression.calleeReference.toResolvedCallableSymbol() ?: return
        val containingClassSymbol = callableSymbol.containingClassLookupTag()?.toFirRegularClassSymbol(context.session) ?: return
        FirJvmModuleAccessibilityCheckingProcedure.checkJvmModuleAccess(context, containingClassSymbol, expression, reporter)
    }
}

object FirJvmModuleAccessibilityCheckingProcedure {
    internal fun checkJvmModuleAccess(
        context: CheckerContext, symbol: FirClassSymbol<*>, element: FirElement, reporter: DiagnosticReporter,
    ) {
        if (symbol.origin.fromSource) return

        val fileFromOurModule = (context.containingFile?.sourceFile as? KtVirtualFileSourceFile)?.virtualFile

        @OptIn(SymbolInternals::class)
        val javaBinarySourceElement = symbol.fir.sourceElement as? JavaBinarySourceElement ?: return

        val referencedPackageFqName = symbol.packageFqName()
        val diagnostic = context.session.javaModuleResolver.resolver.checkAccessibility(
            fileFromOurModule, javaBinarySourceElement.virtualFile, referencedPackageFqName
        ) ?: return

        val source = element.toReference(context.session)?.source ?: element.source
        when (diagnostic) {
            is JavaModuleResolver.AccessError.ModuleDoesNotExportPackage -> {
                reporter.reportOn(
                    source, FirJvmErrors.JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE, diagnostic.dependencyModuleName,
                    referencedPackageFqName.asString(), context,
                )
            }
            is JavaModuleResolver.AccessError.ModuleDoesNotReadModule -> {
                reporter.reportOn(source, FirJvmErrors.JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE, diagnostic.dependencyModuleName, context)
            }
            JavaModuleResolver.AccessError.ModuleDoesNotReadUnnamedModule -> {
                reporter.reportOn(source, FirJvmErrors.JAVA_MODULE_DOES_NOT_READ_UNNAMED_MODULE, context)
            }
        }
    }
}

private val FirSession.javaModuleResolver: FirJavaModuleResolver by FirSession.sessionComponentAccessor()
