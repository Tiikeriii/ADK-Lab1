import java.lang.Math;
import java.util.Scanner;

public class main {
    
    private class Node {
        private Node left;
        private Node right;
        private int maxinsubtree;
        private int value;

        public Node (Node left, Node right) {
            this.left = left;
            this.right = right;
            this.maxinsubtree = Math.max(left.maxinsubtree, right.maxinsubtree);
        }

        public Node (int value) {
            this.value = value;
            this.maxinsubtree = value;
        }
    }

    private class Array {
        private Node root;
        private int height;

        public Array (Node root, int height) {
            this.root = root;
            this.height = height;
        }
    }

    private Array newArray() {
        new Array(null, 0);
        
    }

    public static void main (String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        String[] str = input.split("\\s+");

        switch (str[0]) {
            case "set":
                set(str[1], str[2], str[3]);
        }
    }
}