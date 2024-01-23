// TARGET_BACKEND: JVM
// FILE: J.java
public class J {
    public int j1;
    protected int j2;
    private int j3;

    public void funJ1() {}
    protected void funJ2() {}
    private void funJ3() {}
}


// FILE: test.kt
class A: J() {}

fun test(a: A) {
    a.j1 = 1
    a.j2 = 2

    a.funJ1()
    a.funJ2()
}
