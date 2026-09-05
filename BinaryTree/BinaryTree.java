package BinaryTree;

import java.util.ArrayList;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.List;

/**
 * Binary Tree Traversals: Pre-order, In-order, Post-order
 * --------------------------------------------------------
 * Learning order recommendation:
 *   1. Pre-order  (Node -> Left -> Right)  - visit immediately, easiest to grasp
 *   2. In-order   (Left -> Node -> Right)  - visit is "sandwiched"
 *   3. Post-order (Left -> Right -> Node)  - visit happens last, hardest to grasp
 *
 * All three share the exact same recursive skeleton - only the position
 * of "visit(node)" relative to the two recursive calls changes:
 *
 *   Pre-order:   visit, left, right
 *   In-order:    left, visit, right
 *   Post-order:  left, right, visit
 *
 * This file includes BOTH recursive (simple) and iterative (stack-based,
 * for interview practice) versions of each traversal.
 *
 * Sample tree used below:
 *
 *          1
 *         / \
 *        2   3
 *       / \    \
 *      4   5    6
 *
 *   Pre-order  (Node,Left,Right):  1 2 4 5 3 6
 *   In-order   (Left,Node,Right):  4 2 5 1 3 6
 *   Post-order (Left,Right,Node):  4 5 2 6 3 1
 */

/*
 * SIMPLE WORDS MEIN SAMJHO
 * ------------------------------------------------------------------------
 * Teeno traversals ka skeleton bilkul same hai - bas "visit(node)" kahan
 * karte ho, wahi farak hai:
 *   Pre-order:  pehle node dekho, fir left, fir right
 *   In-order:   pehle left, fir node dekho, fir right (beech mein sandwich)
 *   Post-order: pehle left, fir right, sabse aakhri mein node dekho
 *
 * Recursive version seedha samajh aata hai (definition jaisa hi hai),
 * iterative version stack use karke wahi cheez bina recursion ke karta
 * hai - interview mein dono practice karna acha rehta hai.
 */

public class BinaryTree {

    static class Node {
        int val;
        Node left, right;
        Node(int val) { this.val = val; }
    }

    // ---------------------------------------------------------------
    // RECURSIVE traversals - learn these first, they mirror the
    // definitions almost word-for-word.
    // ---------------------------------------------------------------

    public static void preorderRecursive(Node node, List<Integer> out) {
        if (node == null) return;
        out.add(node.val);                 // visit
        preorderRecursive(node.left, out);
        preorderRecursive(node.right, out);
    }

    public static void inorderRecursive(Node node, List<Integer> out) {
        if (node == null) return;
        inorderRecursive(node.left, out);
        out.add(node.val);                 // visit
        inorderRecursive(node.right, out);
    }

    public static void postorderRecursive(Node node, List<Integer> out) {
        if (node == null) return;
        postorderRecursive(node.left, out);
        postorderRecursive(node.right, out);
        out.add(node.val);                 // visit
    }

    // ---------------------------------------------------------------
    // ITERATIVE traversals (stack-based) - practice these once the
    // recursive versions feel natural. Common interview follow-up.
    // ---------------------------------------------------------------

    // Pre-order: push right then left so left is processed first.
    public static List<Integer> preorderIterative(Node root) {
        List<Integer> out = new ArrayList<>();
        if (root == null) return out;
        Deque<Node> stack = new ArrayDeque<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            Node node = stack.pop();
            out.add(node.val);
            if (node.right != null) stack.push(node.right);
            if (node.left != null) stack.push(node.left);
        }
        return out;
    }

    // In-order: walk left as far as possible, pushing along the way,
    // then pop/visit, then move to the right subtree.
    public static List<Integer> inorderIterative(Node root) {
        List<Integer> out = new ArrayList<>();
        Deque<Node> stack = new ArrayDeque<>();
        Node curr = root;
        while (curr != null || !stack.isEmpty()) {
            while (curr != null) {
                stack.push(curr);
                curr = curr.left;
            }
            curr = stack.pop();
            out.add(curr.val);
            curr = curr.right;
        }
        return out;
    }

    // Post-order: trickiest iteratively. Simple trick used here:
    // do a "reversed pre-order" (Node, Right, Left) using a stack,
    // then reverse the result to get (Left, Right, Node).
    public static List<Integer> postorderIterative(Node root) {
        List<Integer> out = new ArrayList<>();
        if (root == null) return out;
        Deque<Node> stack = new ArrayDeque<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            Node node = stack.pop();
            out.add(0, node.val); // insert at front -> builds reversed order
            if (node.left != null) stack.push(node.left);
            if (node.right != null) stack.push(node.right);
        }
        return out;
    }

    // ---------------------------------------------------------------
    // Demo
    // ---------------------------------------------------------------
    public static void main(String[] args) {
        //          1
        //         / \
        //        2   3
        //       / \    \
        //      4   5    6
        Node root = new Node(1);
        root.left = new Node(2);
        root.right = new Node(3);
        root.left.left = new Node(4);
        root.left.right = new Node(5);
        root.right.right = new Node(6);

        System.out.println("--- Recursive ---");
        List<Integer> pre = new ArrayList<>();
        preorderRecursive(root, pre);
        System.out.println("Pre-order  : " + pre);   // [1, 2, 4, 5, 3, 6]

        List<Integer> in = new ArrayList<>();
        inorderRecursive(root, in);
        System.out.println("In-order   : " + in);    // [4, 2, 5, 1, 3, 6]

        List<Integer> post = new ArrayList<>();
        postorderRecursive(root, post);
        System.out.println("Post-order : " + post);  // [4, 5, 2, 6, 3, 1]

        System.out.println("\n--- Iterative ---");
        System.out.println("Pre-order  : " + preorderIterative(root));
        System.out.println("In-order   : " + inorderIterative(root));
        System.out.println("Post-order : " + postorderIterative(root));
    }
}

