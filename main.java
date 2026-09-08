import java.lang.Math;
import java.util.ArrayList;
import java.util.Scanner;


/**
 * This program implements lab 1 from the course ADK(DD2350). 
 * The purpose of lab 1 is to create a persistent array with various functionality explained in the lab info
 */
public class main {
    private static ArrayList<Array> stack = new ArrayList<Array>();
    private static int top = 0;

    /**
     * Class for the Node implementation
     *
     * A node has a left and right child.
     * Each node has a maxinsubtree and each leaf has a value and a maxinsubtree
     */    
    private static class Node {
        private Node left;
        private Node right;
        private int maxinsubtree;
        private int value;

        // node
        public Node (Node left, Node right) {
            this.left = left;
            this.right = right;
            this.maxinsubtree = Math.max(left.maxinsubtree, right.maxinsubtree);
        }

        // leaf
        public Node (int value) {
            this.value = value;
            this.maxinsubtree = value;
        }
    }

    /**
     * Class for the Array implementation
     *
     * Has a pointer to the root and knows the height of the tree
     */    
    private static class Array {
        private Node root;
        private int height;

        public Array (Node root, int height) {
            this.root = root;
            this.height = height;
        }
    }

    /**
     * Pushes the current Array to the stack
     *
     * @param a The Array to be pushed
     */
    private static void push(Array a) {
        stack.add(top, a);
        top++;
    }

    /**
     * Pops the top array from the stack and returns it
     *
     * @return The popped Array
     */
    private static Array pop() {
        Array a = stack.get(top - 1);
        top--;
        return a;
    }

    /**
     * Creates a new Array and iniitializes the root and height to null and 0 respectively
     *   
     * @return a the newly created Array
     */    
    private static Array newArray() {
        Array a = new Array(null, 0);
        return a;
    }

// height = floor of log2(maxindex)

    /**
     * Sets a new value or replaces a old value in the Array
     * The old Array is pushed onto the stack so changes can be reverted
     * 
     * @param array the new array that will point to the root ofd the new tree
     * @param index the index where the value should be added/replaced
     * @param value the value to add/replace
     * 
     * @return the new array
     */
    private static Array set(Array array, int index, int value) {
        int height = array.height;
        if (height == 0) {
            array.root = new Node(null, null);
        }
        while (height >= 0) {
            if ((index & (1 << height)) == 0) {
                new Node(array.root, null);
            }
            else {
                node.right = new node balalaba
                node.left = a.oldNode.left
            }
            height--;
        }
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