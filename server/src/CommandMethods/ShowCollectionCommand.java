package CommandMethods;

import CollectionClasses.Ticket;
import Network.Response;
import java.util.LinkedList;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

public class ShowCollectionCommand implements Command {
    private final LinkedList<Ticket> collection;

    public ShowCollectionCommand(LinkedList<Ticket> collection) {
        this.collection = collection;
    }

    @Override
    public Response execute() {
        if (collection.isEmpty()) {
            return new Response(true, "Коллекция пуста.", List.of());
        }

        List<Ticket> sorted = collection.stream()
                .sorted(Comparator.comparing(Ticket::getName))
                .collect(Collectors.toList());
        return new Response(true, null, sorted);
    }
}