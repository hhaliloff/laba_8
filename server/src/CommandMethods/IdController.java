/**
 * Класс для обновления id у всех билетов.
 */
package CommandMethods;

import CollectionClasses.Ticket;

import java.util.LinkedList;

public class IdController {
    public static LinkedList<Ticket> ChangeIds(LinkedList<Ticket> collection) {
        int id = 0;
        for (Ticket ticket : collection) {
            ticket.setId(id++);
        }
        return collection;
    }
}
