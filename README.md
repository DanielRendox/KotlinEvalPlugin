# Kotlin IR Eval Plugin

This Kotlin compiler plugin evaluates functions that start with `eval` at compile time, provided they meet specific conditions. This evaluation aims to precompute values for functions, reducing runtime computation.

## Features

- **Compile-time Evaluation**: The plugin evaluates `eval` functions when possible, replacing them with constants or precomputed values in the compiled bytecode.
- **Supported Statements**:
  - Arithmetic operations on `Int` type

## Example

#### Before transformation 

```kotlin
fun main() {
    println(evalAdd(1, 2))
}

fun evalAdd(a: Int, b: Int) = a + b
```

#### After transformation 

```kotlin
fun main() {
    println(3)
}

fun evalAdd(a: Int, b: Int) = a + b
```

If any unsupported code is detected, the plugin leaves the function as-is, deferring evaluation until runtime.

## How It Works

The plugin scans all functions that start with `eval` and attempts to evaluate their body at compile time. The main functionality is implemented in `EvalFunctionTransformer.kt`, and it is tested in `IrPluginTest.kt`. If the plugin’s configuration includes an output file parameter, the plugin dumps the IR tree after the transformation and saves it in the specified file. This file is then read by test cases that verify if the dump contains specific constants evaluated at compile time.
