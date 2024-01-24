// TARGET_BACKEND: JVM
// FULL_JDK

// FILE: Java1.java

public interface Java1<T> extends A<T> { }

// FILE: Java2.java
public interface Java2<T> extends Java3<T>{ }

// FILE: Java3.java

public interface Java3<T> {
    public T foo();
    public void bar(T o);
}

// FILE: Java4.java

public interface Java4<T> extends KotlinInterface<T> { }

// FILE: 1.kt

interface A<T> {
    var a: T;
    fun foo(): T;
    fun bar(o: T);
}

abstract class B : Java1<Int>   //Kotlin ← Java ← Kotlin

class C(override var a: Int?) : Java1<Int> {    //Kotlin ← Java ← Kotlin
    override fun bar(o: Int?) { }

    override fun foo(): Int {
        return 1
    }
}

abstract class D(override var a: Int?) : Java1<Int> {   //Kotlin ← Java ← Kotlin
    override fun bar(o: Int) { }
}

class E : Java2<Int> {  // Kotlin ← Java1 ←Java2
    override fun foo(): Int {
        return 1
    }
    override fun bar(o: Int?) { }
}

abstract class F : Java2<Int> {  // Kotlin ← Java1 ←Java2
    override fun foo(): Int {
        return 1
    }
}

abstract class G : Java2<Int>   // Kotlin ← Java1 ←Java2

abstract class H : Java4<Int>   // Kotlin ← Java ← Kotlin ← Java

class I : Java4<Int> {  // Kotlin ← Java ← Kotlin ← Java
    override fun foo(): Int {
        return 1
    }

    override fun bar(o: Int?) { }
}

abstract class J : Java4<Int> { // Kotlin ← Java ← Kotlin ← Java
    override fun bar(o: Int?) { }
}

interface KotlinInterface<T> : Java3<T>

fun test(b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J) {
    var k: Int = b.a
    var k2: Int = b.foo()
    var k3: Unit = b.bar(1)
    var k4: Unit = b.bar(null)
    var k5: Int? = c.a
    var k6: Int = c.foo()
    var k7: Unit = c.bar(1)
    var k8: Unit = c.bar(null)
    var k9: Int? = d.a
    var k10: Int = d.foo()
    var k11: Unit = d.bar(1)
    var k21: Int = h.foo()
    var k22: Unit = h.bar(1)
    var k23: Unit = h.bar(null)
    var k24: Int = i.foo()
    var k25: Unit = i.bar(1)
    var k26: Unit = i.bar(null)
    var k27: Int = j.foo()
    var k28: Unit = j.bar(1)
    var k29: Unit = j.bar(null)
}