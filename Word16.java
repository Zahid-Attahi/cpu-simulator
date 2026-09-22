
public class Word16 {
    private Bit[] bits; // array to store 16 bits
    

    // Default constructor - initializes all bits to false
    public Word16() {
        bits = new Bit[16]; // create array of 16 bits
        
        for (int i = 0; i < 16; i++) {
            bits[i] = new Bit(false); // initialize each bit to false
        }
    }
    
    // Constructor that takes an array of bits - CRITICAL for unit tests

    public Word16(Bit[] in) {
        bits = new Bit[16]; // create array of 16 bits

        for (int i = 0; i < 16; i++) {
            bits[i] = new Bit(in[i].getValue()); // copy each bit's value
        }
    }
    
    // Copies the values from this instance to result
    public void copy(Word16 result) {
        for (int i = 0; i < 16; i++) {
            result.bits[i].assign(this.bits[i].getValue() ? Bit.boolValues.TRUE : Bit.boolValues.FALSE); // copy each bit
        }
    }
    
    
    public void setBitN(int n, Bit source) {
        bits[n].assign(source.getValue() ? Bit.boolValues.TRUE : Bit.boolValues.FALSE); // assign source's value
    }
    
    // Sets result to be the same value as the nth bit of this word
    public void getBitN(int n, Bit result) {
        result.assign(bits[n].getValue() ? Bit.boolValues.TRUE : Bit.boolValues.FALSE); // copy bit value to result
    }
    
    // Instance method: checks if other is equal to this
    public boolean equals(Word16 other) {
        for (int i = 0; i < 16; i++) {
            if (this.bits[i].getValue() != other.bits[i].getValue()) {
                return false; // return false if any bit differs
            }
        }
        return true; // all bits match
    }
    
    // Static method: checks if a and b are equal

    public static boolean equals(Word16 a, Word16 b) {

        return a.equals(b); // use instance method
    }
    
    // Instance AND: this AND other, store in result
    public void and(Word16 other, Word16 result) {
        for (int i = 0; i < 16; i++) {
            Bit.and(this.bits[i], other.bits[i], result.bits[i]); // AND each bit
        }
    }
    
    // Static AND: a AND b, store in result
    public static void and(Word16 a, Word16 b, Word16 result) {
        a.and(b, result); // use instance method
    }
    
    // Instance OR: this OR other, store in result
    public void or(Word16 other, Word16 result) {

        for (int i = 0; i < 16; i++) {

            Bit.or(this.bits[i], other.bits[i], result.bits[i]); // OR each bit
        }
    }
    
    // Static OR: a OR b, store in result
    public static void or(Word16 a, Word16 b, Word16 result) {
        a.or(b, result); // use instance method
    }
    
    // Instance XOR: this XOR other, store in result

    public void xor(Word16 other, Word16 result) {
        for (int i = 0; i < 16; i++) {
            Bit.xor(this.bits[i], other.bits[i], result.bits[i]); // XOR each bit
        }
    }
    
    // Static XOR: a XOR b, store in result

    public static void xor(Word16 a, Word16 b, Word16 result) {

        a.xor(b, result); // use instance method
    }
    
    // Instance NOT: NOT this, store in result

    public void not(Word16 result) {
        for (int i = 0; i < 16; i++) {

            Bit.not(this.bits[i], result.bits[i]); // NOT each bit
        }
    }
    

    // Static NOT: NOT a, store in result
    public static void not(Word16 a, Word16 result) {
        a.not(result); // use instance method
    }
    
    // Returns string representation with comma-separated bits - EXACT format expected

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Bit bit : bits) {
            sb.append(bit.toString()); // append each bit's string ("t" or "f")
            sb.append(","); // add comma after each bit
        }
        return sb.toString(); // return the constructed string
    }
}