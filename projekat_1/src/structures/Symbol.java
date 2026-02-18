package structures;

public class Symbol {
    private byte value;
    private double probability;
    private int count;
    private String code;

    public Symbol(byte value, double probability, int count, String code) {
        this.value = value;
        this.probability = probability;
        this.count = count;
        this.code = code;
    }

    public byte getValue() {
        return value;
    }

    public double getProbability() {
        return probability;
    }

    public int getCount() {
        return count;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
