import CollectionClasses.Ticket;
import CommandMethods.Add;
import Managers.CollectionManager;
import Managers.DBmanager;
import Network.Request;
import Network.Response;
import Network.UDPServer;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.*;

public class Main {
    public static void main(String[] args) throws Exception {
        int port = 32456;

        String login = "admin";
        String password = "admin";

        // Настройка логгера
        Logger logger = Logger.getLogger(UDPServer.class.getName());
        logger.setLevel(Level.FINE);
        logger.setUseParentHandlers(false);

        LogFormatter formatter = new LogFormatter();

        // Консоль
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.FINE);
        consoleHandler.setFormatter(formatter);
        logger.addHandler(consoleHandler);

        // Файл (без цветов)
        FileHandler fileHandler = new FileHandler("server.log", true);
        fileHandler.setLevel(Level.FINE);
        fileHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(fileHandler);


        // Загрузка коллекции
        DBmanager db = new DBmanager();
        Connection connection = db.getConnection();
        db.initializeDatabase(connection);
        CollectionManager collectionManager = new CollectionManager();

        LinkedList<Ticket> collection = db.getAllTickets(connection);

        ServerRequestHandler handler = new ServerRequestHandler(connection, db, collectionManager, collection);
        UDPServer server = new UDPServer(port, handler, logger);

        Request adminReg = new Request("registration", login, password);
        Response adminSuccess = handler.handle(adminReg);
        if (adminSuccess.isSuccess()) {
            System.out.println("Successfully registered user");
        }
        else {
            System.out.println("Failed to register user");
        }


        // Серверная консоль
        Thread consoleThread = new Thread(() -> {
            Scanner consoleScanner = new Scanner(System.in);
            logger.info("Серверная консоль запущена.");
            while (true) {
                String input = consoleScanner.nextLine().trim();
                String[] parts = input.split(" ", 2);
                String cmd = parts[0];
                String arg = parts.length > 1 ? parts[1] : null;
                if (input.equals("exit")) {
                    logger.info("Завершение работы сервера.");
                    server.stop();
                    break;
                }
                else {
                    if (cmd.equals("add")) {
                        Ticket ticket = Add.add();
                        Request request = new Request(cmd, null, ticket, login, password);
                        Response response = server.processRequestForServer(request);
                        printResponse(response);
                    }
                    else if ((cmd.equals("update") || cmd.equals("add_if_min")) && Integer.parseInt(arg) > 0) {
                        Ticket ticket = Add.add();
                        Request request = new Request(cmd, arg, ticket, login, password);
                        Response response = server.processRequestForServer(request);
                        printResponse(response);
                    }
                    else if (arg != null) {
                        Request request = new Request(cmd, arg, login, password);
                        Response response = server.processRequestForServer(request);
                        printResponse(response);
                    }
                    else {
                        Request request = new Request(cmd, login, password);
                        Response response = server.processRequestForServer(request);
                        printResponse(response);
                    }
                }
            }
        });
        consoleThread.setDaemon(true);
        consoleThread.start();

        server.start();
    }

    private static void printResponse(Response response) {
        if (response != null && response.getMessage() != null) {
            System.out.println(response.getMessage());
        }
        if (response != null && response.getTickets() != null) {
            response.getTickets().forEach(System.out::println);
        }
    }
}