package CommandMethods;

import CollectionClasses.Ticket;
import Network.Response;
import java.util.LinkedList;

public class AddTicketCommand implements Command {
    private final LinkedList<Ticket> collection;
    private final Ticket ticket;

    public AddTicketCommand(LinkedList<Ticket> collection, Ticket ticket) {
        this.collection = collection;
        this.ticket = ticket;
    }

    @Override
    public Response execute() {
        if (ticket == null) {
            return new Response(false, "Ошибка: ticket обязателен");
        }
        collection.add(ticket);
        IdController.ChangeIds(collection);
        return new Response(true, "Билет успешно добавлен в коллекцию!");
    }
}