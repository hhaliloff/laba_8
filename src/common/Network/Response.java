package Network;

import CollectionClasses.Ticket;

import java.io.Serializable;
import java.util.List;

public class Response implements Serializable {
    private static final long serialVersionUID = 1L;

    private final boolean success;
    private final String message;
    private final List<Ticket> tickets;

    public Response(boolean success, String message, List<Ticket> tickets) {
        this.success = success;
        this.message = message;
        this.tickets = tickets;
    }

    public Response(boolean success, String message) {
        this(success, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }
}

