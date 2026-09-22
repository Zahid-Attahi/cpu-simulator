import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Processor {

    private Word32 pc = new Word32();
    private Word32[] registers = new Word32[32];
    private ALU alu = new ALU();
    private Memory memory = new Memory();
    private InstructionCache instructionCache = new InstructionCache();
    private L2Cache l2Cache = new L2Cache();

    public List<String> output = new ArrayList<>();
    public int currentClockCycle = 0;

    private Stack<Word32> callStack = new Stack<>();
    private Stack<Boolean> callHalfStack = new Stack<>();

    private Word16 currentInstruction = new Word16();
    private Word32 fetchedWord = new Word32();

    private boolean onSecondInstruction = false;

    private int destRegIndex = 0;
    private int srcRegIndex = 0;
    private boolean immediateMode = false;

    private Word32 immediateValue = new Word32();

    private boolean savedLess = false;
    private boolean savedEqual = false;

    public Processor() {
        for (int i = 0; i < 32; i++) {
            registers[i] = new Word32();
        }
        instructionCache.connect(memory, l2Cache);
        l2Cache.connect(memory);
    }

    public void load(String[] data) {
        memory.load(data);
        instructionCache.clear();
        l2Cache.clear();
        output.clear();
        callStack.clear();
        callHalfStack.clear();
        currentClockCycle = 0;
        onSecondInstruction = false;
        currentInstruction = new Word16();
        fetchedWord = new Word32();
        pc = new Word32();
        immediateValue = new Word32();
        immediateMode = false;
        savedLess = false;
        savedEqual = false;

        for (int i = 0; i < 32; i++) {
            registers[i] = new Word32();
        }
    }

    private int getOpcode() {
        int opcode = 0;
        int bitValue = 1;
        Bit cur = new Bit(false);

        for (int i = 4; i >= 0; i--) {
            currentInstruction.getBitN(i, cur);
            if (cur.getValue()) {
                opcode += bitValue;
            }
            bitValue *= 2;
        }
        return opcode;
    }

    private int getRegIndex(int start) {
        int index = 0;
        int bitValue = 1;
        Bit cur = new Bit(false);

        for (int i = start + 4; i >= start; i--) {
            currentInstruction.getBitN(i, cur);
            if (cur.getValue()) {
                index += bitValue;
            }
            bitValue *= 2;
        }
        return index;
    }

    private void getImmediate11(Word32 result) {
        Bit cur = new Bit(false);
        Bit falseBit = new Bit(false);
        Bit trueBit = new Bit(true);

        currentInstruction.getBitN(5, cur);
        boolean isNeg = cur.getValue();

        for (int i = 0; i < 21; i++) {
            result.setBitN(i, isNeg ? trueBit : falseBit);
        }

        for (int i = 0; i < 11; i++) {
            currentInstruction.getBitN(5 + i, cur);
            result.setBitN(21 + i, cur);
        }
    }

    private void getImmediate5(int start, Word32 result) {
        Bit cur = new Bit(false);
        Bit falseBit = new Bit(false);
        Bit trueBit = new Bit(true);

        currentInstruction.getBitN(start, cur);
        boolean isNeg = cur.getValue();

        for (int i = 0; i < 27; i++) {
            result.setBitN(i, isNeg ? trueBit : falseBit);
        }

        for (int i = 0; i < 5; i++) {
            currentInstruction.getBitN(start + i, cur);
            result.setBitN(27 + i, cur);
        }
    }

    private void addAluCycles(int opcode) {
        if (opcode == 3) {
            currentClockCycle += 10;
        } else if (opcode == 1 || opcode == 2 || opcode == 4 || opcode == 5
                || opcode == 6 || opcode == 7 || opcode == 11) {
            currentClockCycle += 2;
        }
    }

    private void fetch() {
        if (!onSecondInstruction) {
            pc.copy(instructionCache.address);
            instructionCache.read();
            currentClockCycle += instructionCache.getLastAccessCost();

            instructionCache.value.copy(fetchedWord);
            fetchedWord.getTopHalf(currentInstruction);
            onSecondInstruction = true;
        } else {
            fetchedWord.getBottomHalf(currentInstruction);
            onSecondInstruction = false;

            Word32 one = new Word32();
            Word32 newPC = new Word32();
            TestConverter.fromInt(1, one);
            Adder.add(pc, one, newPC);
            newPC.copy(pc);
        }
    }

    private void decode() {
        int opcode = getOpcode();

        Bit formatBit = new Bit(false);
        currentInstruction.getBitN(5, formatBit);

        boolean isCallReturn = opcode == 0 || opcode == 8 || opcode == 9
                || opcode == 10 || (opcode >= 12 && opcode <= 17);

        if (isCallReturn) {
            immediateMode = true;
            getImmediate11(immediateValue);
        } else {
            immediateMode = formatBit.getValue();

            if (!immediateMode) {
                srcRegIndex = getRegIndex(6);
                destRegIndex = getRegIndex(11);

                registers[srcRegIndex].copy(alu.op1);
                registers[destRegIndex].copy(alu.op2);
            } else {
                destRegIndex = getRegIndex(11);
                getImmediate5(6, immediateValue);
                srcRegIndex = destRegIndex;

                if (opcode == 20) {
                    immediateValue.copy(alu.op1);
                    immediateValue.copy(alu.op2);
                } else {
                    registers[destRegIndex].copy(alu.op1);
                    immediateValue.copy(alu.op2);
                }
            }
        }

        Bit tempBit = new Bit(false);
        for (int i = 0; i < 5; i++) {
            currentInstruction.getBitN(i, tempBit);
            alu.instruction.setBitN(i, tempBit);
        }
    }

    private void execute() {
        int opcode = getOpcode();

        if (opcode == 0) {
            return;
        }

        if (opcode == 8) {
            output.add(Integer.toString(TestConverter.toInt(registers[1])));
            return;
        }

        if (opcode == 10 || opcode == 9) {
            return;
        }

        if (opcode == 11) {
            addAluCycles(opcode);
            alu.doInstruction();
            savedLess = alu.less.getValue();
            savedEqual = alu.equal.getValue();
            return;
        }

        if (opcode >= 12 && opcode <= 17) {
            return;
        }

        addAluCycles(opcode);
        alu.doInstruction();
    }

    private void store() {
        int opcode = getOpcode();

        if (opcode == 0 || opcode == 8 || opcode == 11) {
            return;
        }

        if (opcode == 9) {
            Word32 returnAddr = new Word32();
            pc.copy(returnAddr);
            callStack.push(returnAddr);
            callHalfStack.push(onSecondInstruction);

            immediateValue.copy(pc);
            onSecondInstruction = false;
            return;
        }

        if (opcode == 10) {
            if (!callStack.isEmpty()) {
                callStack.pop().copy(pc);
                onSecondInstruction = callHalfStack.pop();
            }
            return;
        }

        if (opcode >= 12 && opcode <= 17) {
            boolean taken = false;

            switch (opcode) {
                case 12:
                    taken = savedLess || savedEqual;
                    break;
                case 13:
                    taken = savedLess;
                    break;
                case 14:
                    taken = !savedLess || savedEqual;
                    break;
                case 15:
                    taken = !savedLess && !savedEqual;
                    break;
                case 16:
                    taken = savedEqual;
                    break;
                case 17:
                    taken = !savedEqual;
                    break;
                default:
                    break;
            }

            if (taken) {
                immediateValue.copy(pc);
                onSecondInstruction = false;
            }
            return;
        }

        if (opcode == 18) {
            Word32 addr = new Word32();
            currentClockCycle += 2;
            Adder.add(alu.op1, alu.op2, addr);
            addr.copy(l2Cache.address);
            l2Cache.read();
            currentClockCycle += l2Cache.getLastAccessCost();
            l2Cache.value.copy(registers[destRegIndex]);
            return;
        }

        if (opcode == 19) {
            Word32 addr = new Word32();
            currentClockCycle += 2;
            Adder.add(alu.op1, alu.op2, addr);
            addr.copy(l2Cache.address);
            registers[srcRegIndex].copy(l2Cache.value);
            l2Cache.write();
            currentClockCycle += l2Cache.getLastAccessCost();
            return;
        }

        if (opcode == 20) {
            if (immediateMode) {
                immediateValue.copy(registers[destRegIndex]);
            } else {
                registers[srcRegIndex].copy(registers[destRegIndex]);
            }
            return;
        }

        alu.result.copy(registers[destRegIndex]);
    }

    public void run() {
        while (true) {
            fetch();
            decode();

            if (getOpcode() == 0) {
                break;
            }

            execute();
            store();
        }

        System.out.println("Clock Cycles: " + currentClockCycle);
    }
}
