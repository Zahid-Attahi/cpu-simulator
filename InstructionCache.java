
public class InstructionCache {

    public Word32 address = new Word32();
    public Word32 value = new Word32();

    private Memory memory;
    private L2Cache l2Cache;

    private Word32 baseAddress = new Word32();
    private Word32[] words = new Word32[8];
    private boolean valid = false;
    private int lastAccessCost = 0;

    public InstructionCache() {
        for (int i = 0; i < 8; i++) {
            words[i] = new Word32();
        }
    }

    public void connect(Memory memory, L2Cache l2Cache) {
        this.memory = memory;
        this.l2Cache = l2Cache;
    }

    public int getLastAccessCost() {
        return lastAccessCost;
    }

    public void clear() {
        valid = false;
        lastAccessCost = 0;
        baseAddress = new Word32();
        for (int i = 0; i < 8; i++) {
            words[i] = new Word32();
        }
    }

    public void read() {
        int requestedAddress = addressAsInt();

        if (valid && containsAddress(requestedAddress)) {
            words[requestedAddress - baseAddressAsInt()].copy(value);
            lastAccessCost = 10;
            return;
        }

        address.copy(l2Cache.address);
        l2Cache.read();
        lastAccessCost = 10 + l2Cache.getLastAccessCost();

        int base = blockBase(requestedAddress);
        l2Cache.copyBlock(base, words);
        setAddressValue(baseAddress, base);
        valid = true;

        words[requestedAddress - base].copy(value);
    }

    private boolean containsAddress(int requestedAddress) {
        int base = baseAddressAsInt();
        return requestedAddress >= base && requestedAddress < base + 8;
    }

    private int addressAsInt() {
        return wordToInt(address);
    }

    private int baseAddressAsInt() {
        return wordToInt(baseAddress);
    }

    private int blockBase(int requestedAddress) {
        return requestedAddress - (requestedAddress % 8);
    }

    private int wordToInt(Word32 source) {
        int result = 0;
        int bitValue = 1;
        Bit cur = new Bit(false);

        for (int i = 31; i >= 22; i--) {
            source.getBitN(i, cur);
            if (cur.getValue()) {
                result += bitValue;
            }
            bitValue *= 2;
        }
        return result;
    }

    private void setAddressValue(Word32 target, int newValue) {
        TestConverter.fromInt(newValue, target);
    }
}
