package expression.generic;

import java.util.Map;

import expression.math.*;
import expression.ExpressionException;
import expression.GenericExpression;
import expression.parser.ExpressionParser;
import expression.parser.ParseException;
import expression.parser.StringSource;

public class GenericTabulator implements Tabulator {

    private static final Map<String, MathMode<? extends Number>> MATH = Map.of(
        "i", new CheckedIntMath(),
        "bi", new BigIntMath(),
        "d", new DoubleMath(),
        "u", new IntMath(),
        "f", new FloatMath(),
        "b", new ByteMath()
    );

    public static void main(String[] args) {
        if (args.length != 2) {
            showInstruction("Two arguments required.");
            return;
        }
        if (args[0].charAt(0) != '-') {
            showInstruction("Wrong first argument.");
            return;
        }
        String mode = args[0].substring(1);
        if (!MATH.containsKey(mode)) {
            showInstruction("This type does not exist.");
            return;
        }

        int lb = -2, rb = 2;
        try {
            System.out.println(toString(new GenericTabulator().tabulate(MATH.get(mode), args[1], lb, rb, lb, rb, lb, rb)));
        } catch (ParseException e) {
            System.out.println("Invalid expression: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An error has occured: " + e.getMessage());
        }
    }

    public static String toString(Object[][][] table) {
        if (table == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder("[\n");
        for (int x = 0; x < table.length; x++) {
            sb.append("[");
            for (int y = 0; y < table[0].length; y++) {
                sb.append("[");
                for (int z = 0; z < table[0][0].length; z++) {
                    sb.append(table[x][y][z] + next(z, table[0][0].length));
                }
                sb.append(next(y, table[0].length) + "\n");
            }
            sb.append(next(x, table.length) + "\n");
        }
        return sb.toString();
    }

    private static String next(int index, int bound) {
        return index == bound - 1 ? "]" : ", ";
    }

    private static void showInstruction(String string) {
        StringBuilder sb = new StringBuilder(string).append('\n');
        sb.append("First argument must be a type in which the calculations will be performed:\n");
        sb.append("Option\tType\n");
        for (String option : MATH.keySet()) {
            sb.append("-" + option + "\t" + MATH.get(option).getClass().getSimpleName() + "\n");
        }
        sb.append("Second argument must be an expression F(x, y, z)\n");
        System.out.println(sb);
    }

    @Override
    public Object[][][] tabulate(String mode, String expression, int x1, int x2, int y1, int y2, int z1, int z2) throws Exception {
        return tabulate(MATH.get(mode), expression, x1, x2, y1, y2, z1, z2);
    }

    private <N extends Number> Object[][][] tabulate(MathMode<N> math, String stringExpression, int x1, int x2, int y1, int y2, int z1, int z2) throws Exception {
        GenericExpression expression = new ExpressionParser().parse(new StringSource(stringExpression));
        int xSize = x2 - x1 + 1; 
        int ySize = y2 - y1 + 1;
        int zSize = z2 - z1 + 1;

        Object[][][] table = new Object[xSize][ySize][zSize];
        for (int x = 0; x < xSize; x++) {
            N first = math.valueOf(x1 + x);
            for (int y = 0; y < ySize; y++) {
                N second = math.valueOf(y1 + y);
                for (int z = 0; z < zSize; z++) {
                    N third = math.valueOf(z1 + z);
                    try {
                        table[x][y][z] = expression.evaluate(
                            math,
                            first,
                            second,
                            third
                        );
                    } catch (ExpressionException e) {
                        table[x][y][z] = null;
                    }
                }
            }
        }
        return table;
    }

}
