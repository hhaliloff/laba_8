package CommandMethods;

import CollectionClasses.Ticket;
import Network.Response;
import java.util.LinkedList;

public class UpdateTicketCommand implements Command {
    private final LinkedList<Ticket> collection;
    private final int id;
    private final Ticket ticket;

    public UpdateTicketCommand(LinkedList<Ticket> collection, int id, Ticket ticket) {
        this.collection = collection;
        this.id = id;
        this.ticket = ticket;
    }

    @Override
    public Response execute() {
        if (ticket == null) {
            return new Response(false, "Ошибка: ticket обязателен");
        }
        if (id >= collection.size() || id < 0) {
            return new Response(false, "Билета с таким id не существует");
        }
        collection.set(id, ticket);
        IdController.ChangeIds(collection);
        return new Response(true, "Билет с ID " + id + " успешно обновлён!");
    }
}