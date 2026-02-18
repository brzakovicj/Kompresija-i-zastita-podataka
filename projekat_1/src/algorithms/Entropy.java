package algorithms;

public class Entropy {
    public static double calculate(int[] countPerByte, double[] probPerByte) {
        double entropy = 0.0;

        for (int i = 0; i < 256; i++) {
            if(countPerByte[i] > 0) {
                entropy += probPerByte[i] * (Math.log(probPerByte[i]) / Math.log(2));
            }
        }

        return -entropy;
    }
}
