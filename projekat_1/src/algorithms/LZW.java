package algorithms;

import structures.LZWEntry;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LZW {
    public static final int MAX_DICTIONARY_SIZE = 65536;

    private Map<LZWEntry, Integer> initializeEncodingDictionary() {
        Map<LZWEntry, Integer> dictionary = new HashMap<>();

        for (int i = 0; i < 256; i++) {
            byte b = (byte) i;
            LZWEntry entry = new LZWEntry(new byte[]{ b });
            dictionary.put(entry, i);
        }

        return dictionary;
    }

    private ArrayList<Integer> encode(byte[] data) {
        Map<LZWEntry, Integer> dictionary = initializeEncodingDictionary();
        int dictionarySize = 256;

        ArrayList<Integer> encoded = new ArrayList<>();
        LZWEntry currentEntry = new LZWEntry(new byte[]{ data[0] });

        for (int i = 1; i < data.length; i++) {
            byte b = data[i];

            byte[] combinedByteSequence = new byte[currentEntry.getByteSequence().length + 1];
            System.arraycopy(currentEntry.getByteSequence(), 0, combinedByteSequence, 0, currentEntry.getByteSequence().length);
            combinedByteSequence[combinedByteSequence.length - 1] = b;
            LZWEntry combinedEntry = new LZWEntry(combinedByteSequence);

            if (dictionary.containsKey(combinedEntry)) {
                currentEntry = combinedEntry;
            }
            else {
                encoded.add(dictionary.get(currentEntry));

                if (dictionarySize == MAX_DICTIONARY_SIZE) {
                    dictionary = initializeEncodingDictionary();
                    dictionarySize = 256;
                }

                dictionary.put(combinedEntry, dictionarySize++);

                currentEntry = new LZWEntry(new byte[]{ b });
            }
        }

        encoded.add(dictionary.get(currentEntry));

        return encoded;
    }

    private static String bytesToAscii(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append((char) (b & 0xFF));
        }
        return sb.toString();
    }

    public void generateEncodedFile(String fileName, byte[] data) {
        ArrayList<Integer> entries = encode(data);

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(fileName))) {
            for (Integer entry : entries) {
                dos.writeShort(entry);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<Integer, LZWEntry> initializeDecodingDictionary() {
        Map<Integer, LZWEntry> dictionary = new HashMap<>();

        for (int i = 0; i < 256; i++) {
            dictionary.put(i, new LZWEntry(new byte[]{ (byte) i }));
        }

        return dictionary;
    }

    private ArrayList<Integer> readEncodedFile(String fileName) {
        ArrayList<Integer> encoded = new ArrayList<>();
        try (DataInputStream dis = new DataInputStream(new FileInputStream(fileName))) {
            while (dis.available() > 0) {
                encoded.add(dis.readUnsignedShort());
            }

            return encoded;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] decode(ArrayList<Integer> encoded) {
        Map<Integer, LZWEntry> dictionary = initializeDecodingDictionary();
        int dictionarySize = 256;

        ArrayList<Byte> decoded = new ArrayList<>();
        LZWEntry currentEntry = dictionary.get(encoded.getFirst());

        for (byte b : currentEntry.getByteSequence()) {
            decoded.add(b);
        }

        for (int i = 1; i < encoded.size(); i++) {
            int code = encoded.get(i);
            LZWEntry entry;

            if (dictionary.containsKey(code)) {
                entry = dictionary.get(code);
            }
            else if (code == dictionarySize) {
                byte[] combinedEntry = new byte[currentEntry.getByteSequence().length + 1];
                System.arraycopy(currentEntry.getByteSequence(), 0, combinedEntry, 0, currentEntry.getByteSequence().length);
                combinedEntry[combinedEntry.length - 1] = currentEntry.getByteSequence()[0];
                entry = new LZWEntry(combinedEntry);
            }
            else {
                return null;
            }

            for (byte b : entry.getByteSequence()) {
                decoded.add(b);
            }

            byte[] combinedEntry = new byte[currentEntry.getByteSequence().length + 1];
            System.arraycopy(currentEntry.getByteSequence(), 0, combinedEntry, 0, currentEntry.getByteSequence().length);
            combinedEntry[combinedEntry.length - 1] = entry.getByteSequence()[0];

            if (dictionarySize == MAX_DICTIONARY_SIZE) {
                dictionary = initializeDecodingDictionary();
                dictionarySize = 256;
            }

            dictionary.put(dictionarySize++, new LZWEntry(combinedEntry));

            currentEntry = entry;
        }

        byte[] decodedBytes = new byte[decoded.size()];
        for (int i = 0; i < decoded.size(); i++) {
            decodedBytes[i] = decoded.get(i);
        }

        return decodedBytes;
    }

    public void decodeEncodedFile(String fileEncoded, String fileDecoded) {
        ArrayList<Integer> encoded = readEncodedFile(fileEncoded);
        byte[] decoded = decode(encoded);

        try (FileOutputStream fos = new FileOutputStream(fileDecoded)) {
            if (decoded != null) {
                for (byte b : decoded) {
                    fos.write(b);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
