package CommandMethods;

import CollectionClasses.Ticket;
import Network.Response;

import java.time.LocalDate;
import java.util.LinkedList;

public class InfoCommand implements Command {
    private final LinkedList<Ticket> collection;

    public InfoCommand(LinkedList<Ticket> collection) {
        this.collection = collection;
    }

    @Override
    public Response execute() {
        LocalDate earliestDate = collection.stream()
                .map(Ticket::getCreationDate)
                .min(LocalDate::compareTo)
                .orElse(null);

        String message = "Это коллекция билетов на мероприятия Халилова Чингиза и Федорова Вячеслава. "
                + "Посещение всех мероприятий добровольно-обязательное, билеты покупаем на все!! "
                + "Дата создания коллекции - это дата создания самого старого билета в ней.\n"
                + "Тип коллекции: " + collection.getClass() + "\n"
                + "Дата создания: " + (earliestDate == null ? "нет данных" : earliestDate) + "\n"
                + "Количество элементов в коллекции: " + collection.size();
        return new Response(true, message);
    }
}
