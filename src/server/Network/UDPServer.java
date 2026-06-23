package Network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.InetAddress;
import java.util.concurrent.ForkJoinPool;

public class UDPServer {

    public interface RequestHandler {
        Response handle(Request request);
    }

    private static final int DEFAULT_BUFFER_SIZE = 65507;

    private final int port;
    private final int bufferSize;
    private final RequestHandler handler;
    private final Logger logger;

    private final ForkJoinPool readPool = new ForkJoinPool();
    private final ForkJoinPool sendPool = new ForkJoinPool();

    private DatagramSocket socket;
    private volatile boolean running;

    public UDPServer(int port, RequestHandler handler, Logger logger) {
        this(port, DEFAULT_BUFFER_SIZE, handler, logger);
    }

    public UDPServer(int port, int bufferSize, RequestHandler handler, Logger logger) {
        this.port = port;
        this.bufferSize = bufferSize;
        this.handler = Objects.requireNonNull(handler, "handler");
        this.logger = Objects.requireNonNull(logger, "logger");
    }

    public void start() throws IOException {
        if (running) {
            return;
        }

        socket = new DatagramSocket(port);
        socket.setSendBufferSize(4 * 1024 * 1024);
        running = true;

        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            readPool.execute(this::readLoop);
        }

        while (running) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                stop();
            }
        }
    }

    public void stop() {
        readPool.shutdownNow();
        sendPool.shutdownNow();
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        logger.info("UDP server stopped");
    }


    private void readLoop() {
        while (running) {
            try {
                byte[] buffer = new byte[bufferSize];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                socket.receive(packet);

                byte[] requestBytes = Arrays.copyOfRange(
                        packet.getData(),
                        packet.getOffset(),
                        packet.getOffset() + packet.getLength()
                );

                Request request = Serializer.deserialize(requestBytes, Request.class);

                InetAddress clientAddress = packet.getAddress();
                int clientPort = packet.getPort();

                new Thread(() -> processRequest(request, clientAddress, clientPort)).start();

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Ошибка чтения запроса", e);
            }
        }
    }

    private void processRequest(Request request, InetAddress clientAddress, int clientPort) {
        try {
            Response response = handler.handle(request);

            sendPool.execute(() -> sendResponse(response, clientAddress, clientPort));

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка обработки запроса", e);
        }
    }

    public Response processRequestForServer(Request request) {
        try {
            Response response = handler.handle(request);
            return response;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка обработки запроса", e);
        }
        return null;
    }

    private void sendResponse(Response response, InetAddress clientAddress, int clientPort) {
        try {
            byte[] responseBytes = Serializer.serialize(response);

            int chunkSize = 8000;
            int total = (responseBytes.length + chunkSize - 1) / chunkSize;
            if (total == 0) total = 1;

            for (int i = 0; i < total; i++) {
                int from = i * chunkSize;
                int to = Math.min(from + chunkSize, responseBytes.length);

                byte[] chunk = new byte[8 + (to - from)];
                ByteBuffer header = ByteBuffer.wrap(chunk, 0, 8);
                header.putInt(i);
                header.putInt(total);

                System.arraycopy(responseBytes, from, chunk, 8, to - from);

                DatagramPacket reply = new DatagramPacket(
                        chunk,
                        chunk.length,
                        clientAddress,
                        clientPort
                );

                socket.send(reply);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка отправки ответа", e);
        }
    }
}
