package Network;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;

public class UDPClient {

    private static final int BUFFER_SIZE = 65507;
    private static final int TIMEOUT_MS = 2000;
    private static final int MAX_RETRIES = 3;

    private final InetSocketAddress serverAddress;
    private final DatagramChannel channel;

    public UDPClient(String host, int port) throws IOException {
        this.serverAddress = new InetSocketAddress(host, port);
        this.channel = DatagramChannel.open();
        this.channel.configureBlocking(false);
        this.channel.connect(serverAddress);
    }

    public Response sendRequest(Request request) throws IOException {
        byte[] data = Serializer.serialize(request);
        ByteBuffer sendBuffer = ByteBuffer.wrap(data);

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            sendBuffer.rewind();
            channel.send(sendBuffer, serverAddress);

            Response response = waitForResponse();
            if (response != null) {
                return response;
            }

            System.out.println("Таймаут. Повтор...");
        }

        System.out.println("Сервер недоступен после " + MAX_RETRIES + " попыток.");
        return null;
    }

    /**
     * Ждёт ответ от сервера с таймаутом через Selector.
     */
    private Response waitForResponse() throws IOException {
        try (Selector selector = Selector.open()) {
            channel.register(selector, SelectionKey.OP_READ);

            int ready = selector.select(TIMEOUT_MS);
            if (ready == 0) {
                return null; // таймаут
            }

            selector.selectedKeys().clear();

            ByteBuffer receiveBuffer = ByteBuffer.allocate(BUFFER_SIZE);
            channel.receive(receiveBuffer);
            receiveBuffer.flip();

            byte[] bytes = new byte[receiveBuffer.remaining()];
            receiveBuffer.get(bytes);

            return Serializer.deserialize(bytes, Response.class);

        } catch (ClassNotFoundException e) {
            throw new IOException("Ошибка десериализации ответа", e);
        }
    }

    public void close() throws IOException {
        channel.close();
    }
}