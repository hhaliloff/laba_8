package CommandMethods;

import CollectionClasses.Ticket;
import Managers.CollectionManager;
import Managers.DBmanager;
import Network.Response;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.LinkedList;

public class AddIfMinCommand implements Command {
    private final LinkedList<Ticket> collection;
    private final Ticket ticket;
    private final Connection connection;
    private final DBmanager db;
    private final CollectionManager collectionManager;
    private final int owner;

    public AddIfMinCommand(
            LinkedList<Ticket> collection,
            Ticket ticket,
            Connection connection,
            DBmanager db,
            CollectionManager collectionManager,
            int owner
    ) {
        this.collection = collection;
        this.ticket = ticket;
        this.connection = connection;
        this.db = db;
        this.collectionManager = collectionManager;
        this.owner = owner;
    }

    @Override
    public Response execute() {
        // Если билет не был передан (null), возвращаем ошибку
        if (ticket == null) {
            return new Response(false, "Ошибка: ticket обязателен");
        }
        LinkedList<Ticket> collection_snapshot = collection;

        // Если коллекция пуста, добавляем билет сразу
        try {
            if (collection_snapshot.isEmpty()) {
                int id = db.addTicket(connection, ticket, owner);
                if (id > 0) {
                    collectionManager.addTicket(collection, db.getTicketById(connection, id));
                    return new Response(true, "Элемент добавлен в коллекцию, так как коллекция пуста.");
                }
            }

            // Находим билет с минимальной ценой в коллекции
            Ticket minTicket = collection_snapshot.stream()
                    .min(Comparator.comparingDouble(Ticket::getPrice))  // Сортируем по цене
                    .orElse(null);  // Если коллекция пуста, возвращаем null

            // Если минимальный билет найден и новый билет дешевле, добавляем новый билет
            if (minTicket != null && ticket.getPrice() < minTicket.getPrice()) {
                int id = db.addTicket(connection, ticket, owner);
                if (id > 0) {
                    collectionManager.addTicket(collection, db.getTicketById(connection, id));
                    return new Response(true, "Элемент добавлен.");
                }
            }
        } catch (SQLException e) {
            return new Response(
                    false,
                    "Ошибка базы данных: " + e.getMessage()
            );
        }


        // Если условие не выполняется (новый билет не дешевле минимального)
        return new Response(false, "Элемент не добавлен, так как его цена больше или равна минимальной цене в коллекции.");
    }
}