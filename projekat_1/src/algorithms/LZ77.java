package algorithms;

import structures.LZ77Entry;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class LZ77 {
    private int windowSize;
    private int maxMatchLength;

    public LZ77(int windowSize) {
        this.windowSize = windowSize;

        if (this.windowSize > 255) {
            this.windowSize = 255;
        }

        this.maxMatchLength = 255;
    }

    private ArrayList<LZ77Entry> encode(byte[] data) {
        ArrayList<LZ77Entry> entries = new ArrayList<>();
        int maxLength, maxOffset, window, length;
        int i = 0;

        while (i < data.length) {
            maxLength = 0;
            maxOffset = 0;

            window = windowSize;
            if (window > i) {
                window = i;
            }

            for (int j = 1; j < window + 1; j++) {
                length = 0;

                while (i + length < data.length && data[i + length] == data[i - j + length]) {
                    length++;

                    if (length == maxMatchLength) {
                        break;
                    }
                }

                if (length > maxLength) {
                    maxLength = length;
                    maxOffset = j;
                }
            }

            if (maxLength > 0) {
                entries.add(new LZ77Entry(maxOffset, maxLength));
                i += maxLength;
            }
            else {
                entries.add(new LZ77Entry(data[i]));
                i += 1;
            }
        }

        return entries;
    }

    public void generateEncodedFile(String fileName, byte[] data) {
        ArrayList<LZ77Entry> entries = encode(data);

        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            for (LZ77Entry entry : entries) {
                fos.write(entry.getFlag());

                if (entry.getFlag() == 0) {
                    fos.write(entry.getLiteral());
                }
                else {
                    fos.write(entry.getOffset());
                    fos.write(entry.getLength());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Byte> readEncodedFileAndDecode(String fileName) {
        ArrayList<Byte> bytes = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(fileName)) {
            int flag, literal, offset, length, repStart;

            while ((flag = fis.read()) != -1) {
                if (flag == 0) {
                    literal = fis.read();
                    bytes.add((byte) literal);
                }
                else if (flag == 1) {
                    offset = fis.read();
                    length = fis.read();

                    repStart = bytes.size() - offset;

                    for (int i = 0; i < length; i++) {
                        bytes.add(bytes.get(repStart + i));
                    }
                }
            }

            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void decodeEncodedFile(String fileEncoded, String fileDecoded) {
        ArrayList<Byte> bytes = readEncodedFileAndDecode(fileEncoded);

        try (FileOutputStream fos = new FileOutputStream(fileDecoded)) {
            if (bytes != null) {
                for (byte b : bytes) {
                    fos.write(b);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
