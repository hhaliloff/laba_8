package CommandMethods;

import CollectionClasses.Ticket;
import Network.Response;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class PrintFieldDescendingPriceCommand implements Command {
    private final LinkedList<Ticket> collection;

    public PrintFieldDescendingPriceCommand(LinkedList<Ticket> collection) {
        this.collection = collection;
    }

    @Override
    public Response execute() {
        String message = collection.stream()
                .sorted(Comparator.comparingDouble(Ticket::getPrice).reversed())
                .map(ticket -> "Ticket ID: " + ticket.getId() + ", Price: " + ticket.getPrice())
                .collect(Collectors.joining("\n"));
        if (message.isEmpty()) {
            message = "Коллекция пуста.";
        }
        return new Response(true, message);
    }
}
