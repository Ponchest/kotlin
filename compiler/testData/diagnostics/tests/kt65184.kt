// FULL_JDK

// FILE: A.java

public class A<T> {

}

// FILE: B.java

public class B<T> extends A<T> {

}

// FILE: box.kt

import java.util.LinkedList
import java.util.Queue

fun bar(b: A<String>) {}

fun test() {
    bar(<!TYPE_MISMATCH!>B<String?>()<!>)
}
