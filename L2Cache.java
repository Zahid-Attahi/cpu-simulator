

public class L2Cache {

    public Word32 address = new Word32();
    public Word32 value = new Word32();

    private Memory memory;

    private Word32[] lineAddresses = new Word32[4];
    private Word32[][] lines = new Word32[4][8];
    private boolean[] valid = new boolean[4];
    private int nextReplaceLine = 0;
    private int lastAccessCost = 0;

    public L2Cache() {
        for (int i = 0; i < 4; i++) {
            lineAddresses[i] = new Word32();
            for (int j = 0; j < 8; j++) {
                lines[i][j] = new Word32();
            }
        }
    }

    public void connect(Memory memory) {
        this.memory = memory;
    }

    public int getLastAccessCost() {
        return lastAccessCost;
    }

    public void clear() {
        nextReplaceLine = 0;
        lastAccessCost = 0;
        for (int i = 0; i < 4; i++) {
            valid[i] = false;
            lineAddresses[i] = new Word32();
            for (int j = 0; j < 8; j++) {
                lines[i][j] = new Word32();
            }
        }
    }

    public void read() {
        int requestedAddress = addressAsInt();
        int lineIndex = findLine(requestedAddress);

        if (lineIndex >= 0) {
            lines[lineIndex][requestedAddress - blockBase(requestedAddress)].copy(value);
            lastAccessCost = 20;
            return;
        }

        lineIndex = fillLine(requestedAddress);
        lines[lineIndex][requestedAddress - blockBase(requestedAddress)].copy(value);
        lastAccessCost = 360;
    }

    public void write() {
        int requestedAddress = addressAsInt();
        int lineIndex = findLine(requestedAddress);

        if (lineIndex < 0) {
            lineIndex = fillLine(requestedAddress);
            lastAccessCost = 360;
        } else {
            lastAccessCost = 20;
        }

        int wordIndex = requestedAddress - blockBase(requestedAddress);
        value.copy(lines[lineIndex][wordIndex]);
        address.copy(memory.address);
        value.copy(memory.value);
        memory.write();
    }

    public void copyBlock(int base, Word32[] destination) {
        int lineIndex = findLine(base);
        if (lineIndex < 0) {
            return;
        }

        for (int i = 0; i < 8; i++) {
            lines[lineIndex][i].copy(destination[i]);
        }
    }

    private int findLine(int requestedAddress) {
        int base = blockBase(requestedAddress);

        for (int i = 0; i < 4; i++) {
            if (valid[i] && wordToInt(lineAddresses[i]) == base) {
                return i;
            }
        }
        return -1;
    }

    private int fillLine(int requestedAddress) {
        int lineIndex = nextReplaceLine;
        nextReplaceLine = (nextReplaceLine + 1) % 4;

        int base = blockBase(requestedAddress);
        setAddressValue(lineAddresses[lineIndex], base);

        for (int i = 0; i < 8; i++) {
            setAddressValue(memory.address, base + i);
            memory.read();
            memory.value.copy(lines[lineIndex][i]);
        }

        valid[lineIndex] = true;
        return lineIndex;
    }

    private int blockBase(int requestedAddress) {
        return requestedAddress - (requestedAddress % 8);
    }

    private int addressAsInt() {
        return wordToInt(address);
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
