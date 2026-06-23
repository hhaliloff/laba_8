import CollectionClasses.Ticket;
import CollectionClasses.User;
import CommandMethods.*;
import Managers.CollectionManager;
import Managers.DBmanager;
import Network.Request;
import Network.Response;
import Network.UDPServer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Objects;

public class ServerRequestHandler implements UDPServer.RequestHandler {
    private Connection connection;
    private DBmanager db;
    private CollectionManager collectionManager;
    private LinkedList<Ticket> collection;

    public ServerRequestHandler(Connection connection, DBmanager db, CollectionManager collectionManager, LinkedList<Ticket> collection) {
        this.connection = Objects.requireNonNull(connection, "connection");
        this.db = Objects.requireNonNull(db, "db");
        this.collectionManager = Objects.requireNonNull(collectionManager, "collectionManager");
        this.collection = Objects.requireNonNull(collection, "collection");
    }

    public Response handle(Request request) {
        if (request == null || request.getCommand() == null) {
            return new Response(false, "Пустой запрос");
        }

        String login = request.getLogin();
        int owner;
        try {
            owner = db.getIdByLogin(connection, login);
        } catch (SQLException e) {
            return new Response(false, e.getMessage());
        }

        String command = request.getCommand();
        String arg = request.getArg();
        Ticket ticket = request.getTicket();

        Command commandToExecute;

        switch (command) {
            case "registration":
                User userReg = new User(-1, request.getLogin(), request.getPassword());
                commandToExecute = new RegistrationCommand(collection, connection, db, collectionManager, userReg);
                break;

            case "login":
                User userLog = new User(-1, request.getLogin(), request.getPassword());
                commandToExecute = new LoginCommand(collection, connection, db, collectionManager, userLog);
                break;

            case "help":
                commandToExecute = new HelpCommand();
                break;

            case "info":
                commandToExecute = new InfoCommand(collection);
                break;

            case "show":
                int showPage;
                if (arg == null || arg.isBlank()) {
                    showPage = 1;
                } else {
                    try {
                        showPage = Integer.parseInt(arg.trim());
                    } catch (NumberFormatException e) {
                        return new Response(false, "Нужно ввести натуральное число");
                    }
                    if (showPage < 1) {
                        return new Response(false, "Нужно ввести натуральное число");
                    }
                }
                commandToExecute = new ShowCollectionCommand(collection, showPage);
                break;

            case "get_my_tickets":
                int showPageMy;
                if (arg == null || arg.isBlank()) {
                    showPageMy = 1;
                } else {
                    try {
                        showPageMy = Integer.parseInt(arg.trim());
                    } catch (NumberFormatException e) {
                        return new Response(false, "Нужно ввести натуральное число");
                    }
                    if (showPageMy < 1) {
                        return new Response(false, "Нужно ввести натуральное число");
                    }
                }
                commandToExecute = new GetMyTicketsCommand(collection, collectionManager, owner, showPageMy);
                break;

            case "add":
                if (ticket == null) {
                    return new Response(false, "Ошибка: ticket обязателен");
                }
                commandToExecute = new AddTicketCommand(collection, ticket, connection, db, collectionManager, owner);
                break;

            case "add_random":
                int count = parsePositiveIntOrDefault(arg, 1);
                if (count <= 0) {
                    return new Response(false, "Ошибка: количество должно быть положительным");
                }
                commandToExecute = new AddRandomTicketCommand(collection, ticket, connection, db, collectionManager, owner, count);
                break;

            case "update":
                Integer idUpdate = parseId(arg);
                if (idUpdate == null) {
                    return new Response(false, "Ошибка: нужно указать корректный id");
                }
                commandToExecute = new UpdateTicketCommand(collection, idUpdate, ticket, connection, db, collectionManager, owner);
                break;

            case "remove_by_id":
                Integer idRemove = parseId(arg);
                if (idRemove == null) {
                    return new Response(false, "Ошибка: нужно указать корректный id");
                }
                commandToExecute = new RemoveTicketByIdCommand(collection, idRemove, connection, db, collectionManager, owner);
                break;

            case "clear":
                commandToExecute = new ClearCollectionCommand(collection, connection, db, collectionManager, owner);
                break;

            case "remove_last":
                commandToExecute = new RemoveLastCommand(collection, connection, db, collectionManager, owner);
                break;

            case "add_if_min":
                if (ticket == null) {
                    return new Response(false, "Ошибка: ticket обязателен");
                }
                commandToExecute =
                        new AddIfMinCommand(
                                collection,
                                ticket,
                                connection,
                                db,
                                collectionManager,
                                owner
                        );
                break;


            case "filter_less_than_venue":
                Integer lessThan = parseId(arg);
                if (lessThan == null) {
                    return new Response(false, "Ошибка: нужно указать вместимость");
                }
                commandToExecute = new FilterLessThanVenueCommand(collection, lessThan);
                break;

            case "filter_greater_than_venue":
                Integer greaterThan = parseId(arg);
                if (greaterThan == null) {
                    return new Response(false, "Ошибка: нужно указать вместимость");
                }
                commandToExecute = new FilterGreaterThanVenueCommand(collection, greaterThan);
                break;

            case "print_field_descending_price":
                commandToExecute = new PrintFieldDescendingPriceCommand(collection);
                break;

            case "execute_script":
                return new Response(false, "execute_script выполняется на клиенте");

            default:
                return new Response(false, "Неизвестная команда: " + command);
        }

        return commandToExecute.execute();
    }

    private Integer parseId(String value) {
        if (value == null) return null;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private int parsePositiveIntOrDefault(String value, int defaultValue) {
        if (value == null || value.isBlank()) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
