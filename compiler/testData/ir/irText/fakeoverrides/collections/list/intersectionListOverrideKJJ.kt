// TARGET_BACKEND: JVM
// FULL_JDK
// There is a difference between K2 and ENABLE_IR_FAKE_OVERRIDE_GENERATION but not in K1/K2+ENABLE_IR_FAKE_OVERRIDE_GENERATION

// FILE: 1.kt
import java.util.*
import java.util.function.UnaryOperator
import kotlin.Comparator

abstract class A : LinkedList<Int>(), java.util.List<Int> {
    override fun spliterator(): Spliterator<Int> {
        return null!!
    }
    override fun sort(c: Comparator<in Int>?) { }
    override fun replaceAll(operator: UnaryOperator<Int>) { }
}

fun test(a: A){
    a.size
    a.add(1)
    a.get(1)
    a.remove()
    a.removeAt(1)
    a.remove(element = 1)
}