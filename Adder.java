public class Adder {
    
    // Adds two 32-bit numbers using ripple carry additio

    public static void add(Word32 a, Word32 b, Word32 result) {
        Bit carry = new Bit(false); // initial carry is 0
        Bit bitA = new Bit(false); // temporary for a's bit
        Bit bitB = new Bit(false); // temporary for b's bit

        Bit sum = new Bit(false); // temporary for sum bit
        Bit aXorB = new Bit(false); // temporary for a XOR b
        Bit carryOut = new Bit(false); // temporary for carry out
        

        // Process from LSB (bit 31) to MSB (bit 0)
        for (int i = 31; i >= 0; i--) {

            a.getBitN(i, bitA); // get bit i from a
            b.getBitN(i, bitB); // get bit i from b
            
            // Calculate sum = A XOR B XOR carry
            Bit.xor(bitA, bitB, aXorB); // aXorB = A XOR B
            Bit.xor(aXorB, carry, sum); // sum = aXorB XOR carry
            result.setBitN(i, sum); // store sum bit in result
            
            // Calculate carry out = (A AND B) OR (carry AND (A XOR B))
            Bit.and(bitA, bitB, carryOut); // carryOut = A AND B
            Bit.and(carry, aXorB, aXorB); // aXorB = carry AND (A XOR B)
            Bit.or(carryOut, aXorB, carryOut); // carryOut = (A AND B) OR (carry AND (A XOR B))
            carry.assign(carryOut.getValue() ? Bit.boolValues.TRUE : Bit.boolValues.FALSE); // update carry
        }
    }
    
    // Subtracts b from a using two's complement: a - b = a + (~b + 1) 
    public static void subtract(Word32 a, Word32 b, Word32 result) {
        Word32 notB = new Word32(); // temporary for ~b
        Word32 one = new Word32(); // temporary for 1
        Word32 twosComplement = new Word32(); // temporary for ~b + 1
        
        // Create the value 1 (bit 31 is LSB)

        Bit trueBit = new Bit(true);
        Bit falseBit = new Bit(false);
        for (int i = 0; i < 31; i++) {
            
            one.setBitN(i, falseBit); // set all bits except LSB to 0
        }
        one.setBitN(31, trueBit); // set LSB (bit 31) to 1
        
        // Calculate ~b
        b.not(notB);
        
        // Calculate ~b + 1
        add(notB, one, twosComplement);
        
        // Calculate a + (~b + 1)
        add(a, twosComplement, result);
    }
}