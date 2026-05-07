package CommandMethods;

import java.util.LinkedList;
import java.io.IOException;
import CollectionClasses.Ticket;
import Network.Response;

public class SaveCollectionCommand implements Command {
    private final LinkedList<Ticket> collection;
    private final String path;

    public SaveCollectionCommand(LinkedList<Ticket> collection, String path) {
        this.collection = collection;
        this.path = path;
    }

    @Override
    public Response execute() {
        if (path == null || path.isBlank()) {
            return new Response(false, "Ошибка: переменная окружения TICKETS_FILE не задана.");
        }
        try {
            CollectionToFile.collection_to_file(collection, path);
            return new Response(true, "Коллекция успешно сохранена в файл!");
        } catch (IOException e) {
            return new Response(false, "Ошибка при сохранении коллекции в файл: " + e.getMessage());
        }
    }
}