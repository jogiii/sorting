package BinaryTree;

/*
 * DATA STRUCTURE: BINARY SEARCH TREE (BST)
 * ----------------------------------------------------------------------
 * A Binary Search Tree is a binary tree that maintains the BST INVARIANT
 * at every single node:
 *
 *   For any node N:
 *     - ALL values in N's LEFT subtree  are  < N.val
 *     - ALL values in N's RIGHT subtree are  > N.val
 *
 * (This implementation disallows duplicate values for simplicity - a
 * common convention, though some BSTs allow duplicates by always going
 * right, or by storing a count per node.)
 *
 * Example valid BST:
 *
 *          5
 *         / \
 *        2   8
 *       / \    \
 *      1   3    9
 *
 *   In-order traversal: 1, 2, 3, 5, 8, 9  -> always sorted! (see
 *   InorderTraversal.java for why this holds for ANY valid BST)
 *
 * ------------------------------------------------------------------------
 * WHY DOES THE BST INVARIANT MATTER?
 * ------------------------------------------------------------------------
 * It lets us SEARCH, INSERT, and DELETE in O(h) time (h = tree height),
 * because at every node we can discard an ENTIRE subtree just by
 * comparing the target value against the current node's value - exactly
 * like binary search on a sorted array, but on a tree structure instead.
 *
 * For a BALANCED BST, h = O(log n), giving O(log n) operations. BUT if
 * you insert values in already-sorted order (e.g., 1,2,3,4,5...) into a
 * plain BST like this one, it degenerates into a "linked list" shape with
 * h = O(n) - see BalancedBinaryTree.java and consider self-balancing
 * variants (AVL, Red-Black trees) for guaranteed O(log n) in production.
 *
 * ------------------------------------------------------------------------
 * OPERATIONS IMPLEMENTED
 * ------------------------------------------------------------------------
 * 1. insert(val)  - add a new value, maintaining the BST invariant.
 * 2. search(val)  - check if a value exists in the tree.
 * 3. delete(val)  - remove a value, restructuring around it as needed
 *                    (the trickiest operation - 3 cases to handle).
 * 4. inorder()    - returns values in sorted order (sanity check /
 *                    demonstration of the BST's core property).
 *
 * Time Complexity:  O(h) for insert/search/delete, where h = tree height.
 *                    O(log n) average/balanced case, O(n) worst case
 *                    (degenerate/skewed tree).
 * Space Complexity: O(h) recursion stack for recursive operations,
 *                    O(n) total for storing n nodes.
 *
 * ------------------------------------------------------------------------
 * SIMPLE WORDS MEIN SAMJHO
 * ------------------------------------------------------------------------
 * BST ka golden rule: har node ke liye, uske LEFT taraf ke saare values
 * chote hote hain aur RIGHT taraf ke saare values bade hote hain. Bas
 * isi rule ka fayda uthake hum values compare karke pura subtree ek
 * hi step mein discard kar dete hain - bilkul jaise sorted array pe
 * binary search karte hain.
 *
 * Isi wajah se search/insert/delete O(h) time mein ho jaate hain
 * (h = tree ki height). Agar tree balanced hai toh h = O(log n), matlab
 * bahut fast. Par agar tum numbers ko already-sorted order mein daaloge
 * (1,2,3,4,5...), toh tree ek seedhi line (linked list) jaisi ban
 * jaayegi aur h = O(n) ho jaayega - fir koi fayda nahi rahega. Isi
 * problem ko solve karne ke liye AVL ya Red-Black trees use karte hain.
 */
public class BinarySearchTree {

    /**
     * Definition for a BST node. Kept minimal (val, left, right) - same
     * shape as the generic TreeNode used throughout this BinaryTree/
     * folder, just renamed to Node here since this file defines its own
     * standalone BST class (not a LeetCode-style TreeNode problem).
     */
    static class Node {
        int val;
        Node left;
        Node right;
        Node(int val) {
            this.val = val;
        }
    }

    private Node root;

    // =====================================================================
    // INSERT
    // ---------------------------------------------------------------------
    // ALGORITHM (step by step)
    // 1. If the tree is empty (root == null), the new node simply becomes
    //    the root.
    // 2. Otherwise, recursively walk down from the root:
    //      a. If val < node.val -> go left (recurse into node.left).
    //      b. If val > node.val -> go right (recurse into node.right).
    //      c. If val == node.val -> value already exists; do nothing
    //         (this implementation disallows duplicates).
    // 3. At a null spot (where we'd recurse into a non-existent child),
    //    create a new Node there and link it back to the parent.
    //
    // Time:  O(h) - walks down one root-to-leaf path.
    // Space: O(h) - recursion stack.
    //
    // ---------------------------------------------------------------------
    // SIMPLE WORDS MEIN SAMJHO
    // ---------------------------------------------------------------------
    // Agar tree khaali hai toh naya node seedha root ban jaata hai. Warna
    // root se chalna shuru karo: agar daalne wali value current node se
    // CHOTI hai toh left mein jao, BADI hai toh right mein jao. Yeh karte
    // raho jab tak khaali jagah (null) na mil jaaye - wahin naya node
    // bana ke jod do. Agar value already tree mein hai toh kuch mat karo
    // (duplicates allowed nahi hain yahan).
    // =====================================================================
    public void insert(int val) {
        root = insertRecursive(root, val);
    }

    private Node insertRecursive(Node node, int val) {
        if (node == null) {
            return new Node(val);
        }
        if (val < node.val) {
            node.left = insertRecursive(node.left, val);
        } else if (val > node.val) {
            node.right = insertRecursive(node.right, val);
        }
        // val == node.val: duplicate, ignore (no-op).
        return node;
    }

    // =====================================================================
    // SEARCH
    // ---------------------------------------------------------------------
    // ALGORITHM (step by step)
    // 1. Start at the root.
    // 2. If the current node is null, the value isn't in the tree ->
    //    return false.
    // 3. If val == node.val -> found it -> return true.
    // 4. If val < node.val -> the ONLY place it could be is the left
    //    subtree (BST invariant guarantees everything on the right is
    //    bigger) -> recurse left.
    // 5. Otherwise (val > node.val) -> recurse right.
    //
    // Time:  O(h) - discards one entire subtree at each step, just like
    //         binary search on a sorted array.
    // Space: O(h) recursion stack (or O(1) if written iteratively).
    //
    // ---------------------------------------------------------------------
    // SIMPLE WORDS MEIN SAMJHO
    // ---------------------------------------------------------------------
    // Root se shuru karo. Agar current node hi null ho gaya, matlab value
    // tree mein hai hi nahi - false return karo. Value match ho gayi toh
    // true. Agar value current node se choti hai toh sirf LEFT subtree
    // mein dhoondo (BST rule ki wajah se right mein toh yeh mil hi nahi
    // sakti), warna RIGHT mein dhoondo. Har step pe aadha tree discard ho
    // jaata hai - bilkul sorted array pe binary search jaisa feel aata hai.
    // =====================================================================
    public boolean search(int val) {
        return searchRecursive(root, val);
    }

    private boolean searchRecursive(Node node, int val) {
        if (node == null) return false;
        if (val == node.val) return true;
        return val < node.val
                ? searchRecursive(node.left, val)
                : searchRecursive(node.right, val);
    }

    // =====================================================================
    // DELETE
    // ---------------------------------------------------------------------
    // The trickiest BST operation - deleting a node has THREE cases:
    //
    //   CASE 1: Node has NO children (a leaf).
    //     -> Simply remove it (return null to the parent).
    //
    //   CASE 2: Node has EXACTLY ONE child.
    //     -> "Splice out" the node: replace it with its single child
    //        (return that child to the parent).
    //
    //   CASE 3: Node has TWO children. <-- THE HARD CASE, explained in full below.
    //
    // ------------------------------------------------------------------
    // CASE 3 IN DEPTH: why can't we just delete a 2-child node directly?
    // ------------------------------------------------------------------
    // If we simply removed the node, we'd be left with TWO orphaned
    // subtrees (its left subtree and its right subtree) and only ONE
    // slot in the parent to plug them back into. We can't attach both,
    // so we need a different strategy: instead of physically removing
    // the node, we OVERWRITE its value with a value that is safe to
    // place there, then delete that value's original (now-duplicate)
    // location instead - which is guaranteed to be an easy 0-or-1-child
    // case.
    //
    // "Safe to place there" means: a value that is BIGGER than every
    // value in the left subtree (so the BST invariant still holds on
    // the left), and SMALLER than every OTHER value in the right
    // subtree (so it still holds on the right too). There are exactly
    // two candidates that satisfy this: the LARGEST value in the left
    // subtree (the "in-order PREDECESSOR"), or the SMALLEST value in
    // the right subtree (the "in-order SUCCESSOR"). This implementation
    // uses the successor - the smallest value in the right subtree,
    // found by walking as far LEFT as possible starting from
    // node.right (see findMin() below).
    //
    // Why is the successor guaranteed to be bigger than the ENTIRE left
    // subtree? Because it comes from node.right's subtree, and the BST
    // invariant already guarantees everything in node.right's subtree
    // is bigger than node.val, which is itself bigger than everything
    // in node.left's subtree. So: left subtree < node.val < successor.
    //
    // Why is the successor guaranteed to be smaller than every OTHER
    // value remaining in the right subtree? Because by definition it
    // IS the smallest value in that subtree - findMin() walks left
    // until it can't anymore, and in a BST the leftmost node of any
    // subtree always holds that subtree's minimum value.
    //
    // So the 3-step plan is:
    //   a. successor = findMin(node.right)   // smallest value on the right
    //   b. node.val  = successor.val          // "move" it into node's spot
    //   c. node.right = deleteRecursive(node.right, successor.val)
    //      // remove the successor from its ORIGINAL location (it's now
    //      // duplicated - node.val holds a copy of it), so the value
    //      // only exists once in the tree afterward.
    //
    // Step (c) is always a CHEAP case 1-or-2 delete, never case 3 again,
    // because the successor is the LEFTMOST node in that subtree - it
    // can only ever have a RIGHT child (or no child at all), never a
    // left child (if it had one, findMin would have walked into it
    // instead, and that would be the real successor).
    //
    // ------------------------------------------------------------------
    // FULL WORKED EXAMPLE - delete(8) from:
    // ------------------------------------------------------------------
    //          5
    //         / \
    //        2   8
    //           / \
    //          7   9
    //
    //   Step 1: Walk down to node 8. val(8) == node.val(8) -> found it.
    //           node.left = 7 (not null), node.right = 9 (not null)
    //           -> BOTH children exist -> this is CASE 3.
    //
    //   Step 2: successor = findMin(node.right)
    //           node.right is the subtree rooted at 9. Does 9 have a
    //           left child? No -> 9 IS the minimum of that subtree.
    //           successor = node holding value 9.
    //
    //   Step 3: node.val = successor.val
    //           The node that used to hold "8" now holds "9".
    //           Tree temporarily looks like (note: TWO 9's exist now):
    //                  5
    //                 / \
    //                2   9   <- was 8, now overwritten to 9
    //                   / \
    //                  7   9  <- original successor node, now a duplicate
    //
    //   Step 4: node.right = deleteRecursive(node.right, 9)
    //           This deletes value 9 starting from the subtree rooted
    //           at the ORIGINAL 9 node (not the one we just overwrote).
    //           Inside that call: node==9(original), val==node.val,
    //           node.left==null -> CASE 1/2 shortcut -> returns
    //           node.right, which is null (9 was a leaf).
    //           So: node.right = null -> the duplicate/original 9 leaf
    //           is now disconnected from the tree entirely.
    //
    //   FINAL RESULT:
    //          5
    //         / \
    //        2   9
    //           /
    //          7
    //
    //   Notice: the VALUE 8 is gone, the VALUE 9 now appears exactly
    //   ONCE (in the node that used to be "8"), and the BST invariant
    //   still holds everywhere. Success!
    //
    // ------------------------------------------------------------------
    // ALGORITHM (step by step) - full method, all 3 cases
    // ------------------------------------------------------------------
    // 1. If node == null, value not found - nothing to delete, return null.
    // 2. If val < node.val -> recurse left, reassign node.left to the result.
    // 3. If val > node.val -> recurse right, reassign node.right to the result.
    // 4. If val == node.val -> THIS is the node to delete:
    //      a. If node.left == null -> return node.right (handles both
    //         "no children" and "only right child" cases at once - if
    //         node.right is also null, this correctly returns null).
    //      b. Else if node.right == null -> return node.left (handles
    //         "only left child" case).
    //      c. Else (two children) -> run the successor plan above.
    // 5. Return node (possibly restructured) to be relinked by the parent.
    //
    // Time:  O(h) - one root-to-leaf-ish path down, plus (in Case 3) one
    //         more path down to find/remove the successor.
    // Space: O(h) recursion stack.
    //
    // ---------------------------------------------------------------------
    // SIMPLE WORDS MEIN SAMJHO (Case 3 - dono children wala case)
    // ---------------------------------------------------------------------
    // Jab node ke DONO children hon, toh use seedha hata nahi sakte -
    // kyunki fir do alag-alag subtrees (left aur right) bach jaayenge,
    // aur parent ke paas unhe jodne ke liye sirf EK hi jagah hai. Toh
    // trick yeh hai: node ko physically hatao hi mat, bas uski VALUE
    // ko kisi aisi value se badal do jo waha "fit" ho jaaye - fir us
    // value ko uski ASLI jagah se delete kar do (jo hamesha easy case
    // 1/2 hoga).
    //
    // "Fit hone" ka matlab: aisi value jo left subtree ki HAR value se
    // BADI ho (taaki left side ka BST rule tootey na) AUR right subtree
    // ki BAAKI saari values se CHOTI ho (taaki right side ka rule bhi
    // bana rahe). Aisi value milti hai RIGHT subtree ki SABSE CHOTI
    // value mein - jise "in-order successor" kehte hain. Ise dhoondhne
    // ke liye bas node.right se shuru karke jitna ho sake LEFT chalte
    // jao (findMin() function yehi karta hai).
    //
    // Successor left subtree se badi kyun hogi? Kyunki woh node.right
    // wale subtree se aayi hai, aur BST rule ke hisaab se node.right ka
    // pura subtree hamesha node.val se BADA hota hai, aur node.val khud
    // left subtree ki har value se bada hota hai. Toh: left subtree <
    // node.val < successor - hमेशा true hoga.
    //
    // Successor right subtree ki baaki values se choti kyun hogi? Kyunki
    // woh DEFINITION se hi us subtree ki SABSE CHOTI value hai - findMin
    // jitna ho sake left chalta hai, aur BST mein kisi bhi subtree ka
    // leftmost node hamesha uska minimum value hota hai.
    //
    // Toh 3 steps mein kaam ho jaata hai:
    //   a. successor = findMin(node.right)   // right taraf ki sabse choti value
    //   b. node.val = successor.val          // usko current node ki jagah "move" karo
    //   c. node.right = deleteRecursive(node.right, successor.val)
    //      // successor ko uski ASLI jagah se hata do (ab woh duplicate
    //      // ho gaya hai - node.val mein uski copy already aa chuki hai)
    //
    // Step (c) hamesha EASY (Case 1 ya 2) hi hoga, kabhi Case 3 nahi -
    // kyunki successor us subtree ka SABSE LEFT wala node hai, uska
    // sirf RIGHT child ho sakta hai (ya koi child hi nahi), LEFT child
    // kabhi nahi ho sakta (agar hota toh findMin usi mein aage chala
    // jaata aur asli successor wahi hota).
    //
    // ---------------------------------------------------------------------
    // PURA UDAHARAN (worked example) - delete(8) is tree se:
    // ---------------------------------------------------------------------
    //          5                                    5
    //         / \                                  / \
    //        2   8          delete(8)  --->        2   9
    //           / \                                    /
    //          7   9                                  7
    //
    //   1. Node 8 mil gaya (val==node.val). Uske DONO children hain
    //      (7 aur 9) -> yeh CASE 3 hai.
    //   2. successor = findMin(node.right=9) -> 9 ka koi left child
    //      nahi hai, matlab 9 khud hi us subtree ka minimum hai ->
    //      successor = 9.
    //   3. node.val = 9 -> jo node pehle "8" tha, ab woh "9" ho gaya.
    //      (Is waqt tree mein 9 do baar hai - temporary duplicate.)
    //   4. node.right = deleteRecursive(node.right, 9) -> yeh ASLI
    //      9-wale node ko delete karta hai (naya wala nahi, jo humne
    //      abhi overwrite kiya). Woh 9 ek leaf hai (Case 1) -> null
    //      return hota hai -> node.right = null.
    //   5. Result: "8" wala node ab "9" ban chuka hai, uska left child
    //      (7) as-it-is hai, right child null ho gaya - duplicate 9
    //      hamesha ke liye hat gaya. BST rule bhi poori tarah bana
    //      hua hai!
    // =====================================================================
    public void delete(int val) {
        root = deleteRecursive(root, val);
    }

    private Node deleteRecursive(Node node, int val) {
        if (node == null) return null;

        if (val < node.val) {
            node.left = deleteRecursive(node.left, val);
        } else if (val > node.val) {
            node.right = deleteRecursive(node.right, val);
        } else {
            // Found the node to delete.
            if (node.left == null) return node.right;   // 0 or 1 (right) child
            if (node.right == null) return node.left;   // 1 (left) child

            // Two children: find in-order successor (smallest in right subtree).
            Node successor = findMin(node.right);
            node.val = successor.val;
            node.right = deleteRecursive(node.right, successor.val);
        }
        return node;
    }

    private Node findMin(Node node) {
        // Sabse chota value hamesha SABSE LEFT wale node mein milega -
        // kyunki BST rule ke hisaab se left hamesha chota hota hai.
        // Isliye jab tak left child hai, left mein chalte raho.
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    // =====================================================================
    // IN-ORDER TRAVERSAL (sanity check - should always print sorted values)
    // ---------------------------------------------------------------------
    // SIMPLE WORDS MEIN SAMJHO
    // ---------------------------------------------------------------------
    // Left subtree poora ghoomo, fir current node print karo, fir right
    // subtree poora ghoomo. BST mein yeh hamesha ASCENDING SORTED order
    // deta hai - isi liye ise sanity check ke tor pe use karte hain, taaki
    // pata chale ki insert/delete ke baad bhi tree valid BST hi hai.
    // =====================================================================
    public java.util.List<Integer> inorder() {
        java.util.List<Integer> result = new java.util.ArrayList<>();
        inorderRecursive(root, result);
        return result;
    }

    private void inorderRecursive(Node node, java.util.List<Integer> result) {
        if (node == null) return;
        inorderRecursive(node.left, result);
        result.add(node.val);
        inorderRecursive(node.right, result);
    }

    // Simple manual tests.
    // (Manual testing - insert karo, search/delete karke check karo ki
    // in-order traversal hamesha sorted hi aa raha hai.)
    public static void main(String[] args) {
        BinarySearchTree bst = new BinarySearchTree();

        //        Inserting: 5, 2, 8, 1, 3, 7, 9
        //                5
        //               / \
        //              2   8
        //             / \ / \
        //            1  3 7  9
        int[] values = {5, 2, 8, 1, 3, 7, 9};
        for (int v : values) bst.insert(v);

        System.out.println("In-order (sorted)      : " + bst.inorder()); // [1, 2, 3, 5, 7, 8, 9]

        System.out.println("search(7)              : " + bst.search(7));  // true
        System.out.println("search(4)              : " + bst.search(4));  // false

        bst.delete(1); // leaf node
        System.out.println("After delete(1) (leaf) : " + bst.inorder()); // [2, 3, 5, 7, 8, 9]

        bst.delete(2); // node with one child (3)
        System.out.println("After delete(2) (1-child): " + bst.inorder()); // [3, 5, 7, 8, 9]

        bst.delete(8); // node with two children (7, 9)
        System.out.println("After delete(8) (2-children): " + bst.inorder()); // [3, 5, 7, 9]
    }
}

