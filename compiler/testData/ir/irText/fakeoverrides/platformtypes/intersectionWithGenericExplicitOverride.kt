// TARGET_BACKEND: JVM
// FULL_JDK

// MODULE: separate
// FILE: JavaSeparateModule.java
public interface JavaSeparateModule {
    public int a = 1;
    public int foo();
    public void bar(int o);
}

// MODULE: main
// FILE: Java1.java

public interface Java1<T> {
    public T foo();
    public void bar(T o);
}

// FILE: Java2.java

public interface Java2<T> {
    public T foo();
    public void bar(T o);
}

// FILE: Java3.java

public class Java3<T> {
    public T a;
    public T foo(){return a;};
    public void bar(T o){};
}

// FILE: 1.kt

class A<R> : Java1<R>, Java2<R>{    //Kotlin ← Java1, Java2
    override fun foo(): R {
        return null!!
    }
    override fun bar(o: R) { }
}

abstract class B<R> : Java1<R>, Java2<R>{   //Kotlin ← Java1, Java2
    override fun foo(): R {
        return null!!
    }
}

class C<R> : Java1<R>, Java3<R>() { //Kotlin ← Java1, Java2
    override fun foo(): R {
        return null!!
    }
    override fun bar(o: R) { }
}

class D<R>(override var a: R) : Java1<R>, KotlinInterface<R>{   //Kotlin ← Java, Kotlin2
    override fun foo(): R {
        return null!!
    }

    override fun bar(o: R) { }
}

abstract class E<R>(override var a: R) : Java1<R>, KotlinInterface<R>{  //Kotlin ← Java, Kotlin2
    override fun bar(o: R) { }
}

class F<R>(override var a: R) : Java1<R>, Java2<R>, KotlinInterface<R>{ //Kotlin ← Java1, Java2, Kotlin2
    override fun foo(): R {
        return null!!
    }

    override fun bar(o: R) { }
}

abstract class G<R>(override var a: R) : Java1<R>, Java2<R>, KotlinInterface<R>{ //Kotlin ← Java1, Java2, Kotlin2
    override fun bar(o: R) { }
}

class H<R>: Java1<R>, Java2<R>, Java3<R>(){ //Kotlin ← Java1, Java2, Java3
    override fun foo(): R {
        return super.foo()
    }
}

class I<R>: Java1<R>, Java2<R>, Java3<R>(){ //Kotlin ← Java1, Java2, Java3
    override fun bar(o: R) {
        super.bar(o)
    }
}

class J<R>: Java1<R>, KotlinInterface<R>, KotlinInterface2<R>{  //Kotlin ← Java, Kotlin1, Kotlin2
    override var a: R
        get() = null!!
        set(value) {}

    override fun foo(): R {
        return null!!
    }

    override fun bar(o: R) { }
}

abstract class L<R>: Java1<R>, KotlinInterface<R>, KotlinInterface2<R>{ //Kotlin ← Java, Kotlin1, Kotlin2
    override fun foo(): R {
        return null!!
    }
}

interface KotlinInterface<T> {
    var a: T
    fun foo(): T
    fun bar(o: T)
}

interface KotlinInterface2<T> {
    var a: T
    fun foo(): T
    fun bar(o: T)
}

fun test(a: A<Int>, b: B<String>, c: C<Int>,  d: D<String>, e: E<Int>, f: F<Int>,
         g: G<Int>, h: H<Int>, i: I<Int>, j: J<Int>, l: L<Int>) {
    val k: Int = a.foo()
    val k2: Unit = a.bar(1)
    val k3: String = b.foo()
    val k4: Unit = b.bar("")
    val k5: Unit = b.bar(null)
    val k6: Int = c.foo()
    val k7: Unit = c.bar(1)
    val k8: String = d.foo()
    val k9: Unit = d.bar("")
    val k10: String = d.a
    d.a = ""
    val k11: Int = e.foo()
    val k12: Unit = e.bar(1)
    val k13: Int = e.a
    e.a = 1
    val k14: Int = f.foo()
    val k15: Unit = f.bar(1)
    val k16: Int = f.a
    f.a = 1
    val k17: Int = g.foo()
    val k18: Unit = g.bar(1)
    val k19: Int = g.a
    g.a = 1
    val k20: Int = h.foo()
    val k21: Unit = h.bar(1)
    val k22: Int = h.a
    val k23: Unit = h.bar(null)
    h.a = 1
    val k24: Int = i.foo()
    val k25: Unit = i.bar(1)
    val k26: Int = i.a
    i.a = 1
    val k27: Int = j.foo()
    val k28: Unit = j.bar(1)
    val k29: Int = j.a
    j.a = 1
    val k30: Int = l.foo()
    val k31: Unit = l.bar(1)
    val k32: Unit = l.bar(null)
    val k33: Int = l.a
    l.a = 1
}