package CommandMethods;

import CollectionClasses.Ticket;
import Network.Response;

import java.util.LinkedList;

public class RemoveLastCommand implements Command {
    private final LinkedList<Ticket> collection;

    public RemoveLastCommand(LinkedList<Ticket> collection) {
        this.collection = collection;
    }

    @Override
    public Response execute() {
        if (collection.isEmpty()) {
            return new Response(false, "Коллекция пуста.");
        }
        collection.removeLast();
        IdController.ChangeIds(collection);
        return new Response(true, "Последний элемент удалён.");
    }
}
