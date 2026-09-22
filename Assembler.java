//Zahidullah 

import java.util.HashMap;

public class Assembler {

    // map opcode names to their 5-bit binary strings
    private static HashMap<String, String> opcodeMap = new HashMap<>();

    // map register names to their 5-bit binary strings
    private static HashMap<String, String> registerMap = new HashMap<>();

    static {
        opcodeMap.put("halt",       "00000"); // opcode 0  - Call/Return format
        opcodeMap.put("add",        "00001"); // opcode 1  - 2R or immediate
        opcodeMap.put("and",        "00010"); // opcode 2  - 2R or immediate
        opcodeMap.put("multiply",   "00011"); // opcode 3  - 2R or immediate
        opcodeMap.put("leftshift",  "00100"); // opcode 4  - 2R or immediate
        opcodeMap.put("subtract",   "00101"); // opcode 5  - 2R or immediate
        opcodeMap.put("or",         "00110"); // opcode 6  - 2R or immediate
        opcodeMap.put("rightshift", "00111"); // opcode 7  - 2R or immediate
        opcodeMap.put("syscall",    "01000"); // opcode 8  - Call/Return format
        opcodeMap.put("call",       "01001"); // opcode 9  - Call/Return format
        opcodeMap.put("return",     "01010"); // opcode 10 - Call/Return format
        opcodeMap.put("compare",    "01011"); // opcode 11 - 2R or immediate
        opcodeMap.put("ble",        "01100"); // opcode 12 - Call/Return format
        opcodeMap.put("blt",        "01101"); // opcode 13 - Call/Return format
        opcodeMap.put("bge",        "01110"); // opcode 14 - Call/Return format
        opcodeMap.put("bgt",        "01111"); // opcode 15 - Call/Return format
        opcodeMap.put("beq",        "10000"); // opcode 16 - Call/Return format
        opcodeMap.put("bne",        "10001"); // opcode 17 - Call/Return format
        opcodeMap.put("load",       "10010"); // opcode 18 - 2R or immediate
        opcodeMap.put("store",      "10011"); // opcode 19 - 2R or immediate
        opcodeMap.put("copy",       "10100"); // opcode 20 - 2R or immediate

        // Registers r0 through r31
        for (int i = 0; i < 32; i++) {
            String name = "r" + i;
            registerMap.put(name, toBinary5(i));
        }
    }

    // converts an integer to a 5-bit binary string
    private static String toBinary5(int value) {
        StringBuilder sb = new StringBuilder();
        for (int i = 4; i >= 0; i--) {
            sb.append((value >> i) & 1);
        }
        return sb.toString();
    }

    // converts a signed integer to an 11-bit two's complement binary string
    private static String toSignedBinary11(int value) {
        value = value & 0x7FF; // keep only lower 11 bits
        StringBuilder sb = new StringBuilder();
        for (int i = 10; i >= 0; i--) {
            sb.append((value >> i) & 1);
        }
        return sb.toString();
    }

    // converts a signed integer to a 5-bit two's complement binary string
    private static String toSignedBinary5(int value) {
        value = value & 0x1F; // keep only lower 5 bits
        return toBinary5(value);
    }

    // checks if a token is a register
    private static boolean isRegister(String token) {
        return registerMap.containsKey(token.toLowerCase());
    }

    // checks if an opcode uses Call/Return format
    private static boolean isCallReturn(String opName) {
        switch (opName) {
            case "halt":
            case "syscall":
            case "call":
            case "return":
            case "ble":
            case "blt":
            case "bge":
            case "bgt":
            case "beq":
            case "bne":
                return true;
            default:
                return false;
        }
    }

    // assembles a single line of assembly into a 16-bit binary string
    // returns null if the line is empty
    private static String assembleLine(String line) {
        line = line.trim();

        if (line.isEmpty()) {
            return null;
        }

        String[] tokens = line.split("\\s+");
        String opName = tokens[0].toLowerCase();

        String opcode = opcodeMap.get(opName);
        if (opcode == null) {
            throw new RuntimeException("Unknown opcode: " + opName);
        }

        // Call/Return format: 5-bit opcode + 11-bit signed immediate
        if (isCallReturn(opName)) {
            // halt and return have no parameter - immediate is 0
            if (tokens.length == 1) {
                return opcode + "00000000000";
            }
            // syscall, call, ble, blt, bge, bgt, beq, bne have one immediate parameter
            int immediate = Integer.parseInt(tokens[1]);
            String immBits = toSignedBinary11(immediate);
            return opcode + immBits;
        }

        // 2R or immediate format
        if (tokens.length >= 3) {
            String secondToken = tokens[1].toLowerCase();
            String thirdToken = tokens[2].toLowerCase();

            if (isRegister(secondToken) && isRegister(thirdToken)) {
                // 2R format: opcode + 0 + source + destination
                String src = registerMap.get(secondToken);
                String dest = registerMap.get(thirdToken);
                return opcode + "0" + src + dest;

            } else if (!isRegister(secondToken) && isRegister(thirdToken)) {
                // immediate format: opcode + 1 + immediate + destination
                int immediate = Integer.parseInt(secondToken);
                String immBits = toSignedBinary5(immediate);
                String dest = registerMap.get(thirdToken);
                return opcode + "1" + immBits + dest;

            } else if (isRegister(secondToken) && !isRegister(thirdToken)) {
                // immediate format: opcode + 1 + source + immediate
                int immediate = Integer.parseInt(thirdToken);
                String immBits = toSignedBinary5(immediate);
                String src = registerMap.get(secondToken);
                return opcode + "1" + src + immBits;
            }
        } else if (tokens.length == 2) {
            String param = tokens[1].toLowerCase();
            if (isRegister(param)) {
                String reg = registerMap.get(param);
                return opcode + "0" + "00000" + reg;
            } else {
                int immediate = Integer.parseInt(param);
                String immBits = toSignedBinary5(immediate);
                return opcode + "1" + immBits + "00000";
            }
        }

        return opcode + "0" + "00000" + "00000";
    }

    // assembles an array of assembly lines into 16-bit binary strings
    public String[] assemble(String[] assemblyLines) {
        String[] temp = new String[assemblyLines.length];
        int count = 0;

        for (int i = 0; i < assemblyLines.length; i++) {
            String result = assembleLine(assemblyLines[i]);
            if (result != null) {
                temp[count++] = result;
            }
        }

        String[] assembled = new String[count];
        for (int i = 0; i < count; i++) {
            assembled[i] = temp[i];
        }

        return assembled;
    }

    // merges pairs of 16-bit lines into 32-bit lines.
    // if odd number of lines, appends a halt (all zeros) at the end.
    public String[] finalOutput(String[] assembled) {
        String halt = "0000000000000000"; // 16-bit halt

        int inputLength = assembled.length;
        int outputLength = (inputLength + 1) / 2;

        String[] output = new String[outputLength];

        for (int i = 0; i < outputLength; i++) {
            String first = assembled[i * 2];
            String second;
            if (i * 2 + 1 < inputLength) {
                second = assembled[i * 2 + 1];
            } else {
                second = halt;
            }
            output[i] = first + second;
        }

        return output;
    }
}