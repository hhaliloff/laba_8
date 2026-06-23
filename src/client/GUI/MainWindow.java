package GUI;

import Network.UDPClient;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainWindow {
    private static UDPClient client;
    private static String login = null;
    private static String password = null;
    private Locale locale = new Locale("en", "CA");
    private ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
    private static final String SERVER_HOST = "192.168.10.80";
    private static final int SERVER_PORT = 32456;

    public MainWindow(UDPClient client, String login, String password) {
        this.client = client;
        this.login = login;
        this.password = password;
    }

    public createTopPanel() {

        JPanel Panel = new JPanel(new GridLayout(1, 2, 10, 10));

        JLabel userLabel = new JLabel(bundle.getString("current.user") + login);
        JLabel languageLabel = new JLabel(bundle.getString("language"));
        JComboBox<String> languageBox = new JComboBox<>(new String[] {"English", "Русский", "Български", "Íslenska"});

        languageBox.addActionListener(e -> {
            String selected = (String) languageBox.getSelectedItem();

            if (selected.equals("English")) {
                locale = new Locale("en", "CA");
            } else if (selected.equals("Русский")) {
                locale = new Locale("ru");
            } else if (selected.equals("Български")) {
                locale = new Locale("bg");
            } else if (selected.equals("Íslenska")) {
                locale = new Locale("is");
            }
            bundle = ResourceBundle.getBundle("messages", locale);

            updateTexts();
        });
    }
}
