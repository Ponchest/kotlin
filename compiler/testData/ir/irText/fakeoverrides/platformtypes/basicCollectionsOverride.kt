// TARGET_BACKEND: JVM
// FULL_JDK
// WITH_STDLIB

// MODULE: separate
// FILE: Java2.java
import java.util.*;

public class Java2 {
    public List<Integer> a = new ArrayList();
    public Queue<String> b = new ArrayDeque();
    public Set<Object> c = new HashSet();

    public void foo(List<Integer> a) {};
    public List<Integer> bar() { return a; };

    public void foo2(Queue<String> b) {};
    public Queue<String> bar2() { return b; };

    public void foo3(Set<Object> c) {};
    public Set<Object> bar3() { return c; };
}

// MODULE: main
// FILE: Java1.java
import java.util.*;

public class Java1 {
    public List<Integer> a = new ArrayList();
    public Queue<String> b = new ArrayDeque();
    public Set<Object> c = new HashSet();

    public void foo(List<Integer> a) {};
    public List<Integer> bar() { return a; };

    public void foo2(Queue<String> b) {};
    public Queue<String> bar2() { return b; };

    public void foo3(Set<Object> c) {};
    public Set<Object> bar3() { return c; };
}

// FILE: 1.kt
import java.util.*

class A: Java1()    //Kotlin ← Java

class B: Java2()    //Kotlin ← Java (separate module)

class C : Java1() { //Kotlin ← Java with explicit override
    override fun bar(): MutableList<Int> {
        return mutableListOf()
    }
    override fun bar2(): Queue<String> {
        return LinkedList()
    }

    override fun bar3(): MutableSet<Any> {
        return mutableSetOf()
    }
}

class D : Java1() { //Kotlin ← Java with explicit override
    override fun foo(a: MutableList<Int>) { }
    override fun foo2(b: Queue<String>) { }
    override fun foo3(c: MutableSet<Any>) { }
}


fun test(a: A, b: B, c: C, d: D) {
    a.a = listOf(1)
    a.b = LinkedList()
    a.c = setOf(null, 1)
    a.foo(listOf(1,2))
    val k: List<Int> = a.bar()
    a.foo2(LinkedList())
    val k2: Queue<String> = a.bar2()
    a.foo3(setOf("", 1))
    val k3: Set<Any> = a.bar3()

    b.a = listOf(1)
    b.b = LinkedList()
    b.c = setOf(null, 1)
    b.foo(listOf(1,2))
    val k4: List<Int> = b.bar()
    b.foo2(LinkedList())
    val k5: Queue<String> = b.bar2()
    b.foo3(setOf("", 1))
    val k6: Set<Any> = b.bar3()

    c.a = listOf(1)
    c.b = LinkedList()
    c.c = setOf(null, 1)
    c.foo(listOf(1,2))
    c.foo(mutableListOf())
    val k7: List<Int> = c.bar()
    c.foo2(LinkedList())
    val k8: Queue<String> = c.bar2()
    c.foo3(setOf("", 1))
    val k9: Set<Any> = c.bar3()

    d.a = listOf(1)
    d.b = LinkedList()
    d.c = setOf(null, 1)
    d.foo(mutableListOf())
    val k10: List<Int> = d.bar()
    d.foo2(LinkedList())
    val k11: Queue<String> = d.bar2()
    d.foo3(mutableSetOf())
    val k12: Set<Any> = d.bar3()
}