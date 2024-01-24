// TARGET_BACKEND: JVM
// FULL_JDK

// FILE: Java1.java

public class Java1 {
    public static int a = 2;
    public static void foo(Object t) { }
    public static Object bar() {
        return null;
    }
}
// FILE: Java2.java
public interface Java2 {
    public int a = 2;
    public void foo(Object t);
    public Object bar();
}

// FILE: 1.kt

abstract class B : Java1(), Java2

class C : Java1(), Java2 {
    override fun foo(t: String) {}

    override fun bar(): String {
        return ""
    }
}

fun test(b: B, c: C) {
    val k: String = b.bar()
    b.foo("")
    b.foo(null)
    val k2: String = c.bar()
    c.foo("")
}