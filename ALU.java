//Zahidullah

public class ALU {


    public Word16 instruction = new Word16();
    public Word32 op1 = new Word32();
    public Word32 op2 = new Word32();
    public Word32 result = new Word32();
    public Bit less = new Bit(false);
    public Bit equal = new Bit(false);

    // Reads the 5-bit opcode from bits 0-4 of the instruction Word16.
    // Bit 0 is MSB of the opcode, bit 4 is LSB.

    private int getOpcode() {

        int opcode = 0;
        int bitValue = 1;
        Bit cur = new Bit(false);
        for (int i = 4; i >= 0; i--) {
            instruction.getBitN(i, cur);
            if (cur.getValue()) {
                opcode += bitValue;
            }
            bitValue *= 2;
        }
        return opcode;
    }

    public void doInstruction() {

        int opcode = getOpcode();

        switch (opcode) {
            case 1:  // Add
            case 9:  // Call  (needs addition for PC + immediate)
            case 12: // BLE   (needs addition for branch target)
            case 13: // BLT
            case 14: // BGE
            case 15: // BGT
            case 16: // BEQ
            case 17: // BNE
                Adder.add(op1, op2, result);
                break;

            case 2: // AND
                op1.and(op2, result);
                break;

            case 3: // Multiply
                Multiplier.multiply(op1, op2, result);
                break;

            case 4: // LeftShift - shift op1 left by op2 bits
            {
                int shiftAmount = TestConverter.toInt(op2);
                Shifter.LeftShift(op1, shiftAmount, result);
                break;
            }

            case 5: // Subtract - op1 - op2
                Adder.subtract(op1, op2, result);
                break;

            case 6: // OR
                op1.or(op2, result);
                break;

            case 7: // RightShift - shift op1 right by op2 bits
            {
                int shiftAmount = TestConverter.toInt(op2);
                Shifter.RightShift(op1, shiftAmount, result);
                break;
            }

            case 11: // Compare - compare op1 to op2, set less and equal flags
            {
                
                // Compute op1 - op2 to determine relationship
                Word32 diff = new Word32();
                Adder.subtract(op1, op2, diff);

                // Check if diff is zero (equal)

                boolean isZero = true;
                Bit tempBit = new Bit(false);

                for (int i = 0; i < 32; i++) {

                    diff.getBitN(i, tempBit);
                    if (tempBit.getValue()) {
                        isZero = false;
                        break;
                    }
                }

                // Handle overflow by checking original sign bits
                // If signs differ, we can determine ordering without looking at diff sign

                Bit sign1 = new Bit(false);
                Bit sign2 = new Bit(false);
                op1.getBitN(0, sign1); // MSB of op1
                op2.getBitN(0, sign2); // MSB of op2

                boolean lessVal;
                if (sign1.getValue() && !sign2.getValue()) {
                    // op1 is negative, op2 is non-negative: op1 < op2
                    lessVal = true;
                } else if (!sign1.getValue() && sign2.getValue()) {
                    // op1 is non-negative, op2 is negative: op1 > op2
                    lessVal = false;
                } else {
                    // Same sign: subtraction is safe
                    diff.getBitN(0, tempBit);
                    boolean diffNegative = tempBit.getValue();
                    lessVal = diffNegative && !isZero;
                }

                equal.assign(isZero ? Bit.boolValues.TRUE : Bit.boolValues.FALSE);
                less.assign(lessVal ? Bit.boolValues.TRUE : Bit.boolValues.FALSE);
                break;
            }

            default:
                
                break;
        }
    }
}