package structures;

public class LZ77Entry {
    private int flag;
    private byte literal;
    private int length;
    private int offset;

    public LZ77Entry(byte literal) {
        this.flag = 0;
        this.literal = literal;
    }

    public LZ77Entry(int offset, int length) {
        this.flag = 1;
        this.offset = offset;
        this.length = length;
    }

    public int getFlag() {
        return flag;
    }

    public byte getLiteral() {
        return literal;
    }

    public int getLength() {
        return length;
    }

    public int getOffset() {
        return offset;
    }
}
