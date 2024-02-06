// TARGET_BACKEND: JVM
// FULL_JDK
// Difference in $this (parent)

// FILE: J.java
public class J extends B {}


// FILE: test.kt
abstract class A {
    open public var a1 = 0
    open protected var a2 = 0
    open internal var a3 = 0
}

abstract class B: A() {
    override public var a1 = 0
    override protected var a2 = 0
    override internal var a3 = 0
}

class C: J() {}

fun test(c: C) {
    c.a1 = 1
    c.a3 = 2
}
