public class BubbleSort {

    public static void bubbleSort(int[] arr){
        int n = arr.length;

        // Each pass bubbles the largest remaining element to the end
        for(int i = 0; i < n - 1; i++){
            boolean swapped = false;

            // Compare adjacent elements; last i elements are already sorted
            for(int j = 0; j < n - 1 - i; j++){
                if(arr[j] > arr[j + 1]){
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swapped = true;
                }
            }

            // If no swaps in this pass, the array is already sorted
            if(!swapped){
                break;
            }
        }

    }



    static void printArrray(int arr[]){
        int i;
        for(i=0;i<arr.length;i++){
            System.out.print(arr[i]+" ");
        }
        System.out.println();
    }




    public static void main(String args[]){
        int arr[] = {9,1,2,5,6,8,7};
        bubbleSort(arr);
        System.out.println("sorted array :");
        printArrray(arr);
    }

}
