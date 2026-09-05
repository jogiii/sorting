package Heaps;

import java.util.Arrays;

/**
 * A Max-Heap implementation backed by a dynamic array.
 *
 * Invariant: For every node i, heap[i] >= heap[left(i)] and heap[i] >= heap[right(i)].
 * This means the LARGEST element is always at the root (index 0).
 *
 * This is the mirror image of MinHeap.java - every comparison is flipped.
 *
 * SIMPLE WORDS MEIN SAMJHO:
 * Max-Heap mein sabse BADA element hamesha upar (root, index 0) baitha
 * rehta hai. Jab bhi naya element daalte ho, use array ke aakhri mein
 * daalo fir "bubble up" karo - jab tak woh apne parent se bada hai,
 * uske saath swap karte raho, upar chadte raho. MinHeap ka bilkul ulta
 * logic hai yahan.
 */
public class MaxHeap {

    private int[] heap;
    private int size;
    private int capacity;

    public MaxHeap(int capacity){
        this.capacity = capacity;
        this.size = 0;
        this.heap = new int[capacity];
    }

    private int parent(int i){
        return (i-1)/2;
    }

    private int leftChild(int i){
        return 2*i+1;
    }
    private int rightChild(int i){
        return 2*i+2;
    }

    /**
     * Insert a value: place at the end, then bubble up while it is
     * GREATER than its parent (opposite condition to MinHeap).
     * Time Complexity: O(log n)
     */
    public void insert(int value){
        resizeIfNeeded();
        heap[size] = value;
        int i = size;
        size++;
        heapifyUp(i);

    }

    public void heapifyUp(int i){
        while(i>0 && heap[i]>heap[parent(i)]){
            swap(i, parent(i));
            i = parent(i);
        }
    }

    public void resizeIfNeeded(){
        if(size == capacity){
            capacity = capacity*2;
            heap = Arrays.copyOf(heap, capacity);
        }
    }

    public void swap(int i, int j){
        int temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }
    public void printHeap() {
        System.out.println(Arrays.toString(Arrays.copyOf(heap, size)));
    }

    public int extractMax(){
        if(size == 0){
            throw new IllegalStateException("Heap is empty");
        }
        int max = heap[0];
        heap[0] = heap[size-1];
        size--;
        heapifyDown(0);
        return max;

    }

    public void heapifyDown(int i){
        int largest = i;
        int l = leftChild(i);
        int r = rightChild(i);

        if(l<size && heap[l]>heap[largest]){
            largest = l;
        }
        if(r<size && heap[r]>heap[largest]){
            largest = r;
        }
        if(largest != i){
            swap(i, largest);
            heapifyDown(largest);
        }
    }
    public int peek() {
        if (size == 0) throw new IllegalStateException("Heap is empty");
        return heap[0];
    }
    public boolean isEmpty() { return size == 0; }
    public int size() { return size; }

    public static void main(String[] args){
        MaxHeap maxHeap = new MaxHeap(10);
        int[] values = {5,3,8,1,9,2,7};
        for(int v :values){
            maxHeap.insert(v);
        }
        System.out.print("MaxHeap array form: ");
        maxHeap.printHeap();

        System.out.print("Extracting in descending order: ");
        while (!maxHeap.isEmpty()) {
            System.out.print(maxHeap.extractMax() + " ");
        }
        System.out.println();
    }

}

