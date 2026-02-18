package algorithms;

import structures.Node;
import structures.Symbol;
import structures.SymbolMap;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Huffman {
    private ArrayList<Symbol> symbols;
    private ArrayList<SymbolMap> symbolMaps;
    private Node root;

    public Huffman(ArrayList<Symbol> symbols) {
        this.symbols = new ArrayList<>();

        for (Symbol symbol : symbols) {
            this.symbols.add(new Symbol(symbol.getValue(), symbol.getProbability(), symbol.getCount(), symbol.getCode()));
        }

        this.root = generateTree();

        Map<Byte, String> map = new HashMap<>();
        generateCode(this.root, "", map);
        for (Symbol symbol : this.symbols) {
            symbol.setCode(map.get(symbol.getValue()));
        }
    }

    private Node generateTree() {
        ArrayList<Node> nodes = new ArrayList<>();

        for (Symbol symbol : this.symbols) {
            nodes.add(new Node(symbol.getValue(), symbol.getProbability(), null, null));
        }

        while (nodes.size() > 1) {
            nodes.sort((a, b) -> Double.compare(b.getProbability(), a.getProbability()));

            Node left = nodes.getLast();
            nodes.remove(left);
            Node right = nodes.getLast();
            nodes.remove(right);

            Node parent = new Node(null, left.getProbability() +  right.getProbability(), left, right);
            nodes.add(parent);
        }

        return nodes.getFirst();
    }

    private void generateCode(Node node, String code, Map<Byte, String> map) {
        if (node == null) {
            return;
        }

        if(node.isLeaf()) {
            map.put(node.getValue(), code);
            return;
        }

        generateCode(node.getLeft(), code + "0", map);
        generateCode(node.getRight(), code + "1", map);
    }

    private String encode(byte[] data) {
        StringBuilder dataCoded = new StringBuilder();

        for (byte b : data) {
            for (Symbol symbol : symbols) {
                if(b == symbol.getValue()) {
                    dataCoded.append(symbol.getCode());
                    break;
                }
            }
        }

        return dataCoded.toString();
    }

    private String generateSymbolMap() {
        StringBuilder map = new StringBuilder();

        for (Symbol symbol : symbols) {
            map.append(symbol.getValue() & 0xFF).append(":").append(symbol.getCode()).append(" ");
        }

        map.append("\n");
        return map.toString();
    }

    private byte[] bitStringToBytes(String bits) {
        int paddingLength = (8 - bits.length() % 8) % 8;
        bits += "0".repeat(paddingLength);

        byte[] bytes = new byte[bits.length() / 8];

        for (int i = 0; i < bits.length(); i += 8) {
            String byteString = bits.substring(i, i + 8);
            bytes[i / 8] = (byte) Integer.parseInt(byteString, 2);
        }

        return bytes;
    }

    public void generateEncodedFile(String fileName, byte[] data) {
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            String map = generateSymbolMap();
            fos.write(map.getBytes(StandardCharsets.UTF_8));

            String encodedData = encode(data);

            int validBits = encodedData.length();

            DataOutputStream dos = new DataOutputStream(fos);
            dos.writeInt(validBits);

            byte[] encodedDataBytes = bitStringToBytes(encodedData);
            fos.write(encodedDataBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readEncodedFile(String fileName) {
        try (FileInputStream fis = new FileInputStream(fileName)) {
            ByteArrayOutputStream mapBytes = new ByteArrayOutputStream();
            int byteChar;
            while ((byteChar = fis.read()) != -1) {
                if(byteChar == '\n') break;
                mapBytes.write(byteChar);
            }

            symbolMaps = new ArrayList<>();

            String mapLine = mapBytes.toString(StandardCharsets.UTF_8);
            String[] entries = mapLine.split(" ");
            for (String entry : entries) {
                if (entry.isBlank()) { continue; }

                String[] parts = entry.split(":");

                int value = Integer.parseInt(parts[0]);
                String code = parts[1];

                symbolMaps.add(new SymbolMap((byte) value, code));
            }

            DataInputStream dis = new DataInputStream(fis);
            int validBits = dis.readInt();

            byte[] encodedDataBytes = fis.readAllBytes();
            StringBuilder bits = new StringBuilder();
            for (byte b : encodedDataBytes) {
                bits.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
            }

            bits.setLength(validBits);

            return bits.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] decode(String bits) {
        ArrayList<Byte> bytes = new ArrayList<>();
        StringBuilder currentCode = new StringBuilder();

        for (int i = 0; i < bits.length(); i++) {
            currentCode.append(bits.charAt(i));

            for (SymbolMap map : symbolMaps) {
                if (currentCode.toString().equals(map.getCode())) {
                    bytes.add(map.getValue());
                    currentCode.setLength(0);
                    break;
                }
            }
        }

        byte[] byteArray = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            byteArray[i] = bytes.get(i);
        }

        return byteArray;
    }

    private void generateDecodedFile(String fileName, byte[] bytes) {
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void decodeEncodedFile(String fileEncoded, String fileDecoded) {
        String bits = readEncodedFile(fileEncoded);

        if (bits != null) {
            byte[] bytes = decode(bits);
            generateDecodedFile(fileDecoded, bytes);
        }
    }

    private void printSymbols() {
        for (Symbol symbol : symbols) {
            System.out.println((symbol.getValue() & 0xFF) + ": " + symbol.getCode());
        }
    }

    private void printSymbolMap() {
        for (SymbolMap symbol : symbolMaps) {
            System.out.println((symbol.getValue() & 0xFF) + ": " + symbol.getCode());
        }
    }
}
