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
