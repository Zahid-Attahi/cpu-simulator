# CPU Simulator

A software simulation of a CPU with a 16-bit instruction set, written in Java and built from the bit level up. Includes a custom instruction set, an assembler that turns assembly text into machine code, a processor that fetches/decodes/executes instructions, and a two-level cache hierarchy (instruction cache + L2) with realistic cycle-cost modeling.

## Features

- **Bit-level foundation** — no native integer arithmetic; addition, subtraction, multiplication, and shifting are all implemented with real digital-logic algorithms (ripple-carry addition, two's complement, shift-and-add multiplication)
- **Custom 16-bit instruction set** — 21 opcodes covering arithmetic, logic, memory, control flow, and register-copy, across two encoding formats
- **Assembler** — parses human-readable assembly into 16-bit machine instructions and packs them into memory
- **Full fetch → decode → execute → store pipeline** with a 32-register file and a call stack for `call`/`return`
- **Two-level cache hierarchy** — 8-word instruction cache and a 4-line, 8-word L2 cache, write-through on stores
- **Cycle-accurate performance tracking** — every stage adds real cycle costs, so total run time reflects cache hits/misses
- Unit tests covering the ALU, assembler, and memory (JUnit 5)

## How to Compile

The test files (`ALUTest.java`, `AssemblerTest.java`, `MemoryTest.java`) need JUnit 5 on the classpath, so compile the main source separately from the tests.

**Main source only** (verified — compiles cleanly):
```bash
javac $(ls *.java | grep -v Test) -d out
```

**Everything, including tests** (needs `junit-platform-console-standalone.jar` on the classpath — download it from [the JUnit releases page](https://github.com/junit-team/junit5/releases) first):
```bash
javac -cp .:junit-platform-console-standalone.jar *.java -d out
```

## How to Run

**Run the tests:**
```bash
java -jar junit-platform-console-standalone.jar -cp out --scan-classpath
```
(Requires the [JUnit Console Launcher](https://junit.org/junit5/docs/current/user-guide/#running-tests-console-launcher), or just run `ALUTest`, `AssemblerTest`, and `MemoryTest` from your IDE — that's the simpler option if you're not working from the command line.)

**Run a program:** write a small driver that assembles a program and loads it into a `Processor`. This is the actual sum-1-to-100 program (`Sum1To100.asm`), verified to run correctly:

```java
Assembler assembler = new Assembler();
String[] asm = assembler.assemble(new String[] {
    "add 1 r2",           // r2 = counter, starts at 1
    "add 12 r3",          // build the loop limit (100) in r3...
    "leftshift 3 r3",     // ...12 << 3 = 96...
    "add 4 r3",           // ...+ 4 = 100
    "add r2 r1",          // r1 += r2 (accumulator)
    "add 1 r2",           // r2 += 1
    "compare r2 r3",
    "ble 2",              // loop back while r2 <= r3
    "syscall",            // print r1
    "halt"
});
String[] program = assembler.finalOutput(asm);

Processor cpu = new Processor();
cpu.load(program);
cpu.run(); // prints "Clock Cycles: 2998"; cpu.output contains ["5050"]
```

Note: branch instructions (`ble`, `blt`, etc.) jump to an **absolute 32-bit word address**, not a relative offset — each word holds two packed 16-bit instructions, so word address 2 is instruction index 4.

## Instruction Set

21 opcodes, 5 bits each, in two encoding formats.

**2R / Immediate format** (arithmetic, logic, memory, copy):
```
[ opcode: 5 ][ mode: 1 ][ field A: 5 ][ field B: 5 ]
```
`mode = 0` → 2R (field A = source register, field B = destination register). `mode = 1` → immediate (one field is a signed 5-bit value, the other a register).

| Opcode | Name | Operation |
|--------|------|-----------|
| 1 | `add` | dest = src + dest (or + immediate) |
| 2 | `and` | bitwise AND |
| 3 | `multiply` | shift-and-add multiply |
| 4 | `leftshift` | logical left shift |
| 5 | `subtract` | two's-complement subtraction |
| 6 | `or` | bitwise OR |
| 7 | `rightshift` | logical right shift |
| 11 | `compare` | sets `less`/`equal` flags for branches |
| 18 | `load` | load from memory (address = op1 + op2) |
| 19 | `store` | store to memory |
| 20 | `copy` | register-to-register or immediate-to-register move |

**Call/Return format** (control flow):
```
[ opcode: 5 ][ signed immediate: 11 ]
```

| Opcode | Name | Operation |
|--------|------|-----------|
| 0 | `halt` | stop execution |
| 8 | `syscall` | print register `r1` |
| 9 | `call` | push return address, jump |
| 10 | `return` | pop return address |
| 12–17 | `ble` `blt` `bge` `bgt` `beq` `bne` | conditional branch on `compare` flags |

32 general-purpose registers (`r0`–`r31`), 5 bits each.

## Components

| File | Responsibility |
|------|-----------------|
| `Bit.java` | Single-bit primitive with AND/OR/XOR/NOT |
| `Word16.java` / `Word32.java` | Fixed-width bit arrays — instructions are 16-bit, data/addresses are 32-bit |
| `Adder.java` | Ripple-carry addition; subtraction via two's complement |
| `Multiplier.java` | Shift-and-add multiplication |
| `Shifter.java` | Logical left/right shift |
| `ALU.java` | Decodes the opcode and routes to the right operation; computes `compare` flags |
| `Assembler.java` | Parses assembly text into machine code, packs pairs of 16-bit instructions into 32-bit memory words |
| `Memory.java` | 1000-word main memory |
| `InstructionCache.java` | 8-word instruction cache in front of the fetch stage |
| `L2Cache.java` | 4-line, 8-word-per-line cache shared by instruction misses and data load/store |
| `Processor.java` | Fetch/decode/execute/store loop, register file, call stack, cycle counting |
| `TestConverter.java` | Converts between Java `int` and `Word32` (test/debug only) |

## Performance Model

Each stage adds real cycles to `currentClockCycle`:

- Instruction cache hit: 10 cycles; miss adds the L2 cost on top
- L2 hit: 20 cycles; miss (goes to main memory): 360 cycles
- ALU ops (add/and/shift/subtract/or/compare): 2 cycles; multiply: 10 cycles
- Load/store: 2 cycles + the L2 access cost

**Measured results** — three programs doing the same total work (sum 1–100), different memory-access patterns:

| Program | Cycles | Why |
|---------|--------|-----|
| Loop-accumulated sum (no data memory) | 2,998 | Small instruction footprint — instruction cache does almost all the work |
| Sum of a 100-element array | 27,948 | Requires data memory, but sequential layout gives strong spatial locality |
| Sum of a 100-node linked list | 59,792 | Same data volume as the array, but pointer-chasing defeats cache locality |

## Built With

- Java
- JUnit 5

## Author

Solo project — covers digital-logic-level arithmetic, instruction set design, a two-stage cache hierarchy, and cycle-accurate performance modeling from scratch.
