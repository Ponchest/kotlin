// TARGET_BACKEND: JVM
// FULL_JDK
// WITH_STDLIB

// FILE: Java1.java
import java.util.*;

public class Java1 {
    public <T> void foo(T a) { }
    public <T> T bar() {
        return null;
    }
}

// FILE: Java2.java
import java.util.*;

public interface Java2  {
    public <T> void foo(T a);
    public <T> T bar();
}

// FILE: 1.kt

class A : Java1(), Java2

class B : Java1(), Java2 {
    override fun <T : Any?> bar(): T {
        return null!!
    }

    override fun <T : Any?> foo(a: T) { }
}

abstract class C : Java1(), KotlinInterface

interface KotlinInterface {
    fun <T> foo(a: T)
    fun <T> bar(): T
}


fun test(a: A, b: B, c: C) {
    val k: Int = a.bar<Int?>()
    val k3: Any = a.bar()
    val k4: Nothing = a.bar()
    a.foo(1)
    a.foo(null)
    a.foo<Int?>(null)
    a.foo(listOf(null))

    val k5: Int? = b.bar<Int?>()
    val k6: Any = b.bar<Any>()
    val k7: Nothing = b.bar()
    b.foo(1)
    b.foo(null)
    b.foo<Int?>(null)
    b.foo(listOf(null))

    val k8: Int? = c.bar<Int?>()
    val k9: Any = c.bar<Any>()
    val k10: Nothing = c.bar()
    c.foo(1)
    c.foo(null)
    c.foo<Int?>(null)
    c.foo(listOf(null))
}