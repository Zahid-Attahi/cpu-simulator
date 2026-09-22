
public class Multiplier {
    
    // Multiplies two 32-bit numbers using shift-and-add algorithm 

    public static void multiply(Word32 a, Word32 b, Word32 result) {

        Word32 tempResult = new Word32(); // temporary result
        Word32 shiftedA = new Word32(); // for shifted version of a
        Word32 sum = new Word32(); // for addition results

        Bit bitB = new Bit(false); // for bits of b
        Bit falseBit = new Bit(false); // constant false bit
        
        // Initialize result to 0
        for (int i = 0; i < 32; i++) {
            tempResult.setBitN(i, falseBit);
        }
        
        // For each bit of b (from LSB bit 31 to MSB bit 0)
        for (int i = 0; i < 32; i++) {

            b.getBitN(31 - i, bitB); // get bit i from b (start at LSB)
            
            if (bitB.getValue()) { // if the bit is 1


                // Use Shifter to left shift a by i bits
                Shifter.LeftShift(a, i, shiftedA);
                
                // Add shiftedA to tempResult
                Adder.add(tempResult, shiftedA, sum);
                
                // Copy sum to tempResult

                for (int j = 0; j < 32; j++) {
                    Bit tempBit = new Bit(false);

                    sum.getBitN(j, tempBit);
                    tempResult.setBitN(j, tempBit);
                }
            }
        }
        
        // Copy final result
        for (int i = 0; i < 32; i++) {

            Bit tempBit = new Bit(false);
            tempResult.getBitN(i, tempBit);
            
            result.setBitN(i, tempBit);
        }
    }
}