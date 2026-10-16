import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    static final Scanner sc = new Scanner(System.in).useLocale(Locale.US);

    static ComplexMatrix readMatrix(String name) {
        int r = readInt("Матрица " + name + ": число строк = ", 1, 20);
        int c = readInt("Матрица " + name + ": число столбцов = ", 1, 20);
        ComplexMatrix m = new ComplexMatrix(r, c);
        System.out.println("Вводите элементы построчно: действительная и мнимая часть через пробел");
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                System.out.printf("Элемент [%d][%d] (Re Im): ", i + 1, j + 1);
                m.setValue(i, j, new ComplexNumber(readDouble(), readDouble()));
            }
        }
        return m;
    }

    static int readInt(String str, int min, int max) {
        while (true) {
            System.out.print(str);
            try {
                int value = sc.nextInt();
                if (value < min || value > max) {
                    System.out.println("Число должно быть от " + min + " до " + max);
                } else {
                    return value;
                }
            } catch (InputMismatchException e) {
                System.out.println("Введите целое число!");
                sc.next();
            }
        }
    }

    static double readDouble() {
        while (true) {
            try {
                return sc.nextDouble();
            } catch (InputMismatchException e) {
                System.out.print("Некорректное число, попробуйте снова (разделитель — точка): ");
                sc.next();
            }
        }
    }

    static void show(String title, ComplexMatrix M) {
        System.out.println("\n=== " + title + " ===");
        System.out.println(M);
    }

    public static void main(String[] args) {
        System.out.println("Работа с комплексными матрицами");
        ComplexMatrix a = readMatrix("A");
        ComplexMatrix b = readMatrix("B");
        ComplexMatrix result = null;

        boolean running = true;
        while (running) {
            System.out.println("""
                    \nМеню:
                     1 - A + B
                     2 - A - B
                     3 - A * B
                     4 - A / B
                     5 - транспонировать A
                     6 - транспонировать B
                     7 - определитель A
                     8 - определитель B
                     9 - умножить A на число
                    10 - обратная к A
                    11 - показать все матрицы
                    12 - ввести матрицы заново (A и B)
                    13 - записать результат в A 
                    14 - записать результат в B
                    15 - ввести заново только матрицу A (B оставить)
                    16 - ввести заново только матрицу B (A оставить)
                     0 - выход""");

            int cmd = readInt("Выберите пункт: ", 0, 16);
            try {
                switch (cmd) {
                    case 1 -> { result = a.add(b); show("A + B", result); }
                    case 2 -> { result = a.sub(b); show("A - B", result); }
                    case 3 -> { result = a.mul(b); show("A * B", result); }
                    case 4 -> { result = a.div(b); show("A / B", result); }
                    case 5 -> { result = a.transpose(); show("A^T", result); }
                    case 6 -> { result = b.transpose(); show("B^T", result); }
                    case 7 -> System.out.println("det(A) = " + a.determinant());
                    case 8 -> System.out.println("det(B) = " + b.determinant());
                    case 9 -> {
                        System.out.print("Действительная часть числа: ");
                        double re = readDouble();
                        System.out.print("Мнимая часть числа: ");
                        double im = readDouble();
                        result = a.mul(new ComplexNumber(re, im));
                        show("A * (" + re + " + " + im + "i)", result);
                    }
                    case 10 -> { result = a.inverse(); show("A^-1", result); }
                    case 11 -> {
                        show("A", a);
                        show("B", b);
                        if (result != null) {
                            show("Результат последней операции", result);
                        } else {
                            System.out.println("Результ пока нет — выполните операцию.");
                        }
                    }
                    case 12 -> {
                        a = readMatrix("A");
                        b = readMatrix("B");
                        result = null;
                    }
                    case 13 -> {
                        if (result == null) {
                            System.out.println("Сначала выполните операцию — сохранять нечего.");
                        } else {
                            a = result.copy();
                            show("A (обновлена)", a);
                        }
                    }
                    case 14 -> {
                        if (result == null) {
                            System.out.println("Сначала выполните операцию — сохранять нечего.");
                        } else {
                            b = result.copy();
                            show("B (обновлена)", b);
                        }
                    }
                    case 15 -> {
                        a = readMatrix("A");
                        result = null;
                        System.out.println("Матрица A обновлена, результат сброшен.");
                    }
                    case 16 -> {
                        b = readMatrix("B");
                        result = null;
                        System.out.println("Матрица B обновлена, результат сброшен.");
                    }
                    case 0 -> running = false;
                    default -> System.out.println("Неизвестная команда");
                }
            } catch (RuntimeException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
        System.out.println("До новых встреч!");
    }
}
