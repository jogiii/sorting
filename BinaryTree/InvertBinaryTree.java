package BinaryTree;

import java.util.LinkedList;
import java.util.Queue;

/*
 * PROBLEM: INVERT A BINARY TREE
 * ----------------------------------------------------------------------
 * Given the root of a binary tree, invert it: for EVERY node, swap its
 * left and right children. Return the root of the inverted tree.
 *
 * Example:
 *
 *   Input:               Output (inverted):
 *          4                      4
 *         / \                   / \
 *        2   7                 7   2
 *       / \  / \               / \ / \
 *      1  3 6  9              9  6 3  1
 *
 * Every node's left/right children are swapped, at EVERY level - not
 * just the root.
 *
 * ------------------------------------------------------------------------
 * SIMPLE WORDS MEIN SAMJHO
 * ------------------------------------------------------------------------
 * Bas itna karna hai: tree ke HAR node ke left aur right child ko aapas
 * mein swap kar do - jaise tree ka mirror image bana rahe ho. Order
 * matter nahi karta ki pehle kis node ko swap karo, bas har ek node ko
 * ek baar visit karke uske children swap karne hain. Queue (BFS) use
 * karke level by level sab nodes visit kar lo aur swap karte jao.
 *
 * ------------------------------------------------------------------------
 * APPROACH: Level-Order (BFS) traversal using a Queue
 * ------------------------------------------------------------------------
 * We don't actually need to track levels/depth for this problem - we just
 * need to visit EVERY node exactly once and swap its children. A queue
 * (BFS) is a natural way to do this without recursion:
 *
 * 1. If root is null, nothing to invert - return null.
 * 2. Push root into a queue.
 * 3. While the queue isn't empty:
 *      a. Dequeue a node.
 *      b. Swap its left and right child pointers.
 *      c. Enqueue whichever children exist (the NEW left/right, doesn't
 *         matter which order since we're just visiting every node once).
 * 4. Return the (now inverted) root.
 *
 * ------------------------------------------------------------------------
 * DIAGRAM WALKTHROUGH
 * ------------------------------------------------------------------------
 *   Start:        4
 *                / \
 *               2   7
 *              / \  / \
 *             1  3 6  9
 *
 *   queue = [4]
 *
 *   Dequeue 4 -> swap children -> 4.left=7, 4.right=2
 *     queue = [7, 2]   (order doesn't matter, both get visited eventually)
 *
 *   Dequeue 7 -> swap children -> 7.left=9, 7.right=6
 *     queue = [2, 9, 6]
 *
 *   Dequeue 2 -> swap children -> 2.left=3, 2.right=1
 *     queue = [9, 6, 3, 1]
 *
 *   Dequeue 9, 6, 3, 1 -> each is a leaf, no children to swap.
 *     queue = []
 *
 *   Final tree:        4
 *                      / \
 *                     7   2
 *                    / \ / \
 *                   9  6 3  1
 *
 * Time Complexity:  O(n) - every node is visited/enqueued exactly once.
 * Space Complexity: O(w) where w = maximum width of the tree, for the
 *                    queue (worst case O(n) for a complete/wide tree).
 */

/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode() {}
 *     TreeNode(int val) { this.val = val; }
 *     TreeNode(int val, TreeNode left, TreeNode right) {
 *         this.val = val;
 *         this.left = left;
 *         this.right = right;
 *     }
 * }
 *
 * NOTE: TreeNode and Solution are nested as static classes inside this
 * outer InvertBinaryTree class (instead of top-level classes) purely to
 * avoid a "duplicate class" compile conflict with the other traversal
 * files in this same folder, which also define TreeNode/Solution.
 * Functionally they behave identically.
 */
public class InvertBinaryTree {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode() {}
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    static class Solution {

        // =====================================================================
        // Level-Order (BFS) approach using a Queue
        // ---------------------------------------------------------------------
        // Visit every node via BFS; at each node, swap its left/right
        // children, then enqueue the children so they get inverted too.
        //
        // Time:  O(n) - each node dequeued/processed exactly once.
        // Space: O(w) for the queue (w = max width of tree).
        // =====================================================================
        public TreeNode invertTree(TreeNode root) {
            if (root == null) return null;

            Queue<TreeNode> queue = new LinkedList<>();
            queue.offer(root);

            while (!queue.isEmpty()) {
                TreeNode node = queue.poll();

                // Swap left and right children.
                TreeNode temp = node.left;
                node.left = node.right;
                node.right = temp;

                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }

            return root;
        }
    }

    // Helper to print a tree's values in level-order, for demo purposes.
    private static void printLevelOrder(TreeNode root) {
        Queue<TreeNode> queue = new LinkedList<>();
        if (root != null) queue.offer(root);
        StringBuilder sb = new StringBuilder("[");
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            sb.append(node.val);
            if (!queue.isEmpty()) sb.append(", ");
            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
        sb.append("]");
        System.out.println(sb);
    }

    // Simple manual test.
    public static void main(String[] args) {
        //          4
        //         / \
        //        2   7
        //       / \  / \
        //      1  3 6  9
        TreeNode root = new TreeNode(4);
        root.left = new TreeNode(2);
        root.right = new TreeNode(7);
        root.left.left = new TreeNode(1);
        root.left.right = new TreeNode(3);
        root.right.left = new TreeNode(6);
        root.right.right = new TreeNode(9);

        System.out.print("Before invert (level-order): ");
        printLevelOrder(root); // [4, 2, 7, 1, 3, 6, 9]

        Solution solution = new Solution();
        TreeNode inverted = solution.invertTree(root);

        System.out.print("After invert  (level-order): ");
        printLevelOrder(inverted); // [4, 7, 2, 9, 6, 3, 1]
    }
}

