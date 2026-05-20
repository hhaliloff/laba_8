package Network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serializer {

    public static byte[] serialize(Object obj) throws IOException {
        if (obj == null) {
            throw new IllegalArgumentException("Object to serialize is null");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(obj);
        oos.flush();

        return baos.toByteArray();
    }

    public static <T> T deserialize(byte[] data, Class<T> type) throws IOException, ClassNotFoundException {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Serialized data is empty");
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais);

        Object obj = ois.readObject();
        return type.cast(obj);
    }
}