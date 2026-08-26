package BinaryTree;

/*
 * PROBLEM: DIAMETER OF A BINARY TREE
 * ----------------------------------------------------------------------
 * Given the root of a binary tree, return the length of the DIAMETER of
 * the tree - the length (in number of EDGES) of the LONGEST path between
 * ANY two nodes in the tree. This path MAY OR MAY NOT pass through the
 * root.
 *
 * Example:
 *
 *          1
 *         / \
 *        2   3
 *       / \
 *      4   5
 *
 *   Longest path: 4 -> 2 -> 5  (length = 2 edges)
 *   OR:           4 -> 2 -> 1 -> 3  (length = 3 edges)  <-- this is longer!
 *
 *   Diameter = 3
 *
 * IMPORTANT: Diameter is measured in EDGES, not nodes. A path visiting
 * k nodes has (k - 1) edges.
 *
 * ------------------------------------------------------------------------
 * KEY INSIGHT
 * ------------------------------------------------------------------------
 * For ANY node, the longest path that passes THROUGH that node (as the
 * "peak"/highest point of the path) is:
 *
 *   longestPathThroughNode = height(node.left) + height(node.right)
 *
 * (height here = number of edges from node down to its deepest leaf;
 * height(null) = -1, height(leaf) = 0).
 *
 * The overall diameter of the tree is the MAXIMUM of this quantity over
 * ALL nodes - because the true longest path's "peak" (highest node on
 * the path) could be ANY node in the tree, not just the root.
 *
 * ------------------------------------------------------------------------
 * APPROACH: Post-order DFS, computing height and diameter simultaneously
 * ------------------------------------------------------------------------
 * We do a single post-order traversal. At each node, after recursively
 * computing the height of the left and right subtrees, we:
 *   1. Update a running "best diameter found so far" using
 *      leftHeight + rightHeight (the longest path peaking at this node).
 *   2. Return 1 + max(leftHeight, rightHeight) as THIS node's height, to
 *      be used by its parent.
 *
 * This way we compute both height AND diameter in one single O(n) pass,
 * instead of naively recomputing height at every node (which would be
 * O(n^2) in the worst case).
 *
 * ------------------------------------------------------------------------
 * ALGORITHM (step by step)
 * ------------------------------------------------------------------------
 * 1. Keep a class-level (or captured) variable `diameter`, initialized
 *    to 0 - this tracks the best diameter found so far.
 * 2. Define a helper `height(node)`:
 *      a. Base case: if node == null, return -1 (so a leaf, with two
 *         null children, computes height = 1 + max(-1, -1) = 0).
 *      b. Recursively compute leftHeight = height(node.left).
 *      c. Recursively compute rightHeight = height(node.right).
 *      d. Update diameter = max(diameter, leftHeight + rightHeight + 2)
 *         (+2 converts from height-below-node terms back into an edge
 *         count for the path THROUGH node; see diagram below).
 *      e. Return 1 + max(leftHeight, rightHeight) as node's own height.
 * 3. Call height(root) once to kick off the recursion; the answer is
 *    the final value of `diameter`.
 *
 * ------------------------------------------------------------------------
 * DIAGRAM WALKTHROUGH
 * ------------------------------------------------------------------------
 *          1
 *         / \
 *        2   3
 *       / \
 *      4   5
 *
 *   height(4) = 0, height(5) = 0   (leaves)
 *   height(2): leftHeight=height(4)=0, rightHeight=height(5)=0
 *              diameter candidate = 0 + 0 + 2 = 2  -> diameter = 2
 *              returns 1 + max(0,0) = 1
 *   height(3) = 0   (leaf)
 *   height(1): leftHeight=height(2)=1, rightHeight=height(3)=0
 *              diameter candidate = 1 + 0 + 2 = 3  -> diameter = 3
 *              returns 1 + max(1,0) = 2
 *
 *   Final diameter = 3   (path 4 -> 2 -> 1 -> 3, 3 edges) ✔
 *
 * Time Complexity:  O(n) - each node visited exactly once.
 * Space Complexity: O(h) - recursion stack depth = tree height (h).
 */

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

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
 * outer DiameterOfBinaryTree class (instead of top-level classes) purely
 * to avoid a "duplicate class" compile conflict with the other traversal
 * files in this same folder, which also define TreeNode/Solution.
 * Functionally they behave identically.
 */
public class DiameterOfBinaryTree {

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

        // Running best diameter found so far, updated during the DFS.
        private int diameter;

        // =====================================================================
        // APPROACH 1: Recursive post-order DFS (height + diameter in one pass)
        // ---------------------------------------------------------------------
        // DEFAULT entry point used by LeetCode-style signature
        // ("Diameter of Binary Tree").
        //
        // ------------------------------------------------------------------
        // ALGORITHM (step by step)
        // ------------------------------------------------------------------
        // 1. Reset the shared `diameter` field to 0 (tracks the best answer
        //    found so far across the whole traversal).
        // 2. Call the recursive helper `height(root)`. Inside `height(node)`:
        //      a. Base case: if `node == null`, return -1 (an "empty" subtree
        //         has height -1, so that a leaf computes 1 + max(-1,-1) = 0).
        //      b. Recursively compute `leftHeight = height(node.left)` -
        //         this fully finishes exploring the ENTIRE left subtree
        //         first (post-order: left before using its result).
        //      c. Recursively compute `rightHeight = height(node.right)` -
        //         same idea for the right subtree.
        //      d. Now that both children's heights are known, update:
        //           diameter = max(diameter, leftHeight + rightHeight + 2)
        //         This represents the longest path that has `node` as its
        //         highest point: go down-left (leftHeight+1 edges), then
        //         down-right (rightHeight+1 edges).
        //      e. Return `1 + max(leftHeight, rightHeight)` - the height of
        //         THIS node's own subtree, so `node`'s parent can use it in
        //         step (d) for itself.
        // 3. Once `height(root)` returns (whole tree visited), `diameter`
        //    holds the maximum path length seen across ALL nodes - return it.
        //
        // Why post-order? Because computing step (d) for `node` REQUIRES
        // already knowing both children's heights - you can't know how long
        // a path through `node` is until you know how deep each side goes.
        // That dependency (children fully resolved before the parent uses
        // their results) is the definition of post-order.
        //
        // Time:  O(n) - every node visited exactly once.
        // Space: O(h) - recursion stack depth = tree height (h).
        // =====================================================================
        public int diameterOfBinaryTree(TreeNode root) {
            diameter = 0;
            height(root);
            return diameter;
        }

        /**
         * Returns the height (in edges) of the subtree rooted at `node`,
         * while also updating `diameter` with the longest path that peaks
         * at `node` (leftHeight + rightHeight + 2, converting the -1-based
         * height convention back into an edge count).
         *
         * Base case: height(null) = -1, so a leaf node computes
         * 1 + max(-1, -1) = 0, which is correct (a leaf is 0 edges tall).
         */
        private int height(TreeNode node) {
            if (node == null) return -1;

            int leftHeight = height(node.left);
            int rightHeight = height(node.right);

            // Longest path peaking at `node`, in edges:
            // (leftHeight + 1) edges down-left + (rightHeight + 1) edges down-right.
            diameter = Math.max(diameter, leftHeight + rightHeight + 2);

            return 1 + Math.max(leftHeight, rightHeight);
        }

        // =====================================================================
        // APPROACH 2: Iterative post-order DFS using an explicit Stack + Map
        // ---------------------------------------------------------------------
        // Simulates the recursive post-order approach above WITHOUT recursion,
        // using the classic "one stack, one visited-map" iterative post-order
        // pattern. The map doubles as BOTH a "have I already computed this
        // node's height?" check AND storage for that computed height (so
        // parents can look up their children's heights in O(1)).
        //
        // NOTE ON CONVENTION: this version stores height using a "node-count"
        // convention (depth(null) = 0, depth(leaf) = 1) instead of the
        // recursive helper's "edge-count" convention (height(null) = -1,
        // height(leaf) = 0). That's why the diameter update here is simply
        // `leftDepth + rightDepth` (no "+2") - the two conventions are just
        // offset by 1 per side, and the math works out to the same result.
        //
        // ------------------------------------------------------------------
        // ALGORITHM (step by step)
        // ------------------------------------------------------------------
        // 1. Create an empty `map` (TreeNode -> computed height) and an empty
        //    `stack`. Push `root` onto the stack (if non-null). `diameter = 0`.
        // 2. While the stack is non-empty:
        //      a. PEEK (don't pop) the top node.
        //      b. If it has a left child that hasn't been processed yet
        //         (not in `map`), push the left child and continue the loop
        //         (go process it first).
        //      c. Else if it has a right child that hasn't been processed
        //         yet, push the right child and continue (go process it).
        //      d. Otherwise, BOTH children are already fully processed (or
        //         don't exist) - it's safe to finalize this node now:
        //           i.   POP it off the stack.
        //           ii.  Look up leftDepth/rightDepth from `map`
        //                (missing/null child -> defaults to 0).
        //           iii. Update diameter = max(diameter, leftDepth + rightDepth).
        //           iv.  Store map[node] = 1 + max(leftDepth, rightDepth) so
        //                this node's parent can use it later.
        // 3. Return `diameter` once the stack is empty (whole tree processed).
        //
        // Why "peek before pop"? Because a node must stay on the stack while
        // we go check/push its children - we only truly remove ("pop") it
        // once we've confirmed both children are already done, which is
        // exactly what makes this a valid POST-order simulation (children
        // finalized strictly before their parent).
        //
        // Time:  O(n) - each node pushed/popped and map-inserted exactly once.
        // Space: O(n) - the map stores an entry for every node (unlike the
        //         O(h) recursive version, which only needs call-stack space).
        // =====================================================================
        public int diameterOfBinaryTreeV1(TreeNode root) {
            Map<TreeNode, Integer> map = new HashMap<>();
            Stack<TreeNode> stack = new Stack<>();
            int diameter = 0;
            if(root != null){
                stack.push(root);
            }

            while(!stack.isEmpty()){
                TreeNode node = stack.peek();

                if(node.left != null && !map.containsKey(node.left)){
                    stack.push(node.left);
                } else if(node.right != null && !map.containsKey(node.right)){
                    stack.push(node.right);
                }else {
                    stack.pop();
                    int leftDepth = map.getOrDefault(node.left, 0);
                    int rightDepth = map.getOrDefault(node.right, 0);

                    map.put(node,1+Math.max(leftDepth,rightDepth));

                    diameter = Math.max(diameter, leftDepth + rightDepth);
                }
            }
            return diameter;
        }
    }




    // Simple manual test.
    public static void main(String[] args) {
        //          1
        //         / \
        //        2   3
        //       / \
        //      4   5
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(4);
        root.left.right = new TreeNode(5);

        Solution solution = new Solution();
        System.out.println("Recursive (post-order)      : " + solution.diameterOfBinaryTree(root));    // 3
        System.out.println("Iterative (stack + map)     : " + solution.diameterOfBinaryTreeV1(root));   // 3
    }
}

