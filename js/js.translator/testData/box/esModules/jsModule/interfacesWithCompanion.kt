// IGNORE_BACKEND: WASM
// EXPECTED_REACHABLE_NODES: 1238
// ES_MODULES
// FILE: bar.kt
@file:JsModule("./interfacesWithCompanion.mjs")
package bar

external interface Bar {
    companion object {
        fun ok(): String
    }
}

// FILE: test.kt
import bar.Bar

fun box(): String {
    return Bar.ok()
}