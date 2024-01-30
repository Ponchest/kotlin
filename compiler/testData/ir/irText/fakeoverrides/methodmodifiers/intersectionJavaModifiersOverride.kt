// TARGET_BACKEND: JVM
// FULL_JDK

// FILE: Java1.java
public interface Java1 {
    public abstract void foo();
}

// FILE: Java2.java
public interface Java2 {
    public abstract void foo();
}

// FILE: Java3.java
public class Java3 {
    public final void foo(){};
    public native void foo2();
    public synchronized void foo3(){};
}

// FILE: Java4.java
public interface Java4{
    public void foo();
    public void foo2();
    public void foo3();
}

// FILE: 1.kt

abstract class A : Java1, Java2 //Kotlin ← Java1, Java2

class B : Java1, Java2 {
    override fun foo() { }
}

class C : Java3(), Java4

class D : Java3(), Java4 {
    override fun foo2() { }
    override fun foo3() { }
}

class E : Java3(), KotlinInterface  //Kotlin ← Java, Kotlin2

class F: Java3(), KotlinInterface {
    override fun foo2() { }
    override fun foo3() { }
}

abstract class G : Java2, KotlinInterface2

class H : Java2, KotlinInterface2 {
    override fun foo(){ }
}

interface KotlinInterface {
    fun foo()
    fun foo2()
    fun foo3()
}

interface KotlinInterface2 {
    fun foo()
}

fun test(a: A, b: B, c: C, d: D, e: E, f: F, g: G,h: H){
    a.foo()
    b.foo()
    c.foo()
    c.foo2()
    c.foo3()
    d.foo()
    d.foo2()
    d.foo3()
    e.foo()
    e.foo2()
    e.foo3()
    f.foo()
    f.foo2()
    f.foo3()
    g.foo()
    h.foo()
}