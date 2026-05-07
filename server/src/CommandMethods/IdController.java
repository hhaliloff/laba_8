/**
 * Класс для обновления id у всех билетов.
 */
package CommandMethods;

import CollectionClasses.Ticket;

import java.util.LinkedList;
import java.util.stream.IntStream;

public class IdController {
    public static LinkedList<Ticket> ChangeIds(LinkedList<Ticket> collection) {
        IntStream.range(0, collection.size())
                .forEach(i -> collection.get(i).setId(i));
        return collection;
    }
}
