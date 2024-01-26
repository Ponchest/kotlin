/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

// This file was generated automatically. See compiler/ir/bir.tree/tree-generator/ReadMe.md.
// DO NOT MODIFY IT MANUALLY.

package org.jetbrains.kotlin.bir.expressions.impl

import org.jetbrains.kotlin.bir.CompressedSourceSpan
import org.jetbrains.kotlin.bir.declarations.BirAttributeContainer
import org.jetbrains.kotlin.bir.expressions.BirBreak
import org.jetbrains.kotlin.bir.expressions.BirLoop
import org.jetbrains.kotlin.bir.types.BirType

class BirBreakImpl(
    sourceSpan: CompressedSourceSpan,
    type: BirType,
    loop: BirLoop,
    label: String?,
) : BirBreak(BirBreak) {
    private var _sourceSpan: CompressedSourceSpan = sourceSpan
    /**
     * The span of source code of the syntax node from which this BIR node was generated,
     * in number of characters from the start the source file. If there is no source information for this BIR node,
     * the [SourceSpan.UNDEFINED] is used. In order to get the line number and the column number from this offset,
     * [IrFileEntry.getLineNumber] and [IrFileEntry.getColumnNumber] can be used.
     *
     * @see IrFileEntry.getSourceRangeInfo
     */
    override var sourceSpan: CompressedSourceSpan
        get() {
            recordPropertyRead()
            return _sourceSpan
        }
        set(value) {
            if (_sourceSpan != value) {
                _sourceSpan = value
                invalidate()
            }
        }

    private var _attributeOwnerId: BirAttributeContainer = this
    override var attributeOwnerId: BirAttributeContainer
        get() {
            recordPropertyRead()
            return _attributeOwnerId
        }
        set(value) {
            if (_attributeOwnerId !== value) {
                _attributeOwnerId = value
                invalidate()
            }
        }

    private var _type: BirType = type
    override var type: BirType
        get() {
            recordPropertyRead()
            return _type
        }
        set(value) {
            if (_type != value) {
                _type = value
                invalidate()
            }
        }

    private var _loop: BirLoop = loop
    override var loop: BirLoop
        get() {
            recordPropertyRead()
            return _loop
        }
        set(value) {
            if (_loop !== value) {
                _loop = value
                invalidate()
            }
        }

    private var _label: String? = label
    override var label: String?
        get() {
            recordPropertyRead()
            return _label
        }
        set(value) {
            if (_label != value) {
                _label = value
                invalidate()
            }
        }

}
