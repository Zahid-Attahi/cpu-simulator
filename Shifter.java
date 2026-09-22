
public class Shifter {
    
    // Left shifts source by amount bits, stores in result 
    // Left shift moves bits toward MSB (index decreases)
    

    public static void LeftShift(Word32 source, int amount, Word32 result) {

        amount = amount & 31; // only consider lowest 5 bits
        Bit tempBit = new Bit(false); // temporary for bit operations

        Bit falseBit = new Bit(false); // constant false bit
        
        for (int i = 0; i < 32; i++) {
            if (i + amount < 32) { // if source bit exists after shifting left
                source.getBitN(i + amount, tempBit); // get bit from source at higher index

                result.setBitN(i, tempBit); // set in result at lower index
            } else {
                result.setBitN(i, falseBit); // fill with 0 for higher bits
            }
        }
    }
    
    // Right shifts source by amount bits, stores in result 
    // Right shift moves bits toward LSB (index increases)

    public static void RightShift(Word32 source, int amount, Word32 result) {

        amount = amount & 31; // only consider lowest 5 bits
        Bit tempBit = new Bit(false); // temporary for bit operations
        Bit falseBit = new Bit(false); // constant false bit
        
        for (int i = 0; i < 32; i++) {
            if (i - amount >= 0) { // if source bit exists after shifting right
                source.getBitN(i - amount, tempBit); // get bit from source at lower index

                result.setBitN(i, tempBit); // set in result at higher index
            } else {
                result.setBitN(i, falseBit); // fill with 0 for lower bits
            }
        }
    }
}