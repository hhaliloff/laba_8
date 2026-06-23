package CommandMethods;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import CollectionClasses.Ticket;
import Managers.CollectionManager;
import Managers.DBmanager;
import Network.Response;

public class ClearCollectionCommand implements Command {
    private final LinkedList<Ticket> collection;
    private final Connection connection;
    private final DBmanager db;
    private final CollectionManager collectionManager;
    private final int owner;


    public ClearCollectionCommand(LinkedList<Ticket> collection, Connection connection, DBmanager db, CollectionManager collectionManager, int owner) {
        this.collection = collection;
        this.connection = connection;
        this.db = db;
        this.collectionManager = collectionManager;
        this.owner = owner;
    }

    @Override
    public Response execute() {
        LinkedList<Ticket> collection_snapshot = collection;
        try {
            List<Integer> tickets = collection_snapshot.stream()
                    .map(ticket -> ticket.getId())
                    .toList();

            for (Integer ticket : tickets) {
                Boolean added = db.deleteTicket(connection, ticket, owner);
                if (added) {
                    collectionManager.removeById(collection, ticket);
                }
            }
            return new Response(true, "Коллекция успешно очищена!");
        } catch (SQLException e) {
            return new Response(
                    false,
                    "Ошибка базы данных: " + e.getMessage()
            );
        }
    }
}