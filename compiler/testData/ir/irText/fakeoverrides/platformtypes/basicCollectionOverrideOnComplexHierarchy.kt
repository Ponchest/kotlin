// TARGET_BACKEND: JVM
// FULL_JDK
// WITH_STDLIB

// FILE: Java1.java

public class Java1 extends A { }

// FILE: Java2.java
import java.util.*;

public interface Java2 {
    List<Integer> a = new ArrayList();
    Queue<String> b = new ArrayDeque();
    Set<Object> c = new HashSet();

    void foo(List<Integer> a);
    List<Integer> bar();

    void foo2(Queue<String> b);
    Queue<String> bar2();

    void foo3(Set<Object> c);
    Set<Object> bar3();
}

// FILE: Java3.java
public interface Java3 extends KotlinInterface { }

// FILE: 1.kt
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

open class A {
    open var a : ArrayList<Int> = arrayListOf()
    open var b : HashSet<Any?> = hashSetOf()

    open fun foo(a: ArrayList<Int>) {}
    open  fun bar(): ArrayList<Int> { return a }

    open fun foo2(b: HashSet<Any?>) {}
    open fun bar2():HashSet<Any?> { return b }
}

class B : Java1()       //Kotlin ← Java ← Kotlin

class C : Java1() {     //Kotlin ← Java ← Kotlin with explicit override
    override var a: ArrayList<Int>
        get() = arrayListOf()
        set(value) {}

    override fun bar(): ArrayList<Int> {
        return arrayListOf<Int>()
    }

    override fun foo(a: ArrayList<Int>) { }
}

class D : Java1() {     //Kotlin ← Java ← Kotlin with explicit override
    override var b: HashSet<Any?>
        get() = hashSetOf()
        set(value) {}

    override fun bar2(): HashSet<Any?> {
        return hashSetOf()
    }

    override fun foo2(b: HashSet<Any?>) { }
}

abstract class E : Java3    //Kotlin ← Java ← Kotlin ← Java

class F : Java3 {   //Kotlin ← Java ← Kotlin ← Java with explicit override
    override fun foo(a: MutableList<Int>) { }

    override fun bar(): MutableList<Int> {
        return mutableListOf()
    }

    override fun foo2(b: Queue<String>) { }

    override fun bar2(): Queue<String> {
        return LinkedList<String>()
    }

    override fun foo3(c: MutableSet<Any>) { }

    override fun bar3(): MutableSet<Any> {
        return mutableSetOf()
    }

}

interface KotlinInterface : Java2

fun test(b: B, c: C, d: D, e: E, f: F) {
    b.a = arrayListOf(1)
    b.b = hashSetOf(1, "", null)
    b.foo(arrayListOf(1))
    val k1: List<Int> = b.bar()
    b.foo2(hashSetOf(1, "", null))
    val k2: Set<Any?> = b.bar2()

    c.a = arrayListOf(1)
    c.b = hashSetOf(1, "", null)
    c.foo(arrayListOf(1))
    val k3: List<Int> = c.bar()
    c.foo2(hashSetOf(1, "", null))
    val k4: Set<Any?> = c.bar2()

    d.a = arrayListOf(1)
    d.b = hashSetOf(1, "", null)
    d.foo(arrayListOf(1))
    val k5: List<Int> = d.bar()
    d.foo2(hashSetOf(1, "", null))
    val k6: Set<Any?> = d.bar2()

    e.foo(arrayListOf(1))
    val k7: List<Int> = e.bar()
    e.foo2(LinkedList())
    val k8: Queue<String> = e.bar2()
    e.foo3(mutableSetOf())
    val k9: Set<Any?> = e.bar3()

    f.foo(arrayListOf(1))
    val k10: List<Int> = f.bar()
    f.foo2(LinkedList())
    val k11: Queue<String> = f.bar2()
    f.foo3(mutableSetOf())
    val k12: Set<Any?> = f.bar3()
}