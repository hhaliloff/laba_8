package CommandMethods;

import CollectionClasses.Ticket;
import Network.Response;
import java.util.LinkedList;
import java.util.stream.IntStream;

public class AddRandomTicketCommand implements Command {
    private final LinkedList<Ticket> collection;
    private final int count;

    public AddRandomTicketCommand(LinkedList<Ticket> collection) {
        this(collection, 1);
    }

    public AddRandomTicketCommand(LinkedList<Ticket> collection, int count) {
        this.collection = collection;
        this.count = count;
    }

    @Override
    public Response execute() {
        if (count <= 0) {
            return new Response(false, "Ошибка: количество должно быть положительным");
        }
        IntStream.range(0, count).forEach(i -> collection.add(RandomObject.create_random()));
        IdController.ChangeIds(collection);
        return new Response(true, count == 1 ? "Билет с рандомными полями успешно добавлен в коллекцию!" : "Добавлено билетов: " + count);
    }
}