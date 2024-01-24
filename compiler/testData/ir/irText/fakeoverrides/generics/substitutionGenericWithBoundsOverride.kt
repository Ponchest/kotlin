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

class A : Java1<Int>()   //with upper-bound

interface B : Java2<Int>    //with multiple bound

