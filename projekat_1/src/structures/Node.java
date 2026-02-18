package structures;

public class Node {
    private Byte value;
    private double probability;
    private Node left;
    private Node right;

    public Node(Byte value, double probability, Node left, Node right) {
        this.value = value;
        this.probability = probability;
        this.left = left;
        this.right = right;
    }

    public double getProbability() {
        return probability;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public Byte getValue() {
        return value;
    }

    public boolean isLeaf() {
        return left == null && right == null;
    }
}
