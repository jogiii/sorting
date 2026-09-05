package BinaryTree;

/*
 * PROBLEM: LOWEST COMMON ANCESTOR OF A BINARY SEARCH TREE (LeetCode 235)
 * ----------------------------------------------------------------------
 * Given the root of a BST, and two nodes p and q that both exist in the
 * tree, find their LOWEST COMMON ANCESTOR (LCA) - the deepest node that
 * is an ancestor of BOTH p and q.
 *
 * (A node is allowed to be a descendant of itself, per the LeetCode
 * definition - so if p is an ancestor of q, p itself is the LCA.)
 *
 * ------------------------------------------------------------------------
 * SIMPLE WORDS MEIN SAMJHO
 * ------------------------------------------------------------------------
 * Dekh bhai, simple si baat hai: tere paas ek BST hai, aur do nodes hain
 * p aur q. Tujhe unka "sabse pehla common bada-baap" (LCA) dhoondhna hai -
 * matlab woh sabse neeche wala node jahan se p aur q dono reachable hain.
 *
 * Aur haan, agar p khud hi q ka ancestor nikla, toh p hi answer hai -
 * kisi node ko khud ka ancestor maan lena bhi allowed hai, tension mat le.
 *
 * Example:
 *
 *          6
 *        /   \
 *       2     8
 *      / \   / \
 *     0   4 7   9
 *        / \
 *       3   5
 *
 *   LCA(2, 8) = 6   (root splits their paths)
 *   LCA(2, 4) = 2   (2 is an ancestor of 4, so 2 is its own LCA)
 *   LCA(3, 5) = 4
 *
 * ------------------------------------------------------------------------
 * KEY INSIGHT - using the BST invariant
 * ------------------------------------------------------------------------
 * Unlike a GENERIC binary tree (where finding the LCA requires searching
 * both subtrees - an O(n) algorithm), a BST lets us decide which way to
 * go using ONLY value comparisons against the current node, since the
 * BST invariant guarantees:
 *     left subtree < node.val < right subtree
 *
 *   - If BOTH p.val and q.val are LESS than node.val, they must both live
 *     in the LEFT subtree -> recurse left.
 *   - If BOTH p.val and q.val are GREATER than node.val, they must both
 *     live in the RIGHT subtree -> recurse right.
 *   - Otherwise, p and q are on DIFFERENT sides of node (or one of them
 *     EQUALS node.val) - this is exactly the "split point" where their
 *     paths diverge -> the CURRENT node is the LCA.
 *
 * This means we only ever walk down a SINGLE path from the root, instead
 * of exploring both subtrees - giving O(h) time instead of O(n).
 *
 * Ab asli twist yeh hai (yahi trick hai poore solution ki):
 * Normal binary tree mein LCA nikalne ke liye dono taraf (left-right)
 * dhoondhna padta hai - O(n) time waste hota hai. Par BST mein toh
 * ek golden rule follow hota hai har node pe:
 *     left wale saare values chote, right wale saare values bade
 *
 * Toh bas isi rule ka fayda uthate hain, koi rocket science nahi:
 *
 *   - Agar p aur q dono current node ki value se CHOTE hain, matlab
 *     dono LEFT side pe baithe honge -> bas left mein ghus ja.
 *   - Agar p aur q dono current node se BADE hain, matlab dono RIGHT
 *     side pe honge -> right mein ghus ja.
 *   - Agar aisa kuch nahi hua (ek chota hai ek bada, ya current node
 *     khud hi p ya q nikla) - toh bas yahin ruk ja, ye wahi "cross
 *     roads" hai jahan p aur q ke raaste alag hote hain -> ye current
 *     node hi tera LCA hai. Simple.
 *
 * Iska matlab poore tree mein bhatakna nahi padta - bas root se ek
 * seedhi line mein neeche jaana hai, isliye time O(n) nahi, O(h) lagta
 * hai (h = tree ki height, jitna lamba tree utna zyada time).
 *
 * Time Complexity:  O(h) - single root-to-LCA path, h = tree height.
 *                    O(log n) balanced, O(n) worst case (skewed tree).
 * Space Complexity: O(h) recursion stack for the recursive approach,
 *                    O(1) for the iterative approach.
 *
 * Time complexity ka funda: O(h) - root se LCA tak ek hi seedha raasta.
 * Balanced tree mein O(log n) (fast), worst case mein (jab tree ekdum
 * tedha-medha/skewed ho jaaye) O(n) tak ja sakta hai.
 * Space ka funda: recursive wale mein O(h) (recursion stack lagta hai),
 * iterative wale mein bas O(1) - extra kuch nahi chahiye.
 */

/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode(int x) { val = x; }
 * }
 *
 * NOTE: TreeNode and Solution are nested as static classes inside this
 * outer LowestCommonAncestorBST class (instead of top-level classes) to
 * avoid a "duplicate class" compile conflict with the other files in
 * this same folder that also define TreeNode/Solution. Functionally
 * they behave identically.
 */
public class LowestCommonAncestorBST {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int val) { this.val = val; }
    }

    static class Solution {

        /**
         * DEFAULT entry point - delegates to the recursive approach.
         */
        public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
            return lowestCommonAncestorRecursive(root, p, q);
        }

        // =====================================================================
        // APPROACH 1: Recursive
        // ---------------------------------------------------------------------
        // ALGORITHM (step by step)
        // 1. If both p.val and q.val are less than root.val -> the LCA must
        //    be somewhere in root's LEFT subtree -> recurse left.
        // 2. If both p.val and q.val are greater than root.val -> the LCA
        //    must be in root's RIGHT subtree -> recurse right.
        // 3. Otherwise (p and q split across root, or root IS p or q) ->
        //    root is the LCA -> return root.
        //
        // Time:  O(h) - one path down the tree.
        // Space: O(h) recursion stack.
        //
        // Hinglish mein: bas root se check kar - agar dono chote hain toh
        // left chala ja, dono bade hain toh right chala ja, aur agar match
        // nahi hua toh yehi tera LCA hai, wahin ruk jaa aur return kar de.
        // =====================================================================
        public TreeNode lowestCommonAncestorRecursive(TreeNode root, TreeNode p, TreeNode q) {
            if (root == null) return null;

            if (p.val < root.val && q.val < root.val) {
                return lowestCommonAncestorRecursive(root.left, p, q);
            }
            if (p.val > root.val && q.val > root.val) {
                return lowestCommonAncestorRecursive(root.right, p, q);
            }
            // p and q split here (or root.val equals p.val or q.val).
            // Yahi wo point hai jahan p aur q ke raaste alag ho rahe hain.
            return root;
        }

        // =====================================================================
        // APPROACH 2: Iterative
        // ---------------------------------------------------------------------
        // Same logic as the recursive approach, but using a simple loop
        // instead of recursion -> O(1) extra space.
        //
        // Time:  O(h)
        // Space: O(1)
        //
        // Hinglish mein: bilkul wahi recursive wala logic hai, bas function
        // call karne ki jagah ek while loop mein ghoom rahe hain - isse
        // stack space bachta hai, extra memory nahi lagti.
        // =====================================================================
        public TreeNode lowestCommonAncestorIterative(TreeNode root, TreeNode p, TreeNode q) {
            TreeNode node = root;
            while (node != null) {
                if (p.val < node.val && q.val < node.val) {
                    node = node.left;
                } else if (p.val > node.val && q.val > node.val) {
                    node = node.right;
                } else {
                    return node;
                }
            }
            return null; // p or q not found in tree (yaani tree mein mile hi nahi)
        }
    }

    // Simple manual tests.
    public static void main(String[] args) {
        //          6
        //        /   \
        //       2     8
        //      / \   / \
        //     0   4 7   9
        //        / \
        //       3   5
        TreeNode root = new TreeNode(6);
        root.left = new TreeNode(2);
        root.right = new TreeNode(8);
        root.left.left = new TreeNode(0);
        root.left.right = new TreeNode(4);
        root.right.left = new TreeNode(7);
        root.right.right = new TreeNode(9);
        root.left.right.left = new TreeNode(3);
        root.left.right.right = new TreeNode(5);

        Solution solution = new Solution();

        TreeNode two = root.left;
        TreeNode eight = root.right;
        TreeNode four = root.left.right;
        TreeNode three = root.left.right.left;
        TreeNode five = root.left.right.right;

        System.out.println("LCA(2, 8) - recursive : " + solution.lowestCommonAncestor(root, two, eight).val); // 6
        System.out.println("LCA(2, 4) - recursive : " + solution.lowestCommonAncestor(root, two, four).val);  // 2
        System.out.println("LCA(3, 5) - recursive : " + solution.lowestCommonAncestor(root, three, five).val); // 4

        System.out.println("LCA(2, 8) - iterative : " + solution.lowestCommonAncestorIterative(root, two, eight).val); // 6
        System.out.println("LCA(2, 4) - iterative : " + solution.lowestCommonAncestorIterative(root, two, four).val);  // 2
        System.out.println("LCA(3, 5) - iterative : " + solution.lowestCommonAncestorIterative(root, three, five).val); // 4
    }
}

