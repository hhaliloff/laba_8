package CommandMethods;

import CollectionClasses.Ticket;
import Managers.CollectionManager;
import Managers.DBmanager;
import Network.Response;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;

public class RemoveTicketByIdCommand implements Command {
    private final LinkedList<Ticket> collection;
    private final Connection connection;
    private final DBmanager db;
    private final CollectionManager collectionManager;
    private final int owner;
    private final int id;

    public RemoveTicketByIdCommand(LinkedList<Ticket> collection, int id, Connection connection, DBmanager db, CollectionManager collectionManager, int owner) {
        this.collection = collection;
        this.connection = connection;
        this.db = db;
        this.collectionManager = collectionManager;
        this.owner = owner;
        this.id = id;
    }

    @Override
    public Response execute() {

        try {
            LinkedList<Ticket> usertickets = collectionManager.getAllUserTickets(collection, owner);
            LinkedList<Ticket> alltickets = collectionManager.getAll(collection);

            boolean exists = alltickets.stream()
                    .anyMatch(ticket -> ticket.getId() == id);

            boolean isUsers = usertickets.stream()
                    .anyMatch(ticket -> ticket.getId() == id);

            if (exists && !isUsers) {
                return new Response(false, "Этот билет не ваш");
            }
            else if (!exists) {
                return new Response(false, "Нет такого билета");
            }
            else {
                Boolean success = db.deleteTicket(connection, id, owner);
                if (success) {
                    collectionManager.removeById(collection, id);
                    return new Response(true, "Билет с ID " + id + " успешно удалён!");
                }
                else {
                    return new Response(false, "Ошибка!");
                }
            }
        } catch (SQLException e) {
            return new Response(
                    false,
                    "Ошибка базы данных: " + e.getMessage()
            );
        }
    }
}