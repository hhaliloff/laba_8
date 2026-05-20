import CollectionClasses.Ticket;
import CommandMethods.*;
import Network.Request;
import Network.Response;
import Network.UDPServer;

import java.util.LinkedList;
import java.util.Objects;

public class ServerRequestHandler implements UDPServer.RequestHandler {
    private final LinkedList<Ticket> collection;
    private final String storagePath;

    public ServerRequestHandler(LinkedList<Ticket> collection, String storagePath) {
        this.collection = Objects.requireNonNull(collection, "collection");
        this.storagePath = storagePath;
    }

    @Override
    public Response handle(Request request) {
        if (request == null || request.getCommand() == null) {
            return new Response(false, "Пустой запрос");
        }

        String command = request.getCommand();
        String arg = request.getArg();
        Ticket ticket = request.getTicket();

        Command commandToExecute;

        switch (command) {
            case "help":
                commandToExecute = new HelpCommand();
                break;

            case "info":
                commandToExecute = new InfoCommand(collection);
                break;

            case "show":
                commandToExecute = new ShowCollectionCommand(collection);
                break;

            case "add":
                if (ticket == null) {
                    return new Response(false, "Ошибка: ticket обязателен");
                }
                commandToExecute = new AddTicketCommand(collection, ticket);
                break;

            case "add_random":
                int count = parsePositiveIntOrDefault(arg, 1);
                if (count <= 0) {
                    return new Response(false, "Ошибка: количество должно быть положительным");
                }
                commandToExecute = new AddRandomTicketCommand(collection, count);
                break;

            case "update":
                Integer idUpdate = parseId(arg);
                if (idUpdate == null) {
                    return new Response(false, "Ошибка: нужно указать корректный id");
                }
                if (idUpdate < 0 || idUpdate >= collection.size()) {
                    return new Response(false, "Билета с таким id не существует");
                }
                if (ticket == null) {
                    return new Response(true, null);
                }
                commandToExecute = new UpdateTicketCommand(collection, idUpdate, ticket);
                break;

            case "remove_by_id":
                Integer idRemove = parseId(arg);
                if (idRemove == null) {
                    return new Response(false, "Ошибка: нужно указать корректный id");
                }
                commandToExecute = new RemoveTicketByIdCommand(collection, idRemove);
                break;

            case "clear":
                commandToExecute = new ClearCollectionCommand(collection);
                break;

            case "remove_last":
                commandToExecute = new RemoveLastCommand(collection);
                break;

            case "add_if_min":
                if (ticket == null) {
                    return new Response(false, "Ошибка: ticket обязателен");
                }
                commandToExecute = new AddIfMinCommand(collection, ticket);
                break;

            case "shuffle":
                commandToExecute = new ShuffleCommand(collection);
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

            case "save":
                commandToExecute = new SaveCollectionCommand(collection, storagePath);
                break;

            case "exit":
                commandToExecute = new SaveCollectionCommand(collection, storagePath);
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
