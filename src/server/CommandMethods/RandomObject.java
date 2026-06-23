package CommandMethods;

import CollectionClasses.*;

public class RandomObject {

    public static String random_string(int length) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (alphabet.length() * Math.random());
            sb.append(alphabet.charAt(index));
        }
        return sb.toString();
    }

    public static Ticket create_random() {

        int string_length = 1 + (int) (Math.random() * 100);

        String name = random_string(string_length);
        int price = 1 + (int) (Math.random() * 1000);
        String comment = random_string(string_length);
        boolean refundable = Math.random() < 0.5;
        TicketType ticket_type = TicketType.values()[(int) (Math.random() * TicketType.values().length)];

        Coordinates coordinates = new Coordinates((long) (Math.random() * 100), (int) (Math.random() * 100));

        Location location = new Location((float) (Math.random() * 100), (int) (Math.random() * 100), (double) (Math.random() * 100), random_string(string_length));

        Address address = new Address(random_string(string_length), location);

        Venue venue = new Venue(-1, random_string(string_length), 1 + (int) (Math.random() * 1000), VenueType.values()[(int) (Math.random() * VenueType.values().length)], address);

        Ticket ticket = new Ticket(-1, name, coordinates, java.time.LocalDate.now(), price, comment, refundable, ticket_type, venue, -1);

        return ticket;
    }
}