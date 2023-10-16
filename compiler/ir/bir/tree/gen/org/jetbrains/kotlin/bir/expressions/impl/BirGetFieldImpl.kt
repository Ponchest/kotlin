/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

// This file was generated automatically. See compiler/ir/ir.tree/tree-generator/ReadMe.md.
// DO NOT MODIFY IT MANUALLY.

package org.jetbrains.kotlin.bir.expressions.impl

import org.jetbrains.kotlin.bir.BirElement
import org.jetbrains.kotlin.bir.SourceSpan
import org.jetbrains.kotlin.bir.declarations.BirAttributeContainer
import org.jetbrains.kotlin.bir.expressions.BirExpression
import org.jetbrains.kotlin.bir.expressions.BirGetField
import org.jetbrains.kotlin.bir.symbols.BirClassSymbol
import org.jetbrains.kotlin.bir.symbols.BirFieldSymbol
import org.jetbrains.kotlin.bir.types.BirType
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin

class BirGetFieldImpl(
    override var sourceSpan: SourceSpan,
    override var type: BirType,
    override var symbol: BirFieldSymbol,
    override var superQualifierSymbol: BirClassSymbol?,
    receiver: BirExpression?,
    override var origin: IrStatementOrigin?,
) : BirGetField() {
    override var attributeOwnerId: BirAttributeContainer = this

    private var _receiver: BirExpression? = receiver

    override var receiver: BirExpression?
        get() = _receiver
        set(value) {
            if (_receiver != value) {
                replaceChild(_receiver, value)
                _receiver = value
            }
        }
    init {
        initChild(_receiver)
    }

    override fun replaceChildProperty(old: BirElement, new: BirElement?) {
        when {
            this._receiver === old -> this.receiver = new as BirExpression
            else -> throwChildForReplacementNotFound(old)
        }
    }
}
