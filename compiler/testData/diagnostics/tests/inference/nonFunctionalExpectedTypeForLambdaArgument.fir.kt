// !DIAGNOSTICS: -UNUSED_PARAMETER

fun callAny(arg: Any?) {}
fun <T> callParam(arg: T) {}

fun testAny() {
    callAny { error -> error }
    callAny l@{ error -> error }
    callAny({error -> error})
    callAny(({error -> error}))
    callAny(l@{error -> error})
    callAny((l@{error -> error}))
}

fun testAnyCall() {
    callAny <!CANNOT_INFER_PARAMETER_TYPE!>{
        error -> <!UNRESOLVED_REFERENCE!>error<!>()
    }<!>
}

fun testParam() {
    <!CANNOT_INFER_PARAMETER_TYPE!>callParam<!> <!CANNOT_INFER_PARAMETER_TYPE!>{
        <!CANNOT_INFER_PARAMETER_TYPE!>param<!> -> param
    }<!>
}

fun testParamCall() {
    <!CANNOT_INFER_PARAMETER_TYPE!>callParam<!> <!CANNOT_INFER_PARAMETER_TYPE!>{
        <!CANNOT_INFER_PARAMETER_TYPE!>param<!> -> <!UNRESOLVED_REFERENCE!>param<!>()
    }<!>
}

fun testNoContext() {
    { <!VALUE_PARAMETER_WITH_NO_TYPE_ANNOTATION!>it<!> -> it }
}
