package Network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UDPServer {
    private static final String UDP_RESPONSE_TOO_LARGE = "Ошибка: размер ответа превышает допустимый для UDP";

    public interface RequestHandler {
        Response handle(Request request);
    }

    private static final int DEFAULT_BUFFER_SIZE = 65507;

    private final int port;
    private final int bufferSize;
    private final RequestHandler handler;
    private final Logger logger;

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
        running = true;
        logger.info("Сервер запущен на порту " + port);

        byte[] buffer = new byte[bufferSize];
        while (running) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(packet);

                byte[] requestBytes = Arrays.copyOfRange(
                        packet.getData(),
                        packet.getOffset(),
                        packet.getOffset() + packet.getLength()
                );
                logger.info("Новое подключение от: " + packet.getAddress() + ":" + packet.getPort());

                Request request = Serializer.deserialize(requestBytes, Request.class);
                logger.info("Получен запрос: команда='" + request.getCommand() + "'"
                        + (request.getArg() != null ? ", аргумент='" + request.getArg() + "'" : "")
                        + (request.getTicket() != null ? ", билет передан" : ""));

                logger.fine("Начало обработки команды: " + request.getCommand());
                Response response = handler.handle(request);
                logger.fine("Обработка завершена: успех=" + response.isSuccess());

                byte[] responseBytes = Serializer.serialize(response);
                if (responseBytes.length > bufferSize) {
                    logger.warning("Ответ превышает допустимый размер UDP: " + responseBytes.length + " байт");
                    response = new Response(false, UDP_RESPONSE_TOO_LARGE);
                    responseBytes = Serializer.serialize(response);
                }
                DatagramPacket reply = new DatagramPacket(
                        responseBytes,
                        responseBytes.length,
                        packet.getAddress(),
                        packet.getPort()
                );
                socket.send(reply);
                logger.info("Ответ отправлен клиенту " + packet.getAddress() + ":" + packet.getPort()
                        + " | успех=" + response.isSuccess()
                        + " | сообщение='" + response.getMessage() + "'");

            } catch (SocketException e) {
                if (running) {
                    logger.log(Level.SEVERE, "Ошибка сокета", e);
                }
                break;
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Ошибка обработки запроса", e);
            }
        }
    }

    public void stop() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        logger.info("UDP server stopped");
    }
}
