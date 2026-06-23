package Managers;

import CollectionClasses.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedList;

public class DBmanager {
    private static final String URL = "jdbc:postgresql://localhost:5432/studs";
    private static final String USER = "s504634";
    private static final String PASSWORD = "FCNWzjF5MFsVjqWL";

    public void initializeDatabase(
            Connection connection
    ) throws SQLException {
        incrementUsers(connection);
        incrementTickets(connection);
        incrementVenue(connection);
        createUsersTable(connection);
        createTicketsTable(connection);
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public void createUsersTable(Connection connection) throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS users (
                id BIGINT PRIMARY KEY
                      DEFAULT nextval('users_id_seq'),
                login VARCHAR(100) NOT NULL UNIQUE,
                password_hash VARCHAR(128) NOT NULL
                )
                """;

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.execute();
    }

    public void createTicketsTable(Connection connection) throws SQLException {
        String sql = """

            CREATE TABLE IF NOT EXISTS tickets (

                id BIGINT PRIMARY KEY

                    DEFAULT nextval('tickets_id_seq'),

                name TEXT NOT NULL,

                coordinates_x BIGINT NOT NULL,

                coordinates_y INTEGER NOT NULL,

                creation_date DATE NOT NULL,

                price REAL NOT NULL,

                comment TEXT NOT NULL,

                refundable BOOLEAN NOT NULL,

                ticket_type VARCHAR(50) NOT NULL,
                
                venue_id BIGINT NOT NULL,

                venue_name TEXT NOT NULL,

                venue_capacity INTEGER NOT NULL,

                venue_type VARCHAR(50) NOT NULL,

                address_street TEXT NOT NULL,

                location_x REAL NOT NULL,

                location_y INTEGER NOT NULL,

                location_z DOUBLE PRECISION NOT NULL,

                location_name TEXT NOT NULL,

                owner BIGINT NOT NULL

                    REFERENCES users(id)

            )

            """;

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.execute();
    }

    public void incrementUsers(Connection connection) throws SQLException {
        String sql = """
                CREATE SEQUENCE IF NOT EXISTS users_id_seq
                        START WITH 1
                        INCREMENT BY 1;
        """;
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.execute();
    }

    public void incrementTickets(Connection connection) throws SQLException {
        String sql = """
                CREATE SEQUENCE IF NOT EXISTS tickets_id_seq
                        START WITH 1
                        INCREMENT BY 1;
        """;
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.execute();
    }

    public void incrementVenue(Connection connection) throws SQLException {
        String sql = """
                CREATE SEQUENCE IF NOT EXISTS venues_id_seq
                        START WITH 1
                        INCREMENT BY 1;
        """;
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.execute();
    }

    public void addUser(Connection connection, User user) throws SQLException {
        String sql = """
                INSERT INTO users (login, password_hash) VALUES (?, ?)
        """;
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, user.getLogin());
        statement.setString(2, user.getPassword());

        statement.executeUpdate();
    }


    public int addTicket(
            Connection connection,
            Ticket ticket,
            long owner
    ) throws SQLException {
        String sql = """
            INSERT INTO tickets (
                name,
                coordinates_x,
                coordinates_y,
                creation_date,
                price,
                comment,
                refundable,
                ticket_type,
                venue_id,
                venue_name,
                venue_capacity,
                venue_type,
                address_street,
                location_x,
                location_y,
                location_z,
                location_name,
                owner
            )
            VALUES (
                ?, ?, ?, ?, ?, ?, ?, ?, nextval('venues_id_seq'),?, ?, ?, ?, ?, ?, ?, ?, ?
            )
            RETURNING id
            """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    ticket.getName()
            );

            statement.setLong(
                    2,
                    ticket.getCoordinates().getX()
            );

            statement.setInt(
                    3,
                    ticket.getCoordinates().getY()
            );

            statement.setObject(
                    4,
                    ticket.getCreationDate()
            );

            statement.setFloat(
                    5,
                    ticket.getPrice()
            );

            statement.setString(
                    6,
                    ticket.getComment()
            );

            statement.setBoolean(
                    7,
                    ticket.getRefundable()
            );

            statement.setString(
                    8,
                    ticket.getType().name()
            );

            statement.setString(
                    9,
                    ticket.getVenue().getName()
            );

            statement.setInt(
                    10,
                    ticket.getVenue().getCapacity()
            );

            statement.setString(
                    11,
                    ticket.getVenue().getType().name()
            );

            statement.setString(
                    12,
                    ticket.getVenue()
                            .getAddress()
                            .getStreet()
            );

            statement.setFloat(
                    13,
                    ticket.getVenue()
                            .getAddress()
                            .getTown()
                            .getX()
            );

            statement.setInt(
                    14,
                    ticket.getVenue()
                            .getAddress()
                            .getTown()
                            .getY()
            );

            statement.setDouble(
                    15,
                    ticket.getVenue()
                            .getAddress()
                            .getTown()
                            .getZ()
            );

            statement.setString(
                    16,
                    ticket.getVenue()
                            .getAddress()
                            .getTown()
                            .getName()
            );

            statement.setLong(
                    17,
                    owner
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                if (!resultSet.next()) {
                    throw new SQLException(
                            "База данных не вернула id билета"
                    );
                }

                return resultSet.getInt("id");
            }
        }
    }

    public boolean deleteTicket(
            Connection connection,
            long id,
            long owner
    ) throws SQLException {
        String sql = """
            DELETE FROM tickets
            WHERE id = ?
              AND owner = ?
            """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setLong(1, id);
            statement.setLong(2, owner);

            int changedRows =
                    statement.executeUpdate();

            return changedRows == 1;
        }
    }

    public boolean updateTicket(
            Connection connection,
            long owner,
            int id,
            Ticket ticket
    ) throws SQLException {
        String sql = """
            UPDATE tickets
            SET name = ?,
                coordinates_x = ?,
                coordinates_y = ?,
                creation_date = ?,
                price = ?,
                comment = ?,
                refundable = ?,
                ticket_type = ?,
                venue_id = ?,
                venue_name = ?,
                venue_capacity = ?,
                venue_type = ?,
                address_street = ?,
                location_x = ?,
                location_y = ?,
                location_z = ?,
                location_name = ?
            WHERE id = ?
              AND owner = ?
            """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    ticket.getName()
            );

            statement.setLong(
                    2,
                    ticket.getCoordinates().getX()
            );

            statement.setInt(
                    3,
                    ticket.getCoordinates().getY()
            );

            statement.setObject(
                    4,
                    ticket.getCreationDate()
            );

            statement.setFloat(
                    5,
                    ticket.getPrice()
            );

            statement.setString(
                    6,
                    ticket.getComment()
            );

            statement.setBoolean(
                    7,
                    ticket.getRefundable()
            );

            statement.setString(
                    8,
                    ticket.getType().name()
            );

            statement.setInt(
                    9,
                    ticket.getVenue().getId()
            );

            statement.setString(
                    10,
                    ticket.getVenue().getName()
            );

            statement.setInt(
                    11,
                    ticket.getVenue().getCapacity()
            );

            statement.setString(
                    12,
                    ticket.getVenue().getType().name()
            );

            statement.setString(
                    13,
                    ticket.getVenue()
                            .getAddress()
                            .getStreet()
            );

            statement.setFloat(
                    14,
                    ticket.getVenue()
                            .getAddress()
                            .getTown()
                            .getX()
            );

            statement.setInt(
                    15,
                    ticket.getVenue()
                            .getAddress()
                            .getTown()
                            .getY()
            );

            statement.setDouble(
                    16,
                    ticket.getVenue()
                            .getAddress()
                            .getTown()
                            .getZ()
            );

            statement.setString(
                    17,
                    ticket.getVenue()
                            .getAddress()
                            .getTown()
                            .getName()
            );


            statement.setLong(
                    18,
                    id
            );

            statement.setLong(
                    19,
                    owner
            );

            int changedRows =
                    statement.executeUpdate();

            return changedRows == 1;
        }
    }

    public LinkedList<Ticket> getAllTickets(Connection connection) throws SQLException {
        LinkedList<Ticket> collection = new LinkedList<>();
        String sql = """
                SELECT * FROM tickets
                """;
        try (
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery();
        ) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int coordinates_x = resultSet.getInt("coordinates_x");
                int coordinates_y = resultSet.getInt("coordinates_y");
                LocalDate creation_date = resultSet.getObject(
                        "creation_date",
                        LocalDate.class
                );
                float price = resultSet.getFloat("price");
                String comment = resultSet.getString("comment");
                boolean refundable = resultSet.getBoolean("refundable");
                TicketType type = TicketType.valueOf(resultSet.getString("ticket_type"));
                int  venue_id = resultSet.getInt("venue_id");
                String venue_name = resultSet.getString("venue_name");
                int venue_capacity = resultSet.getInt("venue_capacity");
                VenueType venue_type = VenueType.valueOf(resultSet.getString("venue_type"));
                String address_street = resultSet.getString("address_street");
                Float location_x = resultSet.getFloat("location_x");
                int location_y = resultSet.getInt("location_y");
                Double location_z = resultSet.getDouble("location_z");
                String location_name = resultSet.getString("location_name");
                int owner  = resultSet.getInt("owner");

                Coordinates coordinates = new Coordinates(coordinates_x, coordinates_y);

                Location location = new Location(location_x, location_y, location_z, location_name);

                Address address = new Address(address_street, location);

                Venue venue = new Venue(venue_id, venue_name, venue_capacity, venue_type, address);

                Ticket ticket = new Ticket(id, name, coordinates, creation_date, price, comment, refundable, type, venue, owner);

                collection.add(ticket);
            }
        }

        return collection;
    }

    public LinkedList<Ticket> getAllUserTickets(Connection connection, int owner) throws SQLException {
        LinkedList<Ticket> collection = new LinkedList<>();
        String sql = """
                SELECT * FROM tickets
                WHERE owner = ?
        """;

        try (
                PreparedStatement statement = connection.prepareStatement(sql);

        ) {
            statement.setInt(1, owner);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int coordinates_x = resultSet.getInt("coordinates_x");
                int coordinates_y = resultSet.getInt("coordinates_y");
                LocalDate creation_date = resultSet.getObject(
                        "creation_date",
                        LocalDate.class
                );
                float price = resultSet.getFloat("price");
                String comment = resultSet.getString("comment");
                boolean refundable = resultSet.getBoolean("refundable");
                TicketType type = TicketType.valueOf(resultSet.getString("type"));
                int  venue_id = resultSet.getInt("venue_id");
                String venue_name = resultSet.getString("venue_name");
                int venue_capacity = resultSet.getInt("venue_capacity");
                VenueType venue_type = (VenueType) resultSet.getObject("venue_type");
                String address_street = resultSet.getString("location_street");
                Float location_x = resultSet.getFloat("location_x");
                int location_y = resultSet.getInt("location_y");
                Double location_z = resultSet.getDouble("location_z");
                String location_name = resultSet.getString("location_name");

                Coordinates coordinates = new Coordinates(coordinates_x, coordinates_y);

                Location location = new Location(location_x, location_y, location_z, location_name);

                Address address = new Address(address_street, location);

                Venue venue = new Venue(venue_id, venue_name, venue_capacity, venue_type, address);

                Ticket ticket = new Ticket(id, name, coordinates, creation_date, price, comment, refundable, type, venue, owner);

                collection.add(ticket);
            }
        }

        return collection;
    }

    public Ticket getTicketById(Connection connection, int id) throws SQLException {
        String  sql = """
                SELECT * FROM tickets WHERE id = ?
        """;
        try (
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                int coordinates_x = resultSet.getInt("coordinates_x");
                int coordinates_y = resultSet.getInt("coordinates_y");
                LocalDate creation_date = resultSet.getObject(
                        "creation_date",
                        LocalDate.class
                );
                float price = resultSet.getFloat("price");
                String comment = resultSet.getString("comment");
                boolean refundable = resultSet.getBoolean("refundable");
                TicketType type = TicketType.valueOf(resultSet.getString("ticket_type"));
                int  venue_id = resultSet.getInt("venue_id");
                String venue_name = resultSet.getString("venue_name");
                int venue_capacity = resultSet.getInt("venue_capacity");
                VenueType venue_type = VenueType.valueOf(resultSet.getString("venue_type"));
                String address_street = resultSet.getString("address_street");
                Float location_x = resultSet.getFloat("location_x");
                int location_y = resultSet.getInt("location_y");
                Double location_z = resultSet.getDouble("location_z");
                String location_name = resultSet.getString("location_name");
                int owner  = resultSet.getInt("owner");

                Coordinates coordinates = new Coordinates(coordinates_x, coordinates_y);

                Location location = new Location(location_x, location_y, location_z, location_name);

                Address address = new Address(address_street, location);

                Venue venue = new Venue(venue_id, venue_name, venue_capacity, venue_type, address);

                Ticket ticket = new Ticket(id, name, coordinates, creation_date, price, comment, refundable, type, venue, owner);
                return ticket;
            }
            else {
                return null;
            }
        }
    }

    public int getIdByLogin(Connection connection, String login) throws SQLException {
        String sql = """
            SELECT id FROM users WHERE login = ?
            """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, login);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                } else {
                    return -1;
                }
            }
        }
    }

    public LinkedList<User> getAllUsers(Connection connection) throws SQLException {
        LinkedList<User> users = new LinkedList<>();
        String sql = """
                SELECT * FROM users
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql);) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString("login");
                String password = resultSet.getString("password_hash");
                int id = resultSet.getInt("id");

                User user = new User(id, name, password);
                users.add(user);
            }
            return users;
        }
    }

    public User getUserByLogin(Connection connection, User user) throws SQLException {
        String sql = """
                SELECT * FROM users WHERE login = ?
        """;
        try (PreparedStatement statement = connection.prepareStatement(sql);) {
            statement.setString(1, user.getLogin());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String name = resultSet.getString("name");
                    String password = resultSet.getString("password");
                    int id = resultSet.getInt("id");
                    user = new User(id, name, password);
                    return user;
                }
                else {
                    return null;
                }
            }
        }
    }
}
