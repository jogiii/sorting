package BinaryTree;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/*
 * WHAT IS IN-ORDER TRAVERSAL?
 * ----------------------------------------------------------------------
 * In-order traversal is a way of visiting every node in a binary tree
 * in the order: LEFT subtree -> NODE -> RIGHT subtree.
 *
 * In other words, at each node you:
 *   1. Recursively traverse its entire left subtree (in-order) FIRST.
 *   2. Then visit (process) the current node.
 *   3. Then recursively traverse its entire right subtree (in-order).
 *
 * It's called "in-order" because the visit is sandwiched IN BETWEEN
 * the two recursive calls - as opposed to pre-order (visit happens
 * first) or post-order (visit happens last).
 *
 * Example tree:
 *
 *          1
 *         / \
 *        2   3
 *       / \
 *      4   5
 *
 * In-order visiting order: 4, 2, 5, 1, 3
 *   - Go all the way left first: visit 4 (leftmost node).
 *   - Back up to 2, visit it (both... well, its left is done).
 *   - Go right of 2: visit 5.
 *   - Back up to 1 (root), visit it.
 *   - Go right into 3's subtree: visit 3.
 *
 * SPECIAL PROPERTY - Binary Search Trees (BSTs):
 *   If the tree is a BST (left subtree values < node < right subtree
 *   values, for every node), in-order traversal visits nodes in
 *   ASCENDING SORTED ORDER automatically - no extra sorting needed.
 *   This is the #1 reason in-order traversal is so widely used.
 *
 * Common use cases:
 *   - Getting sorted output from a BST.
 *   - Validating whether a binary tree is a valid BST (in-order
 *     sequence must be strictly increasing).
 *   - Finding the k-th smallest/largest element in a BST.
 *
 * Time Complexity:  O(n) - every node is visited exactly once.
 * Space Complexity: depends on approach - O(h) for recursive/stack-based
 *                    approaches (h = tree height), O(1) extra for Morris
 *                    traversal (see approach 3 below).
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
 * outer InorderTraversal class (instead of top-level classes) purely to
 * avoid a "duplicate class" compile conflict with PreorderTraversal.java
 * and PostorderTraversal.java, which also define TreeNode/Solution in
 * this same folder/default-package. Functionally they behave identically.
 */
public class InorderTraversal {

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

        /**
         * DEFAULT entry point used by LeetCode-style signature.
         * Delegates to the iterative-stack approach (most commonly expected).
         */
        public List<Integer> inorderTraversal(TreeNode root) {
            return inorderIterativeStack(root);
        }

        // =====================================================================
        // APPROACH 1: Recursive (Left -> Node -> Right)
        // ---------------------------------------------------------------------
        // The most natural definition-following approach. Uses the call stack
        // implicitly for recursion.
        //
        // Time:  O(n) - visit every node once.
        // Space: O(h) - recursion stack depth = tree height (h). Worst case
        //         O(n) for a skewed tree, O(log n) for a balanced tree.
        // =====================================================================
        public List<Integer> inorderRecursive(TreeNode root) {
            List<Integer> result = new ArrayList<>();
            inorderRecursiveHelper(root, result);
            return result;
        }

        private void inorderRecursiveHelper(TreeNode node, List<Integer> result) {
            if (node == null) return;
            inorderRecursiveHelper(node.left, result);
            result.add(node.val);                         // visit
            inorderRecursiveHelper(node.right, result);
        }

        // =====================================================================
        // APPROACH 2: Iterative using an explicit Stack
        // ---------------------------------------------------------------------
        // Walk left as far as possible, pushing every node along the way.
        // Once you can't go left anymore, pop the top of the stack, visit it,
        // then move into its right subtree and repeat the "walk left" process.
        //
        // Time:  O(n)
        // Space: O(h) for the stack (same bound as recursion depth).
        // =====================================================================
        public List<Integer> inorderIterativeStack(TreeNode root) {
            List<Integer> result = new ArrayList<>();
            Deque<TreeNode> stack = new ArrayDeque<>();
            TreeNode curr = root;

            while (curr != null || !stack.isEmpty()) {
                while (curr != null) {
                    stack.push(curr);
                    curr = curr.left;
                }
                curr = stack.pop();
                result.add(curr.val);      // visit
                curr = curr.right;
            }

            return result;
        }

        // =====================================================================
        // APPROACH 3: Morris Traversal (O(1) extra space, no stack/recursion)
        // ---------------------------------------------------------------------
        // Uses "threading": temporarily links each node's in-order predecessor's
        // right pointer back to itself, so we can walk back up without a stack,
        // then undoes the link once traversed.
        //
        // Steps for each `curr` node:
        //   1. If curr has no left child -> visit curr, move to curr.right.
        //   2. Otherwise, find curr's in-order predecessor (the rightmost node
        //      in curr's left subtree):
        //        a. If predecessor.right is null -> thread predecessor.right =
        //           curr (so we can return here later), then move curr = curr.left
        //           WITHOUT visiting curr yet (its left subtree isn't done).
        //        b. If predecessor.right == curr -> we've already threaded and
        //           fully explored the left subtree; remove the thread
        //           (predecessor.right = null), visit curr NOW (left subtree is
        //           done), then move curr = curr.right.
        //
        // Time:  O(n) - each edge is traversed at most twice (once to create
        //         the thread, once to remove it).
        // Space: O(1) extra - no stack, no recursion.
        // =====================================================================
        public List<Integer> inorderMorris(TreeNode root) {
            List<Integer> result = new ArrayList<>();
            TreeNode curr = root;

            while (curr != null) {
                if (curr.left == null) {
                    result.add(curr.val);      // visit (no left subtree to defer)
                    curr = curr.right;
                } else {
                    TreeNode predecessor = curr.left;
                    while (predecessor.right != null && predecessor.right != curr) {
                        predecessor = predecessor.right;
                    }

                    if (predecessor.right == null) {
                        predecessor.right = curr;      // create thread
                        curr = curr.left;               // go left WITHOUT visiting yet
                    } else {
                        predecessor.right = null;      // remove thread (cleanup)
                        result.add(curr.val);          // visit AFTER left subtree done
                        curr = curr.right;
                    }
                }
            }

            return result;
        }
    }

    // Simple manual tests comparing all three approaches.
    public static void main(String[] args) {
        //       1
        //        \
        //         2
        //        /
        //       3
        TreeNode root = new TreeNode(1);
        root.right = new TreeNode(2);
        root.right.left = new TreeNode(3);

        Solution solution = new Solution();
        System.out.println("Default (stack)   : " + solution.inorderTraversal(root));      // [1, 3, 2]
        System.out.println("Recursive         : " + solution.inorderRecursive(root));       // [1, 3, 2]
        System.out.println("Iterative (stack) : " + solution.inorderIterativeStack(root));  // [1, 3, 2]
        System.out.println("Morris (O(1) sp.) : " + solution.inorderMorris(root));          // [1, 3, 2]
    }
}

