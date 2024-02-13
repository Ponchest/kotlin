# JavaScript Plain Objects compiler plugin

This folder contains the runtime and the compiler plugin of the JavaScript Plain Objects plugin.
The Gradle and Maven plugins are located in the `libraries/tools` directory.

> :warning: **The `js-plain-objects` compiler plugin only works with the K2 compiler.**

## Plugin Overview

The plugin helps to create type-safe plain JavaScript objects. To introduce a type of object, the user should describe it with an `external interface` annotated with `JsPlainObject`.
Example:
```kotlin
@JsPlainObject
external interface User {
    val name: String
    val age: Int
    val email: String?
}
```

The plugin will add a few extra declarations to create and copy the object with such a structure easily:
```kotlin
@JsPlainObject
external interface User {
    val name: String
    val age: Int
    val email: String?
}

// Created by the plugin declarations
inline operator fun User.Companion.invoke(name: String, age: Int, email: String? = NOTHING): User =
    js("({ name: name, age: age, email: email })")

inline fun User.copy(name: String = NOTHING, age: Int = NOTHING, email: String? = NOTHING): User =
    js("Object.assign({}, this, { name: name, age: age, email: email })")
```

So, to create an object with the defined structure, the user should call `User` as a constructor:
```kotlin
fun main() {
    val user = User(name = "Name", age = 10)
    val copy = user.copy(age = 11, email = "some@user.com")

    println(JSON.stringify(user)) 
    // { "name": "Name", "age": 10 }
    println(JSON.stringify(copy)) 
    // { "name": "Name", "age": 11, "email": "some@user.com" }
}
```

The Kotlin code above will be compiled in the next JavaScript code:
```javascript
function main() {
    var user = { name: "Name", age: 10 };
    var copy = Object.assign({}, user, { age: 11, email: "some@user.com" });

    println(JSON.stringify(user));
    // { "name": "Name", "age": 10 }
    println(JSON.stringify(copy));
    // { "name": "Name", "age": 11, "email": "some@user.com" }
}
```

Any JavaScript objects created with this approach are safer because you will have a compile-time error if you use the wrong property name or its value type.

## Project structure overview

The plugin consists of the following parts:

1. `backend` — responsible for IR code generation.
2. `k2` — code resolution and diagnostics for the new K2 Kotlin compiler.
3. `cli` — extension points that allow the plugin to be loaded with `-Xplugin` Kotlin CLI compiler argument.
4. `common` — common declarations for other parts.

Tests and test data are common for all parts and located directly in this module (see `testData` and `tests-gen` folders).

## Building and contributing

### Prerequisites

Before all, it is recommended to read root `README.md` and ensure you have all the necessary things installed (you don't need JDK6 to work with this plugin).

### Installing locally

Run `./gradlew dist install` to get a fresh Kotlin compiler and js-plain-objects plugin in your Maven local with `2.x.255-SNAPSHOT` versions.

### Working with tests

Like most Kotlin project modules, tests are generated based on test data.
Tests are located in the `test-gen` folder and can be run using the green arrow on the IDE gutter or with standard
`./gradlew :plugins:js-plain-objects:compiler-plugin:test` task.
To add a new test, add an appropriate file to the `testData` folder and then re-generate tests with `./gradlew :plugins:js-plain-objects:compiler-plugin:generateTests`.

### Contributing

Follow the common [Kotlin's contribution guidelines](../../docs/contributing.md).
In general, create an issue in [Kotlin's YouTrack](https://youtrack.jetbrains.com/issues/KT). 
