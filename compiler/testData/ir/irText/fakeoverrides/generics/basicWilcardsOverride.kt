// TARGET_BACKEND: JVM
// FULL_JDK

// FILE: Java1.java
import java.util.*;

public class Java1 {
    public void foo(List<? extends Number> a) { }
    public List<? extends Number> bar(){
        return null;
    }
    public void foo2(List<? super Number> a) { }
    public List<? super Number> bar2(){
        return null;
    }
}

// FILE: 1.kt

class A : Java1()

class B: Java1(){
    override fun foo(a: MutableList<out Number>?) { }
    override fun bar(): MutableList<out Number> {
        return null!!
    }

    override fun foo2(a: MutableList<in Number>?) { }
    override fun bar2(): MutableList<in Number> {
        return null!!
    }
}