public class BubbleSort {

    public static void bubbleSort(int[] arr){
        int i,j,temp;

        boolean swapped;
        for( i=0;i<arr.length-1;i++){
            swapped =false; // if array is sorted then thos flag will skip subsequent loops little optimisation
            for(j=0;j<arr.length-1;j++){
                if(arr[j]>arr[j+1]){
                    temp=arr[j];
                    arr[j]=arr[j+1];
                    arr[j+1]=temp;
                    swapped = true;
                }
            }

            if(swapped == false)
                break;
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
        int arr[] = {64,34,25,12,22,11,90};
        int n=arr.length;
        bubbleSort(arr);
        System.out.println("sorted array :");
        printArrray(arr);
    }
    
}
