// TARGET_BACKEND: JVM
// FULL_JDK

// FILE: Java1.java
public class Java1 {
    public String nullableString = "";
    public String bar() {
        return nullableString;
    }
    public void foo(String s) {}
}

// FILE: Java2.java
public interface Java2  {
    @NotNull
    public String nullableString = "";
    @NotNull
    public String bar();
    public void foo(@NotNull String s);
}

// FILE: 1.kt

class A : Java1(), Java2 {
    override fun bar(): String {
        return ""
    }

    override fun foo(s: String) { }
}

class B : Java1(), Java2

fun test(a: A,b: B) {
    val k: String? = a.nullableString
    val k2: String = a.bar()
    a.foo("")

    val k3: String = b.nullableString
    val k4: String = b.bar()
    b.foo("")
    b.foo(null)
}