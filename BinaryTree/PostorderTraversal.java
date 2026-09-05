package BinaryTree;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/*
 * WHAT IS POST-ORDER TRAVERSAL?
 * ----------------------------------------------------------------------
 * Post-order traversal is a way of visiting every node in a binary tree
 * in the order: LEFT subtree -> RIGHT subtree -> NODE.
 *
 * In other words, at each node you:
 *   1. Recursively traverse its entire left subtree (post-order) FIRST.
 *   2. Then recursively traverse its entire right subtree (post-order).
 *   3. Then visit (process) the current node LAST.
 *
 * It's called "post-order" because the node is visited AFTER ("post")
 * both of its children have been fully explored - as opposed to
 * pre-order (visit happens first, before children) or in-order (visit
 * is sandwiched between left and right).
 *
 * Example tree:
 *
 *          1
 *         / \
 *        2   3
 *       / \
 *      4   5
 *
 * Post-order visiting order: 4, 5, 2, 3, 1
 *   - Go left into 2's subtree entirely first: visit 4, then 5,
 *     then finally 2 itself (since both its children are now done).
 *   - Go right into 3's subtree: visit 3 (no children).
 *   - Finally visit 1, since both its subtrees are now fully done.
 *
 * Common use cases:
 *   - Deleting/freeing a tree (must delete children before the parent,
 *     otherwise you'd lose the reference to them).
 *   - Evaluating postfix ("Reverse Polish") expression trees.
 *   - Computing properties that depend on children's results first,
 *     e.g., height of a tree, or the sum of a subtree.
 *
 * Time Complexity:  O(n) - every node is visited exactly once.
 * Space Complexity: depends on approach - O(h) for recursive/stack-based
 *                    approaches (h = tree height), O(1) extra for Morris
 *                    traversal (see approach 3 below).
 */

/*
 * SIMPLE WORDS MEIN SAMJHO
 * ------------------------------------------------------------------------
 * Post-order traversal mein current node ko SABSE AAKHRI mein visit
 * karte hain - pehle left subtree poora, fir right subtree poora, aur
 * tabhi jaake current node ko visit karo. "Left, right, node" order.
 *
 * Yeh tab kaam aata hai jab bacchon (children) ka result pehle chahiye
 * hota hai - jaise tree delete karna (parent delete karne se pehle
 * children delete karo), ya subtree ki height/sum nikaalna.
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
 * outer PostorderTraversal class (instead of top-level classes) purely
 * to avoid a "duplicate class" compile conflict with PreorderTraversal.java,
 * which also defines its own top-level TreeNode/Solution in this same
 * folder/default-package. Functionally they behave identically.
 */
public class PostorderTraversal {

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
        public List<Integer> postorderTraversal(TreeNode root) {
            return postorderIterativeStack(root);
        }

        // =====================================================================
        // APPROACH 1: Recursive (Left -> Right -> Node)
        // ---------------------------------------------------------------------
        // The most natural definition-following approach. Uses the call stack
        // implicitly for recursion.
        //
        // Time:  O(n) - visit every node once.
        // Space: O(h) - recursion stack depth = tree height (h). Worst case
        //         O(n) for a skewed tree, O(log n) for a balanced tree.
        // =====================================================================
        public List<Integer> postorderRecursive(TreeNode root) {
            List<Integer> result = new ArrayList<>();
            postorderRecursiveHelper(root, result);
            return result;
        }

        private void postorderRecursiveHelper(TreeNode node, List<Integer> result) {
            if (node == null) return;
            postorderRecursiveHelper(node.left, result);
            postorderRecursiveHelper(node.right, result);
            result.add(node.val);                        // visit
        }

        // =====================================================================
        // APPROACH 2: Iterative using an explicit Stack
        // ---------------------------------------------------------------------
        // Trick: post-order (Left, Right, Node) is the REVERSE of
        // (Node, Right, Left) - which is just pre-order with left/right swapped.
        //
        // So we do a "modified pre-order" that pushes left before right
        // (instead of right before left), prepend each visited value to the
        // front of the result (or append then reverse at the end), and get
        // post-order order for free.
        //
        // Time:  O(n)
        // Space: O(h) for the stack (same bound as recursion depth).
        // =====================================================================
        public List<Integer> postorderIterativeStack(TreeNode root) {
            List<Integer> result = new ArrayList<>();
            if (root == null) return result;

            Deque<TreeNode> stack = new ArrayDeque<>();
            stack.push(root);

            while (!stack.isEmpty()) {
                TreeNode node = stack.pop();
                result.add(node.val);                     // visit (Node, Right, Left order)
                if (node.left != null) stack.push(node.left);
                if (node.right != null) stack.push(node.right);
            }

            Collections.reverse(result);                  // reverse -> (Left, Right, Node)
            return result;
        }

        // =====================================================================
        // APPROACH 3: Morris Traversal (O(1) extra space, no stack/recursion)
        // ---------------------------------------------------------------------
        // Post-order Morris is the trickiest of the three, because unlike
        // pre-order/in-order, a node isn't visited "on the way through" - it's
        // visited only after BOTH children are done. The standard technique:
        //
        //   1. Add a dummy node as the overall root's parent, with
        //      dummy.left = actual root. This lets us treat the whole tree
        //      uniformly (every real node has a "left spine" to walk).
        //   2. Walk using the same left-threading idea as pre-order/in-order
        //      Morris: for each curr, find its left subtree's rightmost node
        //      (the predecessor) and thread predecessor.right = curr.
        //   3. The key difference: when we detect we've already threaded
        //      (predecessor.right == curr), that means the ENTIRE left
        //      subtree of curr has now been walked - so we reverse-visit the
        //      right edge of curr.left's subtree (i.e., curr.left's right
        //      spine) between curr.left and predecessor, add those values,
        //      then un-reverse the pointers back (restoring the tree),
        //      before removing the thread and moving to curr.right.
        //
        // Time:  O(n) - each edge is traversed a constant number of times.
        // Space: O(1) extra - no stack, no recursion (dummy node aside).
        // =====================================================================
        public List<Integer> postorderMorris(TreeNode root) {
            List<Integer> result = new ArrayList<>();
            if (root == null) return result;

            TreeNode dummy = new TreeNode(0);
            dummy.left = root;
            TreeNode curr = dummy;

            while (curr != null) {
                if (curr.left == null) {
                    curr = curr.right;
                } else {
                    TreeNode predecessor = curr.left;
                    while (predecessor.right != null && predecessor.right != curr) {
                        predecessor = predecessor.right;
                    }

                    if (predecessor.right == null) {
                        predecessor.right = curr;      // create thread
                        curr = curr.left;
                    } else {
                        predecessor.right = null;      // remove thread (cleanup)
                        addReversed(curr.left, predecessor, result); // visit curr.left's right-spine
                        curr = curr.right;
                    }
                }
            }

            return result;
        }

        // Collects values from `from` down to `to` along right-child pointers,
        // then adds them to `result` in REVERSED order (i.e., to -> from),
        // which produces the correct post-order (Left, Right, Node) sequence
        // for the segment of the tree being closed off.
        private void addReversed(TreeNode from, TreeNode to, List<Integer> result) {
            List<Integer> segment = new ArrayList<>();
            TreeNode node = from;
            while (node != to) {
                segment.add(node.val);
                node = node.right;
            }
            segment.add(to.val);
            Collections.reverse(segment);
            result.addAll(segment);
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
        System.out.println("Default (stack)   : " + solution.postorderTraversal(root));      // [3, 2, 1]
        System.out.println("Recursive         : " + solution.postorderRecursive(root));       // [3, 2, 1]
        System.out.println("Iterative (stack) : " + solution.postorderIterativeStack(root));  // [3, 2, 1]
        System.out.println("Morris (O(1) sp.) : " + solution.postorderMorris(root));          // [3, 2, 1]
    }
}

