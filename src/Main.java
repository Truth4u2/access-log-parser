import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Введите первое число и нажмите <Enter>: ");
        int a = new Scanner(System.in).nextInt();
        System.out.println("Введите второе число нажмите <Enter>: ");
        int b = new Scanner(System.in).nextInt();
        System.out.println("Сумма чисел: " + (a + b));
        System.out.println("Разность чисел: " + (a - b));
        System.out.println("Произведение чисел: " + (a * b));
        System.out.println("Частное чисел: " + ((double)a / b));
    }
}