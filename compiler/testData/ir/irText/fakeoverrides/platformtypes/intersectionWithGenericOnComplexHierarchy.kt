// TARGET_BACKEND: JVM
// FULL_JDK

// FILE: Java1.java
public interface Java1<T>  {
    T foo();
    void bar(T o);
}

// FILE: Java2.java
public interface Java2<T> extends A<T> { }

// FILE: Java3.java
public interface Java3<T> {
    T foo();
    void bar(T o);
}

// FILE: Java4.java
public interface Java4<T> extends Java1<T> { }

// FILE: 1.kt

interface A<T> {
    var a: T;
    fun foo(): T;
    fun bar(o: T);
}

abstract class B<T> : Java1<T>, Java2<T> //Kotlin ← Java1, Java2 ← Kotlin2

class C<T>(override var a: T) : Java1<T>, Java2<T> {    //Kotlin ← Java1, Java2 ← Kotlin2
    override fun bar(o: T) { }
    override fun foo(): T {
        return null!!
    }
}

abstract class D<T> : Java1<T>, Java2<T> {  //Kotlin ← Java1, Java2 ← Kotlin2
    override fun foo(): T {
        return null!!
    }
}

abstract class E<T> : Java1<T>, Java2<T>  { //Kotlin ← Java1, Java2 ← Kotlin2
    override fun bar(o: T) { }
}

abstract class F<T> : Kotlin<T>, Java2<T>    // Kotlin ← Java, Kotlin2 ← Kotlin3

abstract class G<T> : Kotlin<T>, Java2<T> { // Kotlin ← Java, Kotlin2 ← Kotlin3
    override fun foo(): T {
        return null!!
    }
}

class H<T>(override var a: T) : Kotlin<T>, Java2<T> {   // Kotlin ← Java, Kotlin2 ← Kotlin3
    override fun bar(o: T) { }

    override fun foo(): T {
        return null!!
    }
}

abstract class I<T> : Kotlin2<T>, Java3<T>   //Kotlin ← Java, Kotlin2 ← Java2

class J<T> : Kotlin2<T>, Java3<T> { //Kotlin ← Java, Kotlin2 ← Java2
    override fun bar(o: T) { }

    override fun foo(): T {
        return null!!
    }
}

abstract class L<T> : Kotlin2<T>, Java3<T> {    //Kotlin ← Java, Kotlin2 ← Java2
    override fun bar(o: T) { }
}

abstract class M<T> : Java4<T>, Java3<T>     //Kotlin ← Java1, Java2 ← Java3

class N<T> : Java4<T>, Java3<T>  {  //Kotlin ← Java1, Java2 ← Java3
    override fun bar(o: T) { }

    override fun foo(): T {
        return null!!
    }
}

abstract class O<T> : Java4<T>, Java3<T> {  //Kotlin ← Java1, Java2 ← Java3
    override fun foo(): T {
        return null!!
    }
}

interface Kotlin<T> {
    fun foo(): T
    fun bar(o: T)
}

interface Kotlin2<T> : Java1<T>

fun test(b: B<Int>, c: C<Any>, d: D<String>, e: E<Int?>, f: F<Any?>, g: G<String?>, h: H<Int>, i: I<Any>,
         j:J<String>, l: L<Int?>, m: M<Any?>, n: N<String?>, o: O<Int>) {
    val k: Int = b.a
    b.a = 1
    val k2: Unit = b.bar(1)
    val k3: Unit = b.bar(null)
    val k4: Int = b.foo()
    val k5: Any = c.a
    c.a = ""
    val k6: Unit = c.bar(1)
    val k7: Unit = c.bar("")
    val k8: Any = c.foo()
    val k9: String = d.a
    d.a = ""
    val k10: Unit = d.bar("")
    val k11: Unit = d.bar(null)
    val k12: String = d.foo()
    val k13: Int? = e.a
    e.a = null
    val k14: Unit = e.bar(1)
    val k15: Unit = e.bar(null)
    val k16: Int? = e.foo()
    val k17: Any? = f.a
    f.a = null
    val k18: Unit = f.bar(1)
    val k19: Unit = f.bar("")
    val k20: Unit = f.bar(null)
    val k21: Any? = f.foo()
    val k22: String? = g.a
    g.a = null
    val k23: Unit = g.bar(null)
    val k24: Unit = g.bar("")
    val k25: Any? = f.foo()
    val k26: Int = h.a
    h.a = 1
    val k27: Unit = h.bar(1)
    val k28: Int = h.foo()
    val k29: Unit = i.bar(1)
    val k30: Unit = i.bar("")
    val k31: Unit = i.bar(null)
    val k32: Any = i.foo()
    val k33: Unit = j.bar("")
    val k34: String = j.foo()
    val k35: Unit = l.bar(1)
    val k36: Unit = l.bar(null)
    val k37: Int? = l.foo()
    val k38: Unit = m.bar(1)
    val k39: Unit = m.bar("")
    val k40: Unit = m.bar(null)
    val k41: Any? = m.foo()
    val k42: Unit = n.bar("")
    val k43: Unit = n.bar(null)
    val k44: String? = n.foo()
    val k45: Unit = o.bar(1)
    val k46: Unit = o.bar(null)
    val k47: Int = o.foo()
}