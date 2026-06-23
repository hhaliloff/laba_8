package CommandMethods;

import CollectionClasses.Ticket;
import CollectionClasses.User;
import Managers.CollectionManager;
import Managers.DBmanager;
import Network.Response;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;

public class RegistrationCommand implements Command {
    private final LinkedList<Ticket> collection;
    private final Connection connection;
    private final DBmanager db;
    private final CollectionManager collectionManager;
    private final User user;

    public RegistrationCommand(LinkedList<Ticket> collection, Connection connection, DBmanager db, CollectionManager collectionManager, User user) {
        this.collection = collection;
        this.connection = connection;
        this.db = db;
        this.collectionManager =  collectionManager;
        this.user =  user;
    }

    @Override
    public Response execute() {
        try {
            String passwordHash = PasswordHasher.hashSHA512(user.getPassword());
            LinkedList<User> users = db.getAllUsers(connection);
            boolean exists = users.stream()
                    .anyMatch(foundUser -> foundUser.getLogin().equals(user.getLogin()));
            if (!exists) {
                user.setPassword(passwordHash);
                db.addUser(connection, user);
                return new Response(true, "Регистрация прошла успешно!");
            }
            else {
                return new Response(false, "Ошибка!");
            }
        } catch (SQLException e) {
            return new Response(
                    false,
                    "Ошибка базы данных: " + e.getMessage()
            );
        }
    }
}
