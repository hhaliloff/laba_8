/**
 * Класс для перевода Ticket в текст и обратно.
 * Используется для сохранения в файл.
 */

package CommandMethods;
import com.thoughtworks.xstream.XStream;
import CollectionClasses.*;

public class XmlCoder {
    public static String code(Ticket ticket) {
        XStream xs = new XStream();
        String xml = xs.toXML(ticket);

        return xml;
    }

    public static Ticket decode(String xml) {
        XStream xs = new XStream();
        xs.allowTypesByWildcard(new String[] {
                "CollectionClasses.*"
        });
        Ticket ticket = (Ticket) xs.fromXML(xml);

        return ticket;
    }
}
