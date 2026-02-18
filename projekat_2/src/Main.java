import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try {
            Matrix matrix = LDPCCode.generateMatrixH(15, 6, 5, 3, 27);
            matrix.printMatrix();

            Map<ArrayOfBits, int[]> table = LDPCCode.generateSyndromeTable(matrix);
            LDPCCode.printSyndromeTable(table);

            int minDistance = LDPCCode.calculateMinimumDistance(matrix);
            System.out.println("Minimalna distanca: " + minDistance);
            System.out.println("Broj gresaka koje je moguce pronaci: " + (minDistance - 1));
            System.out.println("Broj gresaka koje je moguce ispraviti: " + (minDistance - 1)/2);

            int gallagerBreakingPoint = LDPCCode.findGallagerBreakingPoint(matrix, 0.5, 0.5, 10);
            System.out.println("Broj gresaka koje Gallager B algoritam ne uspeva da ispravi: " + gallagerBreakingPoint);

            int canCorrectErrors = (minDistance - 1)/2;
            int gallagerCanCorrectErrors = gallagerBreakingPoint - 1;

            if (gallagerCanCorrectErrors == canCorrectErrors) {
                System.out.println("Gallager-B je dostigao teorijsku granicu.");
            }
            else if (gallagerCanCorrectErrors < canCorrectErrors) {
                System.out.println("Gallager-B je slabiji od teorijske granice za sledeci broj gresaka: " + (canCorrectErrors - gallagerCanCorrectErrors) + ".");
            }
            else {
                System.out.println("Gallager-B ispravlja vise gresaka od teorijske granice.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }
}