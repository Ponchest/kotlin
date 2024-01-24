// TARGET_BACKEND: JVM
// FULL_JDK
// WITH_STDLIB

// FILE: Java1.java
import java.util.*;

public class Java1 {
    public List a = new ArrayList();
    public void foo(List a) {};
    public List bar() { return null; };
}

// FILE: Java2.java
import java.util.*;

public interface Java2  {
    public List a = new ArrayList();
    public void foo(List a);
    public List bar();
}

// FILE: 1.kt

class A : Java1(), Java2

class B : Java1(), Java2 {
    override fun bar(): MutableList<Any?>? {
        return null!!
    }
    override fun foo(a: MutableList<Any?>?) { }
}

abstract class C: Java1(), KotlinInterface

class D : Java1(), KotlinInterface {
    override var a: List<Any?>
        get() = emptyList()
        set(value) {}

    override fun foo(a: List<Any?>) { }

    override fun bar(): List<Any?> {
        return null!!
    }
}

interface KotlinInterface {
    var a: List<Any?>
    fun foo(a: List<Any?>)
    fun bar(): List<Any?>
}

fun test(a: A, b: B, c: C, d: D) {
    a.foo(null)
    a.foo(mutableListOf(null))
    val k: List<Any?> = a.bar()
    b.foo(mutableListOf(null))
    b.foo(null)
    val k2: List<Any?>? = b.bar()
    c.foo(listOf(null))
    c.foo(null)
    val k3: List<Any?> = c.bar()
    d.foo(listOf(null))
    val k4: List<Any?> = d.bar()
}