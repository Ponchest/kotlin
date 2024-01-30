// TARGET_BACKEND: JVM
// FULL_JDK

// FILE: Java1.java
import org.jetbrains.annotations.NotNull;

public class Java1<@NotNull T> {
    public T a;
    public T bar() {
        return a;
    }
    public void foo(T s) {}
}

// FILE: Java2.java
public class Java2 extends Java1<String> { }

// FILE: Java3.java
import org.jetbrains.annotations.Nullable;

public class Java3<@Nullable T> {
    public T a;
    public T bar() {
        return a;
    }
    public void foo(T s) {}
}

// FILE: 1.kt

class A : Java1<Int>()

class B : Java2()   //Kotlin ← Java1 ←Java2

class C : Java3<Int?>()

fun main(a: A, b: B, c: C) {
    val k: Int = a.a
    val k2: Int = a.bar()
    a.foo(1)

    val k3: String = b.a
    val k4: String = b.bar()
    b.foo("")

    val k5: Int? = c.a
    val k6: Int? = c.bar()
    c.foo(1)
    c.foo(null)
}