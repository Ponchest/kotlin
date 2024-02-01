// EXPECTED_REACHABLE_NODES: 1238
// MODULE_KIND: AMD
// FILE: bar.kt
@file:JsModule("bar")
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