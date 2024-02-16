/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.generator

import org.jetbrains.kotlin.generators.tree.ImplementationKind
import org.jetbrains.kotlin.generators.tree.Model
import org.jetbrains.kotlin.generators.tree.StandardTypes
import org.jetbrains.kotlin.ir.generator.config.AbstractIrTreeImplementationConfigurator
import org.jetbrains.kotlin.ir.generator.model.Element
import org.jetbrains.kotlin.ir.generator.model.ListField

object ImplementationConfigurator : AbstractIrTreeImplementationConfigurator() {
    override fun configure(model: Model<Element>): Unit = with(IrTree) {
        impl(anonymousInitializer) {
            isLateinit("body")
        }

        impl(simpleFunction) {
            implementation.name = "IrFunctionImpl"
            defaultEmptyList("valueParameters")
            defaultNull("dispatchReceiverParameter", "extensionReceiverParameter", "body", "correspondingPropertySymbol")
            default("contextReceiverParametersCount", "0")
        }
        impl(functionWithLateBinding) {
            defaultEmptyList("valueParameters")
            defaultNull("dispatchReceiverParameter", "extensionReceiverParameter", "body", "correspondingPropertySymbol")
            default("contextReceiverParametersCount", "0")
            default("containerSource") {
                value = "null"
                withGetter = true
            }
            default("isBound") {
                value = "_symbol != null"
                withGetter = true
            }
            default("symbol") {
                value = "_symbol ?: error(\"\$this has not acquired a symbol yet\")"
                withGetter = true
            }
            default("descriptor") {
                value = "_symbol?.descriptor ?: this.toIrBasedDescriptor()"
                withGetter = true
            }
        }

        impl(constructor) {
            defaultEmptyList("valueParameters")
            defaultNull("dispatchReceiverParameter", "extensionReceiverParameter", "body")
            default("contextReceiverParametersCount", "0")
        }

        impl(field) {
            defaultNull("initializer", "correspondingPropertySymbol")
        }

        impl(property) {
            defaultNull("backingField", "getter", "setter")
        }
        impl(propertyWithLateBinding) {
            defaultNull("backingField", "getter", "setter")
            default("containerSource") {
                value = "null"
                withGetter = true
            }
            default("isBound") {
                value = "_symbol != null"
                withGetter = true
            }
            default("symbol") {
                value = "_symbol ?: error(\"\$this has not acquired a symbol yet\")"
                withGetter = true
            }
            default("descriptor") {
                value = "_symbol?.descriptor ?: this.toIrBasedDescriptor()"
                withGetter = true
            }
        }

        impl(localDelegatedProperty) {
            isLateinit("delegate", "getter")
            defaultNull("setter")
        }

        impl(typeParameter) {
            defaultEmptyList("superTypes")
        }

        impl(valueParameter) {
            defaultNull("defaultValue")
        }

        impl(variable) {
            implementation.putImplementationOptInInConstructor = false
            implementation.constructorParameterOrderOverride =
                listOf("startOffset", "endOffset", "origin", "symbol", "name", "type", "isVar", "isConst", "isLateinit")
            defaultNull("initializer")
            default("factory") {
                value = "error(\"Create IrVariableImpl directly\")"
                withGetter = true
            }
        }

        impl(`class`) {
            kind = ImplementationKind.OpenClass
            defaultNull("thisReceiver", "valueClassRepresentation")
            defaultEmptyList("superTypes", "sealedSubclasses")
            defaultFalse("isExternal", "isCompanion", "isInner", "isData", "isValue", "isExpect", "isFun", "hasEnumEntries")
        }

        impl(enumEntry) {
            implementation.doPrint = false
        }

        impl(script) {
            implementation.doPrint = false
        }

        impl(moduleFragment) {
            implementation.doPrint = false
        }

        impl(errorDeclaration) {
            implementation.doPrint = false
        }

        impl(externalPackageFragment) {
            implementation.doPrint = false
        }

        impl(file) {
            implementation.doPrint = false
        }

        impl(typeAlias) {
            implementation.doPrint = false
        }
    }

    override fun configureAllImplementations(model: Model<Element>) {
        configureFieldInAllImplementations("parent") {
            default(it) {
                isLateinit("parent")
                isMutable("parent")
            }
        }

        configureFieldInAllImplementations("attributeOwnerId") {
            default(it, "this")
        }
        configureFieldInAllImplementations("originalBeforeInline") {
            defaultNull(it)
        }

        configureFieldInAllImplementations("metadata") {
            defaultNull(it)
        }

        configureFieldInAllImplementations("annotations") {
            defaultEmptyList(it)
        }

        configureFieldInAllImplementations("overriddenSymbols") {
            defaultEmptyList(it)
        }

        configureFieldInAllImplementations("typeParameters") {
            defaultEmptyList(it)
        }

        configureFieldInAllImplementations("statements") {
            default(it, "ArrayList(2)")
        }

        configureFieldInAllImplementations("descriptor", { impl -> impl.allFields.any { it.name == "symbol" } }) {
            default(it) {
                value = "symbol.descriptor"
                withGetter = true
            }
        }

        for (element in model.elements) {
            element.defaultImplementation?.let { implementation ->
                for (field in implementation.allFields) {
                    if (field is ListField && field.isChild && field.listType == StandardTypes.mutableList) {
                        field.defaultValueInImplementation = "ArrayList()"
                    }
                }
            }
        }

        for (element in model.elements) {
            if (element.category == Element.Category.Expression) {
                element.defaultImplementation?.doPrint = false
            }
        }
    }
}