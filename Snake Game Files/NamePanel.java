import java.awt.*;
import javax.swing.*;

class NamePanel extends JPanel {
    SnakeFoodRush app;
    JTextField nameField = new JTextField(15);

    NamePanel(SnakeFoodRush app) {
        this.app = app;
        SnakeFoodRush.setupScreen(this);

        JLabel title = SnakeFoodRush.makeLabel("SNAKE FOOD RUSH", 32, true, new Color(80, 220, 100));
        JLabel prompt = SnakeFoodRush.makeLabel("Enter your name:", 18, false, Color.WHITE);

        nameField.setMaximumSize(new Dimension(250, 30));
        nameField.setFont(new Font("Arial", Font.PLAIN, 16));
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton continueButton = SnakeFoodRush.makeButton("Continue");

        continueButton.addActionListener(e -> submitName());
        nameField.addActionListener(e -> submitName());

        JPanel box = SnakeFoodRush.makeBox();
        box.add(title);
        box.add(SnakeFoodRush.space(30));
        box.add(prompt);
        box.add(SnakeFoodRush.space(10));
        box.add(nameField);
        box.add(SnakeFoodRush.space(15));
        box.add(continueButton);
        add(box);
    }

    void submitName() {
        String name = nameField.getText().trim();

        if (name.isEmpty()) {
            name = "Player";
        }
        if (name.length() > 16) {
            name = name.substring(0, 16);
        }

        app.playerName = name;
        app.menuPanel.refresh();
        app.showScreen("MENU");
    }
}
