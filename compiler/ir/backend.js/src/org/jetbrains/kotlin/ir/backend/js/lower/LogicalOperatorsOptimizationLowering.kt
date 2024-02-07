/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.backend.js.lower

import org.jetbrains.kotlin.backend.common.BodyLoweringPass
import org.jetbrains.kotlin.ir.backend.js.JsCommonBackendContext
import org.jetbrains.kotlin.ir.backend.js.JsIrBackendContext
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.IrWhen
import org.jetbrains.kotlin.ir.expressions.impl.IrBlockImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.types.isBoolean
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.isFalseConst
import org.jetbrains.kotlin.ir.util.isTrueConst
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid

// Grabbed from JvmOptimizationLowering
class LogicalOperatorsOptimizationLowering(private val context: JsIrBackendContext) : BodyLoweringPass {
    override fun lower(irBody: IrBody, container: IrDeclaration) {
        irBody.transformChildrenVoid(object : IrElementTransformerVoid() {
            override fun visitWhen(expression: IrWhen): IrExpression {
                super.visitWhen(expression)
                val isCompilerGenerated = expression.origin == null
                // Remove all branches with constant false condition.
                expression.branches.removeIf {
                    it.condition.isFalseConst() && isCompilerGenerated
                }
                if (expression.origin == IrStatementOrigin.ANDAND) {
                    // Replace conjunction condition with intrinsic "and" function call
                    return IrCallImpl.fromSymbolOwner(
                        expression.startOffset,
                        expression.endOffset,
                        context.irBuiltIns.booleanType,
                        context.intrinsics.jsAnd
                    ).apply {
                        putValueArgument(0, expression.branches[0].condition)
                        putValueArgument(1, expression.branches[0].result)
                    }
                }
                if (expression.origin == IrStatementOrigin.OROR) {
                    return IrCallImpl.fromSymbolOwner(
                        expression.startOffset,
                        expression.endOffset,
                        context.irBuiltIns.booleanType,
                        context.intrinsics.jsOr
                    ).apply {
                        putValueArgument(0, expression.branches[0].condition)
                        putValueArgument(1, expression.branches[1].result)
                    }
                }

                // If there are no conditions left, remove the 'when' entirely and replace it with an empty block.
                if (expression.branches.size == 0) {
                    return IrBlockImpl(expression.startOffset, expression.endOffset, context.irBuiltIns.unitType)
                }

                // If the only condition that is left has a constant true condition, remove the 'when' in favor of the result.
                val firstBranch = expression.branches.first()
                if (firstBranch.condition.isTrueConst() && isCompilerGenerated) {
                    return firstBranch.result
                }

                return expression
            }
        })
    }
}