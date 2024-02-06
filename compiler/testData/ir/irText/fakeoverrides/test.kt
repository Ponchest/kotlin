// TARGET_BACKEND: JVM
// FULL_JDK
// WITH_STDLIB

// FILE: Java1.java
public interface Java1 extends CharSequence {}

// FILE: Java2.java
public interface Java2  {
    char get(int index);
}

// FILE: 1.kt
abstract class A : Java1    // Kotlin ← Java1 ←Java2

class B(override val length: Int) : Java1 {
    override fun get(index: Int): Char {
        return 'a'
    }

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        return null!!
    }
}

abstract class C : Java1, KotlinInterface   //Kotlin ← Java, Kotlin2 ← Kotlin3, Java3

class D(override val length: Int) : Java1, KotlinInterface {
    override fun get(index: Int): Char {
        return 'a'
    }

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        return null!!
    }

}

abstract class E : Java1, KotlinInterface2  //Kotlin ← Java, Kotlin2 ← Java2

class F(override val length: Int) : Java1, KotlinInterface2 {
    override fun get(index: Int): Char {
        return 'a'
    }

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        return null!!
    }
}

abstract class G : KotlinInterface, Java2

class H (override val length: Int) : KotlinInterface, Java2 {  //Kotlin ← Java, Kotlin2 ← Kotlin3
    override fun get(index: Int): Char {
        return 'a'
    }

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        return null!!
    }
}

abstract class I : Java2, Java1

class J(override val length: Int) : Java2, Java1 {    //Kotlin ← Java1, Java2 ← Java3
    override fun get(index: Int): Char {
        return 'a'
    }

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        return null!!
    }
}

interface KotlinInterface : CharSequence

interface KotlinInterface2 {
    fun get(index: Int): Any
}

fun test(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J) {
    a.length
    val k : Char = a[2]
    val k2: CharSequence = a.subSequence(2, 5)
    a.isEmpty()

    b.length
    val k3 : Char = b[2]
    val k4: CharSequence = b.subSequence(2, 5)
    b.isEmpty()

    c.length
    val k5 : Char = c[2]
    val k6: CharSequence = c.subSequence(2, 5)
    c.isEmpty()

    d.length
    val k7 : Char = d[2]
    val k8: CharSequence = d.subSequence(2, 5)
    d.isEmpty()

    e.length
    val k9 : Char = e[2]
    val k10: CharSequence = e.subSequence(2, 5)
    e.isEmpty()

    f.length
    val k11 : Char = f[2]
    val k12: CharSequence = f.subSequence(2, 5)
    f.isEmpty()

    g.length
    val k13 : Char = g[2]
    val k14: CharSequence = g.subSequence(2, 5)
    g.isEmpty()

    h.length
    val k15 : Char = h[2]
    val k16: CharSequence = h.subSequence(2, 5)
    h.isEmpty()

    i.length
    val k17 : Char = i[2]
    val k18: CharSequence = i.subSequence(2, 5)
    i.isEmpty()

    j.length
    val k19 : Char = j[2]
    val k20: CharSequence = j.subSequence(2, 5)
    j.isEmpty()
}
