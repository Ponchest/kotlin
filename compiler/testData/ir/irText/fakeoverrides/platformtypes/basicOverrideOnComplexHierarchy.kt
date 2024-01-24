// TARGET_BACKEND: JVM
// FULL_JDK

// FILE: Java1.java

public class Java1 extends A { }

// FILE: Java2.java

public interface Java2 extends KotlinInterface { }

// FILE: Java3.java

public interface Java3 {
    public int a = 0;
    public int foo();
    public void bar(int o);
}

// FILE: 1.kt

open class A {
    open val a: Int = 1
    open var b: Int = 1
    open fun foo(): Int { return 0 }
    open fun bar(o: Int) { }
}

class B : Java1()  //Kotlin ← Java ← Kotlin

class C : Java1() {     //Kotlin ← Java ← Kotlin with explicit override
    override fun bar(o: Int) { }

    override fun foo(): Int {
        return 2
    }

    override var b: Int
        get() = 2
        set(value) {}

    override val a: Int
        get() = 2
}

abstract class D : Java1() {    // Kotlin ← Java ← Kotlin with explicit override
    override fun bar(o: Int) { }

    override val a: Int
        get() = 2
}

interface E : Java2     //Kotlin ← Java ← Kotlin ← Java

class F : Java2 { //Kotlin ← Java ← Kotlin ← Java with explicit override
    override fun foo(): Int {
        return 2
    }
    override fun bar(o: Int) { }
}

abstract class G : Java2 { //Kotlin ← Java ← Kotlin ← Java with explicit override
    override fun foo(): Int {
        return 2
    }
}

interface KotlinInterface : Java3

fun test(a: A, b: B, c: C, d: D, e: E, f: F, g: G) {
    val k1: Int = a.a
    val k2: Int = a.b
    a.b = 3
    val k3: Int = a.foo()
    val k4: Unit = a.bar(1)
    val k5 = b.a
    val k6 = b.b
    b.b = 3
    val k7: Int = b.foo()
    val k8: Unit = b.bar(1)
    val k9 = c.a
    val k10 = c.b
    c.b = 3
    val k11: Int = c.foo()
    val k12: Unit = c.bar(1)
    val k13 = d.a
    val k14 = d.b
    d.b = 3
    val k15: Int = d.foo()
    val k16: Unit = d.bar(1)
    val k17: Int = e.foo()
    val k18: Unit = e.bar(1)
    val k19: Int = f.foo()
    val k20: Unit = f.bar(1)
    val k21: Int = g.foo()
    val k22: Unit = g.bar(1)
}