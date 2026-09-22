
public class TestConverter {
    
    // Converts an integer to a 32-bit word representation

    public static void fromInt(int value, Word32 result) {

        Bit t = new Bit(true); // true bit constant
        
        Bit f = new Bit(false); // false bit constant
        
        // Special case for Integer.MIN_VALUE (-2147483648)

        if (value == Integer.MIN_VALUE) {

            // Manually set bits for -2147483648 (1000...0000 in binary)

            result.setBitN(0, t); 
            for (int i = 1; i < 32; i++) {

                result.setBitN(i, f); // all other bits are 0
            }
            return;
        }
        
        if (value >= 0) {
            // handle positive numbers
            int temp = value; // working copy

            for (int i = 31; i >= 0; i--) {
                // Set each bit based on remainder

                result.setBitN(i, (temp % 2 == 0) ? f : t);
                temp /= 2; // divide by 2 for next bit
            }
        } else {
            // Handle negative numbers using two's complement

            int positiveValue = value * -1; // make positive
            fromInt(positiveValue, result); // get positive representation
            
            // Invert all bits 

            Word32 notResult = new Word32();
            result.not(notResult);
            
            // Add 1 to complete two's complement
            Word32 one = new Word32();

            fromInt(1, one); // create the value 1
            Adder.add(notResult, one, result); 
        }
    }
    
    // Converts a 32-bit word to an integer

    public static int toInt(Word32 value) {

        Bit isNeg = new Bit(false); // to check if number is negative
        value.getBitN(0, isNeg); // get MSB 
        
        // Special case check for Integer.MIN_VALUE 

        if (isNeg.getValue()) {
            boolean allOthersZero = true;

            Bit tempBit = new Bit(false);
            for (int i = 1; i < 32; i++) {
                value.getBitN(i, tempBit);
                if (tempBit.getValue()) {
                    allOthersZero = false;
                    break;
                }
            }
            if (allOthersZero) {
                return Integer.MIN_VALUE; // return -2147483648
            }
        }
        
        Word32 positive = new Word32(); // will hold positive version
        
        if (isNeg.getValue()) {
            // For negative numbers: convert from two's complement

            Word32 notValue = new Word32();
            value.not(notValue); // notValue = ~value
            Word32 one = new Word32();
            fromInt(1, one); // create the value 1
            Adder.add(notValue, one, positive); 

        } else {

            // For positive numbers: just copy
            value.copy(positive);
        }
        
        int bitValue = 1; // current bit value (1, 2, 4, 8, ...)

        int retVal = 0; // accumulator for result

        Bit cur = new Bit(false); // temporary for current bit
        
        // Convert binary to integer 
        for (int i = 31; i > 0; i--) {
            positive.getBitN(i, cur); // get bit i
            if (cur.getValue()) {

                retVal += bitValue; // add bit value if bit is 1
            }
            bitValue *= 2; // next bit value
        }
        
        // Apply sign and return
        return isNeg.getValue() ? -retVal : retVal;
    }
}