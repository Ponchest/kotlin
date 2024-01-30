// TARGET_BACKEND: JVM
// FULL_JDK

// FILE: Java1.java
public class Java1 extends A {
    public Java1(int i) {
        super(i);
    }
}

// FILE: 1.kt
annotation class MyAnnotation

open class A {
    @MyAnnotation
    open fun foo(){}

    @MyAnnotation
    open val a : Int = 1

    @MyAnnotation constructor(i: Int)
}

class B: Java1(1)   //Kotlin ← Java ← Kotlin

class C : Java1(1) {
    override val a: Int
        get() = super.a

    override fun foo() { }
}

fun test(b: B, c: C) {
    b.a
    b.foo()
    c.a
    c.foo()
}

