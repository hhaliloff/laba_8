package CommandMethods;

import CollectionClasses.Ticket;
import Managers.CollectionManager;
import Managers.DBmanager;
import Network.Response;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;

public class UpdateTicketCommand implements Command {
    private final LinkedList<Ticket> collection;
    private final Connection connection;
    private final DBmanager db;
    private final CollectionManager collectionManager;
    private final int owner;
    private final int id;
    private final Ticket ticket;

    public UpdateTicketCommand(LinkedList<Ticket> collection, int id, Ticket ticket, Connection connection, DBmanager db, CollectionManager collectionManager, int owner) {
        this.collection = collection;
        this.id = id;
        this.ticket = ticket;
        this.connection = connection;
        this.db = db;
        this.collectionManager = collectionManager;
        this.owner = owner;
    }

    @Override
    public Response execute() {
        LinkedList<Ticket> usertickets = collectionManager.getAllUserTickets(collection, owner);
        LinkedList<Ticket> alltickets = collectionManager.getAll(collection);

        boolean exists = alltickets.stream()
                .anyMatch(ticket -> ticket.getId() == id);

        boolean isUsers = usertickets.stream()
                .anyMatch(ticket -> ticket.getId() == id);

        try {
            if (ticket == null) {
                return new Response(false, "Ошибка: ticket обязателен");
            }
            if (!exists) {
                return new Response(false, "Билета с таким id не существует");
            }
            else if (!isUsers) {
                return new Response(false, "Этот билет не ваш");
            }
            else{
                ticket.setId(id);
                ticket.setOwner(owner);
                Boolean success = db.updateTicket(connection, owner, id, ticket);
                if (success) {
                    collectionManager.updateTicket(collection, id, ticket);
                    return new Response(true, "Билет с ID " + id + " успешно обновлён!");
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