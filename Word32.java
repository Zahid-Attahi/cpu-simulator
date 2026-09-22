
public class Word32 {
    private Bit[] bits; // array to store 32 bits
    
    // Default constructor - initializes all bits to false
    public Word32() {
        bits = new Bit[32]; // create array of 32 bits
        for (int i = 0; i < 32; i++) {
            bits[i] = new Bit(false); // initialize each bit to false
        }
    }
    
    // Constructor that takes an array of bits
    public Word32(Bit[] in) {
        bits = new Bit[32]; // create array of 32 bits
        for (int i = 0; i < 32; i++) {
            bits[i] = new Bit(in[i].getValue()); // copy each bit's value
        }
    }
    
    // Sets result to bits 0-15 of this word 
    public void getTopHalf(Word16 result) {
        for (int i = 0; i < 16; i++) {
            result.setBitN(i, bits[i]); // copy first 16 bits 
        }
    }
    
    
    public void getBottomHalf(Word16 result) {
        for (int i = 0; i < 16; i++) {
            result.setBitN(i, bits[i + 16]); // copy last 16 bits
        }
    }
    
    // Copies the values from this instance to result
    public void copy(Word32 result) {
        for (int i = 0; i < 32; i++) {
            result.bits[i].assign(this.bits[i].getValue() ? Bit.boolValues.TRUE : Bit.boolValues.FALSE); // copy each bit
        }
    }
    
    // Instance method: checks if other is equal to this
    public boolean equals(Word32 other) {
        for (int i = 0; i < 32; i++) {
            if (this.bits[i].getValue() != other.bits[i].getValue()) {
                return false; // return false if any bit differs
            }
        }
        return true; // all bits match
    }
    
    // static method: checks if a and b are equal
    public static boolean equals(Word32 a, Word32 b) {
        return a.equals(b); // use instance method
    }
    
    // Sets result to be the same value as the nth bit of this word
    public void getBitN(int n, Bit result) {
        result.assign(bits[n].getValue() ? Bit.boolValues.TRUE : Bit.boolValues.FALSE); // copy bit value to result
    }
    
    // Sets the nth bit of this word to source's value
    public void setBitN(int n, Bit source) {
        bits[n].assign(source.getValue() ? Bit.boolValues.TRUE : Bit.boolValues.FALSE); // assign source's value
    }
    
    // Instance AND: this AND other, store in result
    public void and(Word32 other, Word32 result) {
        for (int i = 0; i < 32; i++) {
            Bit.and(this.bits[i], other.bits[i], result.bits[i]); // AND each bit
        }
    }
    
    // Static AND: a AND b, store in result
    public static void and(Word32 a, Word32 b, Word32 result) {
        a.and(b, result); // use instance method
    }
    
    // Instance OR: this OR other, store in result
    public void or(Word32 other, Word32 result) {
        for (int i = 0; i < 32; i++) {
            Bit.or(this.bits[i], other.bits[i], result.bits[i]); // OR each bit
        }
    }
    
    // Static OR: a OR b, store in result
    public static void or(Word32 a, Word32 b, Word32 result) {
        a.or(b, result); // use instance method
    }
    
    // Instance XOR: this XOR other, store in result
    public void xor(Word32 other, Word32 result) {
        for (int i = 0; i < 32; i++) {
            Bit.xor(this.bits[i], other.bits[i], result.bits[i]); // XOR each bit
        }
    }
    
    // Static XOR: a XOR b, store in result
    public static void xor(Word32 a, Word32 b, Word32 result) {
        a.xor(b, result); // use instance method
    }
    
    // Instance NOT: NOT this, store in result
    public void not(Word32 result) {
        for (int i = 0; i < 32; i++) {
            Bit.not(this.bits[i], result.bits[i]); // NOT each bit
        }
    }
    
    // Static NOT: NOT a, store in result
    public static void not(Word32 a, Word32 result) {
        a.not(result); // use instance method
    }
    
    // Returns string representation with comma-separated bits
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Bit bit : bits) {
            sb.append(bit.toString()); // append each bit's string 
            sb.append(","); // add comma after each bit
        }
        return sb.toString(); // return the constructed string
    }
}