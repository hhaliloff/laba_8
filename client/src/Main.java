import CommandMethods.Add;
import CollectionClasses.Ticket;
import Network.Request;
import Network.Response;
import Network.UDPClient;

import java.io.*;
import java.util.*;

public class Main {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5000;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        UDPClient client;
        try {
            client = new UDPClient(SERVER_HOST, SERVER_PORT);
        } catch (IOException e) {
            System.out.println("Не удалось подключиться к серверу: " + e.getMessage());
            return;
        }

        Set<String> scriptsInProcess = new HashSet<>();
        boolean running = true;

        while (running) {
            System.out.print("Введите команду > ");
            String input = scanner.nextLine().trim();

            if (input.isBlank()) continue;

            if (input.equals("exit")) {
                sendAndPrint(client, new Request("exit"));
                running = false;
                break;
            }

            running = processCommand(input, client, scriptsInProcess);
        }

        try {
            client.close();
        } catch (IOException e) {
            System.out.println("Ошибка при закрытии соединения: " + e.getMessage());
        }
        scanner.close();
    }

    private static boolean processCommand(String input, UDPClient client, Set<String> scriptsInProcess) {
        String[] parts = input.split(" ", 2);
        String cmd = parts[0];
        String arg = parts.length > 1 ? parts[1] : null;

        Request request;

        switch (cmd) {
            case "add":
            case "add_if_min": {
                Ticket ticket = Add.add();
                request = new Request(cmd, null, ticket);
                break;
            }

            case "update": {
                if (arg == null) {
                    System.out.println("Ошибка: нужно указать id");
                    return true;
                }
                Ticket ticket = Add.add();
                request = new Request(cmd, arg, ticket);
                break;
            }

            case "execute_script": {
                if (arg == null) {
                    System.out.println("Ошибка: нужно указать имя файла");
                    return true;
                }
                executeScript(arg, client, scriptsInProcess);
                return true;
            }

            default:
                request = new Request(cmd, arg);
                break;
        }

        sendAndPrint(client, request);
        return true;
    }

    private static void executeScript(String fileName, UDPClient client, Set<String> scriptsInProcess) {
        if (scriptsInProcess.contains(fileName)) {
            System.out.println("Ошибка: рекурсивный вызов скрипта " + fileName);
            return;
        }
        scriptsInProcess.add(fileName);
        try (Scanner scriptScanner = new Scanner(new File(fileName))) {
            while (scriptScanner.hasNextLine()) {
                String line = scriptScanner.nextLine().trim();
                if (!line.isBlank()) {
                    processCommand(line, client, scriptsInProcess);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден: " + fileName);
        } finally {
            scriptsInProcess.remove(fileName);
        }
    }

    private static void sendAndPrint(UDPClient client, Request request) {
        try {
            Response response = client.sendRequest(request);
            if (response != null && response.getMessage() != null) {
                System.out.println(response.getMessage());
            }
            if (response != null && response.getTickets() != null) {
                response.getTickets().forEach(System.out::println);
            }
        } catch (IOException e) {
            System.out.println("Ошибка при отправке запроса: " + e.getMessage());
        }
    }
}