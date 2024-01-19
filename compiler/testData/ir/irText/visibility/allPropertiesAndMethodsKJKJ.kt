// ISSUE: KT-61360
// TARGET_BACKEND: JVM
// FILE: J1.java
public class J1 {
    public int j11;
    protected int j12;
    private int j13;

    public void funJ11() {}
    protected void funJ12() {}
    private void funJ13() {}
}


// FILE: J2.java
public class J2 extends A {
    public int j21;
    protected int j22;
    private int j23;

    public void funJ21() {}
    protected void funJ22() {}
    private void funJ23() {}
}


// FILE: test.kt
// kotlin (A) <- java (J1)
abstract class A: J1() {
    public var a1 = 0
    protected var a2 = 0
    private var a3 = 0
    internal var a4 = 0

    public fun funA1() {}
    protected fun funA2() {}
    private fun funA3() {}
    internal fun funA4() {}
}

class B: J2() {}

fun test(b: B) {
    b.j11 = 1
    b.j12 = 2
    b.a1 = 3
    //b.a4 = 4
    b.j21 = 5
    b.j22 = 6

    b.funJ11()
    b.funJ12()
    b.funA1()
    //b.funA4()
    b.funJ21()
    b.funJ22()
}
