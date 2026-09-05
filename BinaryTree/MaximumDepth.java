package BinaryTree;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

/*
 * PROBLEM: MAXIMUM DEPTH (HEIGHT) OF A BINARY TREE
 * ----------------------------------------------------------------------
 * Given the root of a binary tree, find its maximum depth - the number
 * of nodes along the LONGEST path from the root node down to the
 * farthest leaf node.
 *
 * Example:
 *
 *          3
 *         / \
 *        9   20
 *            / \
 *           15  7
 *
 *   Longest path: 3 -> 20 -> 15 (or 3 -> 20 -> 7)  = 3 nodes
 *   Maximum depth = 3
 *
 * A single-node tree has depth 1. An empty tree (null root) has depth 0.
 *
 * ------------------------------------------------------------------------
 * KEY INSIGHT
 * ------------------------------------------------------------------------
 * The depth of a tree rooted at `node` is:
 *
 *   depth(node) = 1 + max(depth(node.left), depth(node.right))
 *
 * ...with the base case depth(null) = 0.
 *
 * This recursive definition is naturally POST-ORDER in spirit: you need
 * to know the depth of BOTH children before you can compute the depth
 * of the current node.
 *
 * ------------------------------------------------------------------------
 * THREE WAYS TO COMPUTE IT
 * ------------------------------------------------------------------------
 * 1. Recursive DFS (post-order style) - the simplest, most natural fit.
 * 2. Iterative BFS (level-order, using a Queue) - count how many "levels"
 *    (rounds of the queue) it takes to drain the whole tree.
 * 3. Iterative DFS (using an explicit Stack of (node, depth) pairs) -
 *    track depth manually alongside each node, take the running max.
 *
 * Time Complexity:  O(n) for all approaches - every node visited once.
 * Space Complexity: O(h) for DFS (recursion or stack, h = tree height),
 *                    O(w) for BFS (w = max width of tree).
 */

/*
 * SIMPLE WORDS MEIN SAMJHO
 * ------------------------------------------------------------------------
 * Maximum depth matlab root se sabse door wale leaf tak kitne nodes
 * ka lamba raasta hai. Formula seedha hai: kisi bhi node ki depth =
 * 1 + (uske left aur right subtree ki depth mein se jo bada ho).
 *
 * Teen tarike se nikaal sakte ho: recursion (sabse simple), BFS/queue
 * se (level-by-level count karo, jitne levels utni hi depth), ya phir
 * stack use karke iterative DFS mein depth manually track karo.
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
 * outer MaximumDepth class (instead of top-level classes) purely to
 * avoid a "duplicate class" compile conflict with the other traversal
 * files in this same folder, which also define TreeNode/Solution.
 * Functionally they behave identically.
 */
public class MaximumDepth {

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
         * ("Maximum Depth of Binary Tree").
         * Delegates to the recursive DFS approach - the simplest, most
         * commonly expected solution.
         */
        public int maxDepth(TreeNode root) {
            return maxDepthRecursive(root);
        }

        // =====================================================================
        // APPROACH 1: Recursive DFS (post-order style)
        // ---------------------------------------------------------------------
        // depth(node) = 1 + max(depth(node.left), depth(node.right))
        // Base case: depth(null) = 0.
        //
        // Time:  O(n) - visit every node once.
        // Space: O(h) - recursion stack depth = tree height (h).
        // =====================================================================
        public int maxDepthRecursive(TreeNode root) {
            if (root == null) return 0;
            int leftDepth = maxDepthRecursive(root.left);
            int rightDepth = maxDepthRecursive(root.right);
            return 1 + Math.max(leftDepth, rightDepth);
        }

        // =====================================================================
        // APPROACH 2: Iterative BFS using a Queue (level-order, count levels)
        // ---------------------------------------------------------------------
        // Process the tree one full level at a time (snapshotting the queue
        // size before draining it, just like level-order traversal). Each
        // full level processed = one unit of depth.
        //
        // ALGORITHM (step by step):
        //   1. If root is null, depth is 0 - return immediately.
        //   2. Create an empty queue and enqueue the root. Initialize
        //      depth = 0.
        //   3. While the queue is NOT empty:
        //        a. Snapshot levelSize = queue.size() - this is exactly how
        //           many nodes belong to the CURRENT level (no more, no
        //           fewer), since they were all enqueued during the
        //           previous round.
        //        b. Loop exactly `levelSize` times:
        //             - Dequeue a node.
        //             - Enqueue its left child (if any) and right child
        //               (if any) - these belong to the NEXT level.
        //        c. After the inner loop finishes, one entire level has
        //           been fully processed -> increment depth by 1.
        //   4. Once the queue is empty, every level has been processed.
        //      Return depth - the total number of levels = maximum depth.
        //
        // WHY THIS WORKS:
        //   The "snapshot levelSize before draining" trick guarantees the
        //   inner loop touches ONLY nodes from the current level, never
        //   accidentally including a child that was just enqueued in the
        //   same round. Each pass through the outer while-loop corresponds
        //   to exactly one depth level, so counting outer-loop iterations
        //   directly gives the maximum depth.
        //
        // Time:  O(n) - each node enqueued/dequeued exactly once.
        // Space: O(w) for the queue (w = max width of tree).
        // =====================================================================
        public int maxDepthIterativeBFS(TreeNode root) {
            if (root == null) return 0;

            Queue<TreeNode> queue = new LinkedList<>();
            queue.offer(root);
            int depth = 0;

            while (!queue.isEmpty()) {
                int levelSize = queue.size(); // nodes belonging to this level
                for (int i = 0; i < levelSize; i++) {
                    TreeNode node = queue.poll();
                    if (node.left != null) queue.offer(node.left);
                    if (node.right != null) queue.offer(node.right);
                }
                depth++; // completed one full level
            }

            return depth;
        }

        // =====================================================================
        // APPROACH 3: Iterative DFS using an explicit Stack of (node, depth)
        // ---------------------------------------------------------------------
        // Push (root, 1) onto the stack. Pop a (node, depth) pair, track the
        // running maximum depth seen so far, then push each child with
        // depth+1. This mimics recursion manually with a stack instead of
        // the call stack.
        //
        // Time:  O(n) - each node pushed/popped exactly once.
        // Space: O(h) for the stack (h = tree height).
        // =====================================================================
        private static class NodeDepthPair {
            TreeNode node;
            int depth;
            NodeDepthPair(TreeNode node, int depth) {
                this.node = node;
                this.depth = depth;
            }
        }

        public int maxDepthIterativeDFS(TreeNode root) {
            if (root == null) return 0;

            Deque<NodeDepthPair> stack = new ArrayDeque<>();
            stack.push(new NodeDepthPair(root, 1));
            int maxDepth = 0;

            while (!stack.isEmpty()) {
                NodeDepthPair current = stack.pop();
                maxDepth = Math.max(maxDepth, current.depth);

                if (current.node.left != null) {
                    stack.push(new NodeDepthPair(current.node.left, current.depth + 1));
                }
                if (current.node.right != null) {
                    stack.push(new NodeDepthPair(current.node.right, current.depth + 1));
                }
            }

            return maxDepth;
        }
    }

    // Simple manual tests comparing all three approaches.
    public static void main(String[] args) {
        //          3
        //         / \
        //        9   20
        //            / \
        //           15  7
        TreeNode root = new TreeNode(3);
        root.left = new TreeNode(9);
        root.right = new TreeNode(20);
        root.right.left = new TreeNode(15);
        root.right.right = new TreeNode(7);

        Solution solution = new Solution();
        System.out.println("Default (recursive) : " + solution.maxDepth(root));             // 3
        System.out.println("Recursive DFS        : " + solution.maxDepthRecursive(root));    // 3
        System.out.println("Iterative BFS        : " + solution.maxDepthIterativeBFS(root)); // 3
        System.out.println("Iterative DFS        : " + solution.maxDepthIterativeDFS(root)); // 3
    }
}

