import java.awt.*;
import javax.swing.*;

class MenuPanel extends JPanel {
    SnakeFoodRush app;
    JLabel greetingLabel = SnakeFoodRush.makeLabel(" ", 16, false, Color.WHITE);
    JLabel highScoreLabel = SnakeFoodRush.makeLabel(" ", 14, false, new Color(255, 215, 0));

    MenuPanel(SnakeFoodRush app) {
        this.app = app;
        SnakeFoodRush.setupScreen(this);

        JLabel title = SnakeFoodRush.makeLabel("SNAKE FOOD RUSH", 28, true, new Color(80, 220, 100));

        JButton freeButton = SnakeFoodRush.makeButton("Free Mode");
        JButton levelButton = SnakeFoodRush.makeButton("Level Mode");
        JButton nameButton = SnakeFoodRush.makeButton("Change Name");

        freeButton.addActionListener(e -> app.gamePanel.startGame(false));
        levelButton.addActionListener(e -> app.gamePanel.startGame(true));
        nameButton.addActionListener(e -> app.showScreen("NAME"));

        JPanel box = SnakeFoodRush.makeBox();
        box.add(title);
        box.add(SnakeFoodRush.space(20));
        box.add(greetingLabel);
        box.add(SnakeFoodRush.space(5));
        box.add(highScoreLabel);
        box.add(SnakeFoodRush.space(30));
        box.add(freeButton);
        box.add(SnakeFoodRush.space(10));
        box.add(levelButton);
        box.add(SnakeFoodRush.space(10));
        box.add(nameButton);
        add(box);
    }

    void refresh() {
        greetingLabel.setText("Welcome, " + app.playerName + "!");
        highScoreLabel.setText("Highest Score: " + app.highScore);
    }
}
