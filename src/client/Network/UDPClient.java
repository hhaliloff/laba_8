package Network;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;

public class UDPClient {

    private static final int BUFFER_SIZE = 65507;
    private static final int TIMEOUT_MS = 60000;
    private static final int MAX_RETRIES = 3;

    private final InetSocketAddress serverAddress;
    private final DatagramChannel channel;

    public UDPClient(String host, int port) throws IOException {
        this.serverAddress = new InetSocketAddress(host, port);
        this.channel = DatagramChannel.open();
        this.channel.socket().setReceiveBufferSize(4 * 1024 * 1024);
        this.channel.configureBlocking(false);
        this.channel.connect(serverAddress);
    }

    public Response sendRequest(Request request) throws IOException {
        byte[] data = Serializer.serialize(request);
        if (data.length > BUFFER_SIZE) {
            throw new IOException("Размер запроса превышает допустимый для UDP");
        }
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

            byte[][] chunks = null;
            int total = -1;
            int received = 0;

            while (true) {
                int ready = selector.select(TIMEOUT_MS);
                if (ready == 0) {
                    return null;
                }
                selector.selectedKeys().clear();

                ByteBuffer receiveBuffer = ByteBuffer.allocate(BUFFER_SIZE);
                channel.receive(receiveBuffer);
                receiveBuffer.flip();

                int index = receiveBuffer.getInt();
                int t = receiveBuffer.getInt();
                byte[] payload = new byte[receiveBuffer.remaining()];
                receiveBuffer.get(payload);

                if (chunks == null) {
                    total = t;
                    chunks = new byte[total][];
                }
                if (chunks[index] == null) {
                    chunks[index] = payload;
                    received++;
                }
                if (received == total) {
                    break;
                }
            }

            int totalLen = 0;
            for (byte[] c : chunks) totalLen += c.length;
            byte[] full = new byte[totalLen];
            int pos = 0;
            for (byte[] c : chunks) {
                System.arraycopy(c, 0, full, pos, c.length);
                pos += c.length;
            }

            return Serializer.deserialize(full, Response.class);
        } catch (ClassNotFoundException e) {
            throw new IOException("Ошибка десериализации ответа", e);
        }
    }

    public void close() throws IOException {
        channel.close();
    }
}
