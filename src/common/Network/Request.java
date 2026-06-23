package Network;

import CollectionClasses.Ticket;

import java.io.Serializable;

public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String command;
    private final String arg;
    private final Ticket ticket;
    private final String login;
    private final String password;

    public Request(String command, String login, String password) {
        this(command, null, null, login, password);
    }

    public Request(String command, String arg, String login, String password) {
        this(command, arg, null, login, password);
    }

    public Request(String command, String arg, Ticket ticket, String login, String password) {
        this.command = command;
        this.arg = arg;
        this.ticket = ticket;
        this.login = login;
        this.password = password;
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

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
