package BinaryTree;

import java.util.ArrayDeque;
import java.util.Deque;

/*
 * PROBLEM: SUBTREE OF ANOTHER TREE
 * ----------------------------------------------------------------------
 * Given the roots of two binary trees `root` and `subRoot`, determine if
 * `subRoot` exists as an EXACT subtree somewhere within `root`. A subtree
 * of `root` is any node in `root` PLUS all of that node's descendants.
 * The tree `subRoot` must match that subtree EXACTLY (same structure,
 * same values) - this is exactly the "Same Tree" check applied at every
 * possible starting node in `root`.
 *
 * Example 1 (subRoot IS a subtree of root):
 *
 *   root:        3              subRoot:    4
 *               / \                        / \
 *              4   5                      1   2
 *             / \
 *            1   2
 *
 *   -> true (the subtree rooted at node 4 in `root` exactly matches subRoot)
 *
 * Example 2 (subRoot is NOT a subtree - extra node breaks exact match):
 *
 *   root:        3              subRoot:    4
 *               / \                        / \
 *              4   5                      1   2
 *             / \
 *            1   2
 *           /
 *          0
 *
 *   -> false (root's node-4 subtree has an extra node "0" that subRoot
 *      doesn't have, so it's no longer an EXACT match)
 *
 * ------------------------------------------------------------------------
 * SIMPLE WORDS MEIN SAMJHO
 * ------------------------------------------------------------------------
 * `subRoot` tab `root` ka subtree kehlaayega jab `root` ke andar KAHIN
 * bhi koi aisa node mil jaaye jiska poora subtree bilkul `subRoot` jaisa
 * hi ho (SameTree.java wala check use karke).
 *
 * Toh bas `root` ke har node pe jaake "Same Tree" check chalao. Kahin
 * bhi match mil gaya toh answer true, warna poora tree ghoomne ke baad
 * bhi na mile toh false.
 *
 * ------------------------------------------------------------------------
 * KEY INSIGHT
 * ------------------------------------------------------------------------
 * `subRoot` is a subtree of `root` if there EXISTS at least one node
 * `node` in `root` such that the subtree rooted at `node` is THE SAME
 * TREE as `subRoot` (using the exact "Same Tree" comparison from
 * SameTree.java).
 *
 * So the algorithm is simply:
 *   1. Walk every node in `root` (any traversal order works - DFS here).
 *   2. At each node, run isSameTree(node, subRoot).
 *   3. If ANY node returns true, the answer is true.
 *   4. If we exhaust all nodes without a match, the answer is false.
 *
 * This reuses the "Same Tree" helper as a building block - a great
 * example of composing simpler tree algorithms into a more complex one.
 *
 * ------------------------------------------------------------------------
 * ALGORITHM (step by step) - Recursive DFS
 * ------------------------------------------------------------------------
 * 1. Base case: if `root == null`, there's nothing left to check.
 *      - If `subRoot` is also null, technically it "matches" an empty
 *        tree, but per problem convention subRoot is non-null, so simply
 *        return false (ran out of root nodes without finding a match).
 * 2. Check if the subtree rooted at THIS `root` node is the same tree as
 *    `subRoot` (using the `isSameTree` helper). If yes -> return true.
 * 3. Otherwise, recursively check root.left and root.right the same way
 *    - return true if EITHER of them contains subRoot as a subtree.
 *
 * ------------------------------------------------------------------------
 * DIAGRAM WALKTHROUGH
 * ------------------------------------------------------------------------
 *   root:        3              subRoot:    4
 *               / \                        / \
 *              4   5                      1   2
 *             / \
 *            1   2
 *
 *   isSubtree(root=3, subRoot=4):
 *     isSameTree(3, 4)? values differ (3 != 4) -> false
 *     recurse left:  isSubtree(root=4, subRoot=4)
 *         isSameTree(4, 4)? values match, then recursively compare
 *           left(1,1) match, right(2,2) match -> isSameTree = true!
 *       -> isSubtree returns true immediately (short-circuits)
 *   Final answer: true
 *
 * Time Complexity:  O(n * m) worst case, where n = number of nodes in
 *                    `root`, m = number of nodes in `subRoot`. For each
 *                    of the n nodes in root, we may run an O(m) same-tree
 *                    check.
 * Space Complexity: O(h) - recursion stack depth = height of `root` (h).
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
 * outer SubtreeOfAnotherTree class (instead of top-level classes) purely
 * to avoid a "duplicate class" compile conflict with the other traversal
 * files in this same folder, which also define TreeNode/Solution.
 * Functionally they behave identically.
 */
public class SubtreeOfAnotherTree {

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
         * ("Subtree of Another Tree"). Delegates to the recursive DFS
         * approach - the simplest, most commonly expected solution.
         */
        public boolean isSubtree(TreeNode root, TreeNode subRoot) {
            return isSubtreeRecursive(root, subRoot);
        }

        // =====================================================================
        // APPROACH 1: Recursive DFS - check every node as a potential root
        // ---------------------------------------------------------------------
        // See the ALGORITHM write-up above for the full step-by-step
        // explanation. In short: at each node of `root`, check if the
        // subtree starting there is the SAME TREE as `subRoot`; if not,
        // recurse into left and right children.
        //
        // Time:  O(n * m) worst case (n = |root|, m = |subRoot|).
        // Space: O(h) - recursion stack depth = height of `root`.
        // =====================================================================
        public boolean isSubtreeRecursive(TreeNode root, TreeNode subRoot) {
            if (root == null) return false;
            if (isSameTree(root, subRoot)) return true;
            return isSubtreeRecursive(root.left, subRoot)
                    || isSubtreeRecursive(root.right, subRoot);
        }

        /**
         * Same "Same Tree" check as SameTree.java's isSameTreeRecursive -
         * duplicated here (rather than importing) so this file is fully
         * self-contained.
         */
        private boolean isSameTree(TreeNode p, TreeNode q) {
            if (p == null && q == null) return true;
            if (p == null || q == null) return false;
            if (p.val != q.val) return false;
            return isSameTree(p.left, q.left) && isSameTree(p.right, q.right);
        }

        // =====================================================================
        // APPROACH 2: Iterative DFS using an explicit Stack
        // ---------------------------------------------------------------------
        // Same idea as Approach 1, but walks `root`'s nodes iteratively with
        // an explicit stack instead of recursion. At each popped node, run
        // the (recursive) isSameTree check against subRoot; push children to
        // keep exploring if no match yet.
        //
        // ALGORITHM (step by step)
        // 1. Push `root` onto the stack (if non-null).
        // 2. While the stack is non-empty:
        //      a. Pop a node.
        //      b. If isSameTree(node, subRoot) -> return true immediately.
        //      c. Otherwise, push node.left and node.right (if non-null) to
        //         keep searching other candidate starting points.
        // 3. If the stack empties without a match -> return false.
        //
        // Time:  O(n * m) worst case, same as Approach 1.
        // Space: O(h) for the stack (h = height of `root`).
        // =====================================================================
        public boolean isSubtreeIterative(TreeNode root, TreeNode subRoot) {
            Deque<TreeNode> stack = new ArrayDeque<>();
            if (root != null) stack.push(root);

            while (!stack.isEmpty()) {
                TreeNode node = stack.pop();

                if (isSameTree(node, subRoot)) return true;

                if (node.left != null) stack.push(node.left);
                if (node.right != null) stack.push(node.right);
            }

            return false;
        }

        // =====================================================================
        // *** IMPORTANT *** APPROACH 3: Serialize + Substring Search
        // ---------------------------------------------------------------------
        // A completely different (and very well-known interview) trick:
        // instead of comparing tree STRUCTURE directly, first turn each tree
        // into a unique STRING (via pre-order serialization), then the
        // subtree question reduces to a plain STRING containment question:
        //
        //   subRoot is a subtree of root  <=>  serialize(root) CONTAINS
        //                                       serialize(subRoot)
        //
        // WHY DOES THIS WORK? Because pre-order traversal (Node -> Left ->
        // Right) uniquely encodes a tree's shape AND values, AS LONG AS we
        // also encode the ABSENCE of children (nulls) - otherwise different
        // trees could serialize to the same string. That's why every null
        // child is explicitly written as the literal "null" instead of being
        // skipped.
        //
        // WHY THE "^" MARKER BEFORE EVERY VALUE? Without a clear boundary
        // between one node's value and the next, digits could accidentally
        // "bleed together" and cause a FALSE POSITIVE match. For example,
        // without markers, a tree containing the value 12 could serialize to
        // "...12nullnull..." which, as a raw string, CONTAINS "2nullnull" -
        // making a completely unrelated single-node tree with value 2 look
        // like a match! Prefixing every value with "^" fixes this: the
        // substring "^2" can never appear inside "^12" (since "^" only ever
        // precedes the FIRST digit of a value), so real node boundaries are
        // always respected and false positives are eliminated.
        //
        // ------------------------------------------------------------------
        // ALGORITHM (step by step)
        // ------------------------------------------------------------------
        // 1. Write a helper `preOrderTraversal(node)` that recursively builds
        //    a string: "null" for a null node, otherwise
        //    "^" + node.val + preOrderTraversal(node.left) + preOrderTraversal(node.right).
        // 2. Serialize BOTH trees: `rootTraversal = preOrderTraversal(root)`,
        //    `subRootTraversal = preOrderTraversal(subRoot)`.
        // 3. Return `rootTraversal.contains(subRootTraversal)` - true if and
        //    only if subRoot's exact serialized shape appears somewhere
        //    inside root's serialized shape.
        //
        // ------------------------------------------------------------------
        // DIAGRAM WALKTHROUGH
        // ------------------------------------------------------------------
        //   root:        3              subRoot:    4
        //               / \                        / \
        //              4   5                      1   2
        //             / \
        //            1   2
        //
        //   preOrderTraversal(subRoot) = "^4^1nullnull^2nullnull"
        //
        //   preOrderTraversal(root)
        //     = "^3"
        //       + preOrderTraversal(node 4)   -> "^4^1nullnull^2nullnull"
        //       + preOrderTraversal(node 5)   -> "^5nullnull"
        //     = "^3^4^1nullnull^2nullnull^5nullnull"
        //
        //   Does rootTraversal CONTAIN "^4^1nullnull^2nullnull" ?
        //     "^3[^4^1nullnull^2nullnull]^5nullnull"  -> YES, found right
        //     after "^3" -> isSubtreeUsingPreOrder returns true. ✔
        //
        // ------------------------------------------------------------------
        // COMPLEXITY / IS IT "BETTER"?
        // ------------------------------------------------------------------
        // Time:  Building both serializations is O(n + m). The final
        //         `.contains()` call is, in the WORST case, O(n * m) (naive
        //         substring search) - same big-O ceiling as the recursive
        //         Same-Tree-at-every-node approach. In PRACTICE it's often
        //         noticeably faster, since `String.contains()` is a highly
        //         optimized, tight native-ish loop with no per-node method
        //         call / object-allocation overhead. For a truly optimal
        //         O(n + m) guarantee, swap `.contains()` for the KMP
        //         (Knuth-Morris-Pratt) substring algorithm.
        // Space: O(n + m) for the two serialized strings, PLUS O(h)
        //         recursion stack while building them - strictly MORE memory
        //         than Approach 1/2 (which only need O(h)), since the full
        //         tree gets materialized as text.
        //
        // Verdict: Same worst-case time complexity as the recursive
        // approach, often faster in practice, but uses more memory (extra
        // O(n+m) for the strings). A great alternative to know for
        // interviews, but not a strict Big-O improvement unless paired with
        // a linear-time string-matching algorithm like KMP.
        // =====================================================================
        public String preOrderTraversal(TreeNode node) {
            if (node == null) {
                return "null";
            }
            StringBuilder sb = new StringBuilder("^");
            sb.append(node.val);
            sb.append(preOrderTraversal(node.left));
            sb.append(preOrderTraversal(node.right));
            return sb.toString();
        }

        public boolean isSubtreeUsingPreOrder(TreeNode root, TreeNode subRoot) {
            String rootTraversal = preOrderTraversal(root);
            String subRootTraversal = preOrderTraversal(subRoot);
            return rootTraversal.contains(subRootTraversal);
        }

    }

    // Simple manual tests comparing both approaches.
    public static void main(String[] args) {
        //   root:        3              subRoot:    4
        //               / \                        / \
        //              4   5                      1   2
        //             / \
        //            1   2
        TreeNode root = new TreeNode(3);
        root.left = new TreeNode(4);
        root.right = new TreeNode(5);
        root.left.left = new TreeNode(1);
        root.left.right = new TreeNode(2);

        TreeNode subRoot = new TreeNode(4);
        subRoot.left = new TreeNode(1);
        subRoot.right = new TreeNode(2);

        //   root2: same as root, but node 4's subtree has an extra node "0"
        //   under node 1 -> subRoot no longer matches exactly.
        TreeNode root2 = new TreeNode(3);
        root2.left = new TreeNode(4);
        root2.right = new TreeNode(5);
        root2.left.left = new TreeNode(1);
        root2.left.left.left = new TreeNode(0);
        root2.left.right = new TreeNode(2);

        Solution solution = new Solution();
        System.out.println("Match case    - default    : " + solution.isSubtree(root, subRoot));            // true
        System.out.println("Match case    - recursive  : " + solution.isSubtreeRecursive(root, subRoot));   // true
        System.out.println("Match case    - iterative  : " + solution.isSubtreeIterative(root, subRoot));   // true
        System.out.println("Match case    - preOrderStr: " + solution.isSubtreeUsingPreOrder(root, subRoot)); // true

        System.out.println("No-match case - default    : " + solution.isSubtree(root2, subRoot));            // false
        System.out.println("No-match case - recursive  : " + solution.isSubtreeRecursive(root2, subRoot));   // false
        System.out.println("No-match case - iterative  : " + solution.isSubtreeIterative(root2, subRoot));   // false
        System.out.println("No-match case - preOrderStr: " + solution.isSubtreeUsingPreOrder(root2, subRoot)); // false
    }
}

