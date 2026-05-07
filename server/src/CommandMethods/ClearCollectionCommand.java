package CommandMethods;

import java.util.LinkedList;
import CollectionClasses.Ticket;
import Network.Response;

public class ClearCollectionCommand implements Command {
    private final LinkedList<Ticket> collection;

    public ClearCollectionCommand(LinkedList<Ticket> collection) {
        this.collection = collection;
    }

    @Override
    public Response execute() {
        collection.clear();  // Очищаем коллекцию
        IdController.ChangeIds(collection);  // Обновляем ID
        return new Response(true, "Коллекция успешно очищена!");
    }
}