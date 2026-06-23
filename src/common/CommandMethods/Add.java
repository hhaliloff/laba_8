/**
 * Класс для создания нового Ticket через ввод с клавиатуры.
 */
package CommandMethods;

import CollectionClasses.*;

import java.util.Scanner;

public class Add {
    public static Ticket add() {
        Scanner scanner = new Scanner(System.in);
        String name = ErrorCatcher.notNullOrEmpty(scanner, "Введите название билета: ");
        long coordinates_x = ErrorCatcher.below104(scanner, "Введите координату x (не больше 104): ");
        int coordinates_y = ErrorCatcher.notInt(scanner, "Введите координату y: ");
        float price = ErrorCatcher.notFloatOrMoreZero(scanner, "Введите цену билета: ");
        String comment = ErrorCatcher.notNullOrEmpty(scanner, "Введите комментарий к билету: ");
        boolean refundable = ErrorCatcher.notBoolean(scanner, "Можно ли вернуть билет? (true/false): ");
        TicketType ticket_type = ErrorCatcher.notTicketType(scanner, "Введите тип билета");
        String venue_name = ErrorCatcher.notNullOrEmpty(scanner, "Введите название места проведения: ");
        int venue_capacity = ErrorCatcher.moreZero(scanner, "Введите вместимость места проведения: ");
        VenueType venue_type = ErrorCatcher.notVenueType(scanner, "Введите тип места проведения");
        String address_street = ErrorCatcher.notEmpty(scanner, "Введите адрес места проведения: ");
        float location_x = (float) (ErrorCatcher.notInt(scanner, "Введите координату x для места встречи: "));
        int location_y = ErrorCatcher.notInt(scanner, "Введите координату y для места встречи: ");
        double location_z = (ErrorCatcher.notInt(scanner, "Введите координату z для места встречи: "));
        String location_name = ErrorCatcher.normalInput(scanner, "Введите город для места встречи: ");

        Location location = new Location(location_x, location_y, location_z, location_name);
        Address address = new Address(address_street, location);
        Venue venue = new Venue(-1, venue_name, venue_capacity, venue_type, address);
        Coordinates coordinates = new Coordinates(coordinates_x, coordinates_y);
        Ticket ticket = new Ticket(-1, name, coordinates, java.time.LocalDate.now(), price, comment, refundable, ticket_type, venue, -1);
        return ticket;

        }
    }