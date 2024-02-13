// TARGET_BACKEND: JVM

// MODULE: separate
// FILE: J2.java
public class J2 {
    public int j1;
    protected int j2;
    private int j3;
    int j4;

    public void funJ1() {}
    protected void funJ2() {}
    private void funJ3() {}
    void funJ4() {}
}

// MODULE: main
// FILE: J.java
public class J {
    public int j1;
    protected int j2;
    private int j3;
    int j4;

    public void funJ1() {}
    protected void funJ2() {}
    private void funJ3() {}
    void funJ4() {}
}


// FILE: test.kt
class A: J()

class B : J2()

class C : J() {
    override fun funJ1() { }
    public override fun funJ2() { }
    internal override fun funJ4() { }
}

class D : J2() {
    override fun funJ1() { }
    public override fun funJ2() { }
}

fun test(a: A, b: B, c: C, d: D) {
    a.j1 = 1
    a.j2 = 2
    a.j4 = 3

    a.funJ1()
    a.funJ2()
    a.funJ4()

    b.j1 = 1
    b.funJ1()

    c.j1 = 1
    c.j2 = 2
    c.j4 = 3

    c.funJ1()
    c.funJ2()
    c.funJ4()

    d.j1 = 1
    d.funJ1()
    d.funJ2()
}
