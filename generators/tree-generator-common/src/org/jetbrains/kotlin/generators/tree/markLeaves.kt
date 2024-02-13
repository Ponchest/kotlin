/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.generators.tree

internal fun markLeaves(elements: List<AbstractElement<*, *, *>>) {
    val leaves = elements.toMutableSet()

    for (element in elements) {
        for (parent in element.elementParents) {
            leaves.remove(parent.element)
        }
    }

    for (el in leaves) {
        el.isLeafElement = true
    }
}
