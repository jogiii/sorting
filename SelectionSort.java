/*
 * SELECTION SORT
 * --------------
 * Idea bilkul seedha hai: har round mein, baaki bache hue (unsorted)
 * part mein se sabse CHOTA element dhoondo, aur usko sabse aage wali
 * unsorted position pe swap kar do. Yeh karte raho jab tak pura array
 * cover na ho jaaye.
 *
 * Matlab: "select karo minimum, fir swap karo" - baar baar.
 *
 * Time: O(n^2) hamesha (best/worst/average sab same) - comparisons
 * kam nahi hote chahe array sorted ho ya na ho.
 * Space: O(1) - in-place sort, extra array nahi chahiye.
 */
public class SelectionSort {

    static void selectionSort(int [] arr){
        int n = arr.length;

        // Traverse through all array elements
        // Last element doesn't need to be compared, so loop runs till n-1
        for(int i=0;i<n-1;i++){
  // Assume the current position holds the minimum element
            int min = i;

            // Find the actual minimum element in the remaining unsorted array
            for(int j =i+1;j<n;j++){
                if(arr[j]<arr[min]){
                    min =j;
                }
            }

            int temp = arr[i];
            arr[i] = arr[min];
            arr[min] = temp;
        }
    }

    static void printArray(int[] arr){
        for (int val : arr) {
            System.out.print(val + " ");
        }
        System.out.println();
    }

    public static void main(String[] args){
        int[] arr = { 64, 25, 12, 22, 11 };

        System.out.print("Original array: ");
        printArray(arr);

        selectionSort(arr);

        System.out.print("Sorted array: ");
        printArray(arr);
    }

}
