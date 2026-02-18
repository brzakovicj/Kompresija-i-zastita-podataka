import algorithms.*;
import structures.Symbol;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        String originalFile = "../resources/test.txt";
        byte[] data = null;

        try {
            data = Files.readAllBytes(Path.of(originalFile));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        int N = data.length;

        int[] countPerByte = new int[256];
        for (byte b : data) {
            int value = b & 0xFF;
            countPerByte[value]++;
        }

        double[] probPerByte = new double[256];
        for (int i = 0; i < 256; i++) {
            probPerByte[i] = (double) countPerByte[i] / N;
        }

        for (int i = 0; i < 256; i++) {
            if (countPerByte[i] > 0) {
                System.out.println((char) i + ":" + probPerByte[i]);
            }
        }

        System.out.println("ENTROPIJA: " + Entropy.calculate(countPerByte, probPerByte));

        ArrayList<Symbol> symbols = new ArrayList<Symbol>();

        for (int i = 0; i < 256; i++) {
            if (countPerByte[i] > 0) {
                symbols.add(new Symbol((byte) i, probPerByte[i], countPerByte[i],""));
            }
        }

        System.out.println("----------------------------------------------------------------------------------");

        ShannonFano sf = new ShannonFano(symbols);
        String sfEncodedFile = "../resources/ShannonFanoEncoded.bin";
        String sfDecodedFile = "../resources/ShannonFanoDecoded.bin";
        sf.generateEncodedFile(sfEncodedFile, data);
        sf.decodeEncodedFile(sfEncodedFile, sfDecodedFile);
        boolean sfResult = compareFiles(originalFile, sfDecodedFile);

        if (sfResult) {
            System.out.println("SHANNON-FANO: Originalni i dekodirani fajl su isti! :)");
        } else {
            System.out.println("SHANNON-FANO: Originalni i dekodirani fajl nisu isti! :(");
        }

        try {
            int sfN = getFileLength(sfEncodedFile);
            double sfRatio = compressionRatio(N, sfN);
            System.out.println("SHANNON-FANO: Stepen kompresije je " + sfRatio + ".");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("----------------------------------------------------------------------------------");

        Huffman h = new Huffman(symbols);
        String hEncodedFile = "../resources/HuffmanEncoded.bin";
        String hDecodedFile = "../resources/HuffmanDecoded.bin";
        h.generateEncodedFile(hEncodedFile, data);
        h.decodeEncodedFile(hEncodedFile, hDecodedFile);
        boolean hResult = compareFiles(originalFile, hDecodedFile);

        if (hResult) {
            System.out.println("HUFFMAN: Originalni i dekodirani fajl su isti! :)");
        } else {
            System.out.println("HUFFMAN: Originalni i dekodirani fajl nisu isti! :(");
        }

        try {
            int hN = getFileLength(hEncodedFile);
            double hRatio = compressionRatio(N, hN);
            System.out.println("HUFFMAN: Stepen kompresije je " + hRatio + ".");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("----------------------------------------------------------------------------------");

        LZ77 lz77 = new LZ77(4);
        String lz77EncodedFile = "../resources/LZ77Encoded.bin";
        String lz77DecodedFile = "../resources/LZ77Decoded.bin";
        lz77.generateEncodedFile(lz77EncodedFile, data);
        lz77.decodeEncodedFile(lz77EncodedFile, lz77DecodedFile);
        boolean lz77Result = compareFiles(originalFile, lz77DecodedFile);

        if (lz77Result) {
            System.out.println("LZ77: Originalni i dekodirani fajl su isti! :)");
        } else {
            System.out.println("LZ77: Originalni i dekodirani fajl nisu isti! :(");
        }

        try {
            int lz77N = getFileLength(lz77EncodedFile);
            double lz77Ratio = compressionRatio(N, lz77N);
            System.out.println("LZ77: Stepen kompresije je " + lz77Ratio + ".");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("----------------------------------------------------------------------------------");

        LZW lzw = new LZW();
        String lzwEncodedFile = "../resources/LZWEncoded.bin";
        String lzwDecodedFile = "../resources/LZWDecoded.bin";
        lzw.generateEncodedFile(lzwEncodedFile, data);
        lzw.decodeEncodedFile(lzwEncodedFile, lzwDecodedFile);
        boolean lzwResult = compareFiles(originalFile, lzwDecodedFile);

        if (lzwResult) {
            System.out.println("LZW: Originalni i dekodirani fajl su isti! :)");
        } else {
            System.out.println("LZW: Originalni i dekodirani fajl nisu isti! :(");
        }

        try {
            int lzwN = getFileLength(lzwEncodedFile);
            double lzwRatio = compressionRatio(N, lzwN);
            System.out.println("LZW: Stepen kompresije je " + lzwRatio + ".");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static double compressionRatio(double originalSize, double compressedSize) {
        return originalSize / compressedSize;
    }

    public static int getFileLength(String fileName) throws IOException {
        byte[] data = null;
        data = Files.readAllBytes(Path.of(fileName));

        return data.length;
    }

    public static boolean compareFiles(String file1, String file2) {
        try {
            byte[] original = Files.readAllBytes(Path.of(file1));
            byte[] decoded = Files.readAllBytes(Path.of(file2));

            if (original.length != decoded.length) {
                return false;
            }

            for (int i = 0; i < original.length; i++) {
                if (original[i] != decoded[i]) {
                    return false;
                }
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}