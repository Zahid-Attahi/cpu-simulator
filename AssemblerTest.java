//Zahidullah

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AssemblerTest {

    @Test
    void assemble() {

        var assembler = new Assembler();
        // test: add r1 r2 -> 2R format, opcode 1 = 00001, format 0, r1=00001, r2=00010

        var result = assembler.assemble(new String[]{"add r1 r2"});
        assertEquals("0000100000100010", result[0]);

        // test: load 15 r7 -> immediate format, opcode 18 = 10010, format 1, imm=01111, r7=00111
        result = assembler.assemble(new String[]{"load 15 r7"});
        assertEquals("1001010111100111", result[0]);

        // test: subtract r3 r5 -> 2R format, opcode 5 = 00101, format 0, r3=00011, r5=00101
        result = assembler.assemble(new String[]{"subtract r3 r5"});
        assertEquals("0010100001100101", result[0]);

        // Test: halt -> opcode 0 = 00000, Call/Return format, 11 zeros
        result = assembler.assemble(new String[]{"halt"});
        assertEquals("0000000000000000", result[0]);
    }

    @Test

    void testInstructions() {
        var assembler = new Assembler();

        // add r1 r2 -> opcode 1 = 00001, format 0, r1=00001, r2=00010
        var result = assembler.assemble(new String[]{"add r1 r2"});
        assertEquals("0000100000100010", result[0]);

        // load 15 r7 -> immediate format, opcode 18 = 10010, format 1, imm=01111, r7=00111
        result = assembler.assemble(new String[]{"load 15 r7"});
        assertEquals("1001010111100111", result[0]);

        // multiply r4 r5 -> opcode 3 = 00011, format 0, r4=00100, r5=00101
        result = assembler.assemble(new String[]{"multiply r4 r5"});
        assertEquals("0001100010000101", result[0]);

        // and r0 r1 -> opcode 2 = 00010, format 0, r0=00000, r1=00001
        result = assembler.assemble(new String[]{"and r0 r1"});
        assertEquals("0001000000000001", result[0]);

        // subtract r2 r3 -> opcode 5 = 00101, format 0, r2=00010, r3=00011
        result = assembler.assemble(new String[]{"subtract r2 r3"});
        assertEquals("0010100001000011", result[0]);

        // or r6 r7 -> opcode 6 = 00110, format 0, r6=00110, r7=00111
        result = assembler.assemble(new String[]{"or r6 r7"});
        assertEquals("0011000011000111", result[0]);

        // compare r1 r2 -> opcode 11 = 01011, format 0, r1=00001, r2=00010
        result = assembler.assemble(new String[]{"compare r1 r2"});
        assertEquals("0101100000100010", result[0]);

        // halt -> opcode 0 = 00000, Call/Return format, 11 zeros
        result = assembler.assemble(new String[]{"halt"});
        assertEquals("0000000000000000", result[0]);
    }

    @Test

    void finalOutput() {
        var assembler = new Assembler();

        // Two lines -> one 32-bit output
        var assembled = assembler.assemble(new String[]{"add r1 r2", "halt"});
        var output = assembler.finalOutput(assembled);

        assertEquals(1, output.length);
        assertEquals("0000100000100010" + "0000000000000000", output[0]);

        // one line (odd) -> should pad with halt (all zeros)
        assembled = assembler.assemble(new String[]{"add r1 r2"});
        output = assembler.finalOutput(assembled);
        assertEquals(1, output.length);
        assertEquals("0000100000100010" + "0000000000000000", output[0]);

        // four lines -> two 32-bit outputs
        assembled = assembler.assemble(new String[]{
            "add r1 r2",
            "subtract r3 r5",
            "halt",
            "halt"
        });

        output = assembler.finalOutput(assembled);
        assertEquals(2, output.length);
    }
}
