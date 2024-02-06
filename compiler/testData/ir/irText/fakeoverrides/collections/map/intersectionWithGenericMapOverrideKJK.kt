// TARGET_BACKEND: JVM
// FULL_JDK
// WITH_STDLIB
// KT-65302 + difference in this + extra override the same with K1

// FILE: 1.kt
import java.util.SortedMap

abstract class A<T> : SortedMap<T, T>, Map<T, T>

abstract class B<T> : SortedMap<T, T>, Map<T, T> {
    override fun put(key: T, value: T): T {
        return null!!
    }

    override fun remove(key: T): T {
        return null!!
    }
}

fun test(a: A<Boolean>, b: B<Boolean?>) {
    a.size
    a[true] = true
    a.put(null, null)
    a.get(true)
    a.get(null)
    a.remove(null)
    a.remove(true)

    b.size
    b.put(false, false)
    b[true] = true
    b.remove(null)
}