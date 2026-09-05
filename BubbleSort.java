/*
 * BUBBLE SORT
 * -----------
 * Simple si baat hai: baar-baar pura array ghumo, aur jab bhi do paas-paas
 * wale elements galat order mein mile (bada pehle, chota baad mein), unko
 * aapas mein swap kar do. Ek baar poori list ghoomne ke baad sabse bada
 * element "bubble" ho ke sabse aakhri mein pahunch jaata hai - isiliye
 * naam Bubble Sort pada.
 *
 * Agar kisi pass mein ek bhi swap nahi hua, matlab array already sorted
 * hai - toh wahin ruk jao (early exit), bekar mein pura loop mat chalao.
 *
 * Time: O(n^2) worst/average case, O(n) best case (already sorted).
 * Space: O(1) - koi extra array nahi chahiye.
 */
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
