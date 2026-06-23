package CommandMethods;

import CollectionClasses.Ticket;
import Network.Response;
import java.util.LinkedList;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

public class ShowCollectionCommand implements Command {
    private static final int PAGE_SIZE = 100;
    private final LinkedList<Ticket> collection;
    private final int page;

    public ShowCollectionCommand(LinkedList<Ticket> collection) {
        this(collection, 1);
    }

    public ShowCollectionCommand(LinkedList<Ticket> collection, int page) {
        this.collection = collection;
        this.page = page < 1 ? 1 : page;
    }

    @Override
    public Response execute() {
        LinkedList<Ticket> collection_snapshot = collection;
        if (collection_snapshot.isEmpty()) {
            return new Response(true, "Коллекция пуста.", List.of());
        }

        long offset = (long) (page - 1) * PAGE_SIZE;
        if (offset >= collection_snapshot.size()) {
            return new Response(true, "На этой странице билетов нет", List.of());
        }

        List<Ticket> sorted = collection_snapshot.stream()
                .sorted(Comparator.comparing(Ticket::getName))
                .skip(offset)
                .limit(PAGE_SIZE)
                .collect(Collectors.toList());
        return new Response(true, null, sorted);
    }
}