import java.lang.Math;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.ArrayList;

public class main {
    private static ArrayList<Array> stack = new ArrayList<Array>();
    private static int top = 0;
    
    private static class Node {
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

    private static class Array {
        private Node root;
        private int height;

        public Array (Node root, int height) {
            this.root = root;
            this.height = height;
        }
    }

    private static void push(Array a) {
        stack.add(top, a);
        top++;
    }

    private static Array pop() {
        Array a = stack.get(top - 1);
        top--;
        return a;
    }

    private static Array newArray() {
        Array a = new Array(null, 0);
        return a;
    }

    private static Array set(Array array, int index, int value) {
        
        return null;
    }

    public static void Main (String[] args) {
        Array a = newArray();
        push(a);

        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;

        while (isRunning) {
            String input = scanner.nextLine();
            String[] str = input.split("\\s+");
        
            switch (str[0]) {
                case "set":
                    int index = Integer.parseInt(str[2]);
                    int value = Integer.parseInt(str[3]);
                    set(a, index, value);
                case "unset":
                    pop();
                case "exit":
                    isRunning = false;
            }
        }
        scanner.close();
    }
}