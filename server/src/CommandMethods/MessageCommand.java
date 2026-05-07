package CommandMethods;

import Network.Response;

public class MessageCommand implements Command {
    private final Response response;

    public MessageCommand(boolean success, String message) {
        this.response = new Response(success, message);
    }

    public MessageCommand(Response response) {
        this.response = response;
    }

    @Override
    public Response execute() {
        return response;
    }
}

