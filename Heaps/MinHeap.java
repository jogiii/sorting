package Heaps;

import java.util.Arrays;

/**
 * A Min-Heap implementation backed by a dynamic array.
 *
 * Invariant: For every node i, heap[i] <= heap[left(i)] and heap[i] <= heap[right(i)].
 * This means the SMALLEST element is always at the root (index 0).
 *
 * Array representation of a complete binary tree, for a node at index i:
 *   - parent index  = (i - 1) / 2
 *   - left child     = 2 * i + 1
 *   - right child    = 2 * i + 2
 */
public class MinHeap {

    private int[] heap;
    private int size;
    private int capacity;

    public MinHeap(int capacity) {
        this.capacity = capacity;
        this.heap = new int[capacity];
        this.size = 0;
    }

    public int parent(int i){
        return (i-1)/2;
    }

    public int leftChild(int i){
        return 2*i+1;
    }
    public int rightChild(int i){
        return 2*i+2;
    }

    public void resizeIfNeeded(){
        if (size == capacity){
            capacity *= 2;
            heap = Arrays.copyOf(heap, capacity);
        }
    }

    public void swap(int i, int j){
        int temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    public void insert(int value){
        resizeIfNeeded();
        heap[size] = value;
        int i = size;
        size++;
        heapifyUp(i);
    }

    public void heapifyUp(int i){
        while(i > 0 && heap[i] < heap[parent(i)]){
            swap(i, parent(i));
            i = parent(i);
        }
    }
    public void printHeap() {
        System.out.println(Arrays.toString(Arrays.copyOf(heap, size)));
    }
    /**
     * Remove and return the smallest element (the root).
     * Steps:
     *  1. Save the root value (min).
     *  2. Move the LAST element in the array to the root position.
     *  3. Shrink the size by 1.
     *  4. "Bubble down" (sift down / heapify): compare the new root with its
     *     children and swap with the smaller child until the heap property
     *     is restored.
     * Time Complexity: O(log n)
     */

    public int extractMin(){
        if (size == 0){
            throw new IllegalStateException("Heap is empty");
        }
        int min = heap[0];
        heap[0] = heap[size - 1];
        size--;
        heapifyDown(0);
        return min;
    }

    private void heapifyDown(int i){
        int smallest = i;
        int l = leftChild(i);
        int r = rightChild(i);

        if(l < size && heap[l] < heap[smallest]){
            smallest = l;
        }
        if(r < size && heap[r] < heap[smallest]){
            smallest = r;
        }
        if(smallest != i){
            swap(i, smallest);
            heapifyDown(smallest);
        }
    }
    /** Return the smallest element without removing it. O(1) */
    public int peek() {
        if (size == 0) throw new IllegalStateException("Heap is empty");
        return heap[0];
    }

    public boolean isEmpty() { return size == 0; }
    public int size() { return size; }
    public static void main(String[] args) {
        MinHeap minHeap = new MinHeap(10);
        int[] values = {5, 3, 8, 1, 9, 2, 7};
        for (int v : values) {
            minHeap.insert(v);
        }

        System.out.print("MinHeap array form: ");
        minHeap.printHeap();

       /* System.out.print("Extracting in ascending order: ");
        while (!minHeap.isEmpty()) {
            System.out.print(minHeap.extractMin() + " ");
        }
        System.out.println();

        // buildHeap example
        int[] arr = {5, 3, 8, 1, 9, 2, 7};
        MinHeap built = MinHeap.buildHeap(arr);
        System.out.print("Built heap (O(n)) array form: ");
        built.printHeap();*/
    }



}

