import CommandMethods.Add;
import CollectionClasses.Ticket;
import Network.Request;
import Network.Response;
import Network.UDPClient;

import java.io.*;
import java.util.*;

public class Main {

    private static final String SERVER_HOST = "192.168.10.80";
    private static final int SERVER_PORT = 32456;
    private static final Scanner scanner = new Scanner(System.in);
    private static String login = null;
    private static String password = null;

    public static void main(String[] args) {

        UDPClient client;
        try {
            client = new UDPClient(SERVER_HOST, SERVER_PORT);
        } catch (IOException e) {
            System.out.println("Не удалось подключиться к серверу: " + e.getMessage());
            return;
        }

        Set<String> scriptsInProcess = new HashSet<>();
        boolean running = true;

        while (login == null || password == null) {
            System.out.println("""
                    Выберите вариант:
                    войти - 1
                    зарегестрироваться - 2""");

            String logreg = scanner.nextLine().trim();

            if (logreg.equals("1")) {
                processCommand("login", client, scriptsInProcess);
            }
            else if (logreg.equals("2")) {
                processCommand("registration", client, scriptsInProcess);
            }
            else {
                System.out.println("Введите либо 1 либо 2");
            }
        }

        if (!(login==null || password==null)){
            while (running) {
                System.out.print("Введите команду > ");
                String input = scanner.nextLine().trim();

                if (input.isBlank()) continue;

                if (normalizeCommand(input).equals("exit")) {
                    running = false;
                    break;
                }

                running = processCommand(input, client, scriptsInProcess);
            }
        }

        try {
            client.close();
        } catch (IOException e) {
            System.out.println("Ошибка при закрытии соединения: " + e.getMessage());
        }
        scanner.close();
    }

    private static boolean processCommand(String input, UDPClient client, Set<String> scriptsInProcess) {
        input = normalizeCommand(input);
        String[] parts = input.split(" ", 2);
        String cmd = parts[0];
        String arg = parts.length > 1 ? parts[1] : null;

        Request request;

        switch (cmd) {

            case "registration": {
                System.out.println("Введите логин:");
                String login = scanner.nextLine().trim();
                System.out.println("Введите пароль:");
                String password1 = scanner.nextLine().trim();
                System.out.println("Повторите пароль:");
                String password2 = scanner.nextLine().trim();

                if (!password1.equals(password2)) {
                    System.out.println("Пароли не совпадают");
                    return true;
                }

                request = new Request(cmd, login, password1);
                break;
            }

            case "login": {
                System.out.println("Введите логин:");
                String login = scanner.nextLine().trim();
                System.out.println("Введите пароль:");
                String password = scanner.nextLine().trim();

                request = new Request(cmd, login, password);
                break;
            }

            case "add": {
                Ticket ticket = Add.add();
                request = new Request(cmd, null, ticket, login, password);
                break;
            }

            case "add_if_min": {
                Ticket ticket = Add.add();
                request = new Request(cmd, null, ticket, login, password);
                break;
            }

            case "update": {
                if (arg == null) {
                    System.out.println("Ошибка: нужно указать id");
                    return true;
                }
                Ticket ticket = Add.add();
                request = new Request(cmd, arg, ticket, login, password);
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

                if (cmd.equals("add_random")){
                    int argint;
                    if (arg == null) {
                        argint = 1;
                    }
                    else{
                        argint = Integer.parseInt(arg);
                    }

                    int total = argint;

                    while (argint >= 100000){
                        sendRequest(client, new Request(cmd, "100000", login, password));
                        argint = argint - 100000;
                    }

                    if (argint > 0){
                        sendRequest(client, new Request(cmd, String.valueOf(argint), login, password));
                    }

                    System.out.println("Добавлено билетов: " + total);
                    return true;
                }
                else {
                    request = new Request(cmd, arg, login, password);
                }
                break;
        }

        if (request.getCommand().equals("login")) {
            sendLogin(client, request);
        }
        else {
            sendAndPrint(client, request);
        }
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
        printResponse(sendRequest(client, request));
    }

    private static Response sendRequest(UDPClient client, Request request) {
        try {
            return client.sendRequest(request);
        } catch (IOException e) {
            System.out.println("Ошибка при отправке запроса: " + e.getMessage());
            return null;
        }
    }

    private static void printResponse(Response response) {
        if (response != null && response.getMessage() != null) {
            System.out.println(response.getMessage());
        }
        if (response != null && response.getTickets() != null) {
            response.getTickets().forEach(System.out::println);
        }
    }

    private static String normalizeCommand(String input) {
        String[] parts = input.split(" ", 2);
        return parts[0].toLowerCase() + (parts.length > 1 ? " " + parts[1] : "");
    }

    private static void sendLogin(UDPClient client, Request request) {
        Response response = sendRequest(client, request);

        if (response == null) {
            return;
        }

        if (response.isSuccess()) {
            login = request.getLogin();
            password = request.getPassword();
        }
    }
}

