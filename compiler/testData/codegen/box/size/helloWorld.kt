// TARGET_BACKEND: WASM

// RUN_THIRD_PARTY_OPTIMIZER
// WASM_DCE_EXPECTED_OUTPUT_SIZE: wasm  36_087
// WASM_DCE_EXPECTED_OUTPUT_SIZE: mjs    4_552
// WASM_OPT_EXPECTED_OUTPUT_SIZE:        8_665

fun box(): String {
    println("Hello, World!")
    return "OK"
}
