// TARGET_BACKEND: JVM

// FILE: JavaProtected.java
public class JavaProtected {
    protected int a = 3;
    protected void foo() {}
}

// FILE: JavaDefault.java
public interface JavaDefault {
    int a = 2;
    void foo();
}

// FILE: JavaPublic.java
public interface JavaPublic {
    public int a = 1;
    public void foo();
}

// FILE: JavaPrivate.java
public class JavaPrivate  {
    private int a = 2;
    private void foo(){}
}

// FILE: test.kt
abstract class A: JavaDefault, KotlinDefault {
    public override fun foo() { }
    public override val a: Int
        get() = 5
}

abstract class B : JavaDefault, KotlinPrivate

class C : JavaDefault, KotlinPrivate {
    public override fun foo() {}
    val a = 5
}

class D : JavaDefault, KotlinProtected() {
    public override fun foo() {}
    protected override val a: Int
        get() = 5
}

class E : JavaDefault, KotlinPublic {
    public override fun foo() {}
    override val a: Int
        get() = 5
}

class F : JavaDefault, KotlinInternal() {
    public override fun foo() {}
    public override val a: Int
        get() = 5
}

class G : JavaPrivate(), KotlinDefault

class H : JavaPrivate(), KotlinDefault {
    override fun foo() {}
    override val a: Int
        get() = 5
}

class I : JavaPrivate(), KotlinPrivate

class J : JavaPrivate(), KotlinPublic

class K : JavaPrivate(), KotlinPublic {
    override fun foo() {}
    override val a: Int
        get() = 5
}

class L : JavaProtected(), KotlinDefault {
    public override fun foo() {}
    public override val a: Int
        get() = 5
}

class M : JavaProtected(), KotlinPrivate

class N : JavaProtected(), KotlinPrivate {
    public override fun foo() {}
    val a = 5
}

class O : JavaProtected(), KotlinPublic {
    public override fun foo() {}
    override val a: Int
        get() = 5
}

class P : JavaPublic, KotlinDefault {
    override fun foo() {}
    override val a: Int
        get() = 5
}

class Q : JavaPublic, KotlinPrivate {
    override fun foo() {}
    val a = 5
}

class R : JavaPublic, KotlinProtected() {
    public override fun foo() {}
    protected override val a: Int
        get() = 5
}

class S : JavaPublic, KotlinPublic {
    override fun foo() {}
    override val a: Int
        get() = 5
}

class T : JavaPublic, KotlinInternal() {
    public override fun foo() {}
    internal override val a: Int
        get() = 5
}

interface KotlinPrivate {
    private val a : Int
        get() = 1
    private fun foo(){}
}

open class KotlinProtected {
    protected open val a : Int = 1
    protected open fun foo(){}
}

interface KotlinDefault {
    val a : Int
        get() = 1
    fun foo(){}
}

interface KotlinPublic {
    public val a : Int
        get() = 1
    public fun foo(){}
}

open class KotlinInternal {
    internal open val a : Int
        get() = 1
    internal open fun foo(){}
}

fun test(a: A, b: B, c: C, d: D, e: E, f: F, h: H, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T){
    a.foo()
    a.a
    b.foo()
    c.foo()
    d.foo()
    e.foo()
    e.a
    f.foo()
    f.a
    h.foo()
    h.a
    j.foo()
    j.a
    k.foo()
    k.a
    l.foo()
    l.a
    m.foo()
    m.a
    n.foo()
    n.a
    o.foo()
    o.a
    p.foo()
    p.a
    q.foo()
    r.foo()
    s.foo()
    s.a
    t.foo()
    t.a
}