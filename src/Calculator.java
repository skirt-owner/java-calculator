import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayDeque;

public class Calculator {
    // Helper method to check if a token is an operator
    private static boolean isOperator(Character token) {
        return (token == '+') ||( token == '-') || (token == '*') || (token == '/');
    }

    // Determine order of operations
    private static int orderOf(Character operator) {
        return switch (operator) {
            case '+', '-' -> 1;
            case '*', '/' -> 2;
            default -> 0;
        };
    }

    // Perform the arithmetic operation
    private static BigDecimal calculate(Character operator, BigDecimal numberA, BigDecimal numberB) {
        return switch (operator) {
            case '+' -> numberA.add(numberB);
            case '-' -> numberA.subtract(numberB);
            case '*' -> numberA.multiply(numberB);
            case '/' -> {
                if (numberB.compareTo(BigDecimal.ZERO) == 0) {
                    throw new ArithmeticException("Can't divide by zero");
                }
                yield numberA.divide(numberB, 10, RoundingMode.HALF_UP);
            }
            default -> BigDecimal.ZERO;
        };
    }

    // Pop and calculate the result for an operation
    private static void prepare(ArrayDeque<Character> operators, ArrayDeque<BigDecimal> numbers) {
        if (operators.isEmpty() || numbers.size() < 2) {
            throw new IllegalArgumentException("Incorrect format of the initial equation");
        }
        char operator = operators.removeFirst();
        BigDecimal numberB = numbers.removeFirst();
        BigDecimal numberA = numbers.removeFirst();
        numbers.push(calculate(operator, numberA, numberB));
    }

    private static boolean isValid(String equation) {
        return !(equation == null || equation.isBlank() || !equation.matches("^[0-9+\\-*/().\\s]+$"));
    }

    private static boolean isDigit(Character token) {
        return Character.isDigit(token) || token == '.';
    }

    // Method to display the result of the calculation
    public String display(String equation) {
        if (!isValid(equation)) throw new IllegalArgumentException("Incorrect equation format");

        ArrayDeque<Character> operators = new ArrayDeque<>();
        ArrayDeque<BigDecimal> numbers = new ArrayDeque<>();

        int length = equation.length();
        Character prevToken = null;

        for (int i = 0; i < length; ++i) {
            char token = equation.charAt(i);

            if (Character.isWhitespace(token)) continue;

            if (token == '.' || Character.isDigit(token)) {
                if (!(prevToken == null || prevToken == 'o' || prevToken == '(' || prevToken == 'u')){
                    throw new IllegalArgumentException("Incorrect format of the initial equation");
                }
                StringBuilder numberBuilder = new StringBuilder();

                int decimalPointCounter = 0;
                while (i < length && (isDigit(equation.charAt(i)))) {
                    if (equation.charAt(i) == '.') decimalPointCounter++;
                    numberBuilder.append(equation.charAt(i++));
                }

                if (decimalPointCounter > 1) {
                    throw new IllegalArgumentException("Incorrect format of the initial equation");
                }

                String number = numberBuilder.toString();
                numbers.push(new BigDecimal(number.compareTo(".") == 0 ? "0.0" : number));
                prevToken = 'n';
                i--;
            } else if (token == '(') {
                operators.push(token);
                prevToken = '(';
            } else if (token == ')') {
                while (!operators.isEmpty() && operators.peek() != '(') {
                    prepare(operators, numbers);
                }
                operators.removeFirst();
                prevToken = ')';
            } else if (isOperator(token)) {
                boolean isUnary = ((prevToken == null) || (prevToken == '(') || (prevToken == 'o'));

                if (isUnary && (token == '-' || token == '+')) {
                    numbers.push(BigDecimal.ZERO);
                    prevToken = 'u';
                } else {
                    while (!operators.isEmpty() && isOperator(operators.peek()) &&
                            orderOf(operators.peek()) >= orderOf(token)) {
                        prepare(operators, numbers);
                    }
                    prevToken = 'o';
                }
                operators.push(token);
            } else {
                throw new IllegalArgumentException("Invalid character encountered: " + token);
            }
        }

        while (!operators.isEmpty()) {
            char op = operators.peek();
            if (op == '(' || op == ')') {
                throw new IllegalArgumentException("Mismatched parentheses.");
            }
            prepare(operators, numbers);
        }

        return numbers.removeFirst().setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    // Main method to test the Calculator
    public static void main(String[] args) {
        Calculator calculator = new Calculator();

        String equation = args.length > 0 ? args[0] : null;

        try {
            String result = calculator.display(equation);
            System.out.println(result);
        } catch (IllegalArgumentException | ArithmeticException e) {
            System.out.println("Equation: " + equation + " --> Error: " + e.getMessage());
        }
    }
}