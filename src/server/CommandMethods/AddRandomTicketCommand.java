package CommandMethods;

import CollectionClasses.Ticket;
import Managers.CollectionManager;
import Managers.DBmanager;
import Network.Response;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.stream.IntStream;

public class AddRandomTicketCommand implements Command {
    private final LinkedList<Ticket> collection;
    private final Connection connection;
    private final DBmanager db;
    private final CollectionManager collectionManager;
    private final int owner;
    private final int count;


    public AddRandomTicketCommand(LinkedList<Ticket> collection,Ticket ticket, Connection connection, DBmanager db, CollectionManager collectionManager, int owner, int count) {
        this.collection = collection;
        this.count = count;
        this.connection = connection;
        this.db = db;
        this.collectionManager = collectionManager;
        this.owner = owner;
    }

    @Override
    public Response execute() {
        if (count <= 0) {
            return new Response(false, "Ошибка: количество должно быть положительным");
        }

        try {
            for (int i = 0; i < count; i++) {
                Ticket ticket = RandomObject.create_random();

                int id = db.addTicket(connection, ticket, owner);

                if (id > 0) {
                    collectionManager.addTicket(collection, db.getTicketById(connection, id));
                }
            }
            return new Response(true, count == 1 ? "Билет с рандомными полями успешно добавлен в коллекцию!" : "Добавлено билетов: " + count);
        } catch (SQLException e) {
            return new Response(
                    false,
                    "Ошибка базы данных: " + e.getMessage()
            );
        }
    }
}