package CommandMethods;

import CollectionClasses.Ticket;
import Managers.CollectionManager;
import Managers.DBmanager;
import Network.Response;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;

public class RemoveLastCommand implements Command {
    private final LinkedList<Ticket> collection;
    private final Connection connection;
    private final DBmanager db;
    private final CollectionManager collectionManager;
    private final int owner;

    public RemoveLastCommand(LinkedList<Ticket> collection,  Connection connection, DBmanager db, CollectionManager collectionManager, int owner) {
        this.collection = collection;
        this.connection = connection;
        this.db = db;
        this.collectionManager = collectionManager;
        this.owner = owner;
    }

    @Override
    public Response execute() {
        try {
            LinkedList<Ticket> tickets = collectionManager.getAllUserTickets(collection, owner);
            if (tickets.isEmpty()) {
                return new Response(false, "Вы не добавили ни одного билета.");
            }
            else {
                int last_id = tickets.size()-1;
                int id = (tickets.get(last_id)).getId();
                Boolean success = db.deleteTicket(connection, id, owner);
                if (success) {
                    collectionManager.removeById(collection, id);
                    return new Response(true, "Последний элемент удалён.");
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
