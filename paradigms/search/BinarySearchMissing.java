package search;

public class BinarySearchMissing {
    public static void main(String[] args) {
        System.out.println(result(args, SearchMissing.ITERATIVE));
    }

    private static String result(String[] args, SearchMissing searchMethod) {
        if (args == null) {
            return "Input array is null.";
        }
        if (args.length < 1) {
            return "Argument element required.";
        }
        int length = args.length - 1;
        int[] array = new int[length];
        try {
            int x = Integer.parseInt(args[0]);
            for (int i = 0; i < length; i++) {
                array[i] = Integer.parseInt(args[i + 1]);
            }
            return Integer.toString(searchMethod.search(array, x));
        } catch (NumberFormatException e) {
            return "Wrong arguments. Integers expected.";
        }
    }
}