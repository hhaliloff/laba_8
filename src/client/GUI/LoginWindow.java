package GUI;

import Network.Request;
import Network.Response;
import Network.UDPClient;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginWindow extends JFrame {
    UDPClient client;

    private JTextField loginField;
    private JPasswordField passwordField;

    public LoginWindow(UDPClient client) {
        this.client = client;

        setTitle("Авторизация");
        setSize(350, 220);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


    }

    private void Show() {
        JPanel mainPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        JLabel loginLabel = new JLabel("Login:");
        JLabel passwordLabel = new JLabel("Password:");

        loginField = new JTextField();
        passwordField = new JPasswordField();

        mainPanel.add(loginLabel);
        mainPanel.add(loginField);
        mainPanel.add(passwordLabel);
        mainPanel.add(passwordField);

        JButton loginButton = new JButton("Log in");
        JButton registerButton = new JButton("Register");

        mainPanel.add(loginButton);
        mainPanel.add(registerButton);

        add(mainPanel);

        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> register());
    }

    private void login() {
        String login = loginField.getText();
        String password = new String(passwordField.getPassword());

        Request request = new Request("login", login, password);
        try {
            Response response = client.sendRequest(request);
            if (response.isSuccess()) {
                MainWindow mainWindow = new MainWindow(client, login, password);
                mainWindow.setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, response.getMessage());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage());
        }
    }

    private void register() {
        String login = loginField.getText();
        String password = new String(passwordField.getPassword());

        Request request = new Request("register", login, password);
        try {
            Response response = client.sendRequest(request);
            if (response.isSuccess()) {
                MainWindow mainWindow = new MainWindow(client, login, password);
                mainWindow.setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, response.getMessage());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage());
        }
    }
}
