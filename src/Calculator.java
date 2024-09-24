package src;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.Scanner;

/**
 * The {@code Calculator} class provides functionality to evaluate arithmetic expressions.
 * It supports basic operations such as addition, subtraction, multiplication, and division,
 * along with handling parentheses and unary operators.
 *
 * <p>This class utilizes Java 22 features, including enhanced switch expressions and improved
 * exception handling mechanisms. It also demonstrates the use of {@code ArrayDeque} for
 * efficient stack operations.</p>
 *
 * <p>Usage Example:</p>
 * <pre>{@code
 * Calculator calculator = new Calculator();
 * String equation = "3 + 4 * (2 - 1)";
 * String result = calculator.display(equation);
 * System.out.println(result); // Outputs: 7
 * }</pre>
 *
 * @author <a href="https://github.com/skirt-owner">skirt-owner</a>
 * @version 1.0
 * @since 2024-09-20
 */
public class Calculator {

    /**
     * Checks if the given character token is a supported operator.
     *
     * <p>Supported operators are: '+', '-', '*', '/'.</p>
     *
     * @param token the character to check
     * @return {@code true} if the token is an operator; {@code false} otherwise
     */
    private static boolean isOperator(Character token) {
        return (token == '+') || (token == '-') || (token == '*') || (token == '/');
    }

    /**
     * Determines the precedence order of the given operator.
     *
     * <p>Operators '*' and '/' have higher precedence (2) compared to '+' and '-' (1).
     * Any other character has a precedence of 0.</p>
     *
     * @param operator the operator character
     * @return an integer representing the precedence order
     */
    private static int orderOf(Character operator) {
        return switch (operator) {
            case '+', '-' -> 1;
            case '*', '/' -> 2;
            default -> 0;
        };
    }

    /**
     * Performs the arithmetic operation based on the provided operator and operands.
     *
     * <p>If a division by zero is attempted, the method returns {@code Double.POSITIVE_INFINITY}.</p>
     *
     * @param operator the operator character ('+', '-', '*', '/')
     * @param numberA  the first operand
     * @param numberB  the second operand
     * @return the result of the operation as a {@code Double}
     */
    private static Double calculate(Character operator, Double numberA, Double numberB) {
        return switch (operator) {
            case '+' -> numberA + numberB;
            case '-' -> numberA - numberB;
            case '*' -> numberA * numberB;
            case '/' -> {
                if (numberB.compareTo(0.0) == 0) {
                    yield Double.POSITIVE_INFINITY;
                }
                yield numberA / numberB;
            }
            default -> 0.0;
        };
    }

    /**
     * Pops the top operator from the operators stack and applies it to the top two numbers
     * in the numbers stack. The result is then pushed back onto the numbers stack.
     *
     * @param operators the stack of operators
     * @param numbers   the stack of numbers
     * @throws IllegalArgumentException if the operator stack is empty or there are fewer than two numbers
     */
    private static void prepare(ArrayDeque<Character> operators, ArrayDeque<Double> numbers) {
        if (operators.isEmpty() || numbers.size() < 2) {
            throw new IllegalArgumentException("Incorrect format of the initial equation");
        }
        char operator = operators.removeFirst();
        Double numberB = numbers.removeFirst();
        Double numberA = numbers.removeFirst();
        numbers.push(calculate(operator, numberA, numberB));
    }

    /**
     * Validates the input equation string.
     *
     * <p>The equation must not be {@code null}, blank, and should only contain digits,
     * operators ('+', '-', '*', '/'), parentheses '(', ')', decimal points '.', and whitespace.</p>
     *
     * @param equation the input equation string
     * @return {@code true} if the equation is valid; {@code false} otherwise
     */
    private static boolean isValid(String equation) {
        return !(equation == null || equation.isBlank() || !equation.matches("^[0-9+\\-*/().\\s]+$"));
    }

    /**
     * Checks if the given character token is a digit or a decimal point.
     *
     * @param token the character to check
     * @return {@code true} if the token is a digit or '.'; {@code false} otherwise
     */
    private static boolean isDigit(Character token) {
        return Character.isDigit(token) || token == '.';
    }

    /**
     * Evaluates the arithmetic expression provided as a string and returns the result.
     *
     * <p>The method handles operator precedence, parentheses, and unary operators.
     * It returns the result rounded to two decimal places, removing any trailing zeros.</p>
     *
     * @param equation the arithmetic expression to evaluate
     * @return the result of the evaluation as a {@code String}
     * @throws IllegalArgumentException if the equation format is incorrect or contains invalid characters
     */
    public String display(String equation) {
        if (!isValid(equation)) {
            throw new IllegalArgumentException("Incorrect format of the initial equation");
        }

        // Stacks for operators and numbers
        ArrayDeque<Character> operators = new ArrayDeque<>();
        ArrayDeque<Double> numbers = new ArrayDeque<>();

        int length = equation.length();
        Character prevToken = null;

        for (int i = 0; i < length; ++i) {
            char token = equation.charAt(i);

            // Skip whitespace characters
            if (Character.isWhitespace(token)) continue;

            if (token == '.' || Character.isDigit(token)) {
                // Validate token sequence for numbers
                if (!(prevToken == null || prevToken == 'o' || prevToken == '(' || prevToken == 'u')) {
                    throw new IllegalArgumentException("Incorrect format of the initial equation around number");
                }
                StringBuilder numberBuilder = new StringBuilder();

                int decimalPointCounter = 0;
                // Build the complete number (including decimal part if any)
                while (i < length && isDigit(equation.charAt(i))) {
                    if (equation.charAt(i) == '.') decimalPointCounter++;
                    numberBuilder.append(equation.charAt(i++));
                }

                // Ensure only one decimal point is present
                if (decimalPointCounter > 1) {
                    throw new IllegalArgumentException("Incorrect format of the decimal number");
                }

                String number = numberBuilder.toString();
                // Handle standalone decimal point
                numbers.push(Double.parseDouble(number.equals(".") ? "0.0" : number));
                prevToken = 'n';
                i--; // Adjust index since it will be incremented in the loop
            } else if (token == '(') {
                // Validate token sequence for parentheses
                if (!(prevToken == null || prevToken == 'o' || prevToken == 'u')) {
                    throw new IllegalArgumentException("Incorrect format of the initial equation  around '('");
                }
                operators.push(token);
                prevToken = '(';
            } else if (token == ')') {
                if (!(prevToken != null && (prevToken == 'n' || prevToken == ')'))) {
                    throw new IllegalArgumentException("Incorrect format of the initial equation around ')'");
                }
                // Resolve all operators inside the parentheses
                while (!operators.isEmpty() && operators.peek() != '(') {
                    prepare(operators, numbers);
                }
                if (operators.isEmpty()) {
                    throw new IllegalArgumentException("Mismatched parentheses.");
                }
                operators.removeFirst(); // Remove the '('
                prevToken = ')';
            } else if (isOperator(token)) {
                // Determine if the operator is unary (e.g., -5)
                boolean isUnary = (prevToken == null || prevToken == '(' || prevToken == 'o');

                if (isUnary && (token == '-' || token == '+')) {
                    // For unary operators, push 0 and treat it as a binary operation
                    numbers.push(0.0);
                    prevToken = 'u';
                } else {
                    // While the top operator has higher or equal precedence, apply it
                    while (!operators.isEmpty() && isOperator(operators.peek()) &&
                            orderOf(operators.peek()) >= orderOf(token)) {
                        prepare(operators, numbers);
                    }
                    prevToken = 'o';
                }
                operators.push(token);
            } else {
                // Invalid character encountered
                throw new IllegalArgumentException("Invalid character encountered: " + token);
            }
        }

        // Apply remaining operators
        while (!operators.isEmpty()) {
            char op = operators.peek();
            if (op == '(' || op == ')') {
                throw new IllegalArgumentException("Mismatched parentheses");
            }
            prepare(operators, numbers);
        }

        // The final result should be the only number left
        Double result = numbers.removeFirst();
        if (result.isInfinite() || result.isNaN()) {
            return result.toString();
        }
        // Format the result to two decimal places, removing trailing zeros
        return BigDecimal.valueOf(result)
                .setScale(2, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }

    /**
     * The main method to test the {@code Calculator} class.
     *
     * <p>It accepts an arithmetic equation as a command-line argument and prints the result.
     * If no argument is provided, it will prompt an error.</p>
     *
     * @param args command-line arguments where the first argument is the equation to evaluate
     */
    public static void main(String[] args) {
        Calculator calculator = new Calculator();

        String equation;
        // Check if the Scanner input is invalid (empty or malformed)
        if (args.length > 0) {
            equation = args[0];
        } else {
            // Use Scanner as the primary input source
            Scanner scanner = new Scanner(System.in);
            // Read first line and remove any leading/trailing spaces
            equation = scanner.nextLine().trim();
            scanner.close();
        }

        try {
            String result = calculator.display(equation);
            System.out.println(result);
        } catch (IllegalArgumentException | ArithmeticException e) {
            System.out.println("Equation: " + equation + " --> Error: " + e.getMessage());
        }
    }
}