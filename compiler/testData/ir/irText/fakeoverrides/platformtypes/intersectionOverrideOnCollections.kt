// TARGET_BACKEND: JVM
// FULL_JDK
// WITH_STDLIB

// FILE: Java1.java
import java.util.*;

public interface Java1 {
    public List<Integer> a = new ArrayList();
    public void foo(List<Integer> a);
    public List<Integer> bar();
}

// FILE: Java2.java
import java.util.*;

public interface Java2 {
    public List<Integer> a = new ArrayList();
    public void foo(ArrayList<Integer> a);
    public ArrayList<Integer> bar();
}

// FILE: 1.kt
import kotlin.collections.ArrayList

interface A : Java1, Java2  //Kotlin ← Java1, Java2

abstract class B: Java1, Java2 {    //Kotlin ← Java1, Java2 with explicit override
    override fun foo(a: MutableList<Int>) { }

    override fun bar(): ArrayList<Int> {
        return null!!
    }
}

interface C : Java2, KotlinInterface //Kotlin ← Java, Kotlin2

abstract class D : Java2, KotlinInterface { //Kotlin ← Java, Kotlin2 with explicit override
    override fun foo(o: ArrayList<Int>) { }

    override fun bar(): ArrayList<Int> {
        return null!!
    }
}

interface KotlinInterface {
    fun foo(o: ArrayList<Int>)
    fun bar(): ArrayList<Int>
}


fun test(a: A, b: B, c: C, d: D){
    a.foo(listOf(1,null))
    a.foo(arrayListOf(1, null))
    val k: MutableList<Int> = a.bar()
    val k2: ArrayList<Int> = a.bar()

    b.foo(mutableListOf(1))
    b.foo(arrayListOf(1, null))
    val k3: MutableList<Int> = b.bar()
    val k4: ArrayList<Int> = b.bar()

    c.foo(arrayListOf(1, null))
    val k5: MutableList<Int> = c.bar()
    val k6: ArrayList<Int> = c.bar()

    d.foo(arrayListOf(1))
    val k7: MutableList<Int> = d.bar()
    val k8: ArrayList<Int> = d.bar()
}
