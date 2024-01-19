// ISSUE: KT-61360
// TARGET_BACKEND: JVM
// FILE: J.java
public class J {
    public void funJ1() {}
    protected void funJ2() {}
}


// FILE: test.kt
class A: J() {
    override fun funJ1() {}
    public override fun funJ2() {}
}

fun test(a: A) {
    a.funJ1()
    a.funJ2()
}
