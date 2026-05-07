import CollectionClasses.Ticket;
import CommandMethods.CollectionToFile;
import Network.UDPServer;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.*;

public class Main {
    public static void main(String[] args) throws Exception {
        int port = 5000;

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

        // Проверка переменной окружения
        String path = System.getenv("TICKETS_FILE");
        if (path == null || path.isBlank()) {
            logger.severe("Переменная окружения TICKETS_FILE не задана. Завершение работы.");
            return;
        }

        // Загрузка коллекции
        LinkedList<Ticket> collection = new LinkedList<>();
        try {
            CollectionToFile.file_to_collection(collection, path);
            logger.info("Коллекция загружена из файла: " + path + " | элементов: " + collection.size());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка чтения коллекции из файла", e);
            return;
        }

        ServerRequestHandler handler = new ServerRequestHandler(collection, path);
        UDPServer server = new UDPServer(port, handler, logger);

        // Сохранение при завершении
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.stop();
            try {
                CollectionToFile.collection_to_file(collection, path);
                logger.info("Коллекция сохранена при завершении.");
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Ошибка сохранения при завершении", e);
            }
        }));

        // Серверная консоль
        Thread consoleThread = new Thread(() -> {
            Scanner consoleScanner = new Scanner(System.in);
            logger.info("Серверная консоль запущена. Команды: save, exit");
            while (true) {
                String input = consoleScanner.nextLine().trim();
                if (input.equals("save")) {
                    try {
                        CollectionToFile.collection_to_file(collection, path);
                        logger.info("Коллекция сохранена.");
                    } catch (IOException e) {
                        logger.log(Level.SEVERE, "Ошибка сохранения", e);
                    }
                } else if (input.equals("exit")) {
                    logger.info("Завершение работы сервера.");
                    server.stop();
                    break;
                } else {
                    logger.warning("Неизвестная команда: " + input);
                }
            }
        });
        consoleThread.setDaemon(true);
        consoleThread.start();

        server.start();
    }
}