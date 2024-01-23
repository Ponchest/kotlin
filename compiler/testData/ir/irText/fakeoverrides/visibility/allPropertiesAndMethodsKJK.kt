// ISSUE: KT-61360
// TARGET_BACKEND: JVM
// FILE: J.java
public class J extends A {
    public int j1;
    protected int j2;
    private int j3;

    public void funJ1() {}
    protected void funJ2() {}
    private void funJ3() {}
}


// FILE: test.kt
abstract class A {
    public var a1 = 0
    protected var a2 = 0
    private var a3 = 0
    internal var a4 = 0

    public fun funA1() {}
    protected fun funA2() {}
    private fun funA3() {}
    internal fun funA4() {}
}

class B: J() {}

fun test(b: B) {
    b.a1 = 1
    //b.a4 = 2
    b.j1 = 3
    b.j2 = 4

    b.funA1()
    //b.funA4()
    b.funJ1()
    b.funJ2()
}
