// TARGET_BACKEND: JVM

// MODULE: separate
// FILE: KotlinInternalSeparate.kt
open class KotlinInternalSeparate {
    @PublishedApi
    internal open val a : Int
        get() = 1
    @PublishedApi
    internal open fun foo() {}
}

// MODULE: main(separate)
// FILE: JavaDefault.java
public interface JavaDefault {
    int a = 2;
    void foo();
}

// FILE: JavaPublic.java
public interface JavaPublic {
    public int a = 2;
    public void foo();
}

// FILE: test.kt

abstract class C : KotlinInternalSeparate()

