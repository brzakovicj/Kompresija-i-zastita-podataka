package structures;

import java.util.Arrays;

public class LZWEntry {
    private byte[] byteSequence;

    public LZWEntry(byte[] byteSequence) {
        this.byteSequence = byteSequence;
    }

    public byte[] getByteSequence() {
        return byteSequence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof LZWEntry)) return false;

        LZWEntry other = (LZWEntry) o;
        return Arrays.equals(this.byteSequence, other.byteSequence);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(byteSequence);
    }
}
