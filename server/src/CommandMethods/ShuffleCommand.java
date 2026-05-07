package CommandMethods;

import CollectionClasses.Ticket;
import Network.Response;

import java.util.Collections;
import java.util.LinkedList;

public class ShuffleCommand implements Command {
    private final LinkedList<Ticket> collection;

    public ShuffleCommand(LinkedList<Ticket> collection) {
        this.collection = collection;
    }

    @Override
    public Response execute() {
        Collections.shuffle(collection);
        IdController.ChangeIds(collection);
        return new Response(true, "Коллекция перемешана!");
    }
}
