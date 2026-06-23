package CommandMethods;

import CollectionClasses.Ticket;
import Managers.CollectionManager;
import Managers.DBmanager;
import Network.Response;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

public class FilterGreaterThanVenueCommand implements Command {
    private final LinkedList<Ticket> collection;
    private final int capacity;

    public FilterGreaterThanVenueCommand(LinkedList<Ticket> collection, int capacity) {
        this.collection = collection;
        this.capacity = capacity;
    }

    @Override
    public Response execute() {
        LinkedList<Ticket> collection_snapshot = collection;
        List<Ticket> filteredTickets = collection_snapshot.stream()
                .filter(ticket -> ticket.getVenue().getCapacity() > capacity)
                .sorted(Comparator.comparing(Ticket::getName))
                .collect(Collectors.toList());

        if (filteredTickets.isEmpty()) {
            return new Response(true, "Нет элементов с вместимостью большим, чем заданное.", filteredTickets);
        }
        return new Response(true, null, filteredTickets);
    }
}
