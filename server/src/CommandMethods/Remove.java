/**
 * Класс для удаления билета из коллекции по id.
 */
package CommandMethods;

import CollectionClasses.Ticket;

import java.util.LinkedList;

public class Remove {
    public static LinkedList<Ticket> remove_by_id(LinkedList<Ticket> collection, int id) {
        if (id < 0 || id >= collection.size()) {
            System.out.println("Билета с таким id не существует!");
            return collection;
        }
        collection.remove(id);
        IdController.ChangeIds(collection);
        System.out.println("Объект класса с индексом " + id + " успешно удалён из коллекции!");
        return collection;
    }
}
