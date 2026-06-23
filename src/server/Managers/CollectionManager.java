package Managers;

import CollectionClasses.Ticket;

import java.util.LinkedList;

import java.util.Optional;

public class CollectionManager {

    public synchronized void addAll(LinkedList<Ticket> collection, LinkedList<Ticket> tickets) {
        collection.addAll(tickets);
    }

    public synchronized void addTicket(LinkedList<Ticket> collection, Ticket ticket) {
        collection.add(ticket);
    }

    public synchronized boolean removeById(LinkedList<Ticket> collection, long id) {
        return collection.removeIf(ticket ->
                ticket.getId() == id

        );

    }

    public synchronized Optional<Ticket> getById(LinkedList<Ticket> collection, long id) {
        return collection.stream()
                .filter(ticket -> ticket.getId() == id)
                .findFirst();

    }

    public synchronized LinkedList<Ticket> getAll(LinkedList<Ticket> collection) {
        // Возвращаем копию, а не исходную коллекцию
        return new LinkedList<>(collection);

    }

    public synchronized int size(LinkedList<Ticket> collection) {
        return collection.size();
    }

    public synchronized void clearByOwner(LinkedList<Ticket> collection, int owner) {
        collection.removeIf(ticket ->
                ticket.getOwner() == owner
        );

    }

    public synchronized LinkedList<Ticket> getAllUserTickets(LinkedList<Ticket> collection, int owner) {
        LinkedList<Ticket> tickets = new LinkedList<>();
        for (Ticket ticket : collection) {
            if (ticket.getOwner() == owner) {
                tickets.add(ticket);
            }
        }
        return tickets;
    }

    public synchronized boolean updateTicket(LinkedList<Ticket> collection, int id, Ticket newTicket) {
        for (int i = 0; i < collection.size(); i++) {
            Ticket currentTicket = collection.get(i);

            if (currentTicket.getId() == id) {
                newTicket.setId(id);
                newTicket.setOwner(currentTicket.getOwner());
                collection.set(i, newTicket);
                return true;
            }
        }

        return false;
    }

}
