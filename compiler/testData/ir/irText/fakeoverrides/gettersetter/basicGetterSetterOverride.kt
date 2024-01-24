// TARGET_BACKEND: JVM
// FULL_JDK

// FILE: Java1.java
public class Java1 {
    public int a = 1;
    public int getA() {
        return a;
    }
    public void setA(int t) {
        a = t;
    }

    public boolean b = true;
    public boolean isB() {
        return b;
    }
    public void setB(boolean t) {
        b = t;
    }

    public String c = "";
    public String getC() {
        return c;
    }

    public Integer d = 1;
    public void setD(Integer t) {
        d = t;
    }
}
// FILE: Java2.java
public interface Java2 extends KotlinInterface { }

// FILE: Java3.java
public interface Java3  {
    public int a = 0;
    public int getA();
    public void setA(int t);

    public boolean b = true;
    public boolean isB();
}

// FILE: Java4.java
public interface Java4 extends KotlinInterface2{}

// FILE: 1.kt
class A : Java1()

class B : Java1() {
    override fun getA(): Int {
        return 12
    }

    override fun setA(t: Int) {
        a = 10
    }

    override fun isB(): Boolean {
        return false
    }
}

abstract class C : Java2 {  //Kotlin ← Java ← Kotlin
    override val a: Int
        get() = 1
}

abstract class D : Java4 //Kotlin ← Java ← Kotlin ← Java

class E : Java4 {   //Kotlin ← Java ← Kotlin ← Java
    override fun getA(): Int {
        return 1
    }
    override fun setA(t: Int) { }
    override fun isB(): Boolean {
        return false
    }
}

interface KotlinInterface {
    val a: Int;
    var b: Int;
}

interface KotlinInterface2 : Java3

fun test(a: A, b: B, c: C, d: D, e: E) {
    val k: Int = a.a
    a.a = 3
    val k2: Boolean = a.b
    val k3: Boolean = a.isB
    a.b = true
    a.isB = false
    val k4: String = a.c
    a.c = ""
    val k5: Int = a.d
    a.d = 3

    val k6: Int = b.a
    b.a = 3
    val k7: Boolean = b.b
    val k8: Boolean = b.isB
    b.b = true
    b.isB = false
    val k9: String = b.c
    b.c = "3"
    val k10: Int = b.d
    b.d = 3

    val k11: Int = c.a
    val k12: Int = c.b
    c.b = 2

    val k13: Int = d.a
    val k14: Boolean = d.isB
    d.a = 2

    val k15: Int = e.a
    val k16: Boolean = e.isB
    e.a = 2
}