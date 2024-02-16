/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.sir.tree.generator

import org.jetbrains.kotlin.generators.tree.Model
import org.jetbrains.kotlin.sir.tree.generator.config.AbstractSwiftIrTreeImplementationConfigurator
import org.jetbrains.kotlin.sir.tree.generator.model.Element

object ImplementationConfigurator : AbstractSwiftIrTreeImplementationConfigurator() {

    override fun configure(model: Model<Element>) = with(SwiftIrTree) {
        // Declare custom implementation classes, see org.jetbrains.kotlin.fir.tree.generator.ImplementationConfigurator
    }

    override fun configureAllImplementations(model: Model<Element>) {
        // Use configureFieldInAllImplementations to customize certain fields in all implementation classes
        configureFieldInAllImplementations(
            field = "parent",
        ) {
            isMutable(it)
            isLateinit(it)
        }
    }
}