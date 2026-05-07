package CommandMethods;

import CollectionClasses.Ticket;
import Network.Response;

import java.util.Comparator;
import java.util.LinkedList;

public class AddIfMinCommand implements Command {
    private final LinkedList<Ticket> collection;
    private final Ticket ticket;

    public AddIfMinCommand(LinkedList<Ticket> collection, Ticket ticket) {
        this.collection = collection;
        this.ticket = ticket;
    }

    @Override
    public Response execute() {
        // Если билет не был передан (null), возвращаем ошибку
        if (ticket == null) {
            return new Response(false, "Ошибка: ticket обязателен");
        }

        // Если коллекция пуста, добавляем билет сразу
        if (collection.isEmpty()) {
            collection.add(ticket);
            IdController.ChangeIds(collection);  // Обновляем ID
            return new Response(true, "Элемент добавлен в коллекцию, так как коллекция пуста.");
        }

        // Находим билет с минимальной ценой в коллекции
        Ticket minTicket = collection.stream()
                .min(Comparator.comparingDouble(Ticket::getPrice))  // Сортируем по цене
                .orElse(null);  // Если коллекция пуста, возвращаем null

        // Если минимальный билет найден и новый билет дешевле, добавляем новый билет
        if (minTicket != null && ticket.getPrice() < minTicket.getPrice()) {
            collection.add(ticket);  // Добавляем новый билет
            IdController.ChangeIds(collection);  // Обновляем ID
            return new Response(true, "Элемент добавлен.");
        }

        // Если условие не выполняется (новый билет не дешевле минимального)
        return new Response(false, "Элемент не добавлен, так как его цена больше или равна минимальной цене в коллекции.");
    }
}