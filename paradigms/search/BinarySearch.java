package search;

public class BinarySearch {
    public static void main(String[] args) {
        int length = args.length - 1;
        int[] array = new int[length];
        int x = Integer.parseInt(args[0]);
        for (int i = 0; i < length; i++) {
            array[i] = Integer.parseInt(args[i + 1]);
        }
        System.out.println(Search.recursive(array, x));
    }
}