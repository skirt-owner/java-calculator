import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class CalculatorTest {

    @ParameterizedTest
    @MethodSource("provideValidTest")
    void testValidExpressions(String input, Double expectedOutput) {
        Assertions.assertEquals(expectedOutput, Calculator.calculate(input));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidTest")
    void testInvalidExpressions(String input) {
        Assertions.assertThrows(Exception.class, () -> Calculator.calculate(input));
    }

    private static Stream<Arguments> provideValidTest() {
        return Stream.of(
                // Простые операции
                Arguments.of("-1", -1d),
                Arguments.of("1 * (-(-(5*2)+4))", 6d),
                Arguments.of("-(4+7)", -11d),
                Arguments.of("1 + -(4*2)", -7d),

                Arguments.of("2+2", 4d),
                Arguments.of("2-2", 0d),
                Arguments.of("3*-5", -15d),
                Arguments.of("3/2", 1.5d),

                // Операции с приоритетом
                Arguments.of("2+3*4", 14d),
                Arguments.of("(2+3)*4", 20d),
                Arguments.of("10/(2+3)", 2d),

                // Отрицательные числа
                Arguments.of("-5+3", -2d),
                Arguments.of("-5-3", -8d),
                Arguments.of("-5*-3", 15d),
                Arguments.of("-5/2", -2.5d),

                // Сложные выражения
                Arguments.of("1 + (2 - 3) * 2", -1d),
                Arguments.of("2 + ((3 + 4) * 2)", 16d),
                Arguments.of("10 + 2 * (6 / (3 - 1))", 16d),

                // Дробные числа
                Arguments.of("1.5 + 2.5", 4d),
                Arguments.of("5.0 / 2.0", 2.5d),
                Arguments.of("-1.5 * -2", 3d),

                // Более сложные выражения
                Arguments.of("3 + 4 * 2 / ( 1 - 5 )*0.145", 2.71d), // проверка с операциями в скобках
                Arguments.of("1 + 2 * (3 + 4) / 7", 3d),            // операторы с одинаковым приоритетом
                Arguments.of("-3 + 6 / 3 * (2 - 1)", -1d),          // смешанные операции с отрицательными числами
                Arguments.of("5 + -3 * 6 + 2", -11d),               // отрицательные операнды и смешанные операторы
                Arguments.of("(-1 + 2.5) * 3", 4.5d),               // скобки с дробными числами
                Arguments.of("((2 + 3) * 4) - (1 + 5) / 2", 17d),   // несколько уровней вложенности

                // Тесты с большими числами
                Arguments.of("1000000 + 1000000", 2000000d),        // большие числа
                Arguments.of("-9999999 * 9999999", -99999980000001d), // большие отрицательные числа

                // Пограничные значения
                Arguments.of("0+0", 0d),
                Arguments.of("-0+0", 0d),
                Arguments.of("0.00001 * 100000", 1d)
        );
    }

    private static Stream<Arguments> provideInvalidTest() {
        return Stream.of(
                // Некорректные выражения
                Arguments.of("5 / 0"),                // деление на 0
                Arguments.of("4 + (-)"),                // некорректная запись
                Arguments.of("2 + * 3"),              // ошибка синтаксиса
                Arguments.of("3 + (4 - 2"),           // незакрытая скобка
                Arguments.of(")3 + 4("),              // неправильная последовательность скобок
                Arguments.of("abc"),                  // недопустимые символы
                Arguments.of("5..2 + 1"),             // два разделителя для дробной части
                Arguments.of(""),                     // пустая строка
                Arguments.of("-"),                    // одинарный минус без числа
                Arguments.of("5 + (2 + 3"),           // незакрытая скобка
                Arguments.of("()"),                   // пустые скобки
                Arguments.of("2 + ()"),               // операция с пустыми скобками
                Arguments.of("10 + 5 /"),             // операция без второго операнда
                Arguments.of("2 + (3 * )"),           // оператор внутри скобок без операнда
                Arguments.of("3 ** 2"),               // неправильный оператор (двойное умножение)
                Arguments.of("2...2 + 1"),            // слишком много точек
                Arguments.of("2 + -"),                // оператор без числа
                Arguments.of("(((2+3))"),             // лишние скобки, отсутствует закрывающая
                Arguments.of("2 + 3 + "),             // незавершённое выражение
                Arguments.of("3/"),                   // деление без второго операнда
                Arguments.of("((2 + 3) - 4 * 5))")                // незакрытая скобка в конце
        );
    }
}
