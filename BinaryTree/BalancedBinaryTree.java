package BinaryTree;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/*
 * PROBLEM: BALANCED BINARY TREE
 * ----------------------------------------------------------------------
 * Given the root of a binary tree, determine if it is HEIGHT-BALANCED.
 *
 * A binary tree is height-balanced if, for EVERY node in the tree, the
 * height difference between its left subtree and right subtree is AT
 * MOST 1 (i.e. |height(left) - height(right)| <= 1) - and this must hold
 * recursively for every node, not just the root.
 *
 * Example 1 (balanced):
 *
 *          1
 *         / \
 *        2   3
 *       / \
 *      4   5
 *
 *   Every node's left/right subtree heights differ by at most 1 -> true.
 *
 * Example 2 (NOT balanced):
 *
 *          1
 *         /
 *        2
 *       /
 *      3
 *     /
 *    4
 *
 *   At node 1: left subtree height = 3, right subtree height = -1
 *   (no right child). Difference = 4 > 1 -> NOT balanced.
 *
 * ------------------------------------------------------------------------
 * WHY DOES THIS MATTER?
 * ------------------------------------------------------------------------
 * Height-balanced trees guarantee O(log n) height, which is what makes
 * operations like search/insert/delete on balanced BSTs (AVL trees,
 * Red-Black trees, etc.) run in O(log n) instead of degrading to O(n)
 * on a skewed/"linked-list-like" tree.
 *
 * ------------------------------------------------------------------------
 * APPROACH 1 (NAIVE): Top-down - recompute height at every node
 * ------------------------------------------------------------------------
 * For each node, check if |height(left) - height(right)| <= 1, AND
 * recursively check that both the left and right subtrees are ALSO
 * balanced. This recomputes height() from scratch at every single node,
 * leading to redundant work.
 *
 * Time Complexity:  O(n^2) worst case - height() is O(n) and gets called
 *                    once per node in the worst case (skewed tree).
 * Space Complexity: O(h) recursion stack.
 *
 * ------------------------------------------------------------------------
 * APPROACH 2 (OPTIMIZED): Bottom-up post-order, short-circuit on failure
 * ------------------------------------------------------------------------
 * Instead of recomputing height repeatedly, compute height and check
 * balance SIMULTANEOUSLY in a single post-order pass. As soon as any
 * subtree is found to be unbalanced, propagate a sentinel value (-2)
 * all the way up to signal "already broken, stop checking" - avoiding
 * unnecessary work once we know the answer is false.
 *
 * ------------------------------------------------------------------------
 * ALGORITHM (step by step) for Approach 2
 * ------------------------------------------------------------------------
 * 1. Define a helper `checkHeight(node)` that returns:
 *      - the height of the subtree rooted at `node` (using the
 *        height(null) = -1, height(leaf) = 0 convention), IF the
 *        subtree is balanced, OR
 *      - the sentinel value -2 if the subtree is (or contains) an
 *        unbalanced node.
 * 2. Base case: if `node == null`, return -1 (empty subtree is trivially
 *    balanced with height -1).
 * 3. Recursively compute `leftHeight = checkHeight(node.left)`.
 *      - If it's already -2 (unbalanced detected below), immediately
 *        return -2 (short-circuit - no need to check the right side).
 * 4. Recursively compute `rightHeight = checkHeight(node.right)`.
 *      - Same short-circuit: if -2, return -2 immediately.
 * 5. Now check THIS node: if |leftHeight - rightHeight| > 1, this node
 *    itself is unbalanced -> return -2.
 * 6. Otherwise, return `1 + max(leftHeight, rightHeight)` - this node's
 *    own height, to be used by its parent.
 * 7. In the main `isBalanced(root)` method, simply call
 *    `checkHeight(root) != -2` - if the sentinel ever bubbled all the
 *    way up, the tree is unbalanced; otherwise it's balanced.
 *
 * Time Complexity:  O(n) - each node visited exactly once, no redundant
 *                    height recomputation.
 * Space Complexity: O(h) - recursion stack depth = tree height (h).
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
 * outer BalancedBinaryTree class (instead of top-level classes) purely
 * to avoid a "duplicate class" compile conflict with the other traversal
 * files in this same folder, which also define TreeNode/Solution.
 * Functionally they behave identically.
 */
public class BalancedBinaryTree {

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
         * DEFAULT entry point used by LeetCode-style signature
         * ("Balanced Binary Tree"). Delegates to the optimized O(n)
         * bottom-up approach.
         */
        public boolean isBalanced(TreeNode root) {
            return isBalancedOptimized(root);
        }

        // =====================================================================
        // APPROACH 1: Naive top-down (recompute height at every node)
        // ---------------------------------------------------------------------
        // Time:  O(n^2) worst case - height() is O(n), called per node.
        // Space: O(h) recursion stack.
        // =====================================================================
        public boolean isBalancedNaive(TreeNode root) {
            if (root == null) return true;

            int leftHeight = height(root.left);
            int rightHeight = height(root.right);

            if (Math.abs(leftHeight - rightHeight) > 1) return false;

            return isBalancedNaive(root.left) && isBalancedNaive(root.right);
        }

        private int height(TreeNode node) {
            if (node == null) return -1;
            return 1 + Math.max(height(node.left), height(node.right));
        }

        // =====================================================================
        // APPROACH 2: Optimized bottom-up post-order with short-circuiting
        // ---------------------------------------------------------------------
        // See the ALGORITHM write-up above the class-level comment block for
        // the full step-by-step explanation. In short: compute height and
        // check balance in ONE pass, propagating a -2 sentinel upward the
        // instant any imbalance is found so we can stop early.
        //
        // Time:  O(n) - each node visited exactly once.
        // Space: O(h) - recursion stack depth = tree height (h).
        // =====================================================================
        private static final int UNBALANCED = -2;

        public boolean isBalancedOptimized(TreeNode root) {
            return checkHeight(root) != UNBALANCED;
        }

        private int checkHeight(TreeNode node) {
            if (node == null) return -1;

            int leftHeight = checkHeight(node.left);
            if (leftHeight == UNBALANCED) return UNBALANCED; // short-circuit

            int rightHeight = checkHeight(node.right);
            if (rightHeight == UNBALANCED) return UNBALANCED; // short-circuit

            if (Math.abs(leftHeight - rightHeight) > 1) return UNBALANCED;

            return 1 + Math.max(leftHeight, rightHeight);
        }

        // =====================================================================
        // APPROACH 3: Iterative post-order DFS using an explicit Stack + Map
        // ---------------------------------------------------------------------
        // Same "one stack, one visited-map" iterative post-order pattern used
        // for the iterative diameter solution (diameterOfBinaryTreeV1) - but
        // here the map stores each node's height, and we check the balance
        // condition (|leftDepth - rightDepth| <= 1) the moment both children
        // of a node are finalized. As soon as ANY node fails that check, we
        // short-circuit (stop the loop immediately) instead of continuing to
        // process the rest of the tree.
        //
        // ------------------------------------------------------------------
        // ALGORITHM (step by step)
        // ------------------------------------------------------------------
        // 1. Create an empty `map` (TreeNode -> computed height) and an empty
        //    `stack`. Push `root` onto the stack (if non-null).
        //    Initialize `balanced = true`.
        // 2. Loop `while (!stack.isEmpty() && balanced)` - the `&& balanced`
        //    is the short-circuit condition: the instant `balanced` becomes
        //    false, the loop stops on its very next check.
        // 3. PEEK (don't pop) the top node.
        //      a. If it has a left child not yet in `map`, push it and
        //         continue (go finalize the left child first).
        //      b. Else if it has a right child not yet in `map`, push it
        //         and continue (go finalize the right child first).
        //      c. Otherwise, both children are already finalized (or don't
        //         exist) - finalize THIS node now:
        //           i.   POP it off the stack.
        //           ii.  Look up leftDepth/rightDepth from `map` (missing
        //                child -> defaults to 0, meaning "no subtree").
        //           iii. If |leftDepth - rightDepth| > 1 -> set
        //                `balanced = false` and `break` immediately (no
        //                need to compute/store this node's height, the
        //                answer is already determined).
        //           iv.  Otherwise, store `map[node] = 1 + max(leftDepth,
        //                rightDepth)` so this node's parent can use it.
        // 4. Return `balanced` once the loop ends (either the stack emptied
        //    - fully balanced - or we broke out early due to an imbalance).
        //
        // Time:  O(n) worst case (fully balanced tree needs a full pass),
        //         but can terminate MUCH earlier if an imbalance is found
        //         near the start of traversal order (best case much less
        //         than O(n)).
        // Space: O(n) - the map stores an entry for every processed node
        //         (unlike the O(h) recursive approaches, which only need
        //         call-stack space).
        // =====================================================================
        public boolean isBalancedIterativeV1(TreeNode root) {
            Map<TreeNode, Integer> map = new HashMap<>();
            Stack<TreeNode> stack = new Stack<>();
            boolean balanced = true;

            if (root != null) {
                stack.push(root);
            }

            while (!stack.isEmpty() && balanced) {
                TreeNode node = stack.peek();

                if (node.left != null && !map.containsKey(node.left)) {
                    stack.push(node.left);
                } else if (node.right != null && !map.containsKey(node.right)) {
                    stack.push(node.right);
                } else {
                    stack.pop();
                    int leftDepth = map.getOrDefault(node.left, 0);
                    int rightDepth = map.getOrDefault(node.right, 0);

                    if (Math.abs(leftDepth - rightDepth) > 1) {
                        balanced = false; // short-circuit
                        break;
                    }

                    map.put(node, 1 + Math.max(leftDepth, rightDepth));
                }
            }

            return balanced;
        }
    }

    // Simple manual tests comparing both approaches.
    public static void main(String[] args) {
        //        Balanced tree:
        //          1
        //         / \
        //        2   3
        //       / \
        //      4   5
        TreeNode balanced = new TreeNode(1);
        balanced.left = new TreeNode(2);
        balanced.right = new TreeNode(3);
        balanced.left.left = new TreeNode(4);
        balanced.left.right = new TreeNode(5);

        //        Unbalanced tree (skewed left):
        //          1
        //         /
        //        2
        //       /
        //      3
        //     /
        //    4
        TreeNode unbalanced = new TreeNode(1);
        unbalanced.left = new TreeNode(2);
        unbalanced.left.left = new TreeNode(3);
        unbalanced.left.left.left = new TreeNode(4);

        Solution solution = new Solution();
        System.out.println("Balanced tree   - default    : " + solution.isBalanced(balanced));            // true
        System.out.println("Balanced tree   - naive      : " + solution.isBalancedNaive(balanced));       // true
        System.out.println("Balanced tree   - optimized  : " + solution.isBalancedOptimized(balanced));   // true
        System.out.println("Balanced tree   - iterative  : " + solution.isBalancedIterativeV1(balanced)); // true

        System.out.println("Unbalanced tree - default    : " + solution.isBalanced(unbalanced));             // false
        System.out.println("Unbalanced tree - naive      : " + solution.isBalancedNaive(unbalanced));        // false
        System.out.println("Unbalanced tree - optimized  : " + solution.isBalancedOptimized(unbalanced));    // false
        System.out.println("Unbalanced tree - iterative  : " + solution.isBalancedIterativeV1(unbalanced));  // false
    }
}

