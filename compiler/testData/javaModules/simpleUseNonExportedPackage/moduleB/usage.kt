package test

import a.*
import a.impl.*

val a1: A = A()
val a2: A = A.getInstance()
val a3: AImpl = A.getInstance()
val a4: String = AImpl.method()
val a5: String = AImpl.field
