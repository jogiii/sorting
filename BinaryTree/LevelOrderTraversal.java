package BinaryTree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/*
 * WHAT IS LEVEL-ORDER TRAVERSAL?
 * ----------------------------------------------------------------------
 * Level-order traversal visits every node in a binary tree LEVEL BY
 * LEVEL, from top to bottom, and within each level from LEFT to RIGHT.
 *
 * Unlike pre-order/in-order/post-order (which are all DEPTH-FIRST -
 * they dive as deep as possible down one branch before backtracking),
 * level-order is BREADTH-FIRST: it visits ALL nodes at depth 0, then
 * ALL nodes at depth 1, then ALL nodes at depth 2, and so on.
 *
 * Example tree:
 *
 *          1
 *         / \
 *        2   3
 *       / \    \
 *      4   5    6
 *
 * Level-order visiting order (flat): 1, 2, 3, 4, 5, 6
 *
 * ------------------------------------------------------------------------
 * WHY BFS (queue) INSTEAD OF DFS (stack/recursion)?
 * ------------------------------------------------------------------------
 * Pre/in/post-order all use a STACK (explicitly or via the recursive call
 * stack) because they need LIFO (last-in-first-out) behavior - dive deep,
 * then backtrack.
 *
 * Level-order instead needs FIFO (first-in-first-out) behavior - process
 * nodes in the same order they were discovered, level by level. That's
 * exactly what a QUEUE gives you, which is why level-order is the classic
 * example of Breadth-First Search (BFS) on a tree.
 *
 * Time Complexity:  O(n) - every node is visited/enqueued exactly once.
 * Space Complexity: O(w) where w = maximum width of the tree, for the
 *                    queue, plus O(n) for the output list.
 */

/*
 * SIMPLE WORDS MEIN SAMJHO
 * ------------------------------------------------------------------------
 * Level-order traversal matlab tree ko upar se neeche, level-by-level
 * dekhna - pehle root wala level, fir uske bache (children) wala level,
 * fir unke bache wala level, aur aise hi aage. Har level ke andar left
 * se right order follow hota hai.
 *
 * Isko Queue (FIFO) se karte hain, Stack se nahi - kyunki hume "jo pehle
 * mila usko pehle process karo" wala behavior chahiye, "sabse last mein
 * mila usko pehle karo" wala nahi. Yehi BFS (Breadth-First Search) ka
 * asli udaharan hai tree ke context mein.
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
 * outer LevelOrderTraversal class (instead of top-level classes) purely
 * to avoid a "duplicate class" compile conflict with the other traversal
 * files in this same folder, which also define TreeNode/Solution.
 * Functionally they behave identically.
 */
public class LevelOrderTraversal {

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
        // Iterative BFS using a Queue - returns a FLAT List<Integer>
        // ---------------------------------------------------------------------
        // Push root into a queue. Then repeatedly:
        //   - dequeue a node, visit it (add to result)
        //   - enqueue its left child, then its right child
        // A queue is FIFO, so nodes are naturally processed in the exact
        // order they were discovered - level by level, left to right.
        //
        // Time:  O(n) - each node enqueued/dequeued exactly once.
        // Space: O(w) for the queue (w = max width of tree), O(n) for result.
        // =====================================================================
        public List<Integer> levelOrder(TreeNode root) {
            List<Integer> result = new ArrayList<>();
            if (root == null) return result;

            Queue<TreeNode> queue = new LinkedList<>();
            queue.offer(root);

            while (!queue.isEmpty()) {
                TreeNode node = queue.poll();
                result.add(node.val);      // visit
                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }

            return result;
        }
    }

    // Simple manual test.
    public static void main(String[] args) {
        //          1
        //         / \
        //        2   3
        //       / \    \
        //      4   5    6
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(4);
        root.left.right = new TreeNode(5);
        root.right.right = new TreeNode(6);

        Solution solution = new Solution();
        System.out.println(solution.levelOrder(root)); // [1, 2, 3, 4, 5, 6]
    }
}

