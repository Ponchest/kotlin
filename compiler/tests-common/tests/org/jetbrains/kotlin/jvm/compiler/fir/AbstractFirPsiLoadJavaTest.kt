/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.jvm.compiler.fir

import org.jetbrains.kotlin.jvm.compiler.ir.AbstractIrLoadJavaTest
import org.jetbrains.kotlin.test.FirParser

abstract class AbstractFirPsiLoadJavaTest : AbstractIrLoadJavaTest() {
    override val useFir: Boolean
        get() = true

    override val firParser: FirParser
        get() = FirParser.Psi
}
