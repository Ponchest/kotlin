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
public class J2 extends J1 {
    public int j21;
    protected int j22;
    private int j23;

    public void funJ21() {}
    protected void funJ22() {}
    private void funJ23() {}
}


// FILE: test.kt
class A: J2() {}

fun test(a: A) {
    a.j11 = 1
    a.j12 = 2
    a.j21 = 3
    a.j22 = 4

    a.funJ11()
    a.funJ12()
    a.funJ21()
    a.funJ22()
}
