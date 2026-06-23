package CommandMethods;

import CollectionClasses.Ticket;
import CollectionClasses.User;
import Managers.CollectionManager;
import Managers.DBmanager;
import Network.Response;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;

public class LoginCommand implements Command {
    private final LinkedList<Ticket> collection;
    private final Connection connection;
    private final DBmanager db;
    private final CollectionManager collectionManager;
    private final User user;

    public LoginCommand(LinkedList<Ticket> collection, Connection connection, DBmanager db, CollectionManager collectionManager, User user) {
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
            for (User founduser : users) {
                if (founduser.getLogin().equals(user.getLogin())) {
                    if (passwordHash.equals(founduser.getPassword())) {
                        return new Response(true, "Успешный вход!");
                    }
                    else {
                        return new Response(false, "Пароль неверный!");
                    }
                }
            }
            return new Response(false, "Нет такого пользователя");
        } catch (SQLException e) {
            return new Response(
                    false,
                    "Ошибка базы данных: " + e.getMessage()
            );
        }
    }
}
