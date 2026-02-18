import java.util.*;

public class LDPCCode {

    public static Matrix generateMatrixH(int n, int k, int wr, int wc, int seed) throws Exception {
        if ((n - k) * wr != n * wc) {
            throw new Exception("Nije zadovoljen uslov: m * wr == n * wc! Broj jedinica u vrstama mora biti jednak broju jedinica u kolonama.");
        }

        Matrix matrix = new Matrix(n - k, n);

        for (int i = 0; i < matrix.getRows() / wc; i++) {
            for (int j = 0; j < wr; j++) {
                matrix.setValue(i, i * wr + j, 1);
            }
        }

        Random rand = new Random(seed);

        int[] cols = new int[matrix.getCols()];
        for (int i = 0; i < cols.length; i++) {
            cols[i] = i;
        }

        boolean uniqueColumns = false;
        boolean columnMatch;

        while (!uniqueColumns) {
            uniqueColumns = true;

            for (int group = 1; group < wc; group++) {
                shuffle(cols, rand);

                for (int col = 0; col < matrix.getCols(); col++) {
                    for (int row = 0; row < matrix.getRows() / wc; row++) {
                        matrix.setValue(row + group * (matrix.getRows() / wc), col, matrix.getValue(row, cols[col]));
                    }
                }
            }

            for (int i = 0; i < matrix.getCols() - 1; i++) {
                columnMatch = true;

                for (int col = i + 1; col < matrix.getCols(); col++) {
                    columnMatch = true;

                    for (int row = 0; row < matrix.getRows(); row++) {
                        if (matrix.getValue(row, col) != matrix.getValue(row, i)) {
                            columnMatch = false;
                            break;
                        }
                    }

                    if (columnMatch) {
                        uniqueColumns = false;
                        break;
                    }
                }

                if (columnMatch) {
                    break;
                }
            }
        }

        return matrix;
    }

    private static void shuffle(int[] cols, Random rand) {
        int selected, temp;
        for (int i = cols.length - 1; i > 0; i--) {
            selected = rand.nextInt(i + 1);

            temp = cols[selected];
            cols[selected] = cols[i];
            cols[i] = temp;
        }
    }

    private static int[] calculateSyndrome(Matrix matrix, int[] y) {
        int rows = matrix.getRows();
        int cols = matrix.getCols();

        int[] syndrome = new int[rows];

        int sum;
        for (int i = 0; i < rows; i++) {
            sum = 0;

            for (int j = 0; j < cols; j++) {
                sum += matrix.getValue(i, j) * y[j];
            }

            syndrome[i] = sum % 2;
        }

        return syndrome;
    }

    public static Map<ArrayOfBits, int[]> generateSyndromeTable(Matrix matrix) {
        Map<ArrayOfBits, int[]> table = new HashMap<>();

        int correctorLength = matrix.getCols();
        int maxTableSize = (int) Math.pow(2, matrix.getRows());

        for (int w = 0; w <= correctorLength; w++) {
            generateCorrectors(correctorLength, w, 0, new int[correctorLength], table, maxTableSize, matrix);

            if (table.size() == maxTableSize) {
                break;
            }
        }

        return table;
    }

    public static void printSyndromeTable(Map<ArrayOfBits, int[]> table) {
        for (Map.Entry<ArrayOfBits, int[]> entry : table.entrySet()) {

            ArrayOfBits syndrome = entry.getKey();
            int[] corrector = entry.getValue();

            System.out.print("Syndrome: ");
            System.out.print(Arrays.toString(syndrome.getBits()));

            System.out.print("  ->  Corrector: ");
            System.out.println(Arrays.toString(corrector));
        }
    }

    private static void generateCorrectors(int correctorLength, int numOf1s, int start, int[] e, Map<ArrayOfBits, int[]> table, int maxTableSize, Matrix matrix) {
        if (table.size() == maxTableSize) {
            return;
        }

        if (numOf1s == 0) {
            ArrayOfBits syndromeArray = new ArrayOfBits(calculateSyndrome(matrix, e));

            if (!table.containsKey(syndromeArray)) {
                table.put(syndromeArray, e.clone());
            }

            return;
        }

        for (int i = start; i <= correctorLength - numOf1s; i++) {
            e[i] = 1;
            generateCorrectors(correctorLength, numOf1s - 1, i + 1, e, table, maxTableSize, matrix);
            e[i] = 0;

            if (table.size() == maxTableSize) {
                return;
            }
        }
    }

    private static int findMinWeight(Matrix H, int[] word, int numOf1s, int start, int minWeight) {
        if (minWeight < Integer.MAX_VALUE) {
            return minWeight;
        }

        if (numOf1s == 0) {
            int[] syndrome = calculateSyndrome(H, word);

            boolean isCodeword = true;

            for (int i = 0; i < syndrome.length; i++) {
                if (syndrome[i] != 0) {
                    isCodeword = false;
                    break;
                }
            }

            if (isCodeword) {
                int sum = 0;

                for (int node = 0; node < word.length; node++) {
                    sum += word[node];
                }

                minWeight = sum;
            }

            return minWeight;
        }

        for (int i = start; i <= word.length - numOf1s; i++) {
            word[i] = 1;
            minWeight = findMinWeight(H, word, numOf1s - 1, i + 1, minWeight);
            word[i] = 0;

            if (minWeight < Integer.MAX_VALUE) {
                return minWeight;
            }
        }

        return minWeight;
    }

    public static int calculateMinimumDistance(Matrix H) {
        int n = H.getCols();
        int[] word = new int[n];
        int minWeight = Integer.MAX_VALUE;

        for (int numOf1s = 1; numOf1s < word.length; numOf1s++) {
            minWeight = findMinWeight(H, word, numOf1s, 0, minWeight);

            if (minWeight < Integer.MAX_VALUE) {
                return minWeight;
            }
        }

        return minWeight;
    }

    public static int[] gallagerB(Matrix H, int[] y, double th0, double th1, int maxInterations) {
        int[] corrected = y.clone();

        for (int i = 0; i < maxInterations; i++) {
            int[] syndrome = calculateSyndrome(H, corrected);
            boolean noError = true;

            for (int j = 0; j < syndrome.length; j++) {
                if (syndrome[j] == 1) {
                    noError = false;
                    break;
                }
            }

            if (noError) {
                return corrected;
            }

            int[] shouldBe1 = new int[corrected.length];
            int[] shouldBe0 = new int[corrected.length];
            int sum, expected;

            for (int row = 0; row < H.getRows(); row++) {
                for (int col = 0; col < H.getCols(); col++) {
                    if(H.getValue(row, col) == 0) {
                        continue;
                    }

                    sum = 0;
                    for (int j = 0; j < H.getCols(); j++) {
                        if (H.getValue(row, j) == 1 && j != col) {
                            sum += corrected[j];
                        }
                    }

                    expected = sum % 2;

                    if (expected == 1) {
                        shouldBe1[col]++;
                    }
                    else {
                        shouldBe0[col]++;
                    }
                }
            }

            for (int node = 0; node < corrected.length; node++) {
                if (shouldBe0[node] >= th0 * (shouldBe0[node] + shouldBe1[node])) {
                    corrected[node] = 0;
                }
                else if (shouldBe1[node] >= th1 * (shouldBe0[node] + shouldBe1[node])) {
                    corrected[node] = 1;
                }
            }
        }

        return corrected;
    }

    private static int findMinErrors(Matrix H, int[] error, int numOf1s, int start, int[] code, int minErrors, double th0, double th1, int maxIterations) {
        if (minErrors < Integer.MAX_VALUE) {
            return minErrors;
        }

        if (numOf1s == 0) {
            int[] y = new int[code.length];
            for (int node = 0; node < y.length; node++) {
                y[node] = (code[node] + error[node]) % 2;
            }

            int[] decoded = gallagerB(H, y, th0, th1, maxIterations);

            boolean hasError = false;
            int sum = 0;

            for (int node = 0; node < decoded.length; node++) {
                if (decoded[node] != code[node]) {
                    hasError = true;
                }

                sum += error[node];
            }

            if (hasError) {
                minErrors = sum;
            }

            return minErrors;
        }

        for (int i = start; i <= error.length - numOf1s; i++) {
            error[i] = 1;
            minErrors = findMinErrors(H, error, numOf1s - 1, i + 1, code, minErrors, th0, th1, maxIterations);
            error[i] = 0;

            if (minErrors < Integer.MAX_VALUE) {
                return minErrors;
            }
        }

        return minErrors;
    }

    public static int findGallagerBreakingPoint(Matrix H, double th0, double th1, int maxInterations) {
        int n = H.getCols();
        int[] code = new int[n];
        int[] error = new int[n];
        int minErrors = Integer.MAX_VALUE;

        for (int numOf1s = 1; numOf1s < error.length; numOf1s++) {
            minErrors = findMinErrors(H, error, numOf1s, 0, code, minErrors, th0, th1, maxInterations);

            if (minErrors < Integer.MAX_VALUE) {
                return minErrors;
            }
        }

        return minErrors;
    }
}
