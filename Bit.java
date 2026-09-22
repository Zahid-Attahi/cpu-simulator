
public class Bit {
    public enum boolValues { FALSE, TRUE }
    
    private boolean value; // stores the actual bit value
    
    // Constructor with initial value
    public Bit(boolean value) {
        this.value = value; // set the bit to the given value
    }
    
    // Returns the current value of the bit
    public boolean getValue() {
        return value; // return the stored boolean value
    }
    
    // Sets the bit to the given boolean value
    public void set(boolean value) {
        this.value = value; // set the value directly
    }
    
    // Assigns a boolValues enum value to this bit
    public void assign(boolValues value) {
        this.value = (value == boolValues.TRUE); // convert enum to boolean
    }
    
    // Assigns a boolean value directly
    public void assign(boolean value) {
        this.value = value; // set the boolean value directly
    }
    
    // Instance method: AND this bit with b2, store in result
    public void and(Bit b2, Bit result) {
        result.value = this.value && b2.value; // perform logical AND
    }
    
    // Static method: AND b1 and b2, store in result
    public static void and(Bit b1, Bit b2, Bit result) {
        result.value = b1.value && b2.value; // perform logical AND
    }
    
    // Instance method: OR this bit with b2, store in result
    public void or(Bit b2, Bit result) {
        result.value = this.value || b2.value; // perform logical OR
    }
    
    // Static method: OR b1 and b2, store in result
    public static void or(Bit b1, Bit b2, Bit result) {
        result.value = b1.value || b2.value; // perform logical OR
    }
    
    // Instance method: XOR this bit with b2, store in result
    public void xor(Bit b2, Bit result) {
        result.value = this.value ^ b2.value; // perform logical XOR
    }
    
    // Static method: XOR b1 and b2, store in result
    public static void xor(Bit b1, Bit b2, Bit result) {
        result.value = b1.value ^ b2.value; // perform logical XOR
    }
    
    // Static method: NOT b2, store in result
    public static void not(Bit b2, Bit result) {
        result.value = !b2.value; // perform logical NOT
    }
    
    // Instance method: NOT this bit, store in result
    public void not(Bit result) {
        result.value = !this.value; // perform logical NOT
    }
    
    // Returns string representation: "1" for true, "0" for false
    public String toString() {
        return value ? "1" : "0"; 
    }
}