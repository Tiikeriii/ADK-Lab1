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

        // Node
        public Node (Node left, Node right) {
            this.left = left;
            this.right = right;
            this.value = 0;
        }

        // Leaf
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

        public Array(Node root, int height) {
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
        Array a = new Array(null, 1);
        return a;
    }

// height = floor of log2(maxindex)

    /**
     * Sets a new value or replaces a old value in the Array
     * The old Array is pushed onto the stack so changes can be reverted
     * 
     * @param oldAarray the old array
     * @param index the index where the value should be added/replaced
     * @param value the value to add/replace
     * 
     * @return the new array
     */
    private static Array set(Array oldArray, int index, int value) {

    Node currNewNode = null;
    Node currOldNode = null;
    Node newNode = null;
    Array newArray = null;

    int oldHeight = (oldArray != null) ? oldArray.height : 1;
    int height = oldHeight;

    if((index >>> height) != 0) {
        height = 32-Integer.numberOfLeadingZeros(index);
    }

    int level = height;
    boolean diverged = false;

    while(level >= 1) {
        if ((index & (1 << level - 1)) == 0) {

            if(height == level) {
                newNode = new Node(null, null);
                newArray = new Array(newNode, height);
                currNewNode = newArray.root;
            } else {
                newNode = currNewNode;
            }
            if (!diverged && level == oldHeight) {
                currOldNode = (oldArray.root != null) ? oldArray.root : new Node(null, null);
            }
            if(level >= 2) {
                if (!diverged && level <= oldHeight) {
                    newNode.right = currOldNode.right;
                } else {
                    newNode.right = new Node(null, null);
                }
                newNode.left = new Node(null,null);
            } else {
                newNode.right = (!diverged) ? currOldNode.right : new Node(null, null);
                newNode.left = new Node(value);
            }
            currNewNode = newNode.left;
            if (!diverged && level <= oldHeight) {
                currOldNode = currOldNode.left;
            }

        } else {

            if(height == level) {
                newNode = new Node(null, null);
                newArray = new Array(newNode, height);
                currNewNode = newArray.root;
            } else {
                newNode = currNewNode;
            }
            if (!diverged && level == oldHeight) {
                currOldNode = (oldArray.root != null) ? oldArray.root : new Node(null, null);
            }
            if(level >= 2) {
                if (!diverged && level <= oldHeight) {
                    newNode.left = currOldNode.left;
                } else if (!diverged) {
                    newNode.left = wrapOldTree(oldArray, level - 1, oldHeight);
                    diverged = true;
                } else {
                    newNode.left = new Node(null, null);
                }
                newNode.right = new Node(null,null);
            } else {
                newNode.left = (!diverged) ? currOldNode.left : new Node(null, null);
                newNode.right = new Node(value);
            }
            currNewNode = newNode.right;
            if (!diverged && level <= oldHeight) {
                currOldNode = currOldNode.right;
            }
        }
        level--;
    }

    return newArray;
}

    /**
     * wrapOldTree is a wrap function that creates empty "dummy" nodes until a dummy node can point to the old root.
     * It also creates neighbour nodes for each level so future set commands work
     * 
     * 
     * @param oldArray the old array
     * @param level the current level
     * @param oldHeight the height of the old array
     * 
     * @return n the highest node of the wrapped old tree
     */
    private static Node wrapOldTree(Array oldArray, int level, int oldHeight) {
        if (level == oldHeight) {
            return (oldArray.root != null) ? oldArray.root : new Node(null, null);
        }
        Node n = new Node(null, null);
        n.left = wrapOldTree(oldArray, level - 1, oldHeight);
        n.right = new Node(null, null);
        return n;
    }

    /**
     * get(Array array, int index) starts the recursivce iteration of the array.
     * 
     * @param array the array to search through
     * @param index the index to search for
     * 
     * @return the value of the index
     * @return 0 if the index has not been set
     */
    private static int get(Array array, int index) {

        // Exempel: Height = 2 (nod (2) ->nod (1) ->leaf (0))
        // Exempel: index = 4 
        // index >>> a.height = 0100 >>> 2 = 0001 -> tree nog big enough
        if((index >>> array.height) != 0) {
            return 0; // Error
        }

        return get(array.root, array.height, index);

    }

    /**
     * get(Node node, int level, int index) iterates recursively through the binary path of the index and returns the value.
     * 
     * @param node the current node
     * @param level the current level
     * @param index the index to search for
     * 
     * @return the value of the index
     * @return 0 if the index has not been set
     */
    private static int get(Node node, int level, int index) {
            
        // Given each level, do we go left (0) or right (1) (based on index)
        int bit = (index & (1 << level - 1));
        
        if(level == 0) {
            return node.value;
        }

        if(bit == 0) {
            // Continue on left node
            if (node.left == null) {
                return 0;
            }
            return get(node.left, level - 1, index);
        } else {
            // Continue on right node
            if (node.right == null) {
                return 0;
            }
            return get(node.right, level - 1, index);
        }
    }

    public static void main(String[] args) {

        Array array = newArray();
        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;

        while (isRunning) {
            String input = scanner.nextLine();
            String[] str = input.split("\\s+");
        
            switch (str[0]) {
                case "set": {
                    int index = Integer.parseInt(str[1]);
                    int value = Integer.parseInt(str[2]);
                    push(array);
                    array = set(array, index, value);
                    break;
                }
                case "get": {
                    if (array == null || array.root == null) {
                        System.out.println("Array is empty");
                    }
                    else {
                        int index = Integer.parseInt(str[1]);
                        int value = get(array, index);
                        System.out.println(value);
                    }
                    break;
                }
                case "unset": {
                    array = pop();
                    break;
                    }
                case "exit": {
                    isRunning = false;
                    break;
                    }
                default: {
                    System.out.println("Invalid command\n");
                    break;
                }
            }
        }
        scanner.close();
    }
}