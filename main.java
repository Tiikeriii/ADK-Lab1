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
    ArrayList<Node> spine = new ArrayList<Node>();

    int oldHeight = (oldArray != null) ? oldArray.height : 1;
    int height = oldHeight;

    if((index >>> height) != 0) {
        height = 32-Integer.numberOfLeadingZeros(index);
    }

    int level = height;
    boolean diverged = false;

    while(level >= 1) {
        if (((index  >> (level - 1)) & 1) == 0) {

            if(height == level) {
                newNode = new Node(null, null);
                newArray = new Array(newNode, height);
                currNewNode = newArray.root;
            } else {
                newNode = currNewNode;
            }
            spine.add(newNode);

            if (!diverged && level == oldHeight) {
                currOldNode = (oldArray.root != null) ? oldArray.root : new Node(null, null);
            }

            if(level >= 2) {
                if (!diverged && level <= oldHeight) {
                    newNode.right = (currOldNode.right != null) ? currOldNode.right : new Node(null, null);
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
                currOldNode = (currOldNode.left != null) ? currOldNode.left : new Node(null, null);
            }

        } else {

            if(height == level) {
                newNode = new Node(null, null);
                newArray = new Array(newNode, height);
                currNewNode = newArray.root;
            } else {
                newNode = currNewNode;
            }
            spine.add(newNode);

            if (!diverged && level == oldHeight) {
                currOldNode = (oldArray.root != null) ? oldArray.root : new Node(null, null);
            }

            if(level >= 2) {
                // Old tree and new index still shares a binary path
                if (!diverged && level <= oldHeight) {
                    newNode.left = (currOldNode.left != null) ? currOldNode.left : new Node(null, null);
                }
                // new index has diverged from the old tree 
                else if (!diverged) { 
                    newNode.left = wrapOldTree(oldArray, level - 1, oldHeight);
                    diverged = true;
                // old tree is integrated in new tree
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
                currOldNode = (currOldNode.right != null) ? currOldNode.right : new Node(null, null);
            }
        }
        level--;
    }
    for (int i = spine.size() - 1; i >= 0; i--) {
        Node currNode = spine.get(i);
        int leftMax = (currNode.left != null) ? currNode.left.maxinsubtree : -1;
        int rightMax = (currNode.right != null) ? currNode.right.maxinsubtree : -1;
        currNode.maxinsubtree = Math.max(leftMax, rightMax);
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
        n.maxinsubtree = n.left.maxinsubtree;
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
            
        if (node == null) {
            return 0;
        }
        // Given each level, do we go left (0) or right (1) (based on index)
        int path = (index  >> (level - 1)) & 1;
        
        if(level == 0) {
            return node.value;
        }

        if(path == 0) {
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

    /**
     * maxInInterval finds the max value in the given interval
     * 
     * @param array the array to search through
     * @param left the lower bound in the interval
     * @param right the upper bound in the interval
     * 
     * @return the max value in the array in the given interval
     */
    private static int maxInInterval(Array array, int left, int right) {
        if ((left < 0 || right < 0) || (left > right) || array.height < (32-Integer.numberOfLeadingZeros(left))) {
            return 0;
        }
        if (array.root == null) {
            return 0;
        }

        int maxIndex = -1 >>> (32 - array.height);
        int maxininterval = 0;

        if (left <= maxIndex) {
            int clampedRight = Math.min(right, maxIndex);
            maxininterval = Math.max(maxininterval, maxSegment(array.root, left, clampedRight, array.height));
        }

        return maxininterval;
    }

    /**
     * maxRightSegment finds the biggest value that is to the right (inclusive) of @param left
     * maxRightSegment is a recursive helper function for maxSegment
     * 
     * @param root the current root 
     * @param left the lower bound of the interval
     * @param height the height of the current root
     */
    private static int maxRightSegment(Node root, int left, int height) {
        if (root == null) {
            return 0;
        }
        int path = (left  >> (height - 1)) & 1;
        if (height == 1) {
            int leftVal = (root.left != null) ? root.left.value : 0;
            int rightVal = (root.right != null) ? root.right.value : 0;
            return (path == 0) ? Math.max(leftVal, rightVal) : rightVal;
        }
        if (path == 0) {
            int rightMax = (root.right != null) ? root.right.maxinsubtree : 0;
            return Math.max(rightMax, maxRightSegment(root.left, left, height - 1));
        }
        else {
            return maxRightSegment(root.right, left, height - 1);
        }
    }

    /**
     * maxLeftSegment finds the biggest value that is to the left (inclusive) of @param left
     * maxLeftSegment is a recursive helper function for maxSegment
     * 
     * @param root the current root 
     * @param right the upper bound of the interval
     * @param height the height of the current root
     */
    private static int maxLeftSegment(Node root, int right, int height) {
        if (root == null) {
            return 0;
        }
        int path = (right  >> (height - 1)) & 1;
        if (height == 1) {
            int leftVal = (root.left != null) ? root.left.value : 0;
            int rightVal = (root.right != null) ? root.right.value : 0;
            return (path == 0) ? leftVal : Math.max(leftVal, rightVal);
        }
        if (path == 0) {
            return maxLeftSegment(root.left, right, height - 1);
        }
        else {
            int leftMax = (root.left != null) ? root.left.maxinsubtree : 0;
            return Math.max(leftMax, maxLeftSegment(root.right, right, height - 1));
        }
    }

    /**
     * maxSegment finds the biggest value in a given interval
     * maxSegment is a recursive helper function for maxInInterval
     * 
     * @param root the curren root
     * @param left the lower bound
     * @param right the upper bound
     * @param height the height of the current root
     * 
     * @return the max value in the given interval
     */
    private static int maxSegment(Node root, int left, int right, int height) {
        int leftPath = (left  >> (height - 1)) & 1;
        int rightPath = (right  >> (height - 1)) & 1;
        if (height == 0) {
            return -1;
        }
        else if (height == 1) {
            int leftVal = (root.left != null) ? root.left.value : 0;
            int rightVal = (root.right != null) ? root.right.value : 0;
            if (leftPath == 0 && rightPath == 0) {
                return leftVal;
            }
            else if (leftPath == 1 && rightPath == 1) {
                return rightVal;
            }
            else {
                return Math.max(leftVal, rightVal);
            }
        }
        else if (leftPath == 0 && rightPath == 0) {
            return maxSegment(root.left, left, right, height - 1);
        }
        else if (leftPath == 1 && rightPath == 1) {
            return maxSegment(root.right, left, right, height - 1);
        }
        else {
            int maxLeft = maxRightSegment(root.left, left, height - 1);
            int maxRight = maxLeftSegment(root.right, right, height - 1);
            return Math.max(maxLeft, maxRight);
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
                    if (str.length < 3) {
                        System.out.println("Command should be: set index value");
                        break;
                    }
                    int index = Integer.parseInt(str[1]);
                    int value = Integer.parseInt(str[2]);
                    push(array);
                    array = set(array, index, value);
                    break;
                }
                case "get": {
                    if (str.length < 2) {
                        System.out.println("Command should be: get index");
                    }
                    else if (array == null || array.root == null) {
                        System.out.println("Array is empty");
                    }
                    else {
                        int index = Integer.parseInt(str[1]);
                        int value = get(array, index);
                        System.out.println(value);
                    }
                    break;
                }
                case "maxininterval": {
                    if (str.length < 3) {
                        System.out.println("Command should be: maxininterval left right");
                        break;
                    }
                    int left = Integer.parseInt(str[1]);
                    int right = Integer.parseInt(str[2]);
                    int maxinsubtree = maxInInterval(array, left, right);
                    System.out.println(maxinsubtree);
                    break;
                }
                case "unset": {
                    if (top == 0) {
                        System.out.println("Array is empty!");
                        break;
                    }
                    array = pop();
                    break;
                    }
                case "exit": {
                    isRunning = false;
                    break;
                    }
                default: {
                    System.out.println("Invalid command!\n");
                    break;
                }
            }
        }
        scanner.close();
    }
}