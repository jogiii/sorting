package Heaps;

import java.util.Arrays;

/**
 * Heap Sort
 * ---------
 * Heap Sort sorts an array IN-PLACE using the Max-Heap data structure.
 *
 * High level idea:
 *  1. Build a max-heap from the input array. After this step, the largest
 *     element is at index 0 (the root).
 *  2. Swap the root (largest) with the last element of the (unsorted part of the)
 *     array. This places the largest element in its final sorted position.
 *  3. Reduce the "heap size" by 1 (excluding the now-sorted last element) and
 *     heapify-down the root to restore the max-heap property.
 *  4. Repeat steps 2-3 until the heap size is 1.
 *
 * Because we repeatedly extract the max and place it at the end, the array
 * ends up sorted in ASCENDING order.
 *
 * Time Complexity:
 *  - Build heap: O(n)
 *  - n calls to heapify (each O(log n)): O(n log n)
 *  - Total: O(n log n) in all cases (best, average, worst) - very predictable!
 *
 * Space Complexity: O(1) extra space (sorts in-place, ignoring recursion stack
 *                    for heapify which is O(log n)).
 *
 * Stability: NOT stable (equal elements can be reordered relative to each other).
 */
public class HeapSort {

    public static void heapSort(int[] arr) {
        int n = arr.length;

        // Step 1: Build max heap.
        // Start from the last non-leaf node and heapify down to the root.
        // Leaf nodes are already trivially valid heaps, so we skip them.
        //
        // Why are leaf nodes trivially valid heaps?
        // The max-heap property only constrains a node relative to its
        // CHILDREN (parent >= children). A leaf has no children, so there is
        // nothing to compare against - the property holds vacuously.
        //
        // Concrete example: arr = {12, 11, 13, 5, 6, 7}, n = 6
        //
        //          12(0)
        //         /      \
        //     11(1)       13(2)
        //     /   \        /
        //   5(3) 6(4)   7(5)
        //
        // Node 3 (value 5): children would be at 2*3+1=7 and 2*3+2=8, both
        //                    >= n=6 -> no children -> leaf, trivially valid.
        // Node 4 (value 6): children at 9, 10 -> out of bounds -> leaf.
        // Node 5 (value 7): children at 11, 12 -> out of bounds -> leaf.
        //
        // The last non-leaf index is n/2 - 1 = 6/2 - 1 = 2, so the loop below
        // only visits indices 2, 1, 0 (the internal nodes), skipping the
        // leaves at 3, 4, 5 since heapify() on them would be a guaranteed
        // no-op (left/right both out of range -> largest stays i -> no swap).
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(arr, n, i);
        }

        // Step 2: Extract elements one by one from the heap.
        for (int end = n - 1; end > 0; end--) {
            // Move current root (the max of the remaining heap) to the end.
            swap(arr, 0, end);

            // Call heapify on the reduced heap (size = end, root = 0).
            heapify(arr, end, 0);
        }
    }

    /**
     * Sift-down: ensures the subtree rooted at index i is a valid max-heap,
     * assuming the subtrees rooted at its children are already valid heaps.
     *
     * @param arr the array being treated as a heap
     * @param n   the current logical size of the heap (elements beyond n are
     *            already sorted and excluded)
     * @param i   the root index of the subtree to fix
     */
    private static void heapify(int[] arr, int n, int i) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if (left < n && arr[left] > arr[largest]) {
            largest = left;
        }
        if (right < n && arr[right] > arr[largest]) {
            largest = right;
        }

        if (largest != i) {
            swap(arr, i, largest);
            // Recursively heapify the affected subtree.
            heapify(arr, n, largest);
        }
    }

    private static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    public static void main(String[] args) {
        int[] arr = {12, 11, 13, 5, 6, 7};
        System.out.println("Before: " + Arrays.toString(arr));
        heapSort(arr);
        System.out.println("After:  " + Arrays.toString(arr));
    }
}

