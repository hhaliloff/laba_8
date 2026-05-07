package Network;

import CollectionClasses.Ticket;

import java.io.Serializable;

public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String command;
    private final String arg;
    private final Ticket ticket;

    public Request(String command) {
        this(command, null, null);
    }

    public Request(String command, String arg) {
        this(command, arg, null);
    }

    public Request(String command, String arg, Ticket ticket) {
        this.command = command;
        this.arg = arg;
        this.ticket = ticket;
    }

    public String getCommand() {
        return command;
    }

    public String getArg() {
        return arg;
    }

    public Ticket getTicket() {
        return ticket;
    }
}
