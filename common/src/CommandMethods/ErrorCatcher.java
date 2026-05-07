/**
 * Класс для проверки правильности ввода пользователя.
 * Если введено неправильно — просит ввести снова.
 */
package CommandMethods;

import CollectionClasses.TicketType;
import CollectionClasses.VenueType;

import java.util.Scanner;

public class ErrorCatcher {
    public static int notInt(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Ошибка! Нужно ввести число.");
            }
        }
    }

    public static int moreZero(Scanner scanner, String message) {
        while (true) {
            int input = notInt(scanner, message);
            if (input > 0) {
                return input;
            } else {
                System.out.println("Ошибка! Число должно быть больше 0.");
            }
        }
    }

    public static float notFloatOrMoreZero(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            try {
                float input = Float.parseFloat(scanner.nextLine());
                if (input > 0) {
                    return input;
                } else {
                    System.out.println("Ошибка! Число должно быть больше 0.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка! Нужно ввести число.");
            }
        }
    }

    public static long below104(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            try {
                long input = Long.parseLong(scanner.nextLine());
                if (input <= 104) {
                    return input;
                } else {
                    System.out.println("Ошибка! Число должно быть меньше или равно 104.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка! Нужно ввести число.");
            }
        }
    }

    public static String notNull(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine();
            if (input != null) {
                return input;
            } else {
                System.out.println("Ошибка! Ввод не может быть null.");
            }
        }
    }

    public static String notEmpty(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine();
            if (!input.isEmpty()) {
                return input;
            } else {
                System.out.println("Ошибка! Ввод не может быть пустым.");
            }
        }
    }

    public static String notNullOrEmpty(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine();
            if (input != null && !input.isEmpty()) {
                return input;
            } else {
                System.out.println("Ошибка! Ввод не может быть null или пустым.");
            }
        }
    }

    public static Boolean notBoolean(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false")) {
                return Boolean.parseBoolean(input);
            } else {
                System.out.println("Ошибка! Нужно ввести true или false.");
            }
        }
    }

    public static TicketType notTicketType(Scanner scanner, String message) {
        String availableTypes = "";
        for (TicketType type : TicketType.values()) {
            availableTypes = availableTypes + type + " ";
        }
        while (true) {
            System.out.print(message + " (доступные типы: " + availableTypes + "): ");
            String input = scanner.nextLine();
            try {
                return TicketType.valueOf(input.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка! Нужно ввести один из следующих типов билетов: " + availableTypes);
            }
        }
    }

    public static VenueType notVenueType(Scanner scanner, String message) {
        String availableTypes = "";
        for (VenueType type : VenueType.values()) {
            availableTypes = availableTypes + type + " ";
        }
        while (true) {
            System.out.print(message + " (доступные типы: " + availableTypes + "): ");
            String input = scanner.nextLine();
            try {
                return VenueType.valueOf(input.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка! Нужно ввести один из следующих типов мест проведения: " + availableTypes);
            }
        }
    }

    public static String normalInput(Scanner scanner, String message) {
        System.out.print(message);
        return scanner.nextLine();
    }
}
