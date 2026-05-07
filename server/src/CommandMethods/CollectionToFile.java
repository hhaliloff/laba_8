/**
 * Класс для сохранения коллекции Ticket в файл и загрузки из файла.
 * Используется простой XML-подобный текст.
 */
package CommandMethods;

import CollectionClasses.Ticket;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class CollectionToFile {
    public static void collection_to_file(LinkedList<Ticket> collection, String path) throws IOException {
        String file_xml_out = collection.stream()
                .map(XmlCoder::code)
                .collect(Collectors.joining());
        FileWriterReader.write(file_xml_out, path);
    }

    public static void file_to_collection(LinkedList<Ticket> collection, String path) throws IOException {
        String file_xml = FileWriterReader.read(path);
        List<String> xml_codes = new LinkedList<>();

        while (file_xml.contains("<CollectionClasses.Ticket>")) {
            String one_code = file_xml.substring(file_xml.indexOf("<CollectionClasses.Ticket>"), file_xml.indexOf("</CollectionClasses.Ticket>")+27);
            xml_codes.add(one_code);
            file_xml = file_xml.substring(file_xml.indexOf("</CollectionClasses.Ticket>")+27);
        }

        xml_codes.stream()
                .map(XmlCoder::decode)
                .forEach(collection::add);
    }
}
