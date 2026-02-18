import java.util.Arrays;

public class ArrayOfBits {
    private int[] bits;

    public ArrayOfBits(int[] bits) {
        this.bits = bits;
    }

    public int[] getBits() {
        return bits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ArrayOfBits)) return false;

        ArrayOfBits other = (ArrayOfBits) o;
        return Arrays.equals(this.bits, other.bits);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bits);
    }
}
