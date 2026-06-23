package CommandMethods;

import CollectionClasses.Ticket;
import Managers.CollectionManager;
import Managers.DBmanager;
import Network.Response;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;

public class AddTicketCommand implements Command {
    private final LinkedList<Ticket> collection;
    private final Connection connection;
    private final DBmanager db;
    private final CollectionManager collectionManager;
    private final int owner;
    private final Ticket ticket;

    public AddTicketCommand(LinkedList<Ticket> collection,Ticket ticket, Connection connection, DBmanager db, CollectionManager collectionManager, int owner) {
        this.collection = collection;
        this.ticket = ticket;
        this.connection = connection;
        this.db = db;
        this.collectionManager = collectionManager;
        this.owner = owner;
    }

    @Override
    public Response execute() {
        if (ticket == null) {
            return new Response(false, "Ошибка: ticket обязателен");
        }

        try {
            int id = db.addTicket(connection, ticket, owner);
            collectionManager.addTicket(collection, db.getTicketById(connection, id));
            return new Response(true, "Билет успешно добавлен в коллекцию!");
        } catch (SQLException e) {
            return new Response(
                    false,
                    "Ошибка базы данных: " + e.getMessage()
            );
        }
    }
}