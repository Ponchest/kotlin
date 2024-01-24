// TARGET_BACKEND: JVM
// FULL_JDK

// FILE: Java1.java

public class Java1<T extends Number> {
    public void foo(T t) { }
    public T bar() {
        return null;
    }
}

// FILE: Java2.java
public interface Java2<T extends Number&Comparable>  {
    public void foo(T t);
    public T bar();
}

// FILE: 1.kt

class C: Java1<Int>(), Java2<Int>   //Kotlin ← Java1, Java2

class D<T>: Java1<T>(), Java2<T> where T: Number, T: Comparable<T>