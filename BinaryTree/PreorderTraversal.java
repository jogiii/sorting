package BinaryTree;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/*
 * WHAT IS PRE-ORDER TRAVERSAL?
 * ----------------------------------------------------------------------
 * Pre-order traversal is a way of visiting every node in a binary tree
 * in the order: NODE -> LEFT subtree -> RIGHT subtree.
 *
 * In other words, at each node you:
 *   1. Visit (process) the current node FIRST.
 *   2. Then recursively traverse its entire left subtree (pre-order).
 *   3. Then recursively traverse its entire right subtree (pre-order).
 *
 * It's called "pre-order" because the node is visited BEFORE ("pre")
 * its children are explored - as opposed to in-order (visit is in the
 * middle, between left and right) or post-order (visit happens after,
 * "post", both children are done).
 *
 * Example tree:
 *
 *          1
 *         / \
 *        2   3
 *       / \
 *      4   5
 *
 * Pre-order visiting order: 1, 2, 4, 5, 3
 *   - Visit 1 first.
 *   - Go left into 2's subtree entirely (visit 2, then 4, then 5)
 *     before ever looking at 3.
 *   - Finally go right and visit 3.
 *
 * Common use cases:
 *   - Copying/cloning a tree (create the parent node before its children).
 *   - Serializing a tree to a string/file (so it can later be rebuilt by
 *     reading node values in the same top-down order).
 *   - Prefix notation for expression trees (e.g., "+ 1 2" for 1 + 2).
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
 */
class TreeNode {
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

class Solution {

    /**
     * DEFAULT entry point used by LeetCode-style signature.
     * Delegates to the iterative-stack approach (most commonly expected).
     */
    public List<Integer> preorderTraversal(TreeNode root) {
        return preorderIterativeStack(root);
    }

    // =====================================================================
    // APPROACH 1: Recursive (Node -> Left -> Right)
    // ---------------------------------------------------------------------
    // The most natural definition-following approach. Uses the call stack
    // implicitly for recursion.
    //
    // Time:  O(n) - visit every node once.
    // Space: O(h) - recursion stack depth = tree height (h). Worst case
    //         O(n) for a skewed tree, O(log n) for a balanced tree.
    // =====================================================================
    public List<Integer> preorderRecursive(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        preorderRecursiveHelper(root, result);
        return result;
    }

    private void preorderRecursiveHelper(TreeNode node, List<Integer> result) {
        if (node == null) return;
        result.add(node.val);                       // visit
        preorderRecursiveHelper(node.left, result);
        preorderRecursiveHelper(node.right, result);
    }

    // =====================================================================
    // APPROACH 2: Iterative using an explicit Stack
    // ---------------------------------------------------------------------
    // Push root, then repeatedly:
    //   - pop a node, visit it (add to result)
    //   - push its right child first, then left child
    //     (so left gets popped/visited before right)
    //
    // Time:  O(n)
    // Space: O(h) for the stack (same bound as recursion depth).
    // =====================================================================
    public List<Integer> preorderIterativeStack(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) return result;

        Deque<TreeNode> stack = new ArrayDeque<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            result.add(node.val);          // visit
            if (node.right != null) stack.push(node.right);
            if (node.left != null) stack.push(node.left);
        }

        return result;
    }

    // =====================================================================
    // APPROACH 3: Morris Traversal (O(1) extra space, no stack/recursion)
    // ---------------------------------------------------------------------
    // Uses "threading": temporarily links each node's predecessor's right
    // pointer back to itself, so we can walk back up without a stack, then
    // undoes the link once traversed.
    //
    // Steps for each `curr` node:
    //   1. If curr has no left child -> visit curr, move to curr.right.
    //   2. Otherwise, find curr's in-order predecessor (the rightmost node
    //      in curr's left subtree):
    //        a. If predecessor.right is null -> visit curr NOW (this is the
    //           key difference from Morris in-order: pre-order visits the
    //           node BEFORE diving into the left subtree). Thread
    //           predecessor.right = curr, then move curr = curr.left.
    //        b. If predecessor.right == curr -> we've already threaded and
    //           fully explored the left subtree; remove the thread
    //           (predecessor.right = null) and move curr = curr.right.
    //
    // Time:  O(n) - each edge is traversed at most twice (once to create
    //         the thread, once to remove it).
    // Space: O(1) extra - no stack, no recursion. (Output list itself is
    //         O(n) but that's unavoidable since we must return n values.)
    // =====================================================================
    public List<Integer> preorderMorris(TreeNode root) {
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
                    result.add(curr.val);          // visit BEFORE going left
                    predecessor.right = curr;      // create thread
                    curr = curr.left;
                } else {
                    predecessor.right = null;      // remove thread (cleanup)
                    curr = curr.right;
                }
            }
        }

        return result;
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
        System.out.println("Default (stack)   : " + solution.preorderTraversal(root));      // [1, 2, 3]
        System.out.println("Recursive         : " + solution.preorderRecursive(root));       // [1, 2, 3]
        System.out.println("Iterative (stack) : " + solution.preorderIterativeStack(root));  // [1, 2, 3]
        System.out.println("Morris (O(1) sp.) : " + solution.preorderMorris(root));          // [1, 2, 3]
    }
}

