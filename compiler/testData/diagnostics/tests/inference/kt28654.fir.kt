// !DIAGNOSTICS: -UNUSED_VARIABLE
// Related issue: KT-28654

fun <K> select(): K = <!RETURN_TYPE_MISMATCH!>run { }<!>

fun test() {
    val x: Int = select()
    val t = <!CANNOT_INFER_PARAMETER_TYPE, NEW_INFERENCE_NO_INFORMATION_FOR_PARAMETER!>select<!>()
}
