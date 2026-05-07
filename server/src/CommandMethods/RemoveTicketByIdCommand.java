package CommandMethods;

import CollectionClasses.Ticket;
import Network.Response;
import java.util.LinkedList;

public class RemoveTicketByIdCommand implements Command {
    private final LinkedList<Ticket> collection;
    private final int id;

    public RemoveTicketByIdCommand(LinkedList<Ticket> collection, int id) {
        this.collection = collection;
        this.id = id;
    }

    @Override
    public Response execute() {
        if (id >= collection.size() || id < 0) {
            return new Response(false, "Билета с таким id не существует!");
        }
        collection.remove(id);
        IdController.ChangeIds(collection);
        return new Response(true, "Билет с ID " + id + " успешно удалён!");
    }
}