import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.Scanner;

/**
 * The {@code Calculator} class provides functionality to evaluate arithmetic expressions.
 * It supports basic operations such as addition, subtraction, multiplication, and division,
 * along with handling parentheses and unary operators.
 *
 * <p>It also demonstrates the use of {@code ArrayDeque} for
 * efficient stack operations.</p>
 *
 * <p>Usage Example:</p>
 * <pre>{@code
 * Calculator calculator = new Calculator();
 * String equation = "3 + 4 * (2 - 1)";
 * String result = calculator.calculate(equation);
 * System.out.println(result); // Outputs: 7
 * }</pre>
 *
 * @author <a href="https://github.com/skirt-owner">skirt-owner</a>
 * @version 1.0
 * @since 2024-09-20
 */
public class Calculator {

    /**
     * Enum representing the various parsing states for validating token sequences
     * in an arithmetic expression.
     */
    private enum ParsingState {
        START,         // Initial state, before processing any token
        NUMBER,        // A number was just processed
        OPERATOR,      // An operator (+, -, *, /) was just processed
        OPEN_PAREN,    // An opening parenthesis was just processed
        CLOSE_PAREN,   // A closing parenthesis was just processed
        UNARY_OPERATOR // A unary operator (-) was just processed
    }

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
    private static Double getResultOf(Character operator, Double numberA, Double numberB) {
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
            throw new IllegalArgumentException("Incorrect format of the initial equation.");
        }
        char operator = operators.removeFirst();
        Double numberB = numbers.removeFirst();
        Double numberA = numbers.removeFirst();
        numbers.push(getResultOf(operator, numberA, numberB));
    }


    /**
     * Check usage of operators.
     *
     * <p>Same operators should always be separated by whitespace.</p>
     *
     * @param equation the input equation string.
     * @return {@code true} if usage of operators is correct; {@code false} otherwise
     */
    private static boolean checkOperatorsUsage(String equation) {
        int length = equation.length();

        for (int i = 0; i < length - 1; i++) {
            char currentToken = equation.charAt(i);
            char nextToken = equation.charAt(i + 1);

            if (isOperator(currentToken) && isOperator(nextToken) && (currentToken == nextToken)) {
                return false;
            }
        }

        return true;
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
        // This helps to handle numbers like '.', '.0' or '0.'
         return Character.isDigit(token) || token == '.';
    }

    /**
     * Processes a numeric token in the input equation.
     *
     * <p>Reads a sequence of digits (and optionally one decimal point) from the equation,
     * builds the number, and pushes it onto the numbers stack.</p>
     *
     * @param equation     the input arithmetic expression
     * @param currentIndex the current index in the input string
     * @param numbers      the stack of numbers where the parsed number will be pushed
     * @return the updated index after processing the number
     */
    private static int processNumber(String equation, int currentIndex, ArrayDeque<Double> numbers) {
        StringBuilder numberBuilder = new StringBuilder();
        int length = equation.length();
        int decimalPointCounter = 0;

        while (currentIndex < length && isDigit(equation.charAt(currentIndex))) {
            if (equation.charAt(currentIndex) == '.') decimalPointCounter++;
            numberBuilder.append(equation.charAt(currentIndex++));
        }

        // If there are more than one decimal point in built number - throws exception
        if (decimalPointCounter > 1) {
            throw new IllegalArgumentException("Invalid decimal number format: " + numberBuilder);
        }

        String number = numberBuilder.toString();
        // Double does not support use of decimal point to represent 0.0
        numbers.push(Double.parseDouble(number.equals(".") ? "0.0d" : number));

        return currentIndex - 1; // Adjust index after the loop
    }

    /**
     * Processes an opening parenthesis by pushing it onto the operators stack.
     *
     * @param operators the stack of operators
     */
    private static void processOpeningParenthesis(ArrayDeque<Character> operators) {
        operators.push('(');
    }

    /**
     * Processes a closing parenthesis, applying operators inside the parentheses
     * until an opening parenthesis is encountered.
     *
     * @param operators the stack of operators
     * @param numbers   the stack of numbers
     * @throws IllegalArgumentException if mismatched parentheses are found
     */
    private static void processClosingParenthesis(ArrayDeque<Character> operators, ArrayDeque<Double> numbers) {
        while (!operators.isEmpty() && operators.peek() != '(') {
            prepare(operators, numbers);
        }
        if (operators.isEmpty()) {
            throw new IllegalArgumentException("Mismatched parentheses.");
        }
        // Remove '(' from deque
        operators.removeFirst();
    }

    /**
     * Processes an operator by determining its precedence and applying any higher-precedence
     * operators already on the stack.
     *
     * @param token     the operator character
     * @param operators the stack of operators
     * @param numbers   the stack of numbers
     */
    private static void processOperator(Character token, ArrayDeque<Character> operators, ArrayDeque<Double> numbers) {
        while (!operators.isEmpty() && isOperator(operators.peek()) &&
                orderOf(operators.peek()) >= orderOf(token)) {
            prepare(operators, numbers);
        }
        operators.push(token);
    }

    /**
     * Processes a unary operator (such as negative sign) by treating it as an operator
     * with an implicit 0 as the first operand.
     *
     * @param token     the operator character
     * @param operators the stack of operators
     * @param numbers   the stack of numbers
     */
    private static void processUnary(Character token, ArrayDeque<Character> operators, ArrayDeque<Double> numbers) {
        // This way we don't need to trace unary operators and separate them from other operators in deque
        // Because we represent them as simple operator.
        operators.push(token);
        numbers.push(0.0d);
    }

    /**
     * Applies all remaining operators in the stack to the numbers stack after the entire equation is processed.
     *
     * @param operators the stack of operators
     * @param numbers   the stack of numbers
     * @throws IllegalArgumentException if there are mismatched parentheses or incorrect operator sequences
     */
    private static void applyRemainingOperators(ArrayDeque<Character> operators, ArrayDeque<Double> numbers) {
        while (!operators.isEmpty()) {
            char operator = operators.peek();
            if (operator == '(' || operator == ')') {
                throw new IllegalArgumentException("Mismatched parentheses.");
            }
            prepare(operators, numbers);
        }
    }

    /**
     * Formats the final result by rounding it to two decimal places and removing any trailing zeros.
     *
     * @param result the calculated result
     * @return the formatted result as a {@code Double}
     */
    private static Double formatResult(Double result) {
        // I like how BigDecimal has methods to trim number or round it.
        // It's a simple project, so I don't want to ignore it.
        return Double.valueOf(BigDecimal.valueOf(result)
                .setScale(2, RoundingMode.HALF_UP)
                .stripTrailingZeros().toPlainString());
    }

    /**
     * Evaluates the arithmetic expression provided as a string and returns the result.
     *
     * <p>The method handles operator precedence, parentheses, and unary operators.
     * It returns the result rounded to two decimal places, removing any trailing zeros.</p>
     *
     * @param equation the arithmetic expression to evaluate
     * @return the result of the evaluation as a {@code Double}
     * @throws IllegalArgumentException if the equation format is incorrect or contains invalid characters
     */
    public static Double calculate(String equation) {
        if (!isValid(equation)) {
            throw new IllegalArgumentException("Incorrect format of the initial equation");
        }

        if (!checkOperatorsUsage(equation)) {
            throw new IllegalArgumentException("Bad usage of operators: ++");
        }

        // Stacks for operators and numbers
        ArrayDeque<Character> operators = new ArrayDeque<>();
        ArrayDeque<Double> numbers = new ArrayDeque<>();

        int length = equation.length();
        ParsingState state = ParsingState.START;

        for (int i = 0; i < length; ++i) {
            char token = equation.charAt(i);

            // Skip whitespace characters
            if (Character.isWhitespace(token)) continue;

            if (isDigit(token)) {
                // Digit can be placed:
                // 1. At the beginning of equation
                // 2. After operator
                // 3. After unary operator
                // 4. And after '('
                if (state != ParsingState.START && state != ParsingState.OPERATOR &&
                        state != ParsingState.OPEN_PAREN && state != ParsingState.UNARY_OPERATOR) {
                    throw new IllegalArgumentException("Unexpected number at position " + i);
                }
                i = processNumber(equation, i, numbers);
                state = ParsingState.NUMBER;
            } else if (token == '(') {
                // '(' can be placed:
                // 1. At the beginning of equation
                // 2. After operator
                // 3. After unary operator
                // 4. And after another '('
                if (state != ParsingState.START && state != ParsingState.OPERATOR &&
                        state != ParsingState.OPEN_PAREN && state != ParsingState.UNARY_OPERATOR) {
                    throw new IllegalArgumentException("Unexpected '(' at position " + i);
                }
                processOpeningParenthesis(operators);
                state = ParsingState.OPEN_PAREN;  // Update state after '('
            } else if (token == ')') {
                // ')' can be placed:
                // 1. After number
                // 2. And after another ')'
                if (state != ParsingState.NUMBER && state != ParsingState.CLOSE_PAREN) {
                    throw new IllegalArgumentException("Unexpected ')' at position " + i);
                }
                processClosingParenthesis(operators, numbers);
                state = ParsingState.CLOSE_PAREN;  // Update state after ')'
            } else if ((token == '-' || token == '+') && (state == ParsingState.START || state == ParsingState.OPEN_PAREN ||
                    state == ParsingState.OPERATOR)) {
                // We do not want to ignore basic operators '-+'
                // So we check for its unary in main `if` statement

                // Unary operator can be placed:
                // 1. At the beginning of equation
                // 2. After '('
                // 3. After basic operator
                processUnary(token, operators, numbers);
                state = ParsingState.UNARY_OPERATOR;
            } else if (isOperator(token)) {
                // Operator can be placed:
                // 1. After number
                // 2. After ')'
                if (state != ParsingState.NUMBER && state != ParsingState.CLOSE_PAREN) {
                    throw new IllegalArgumentException("Unexpected operator at position " + i);
                }
                processOperator(token, operators, numbers);
                state = ParsingState.OPERATOR;
            } else {
                throw new IllegalArgumentException("Invalid character encountered: " + token);
            }
        }

        // Apply remaining operators
        applyRemainingOperators(operators, numbers);

        // The final result should be the only number left
        Double result = numbers.removeFirst();
        // Result is `Infinity` or `NaN` can appear only in one case: division by zero
        // So we can be defined throwing an exception;
        // Case like 1 / inf == 0 - so we are all good
        if (result.isInfinite() || result.isNaN()) {
            throw new ArithmeticException("Can't divide by zero");
        }

        // Format the result to two decimal places, removing trailing zeros
        return formatResult(result);
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
        String equation;

        // Check if we have default case of using `args`
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
            Double result = calculate(equation);
            System.out.println(result);
        } catch (IllegalArgumentException | ArithmeticException e) {
            System.out.println("Equation: " + equation + " --> Error: " + e.getMessage());
        }
    }
}
