package BinaryTree;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

/*
 * PROBLEM: SAME TREE
 * ----------------------------------------------------------------------
 * Given the roots of two binary trees `p` and `q`, determine if they are
 * the SAME tree - meaning structurally identical AND every corresponding
 * node has the same value.
 *
 * Example 1 (same):
 *
 *   p:      1          q:      1
 *          / \                / \
 *         2   3              2   3
 *
 *   -> true
 *
 * Example 2 (different structure):
 *
 *   p:      1          q:      1
 *          /                    \
 *         2                      2
 *
 *   -> false (2 is on the left in p, but on the right in q)
 *
 * Example 3 (different value):
 *
 *   p:      1          q:      1
 *          / \                / \
 *         2   1              2   3
 *
 *   -> false (right child differs: 1 vs 3)
 *
 * ------------------------------------------------------------------------
 * KEY INSIGHT
 * ------------------------------------------------------------------------
 * Two trees are the same if and only if:
 *   1. Both roots are null (both empty -> trivially same), OR
 *   2. Both roots are non-null, their values are equal, AND their left
 *      subtrees are the same AND their right subtrees are the same.
 *
 * If exactly one of the two current nodes is null (but not the other),
 * the trees differ in structure -> false immediately.
 *
 * This is naturally a PRE-order-style check: compare the node first,
 * then recurse into left, then right (order doesn't actually matter here
 * since all three checks must pass anyway - but visiting node first lets
 * us short-circuit on a value mismatch as early as possible).
 *
 * Time Complexity:  O(min(n, m)) - stops as soon as a mismatch is found;
 *                    worst case (trees identical) visits every node once,
 *                    O(n) where n = number of nodes.
 * Space Complexity: O(h) - recursion/stack depth = height of the smaller
 *                    tree (h).
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
 * outer SameTree class (instead of top-level classes) purely to avoid a
 * "duplicate class" compile conflict with the other traversal files in
 * this same folder, which also define TreeNode/Solution. Functionally
 * they behave identically.
 */
public class SameTree {

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
         * DEFAULT entry point used by LeetCode-style signature ("Same Tree").
         * Delegates to the recursive approach - the simplest, most commonly
         * expected solution.
         */
        public boolean isSameTree(TreeNode p, TreeNode q) {
            return isSameTreeRecursive(p, q);
        }

        // =====================================================================
        // APPROACH 1: Recursive DFS
        // ---------------------------------------------------------------------
        // ALGORITHM (step by step)
        // 1. If both p and q are null -> both empty subtrees -> return true.
        // 2. If exactly one of p/q is null (the other isn't) -> structural
        //    mismatch -> return false.
        // 3. If p.val != q.val -> value mismatch at this node -> return false.
        // 4. Otherwise, return true only if BOTH:
        //      isSameTreeRecursive(p.left, q.left)  AND
        //      isSameTreeRecursive(p.right, q.right)
        //    are true (recursively same left subtrees AND same right subtrees).
        //
        // Time:  O(n) worst case - visits every node once if trees match.
        // Space: O(h) - recursion stack depth = height of the smaller tree.
        // =====================================================================
        public boolean isSameTreeRecursive(TreeNode p, TreeNode q) {
            if (p == null && q == null) return true;
            if (p == null || q == null) return false;
            if (p.val != q.val) return false;

            return isSameTreeRecursive(p.left, q.left)
                    && isSameTreeRecursive(p.right, q.right);
        }

        // =====================================================================
        // APPROACH 2: Iterative DFS using an explicit Stack of pairs
        // ---------------------------------------------------------------------
        // Simulates the recursion above using an explicit stack that holds
        // PAIRS of nodes (one from each tree) still needing comparison.
        //
        // ALGORITHM (step by step)
        // 1. Push the pair (p, q) onto the stack.
        // 2. While the stack is non-empty:
        //      a. Pop a pair (n1, n2).
        //      b. If both null -> continue (this branch matches, nothing
        //         more to check here).
        //      c. If exactly one is null, OR their values differ -> return
        //         false immediately (short-circuit).
        //      d. Otherwise, push (n1.left, n2.left) and (n1.right, n2.right)
        //         onto the stack to be checked later.
        // 3. If the stack empties without ever finding a mismatch, all
        //    corresponding nodes matched -> return true.
        //
        // Time:  O(n) worst case.
        // Space: O(h) for the stack (h = height of the smaller tree).
        // =====================================================================
        private static class NodePair {
            TreeNode first;
            TreeNode second;
            NodePair(TreeNode first, TreeNode second) {
                this.first = first;
                this.second = second;
            }
        }

        public boolean isSameTreeIterative(TreeNode p, TreeNode q) {
            Deque<NodePair> stack = new ArrayDeque<>();
            stack.push(new NodePair(p, q));

            while (!stack.isEmpty()) {
                NodePair pair = stack.pop();
                TreeNode n1 = pair.first;
                TreeNode n2 = pair.second;

                if (n1 == null && n2 == null) continue;
                if (n1 == null || n2 == null || n1.val != n2.val) return false;

                stack.push(new NodePair(n1.left, n2.left));
                stack.push(new NodePair(n1.right, n2.right));
            }

            return true;
        }

        // =====================================================================
        // APPROACH 3: Iterative BFS using a Queue (level-order style)
        // ---------------------------------------------------------------------
        // WHY THIS ALSO WORKS: "Same Tree" doesn't actually require any
        // particular VISIT ORDER - all we need is to compare EVERY pair of
        // corresponding nodes (one from p, one from q) and bail out the
        // moment any pair mismatches (either in nullness or in value). DFS
        // (stack) and BFS (queue) both visit every node exactly once - they
        // just differ in the ORDER they do it in - so either works equally
        // well here. Level-order/BFS is simply the "queue" version of the
        // exact same pairwise-comparison idea used in Approach 2's stack.
        //
        // ALGORITHM (step by step)
        // 1. Enqueue the pair (p, q).
        // 2. While the queue is non-empty:
        //      a. Dequeue a pair (n1, n2).
        //      b. If both null -> continue (this branch matches so far).
        //      c. If exactly one is null, OR values differ -> return false
        //         immediately (short-circuit).
        //      d. Otherwise, enqueue (n1.left, n2.left) and
        //         (n1.right, n2.right) to be checked in a later round.
        // 3. If the queue empties without finding a mismatch -> return true.
        //
        // Time:  O(n) worst case - every node pair enqueued/dequeued once.
        // Space: O(w) for the queue (w = max width of the smaller tree),
        //         versus O(h) for the DFS/stack version - so DFS is
        //         generally more memory-efficient for wide, shallow trees,
        //         while BFS/queue is more memory-efficient for narrow, deep
        //         (skewed) trees. Either is correct; DFS is simply the more
        //         common/idiomatic choice for this specific problem since
        //         it doesn't need level-by-level information at all.
        // =====================================================================
        public boolean isSameTreeLevelOrder(TreeNode p, TreeNode q) {
            Queue<NodePair> queue = new LinkedList<>();
            queue.offer(new NodePair(p, q));

            while (!queue.isEmpty()) {
                NodePair pair = queue.poll();
                TreeNode n1 = pair.first;
                TreeNode n2 = pair.second;

                if (n1 == null && n2 == null) continue;
                if (n1 == null || n2 == null || n1.val != n2.val) return false;

                queue.offer(new NodePair(n1.left, n2.left));
                queue.offer(new NodePair(n1.right, n2.right));
            }

            return true;
        }



        // =====================================================================
        // *** IMPORTANT *** APPROACH 4: Iterative BFS using a SINGLE Queue<TreeNode>
        // ---------------------------------------------------------------------
        // This is functionally IDENTICAL to Approach 3 (isSameTreeLevelOrder),
        // but WITHOUT the extra `NodePair` wrapper class. Instead of storing
        // pairs, we simply enqueue nodes from BOTH trees interleaved, ALWAYS
        // in pairs of two consecutive `offer()` calls: (p's node, q's node).
        // Because a Queue is FIFO, polling twice in a row always retrieves
        // one such pair back out together, in the same order they went in.
        //
        // This is a very common, interview-friendly trick worth memorizing:
        // "encode a pair by pushing/popping two related items consecutively
        // into a single queue/stack, instead of creating a wrapper object."
        //
        // ------------------------------------------------------------------
        // ALGORITHM (step by step)
        // ------------------------------------------------------------------
        // 1. Create a single `Queue<TreeNode>`.
        // 2. Enqueue `p`, then enqueue `q` (a "pair", one from each tree).
        // 3. While the queue is non-empty:
        //      a. Dequeue TWO nodes in a row: `first` (from p's side) and
        //         `second` (from q's side) - these always correspond to the
        //         SAME pair because they were enqueued together as a pair.
        //      b. If both are null -> this branch matches (both trees ran
        //         out of nodes here at the same time) -> continue.
        //      c. If exactly one is null, OR their values differ -> the
        //         trees differ here -> return false immediately.
        //      d. Otherwise, enqueue the NEXT pair to check:
        //           first.left,  second.left    (left children pair)
        //           first.right, second.right   (right children pair)
        //         Note these are enqueued as TWO separate two-node pairs,
        //         preserving the "always dequeue two at a time" invariant.
        // 4. If the queue drains completely without a mismatch -> true.
        //
        // ------------------------------------------------------------------
        // DIAGRAM WALKTHROUGH
        // ------------------------------------------------------------------
        //   p:      1          q:      1
        //          / \                / \
        //         2   3              2   3
        //
        //   Step 1: queue = [p(1), q(1)]
        //
        //   Round 1: dequeue first=p(1), second=q(1)
        //     both non-null, values equal (1==1) -> ok, continue
        //     enqueue (p(1).left=2, q(1).left=2)   -> queue += [2, 2]
        //     enqueue (p(1).right=3, q(1).right=3) -> queue += [3, 3]
        //     queue = [2, 2, 3, 3]
        //
        //   Round 2: dequeue first=2, second=2
        //     both non-null, values equal (2==2) -> ok
        //     enqueue (2.left=null, 2.left=null)   -> queue += [null, null]
        //     enqueue (2.right=null, 2.right=null) -> queue += [null, null]
        //     queue = [3, 3, null, null, null, null]
        //
        //   Round 3: dequeue first=3, second=3
        //     both non-null, values equal (3==3) -> ok
        //     enqueue (3.left=null, 3.left=null)   -> queue += [null, null]
        //     enqueue (3.right=null, 3.right=null) -> queue += [null, null]
        //     queue = [null, null, null, null, null, null, null, null]
        //
        //   Remaining rounds: dequeue (null, null) pairs four times ->
        //     each time "both null" -> continue, nothing more enqueued.
        //
        //   Queue empties -> return true.  ✔ Matches expected result.
        //
        // Why is it safe to always dequeue/enqueue in twos? Because we NEVER
        // enqueue a lone node - every single `offer()` call in this method is
        // immediately followed by another `offer()` for its "partner" node
        // from the other tree, so the queue's FIFO ordering guarantees every
        // pair of consecutive `poll()` calls retrieves exactly one such pair,
        // in the same (first, second) = (p-side, q-side) order it was pushed.
        //
        // Time:  O(n) worst case - every node pair enqueued/dequeued once.
        // Space: O(w) for the queue (w = max width of the smaller tree).
        // =====================================================================
        public boolean isSameTreeLevelOrderV2(TreeNode p, TreeNode q) {
            Queue<TreeNode> queue = new LinkedList<>();
            queue.offer(p);
            queue.offer(q);

            // loop until the queue is empty
            while (!queue.isEmpty()) {
                TreeNode first = queue.poll();
                TreeNode second = queue.poll();

                if (first == null && second == null) continue;
                if (first == null || second == null || first.val != second.val) return false;

                queue.offer(first.left);
                queue.offer(second.left);
                queue.offer(first.right);
                queue.offer(second.right);
            }

            return true;
        }
    }

    // Simple manual tests comparing all approaches.
    public static void main(String[] args) {
        //   p:      1          q:      1
        //          / \                / \
        //         2   3              2   3
        TreeNode p1 = new TreeNode(1);
        p1.left = new TreeNode(2);
        p1.right = new TreeNode(3);

        TreeNode q1 = new TreeNode(1);
        q1.left = new TreeNode(2);
        q1.right = new TreeNode(3);

        //   p:      1          q:      1
        //          /                    \
        //         2                      2
        TreeNode p2 = new TreeNode(1);
        p2.left = new TreeNode(2);

        TreeNode q2 = new TreeNode(1);
        q2.right = new TreeNode(2);

        Solution solution = new Solution();
        System.out.println("Same trees   - default    : " + solution.isSameTree(p1, q1));            // true
        System.out.println("Same trees   - recursive  : " + solution.isSameTreeRecursive(p1, q1));   // true
        System.out.println("Same trees   - iterative  : " + solution.isSameTreeIterative(p1, q1));   // true
        System.out.println("Same trees   - levelOrder : " + solution.isSameTreeLevelOrder(p1, q1));  // true
        System.out.println("Same trees   - levelOrderV2: " + solution.isSameTreeLevelOrderV2(p1, q1)); // true

        System.out.println("Diff trees   - default    : " + solution.isSameTree(p2, q2));            // false
        System.out.println("Diff trees   - recursive  : " + solution.isSameTreeRecursive(p2, q2));   // false
        System.out.println("Diff trees   - iterative  : " + solution.isSameTreeIterative(p2, q2));   // false
        System.out.println("Diff trees   - levelOrder : " + solution.isSameTreeLevelOrder(p2, q2));  // false
        System.out.println("Diff trees   - levelOrderV2: " + solution.isSameTreeLevelOrderV2(p2, q2)); // false
    }
}




