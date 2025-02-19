// !LANGUAGE: +SuspendOnlySamConversions
// TARGET_BACKEND: JVM_IR
// JVM_ABI_K1_K2_DIFF: KT-62855

fun interface FI {
    suspend fun call() // suspending now(!!!)
}

fun accept(fi: FI): Int = 1

suspend fun foo() {}
fun foo2() {}

fun box(): String {
    val fi0: () -> Unit = {}
    accept(fi0)

    val fi: suspend () -> Unit = {} // Lambda of a suspending(!!!) functional type
    accept(fi) // ERROR: Type mismatch. Required: FI Found: suspend () → Unit

    accept(::foo)
    val x1 = ::foo
    accept(x1) // ERROR: Type mismatch. Required: FI Found: suspend () → Unit

    accept(::foo2)
    val x2 = ::foo2
    accept(x2) // ERROR: Type mismatch. Required: FI Found: suspend () → Unit

    return "OK"
}

