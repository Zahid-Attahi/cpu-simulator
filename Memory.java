//Zahidullah

public class Memory {

    public Word32 address = new Word32();
    public Word32 value = new Word32();

    private final Word32[] dram = new Word32[1000];

    public Memory() {

        // Initialize all memory locations to zero
        for (int i = 0; i < 1000; i++) {
            dram[i] = new Word32();
        }
    }

    // Converts the address Word32 to an unsigned integer (range 0-999)
    // Addresses are always unsigned per the assignment spec

    public int addressAsInt() {
           int result = 0;
        int bitValue = 1;
        Bit cur = new Bit(false);

        // Accumulate from LSB (bit 31) up through the bits needed for 0-999
        
        for (int i = 31; i >= 22; i--) {

            address.getBitN(i, cur);
            if (cur.getValue()) {
                result += bitValue;
            }
            bitValue *= 2;
        }
        return result;
    }

    // Reads from dram at the current address into value
    public void read() {
        int addr = addressAsInt();
        dram[addr].copy(value);
    }

    // Writes the current value into dram at the current address

    public void write() {
        int addr = addressAsInt();
        value.copy(dram[addr]);
    }

    // Loads an array of 32-character binary strings into memory starting at address 0.
    // Each string must be exactly 32 characters of '0' or '1'.
    // Throws IllegalArgumentException if any string is not exactly 32 characters.
    public void load(String[] data) {

        Bit trueBit = new Bit(true);
        Bit falseBit = new Bit(false);

        for (int i = 0; i < data.length; i++) {

            String line = data[i];
            if (line.length() != 32) {
                throw new IllegalArgumentException(
                    "Invalid line length at index " + i + ": expected 32 characters, got " + line.length());
            }
            for (int j = 0; j < 32; j++) {
                
                char c = line.charAt(j);
                if (c == '1') {
                    dram[i].setBitN(j, trueBit);

                } else {
                    dram[i].setBitN(j, falseBit);
                }
            }
        }
    }
}