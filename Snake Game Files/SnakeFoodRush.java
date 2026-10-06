import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;
import javax.sound.sampled.*;
import javax.swing.*;

public class SnakeFoodRush extends JFrame {

    static final int CELL = 20;
    static final int COLS = 30;
    static final int ROWS = 25;
    static final String HIGH_SCORE_FILE = "snake_highscore.txt";

    String playerName = "Player";
    int highScore = 0;
    int previousScore = -1;

    SoundPlayer sounds = new SoundPlayer();

    CardLayout cards = new CardLayout();
    JPanel screens = new JPanel(cards);
    NamePanel namePanel;
    MenuPanel menuPanel;
    GamePanel gamePanel;
    GameOverPanel gameOverPanel;

    public SnakeFoodRush() {
        setTitle("Snake Food Rush");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        loadHighScore();

        namePanel = new NamePanel(this);
        menuPanel = new MenuPanel(this);
        gamePanel = new GamePanel(this);
        gameOverPanel = new GameOverPanel(this);

        screens.add(namePanel, "NAME");
        screens.add(menuPanel, "MENU");
        screens.add(gamePanel, "GAME");
        screens.add(gameOverPanel, "GAMEOVER");

        add(screens);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        showScreen("NAME");
    }

    void showScreen(String name) {
        cards.show(screens, name);

        if (name.equals("GAME")) {
            gamePanel.requestFocusInWindow();
        }
        if (name.equals("NAME")) {
            namePanel.nameField.requestFocusInWindow();
        }
    }

    void gameOver(int score, int level, boolean levelMode) {
        boolean newHighScore = score > highScore;

        if (newHighScore) {
            highScore = score;
            saveHighScore();
        }

        gameOverPanel.showResult(score, level, levelMode, newHighScore, previousScore);

        previousScore = score;
        menuPanel.refresh();
        showScreen("GAMEOVER");
    }

    void loadHighScore() {
        try (Scanner reader = new Scanner(new File(HIGH_SCORE_FILE))) {
            highScore = reader.nextInt();
        } catch (FileNotFoundException | NoSuchElementException e) {
            highScore = 0;
        }
    }

    void saveHighScore() {
        try (PrintWriter writer = new PrintWriter(HIGH_SCORE_FILE)) {
            writer.println(highScore);
        } catch (FileNotFoundException e) {
            System.out.println("Could not save high score: " + e);
        }
    }

    static void setupScreen(JPanel panel) {
        panel.setPreferredSize(new Dimension(COLS * CELL, ROWS * CELL));
        panel.setBackground(new Color(20, 20, 30));
        panel.setLayout(new GridBagLayout());
    }

    static JPanel makeBox() {
        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        return box;
    }

    static JLabel makeLabel(String text, int size, boolean bold, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", bold ? Font.BOLD : Font.PLAIN, size));
        label.setForeground(color);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    static JButton makeButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setMaximumSize(new Dimension(220, 35));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    static Component space(int height) {
        return Box.createRigidArea(new Dimension(0, height));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SnakeFoodRush());
    }
}

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

class GamePanel extends JPanel implements ActionListener {
    SnakeFoodRush app;

    static final int START_DELAY = 120;
    static final int MIN_DELAY = 55;
    static final int FRUITS_PER_LEVEL = 10;

    Timer timer;

    ArrayList<Point> snake = new ArrayList<>();
    ArrayList<Point> obstacles = new ArrayList<>();
    Point fruit;
    Random random = new Random();

    int dx, dy;
    int nextDx, nextDy;

    boolean levelMode;
    boolean running;
    boolean paused;

    int score;
    int level;
    int fruitsEaten;

    GamePanel(SnakeFoodRush app) {
        this.app = app;
        setPreferredSize(new Dimension(SnakeFoodRush.COLS * SnakeFoodRush.CELL,
                SnakeFoodRush.ROWS * SnakeFoodRush.CELL));
        setBackground(new Color(20, 20, 28));
        setFocusable(true);

        timer = new Timer(START_DELAY, this);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKey(e.getKeyCode());
            }
        });
    }

    void startGame(boolean levelMode) {
        this.levelMode = levelMode;
        score = 0;
        level = 1;
        fruitsEaten = 0;
        running = true;
        paused = false;

        snake.clear();
        int startX = SnakeFoodRush.COLS / 2;
        int startY = SnakeFoodRush.ROWS / 2;
        snake.add(new Point(startX, startY));
        snake.add(new Point(startX - 1, startY));
        snake.add(new Point(startX - 2, startY));

        dx = 1;
        dy = 0;
        nextDx = 1;
        nextDy = 0;

        obstacles.clear();
        placeFruit();

        timer.setDelay(START_DELAY);
        timer.restart();
        app.sounds.startMusic();

        app.showScreen("GAME");
        repaint();
    }

    void handleKey(int key) {
        if (!running) {
            return;
        }

        if (key == KeyEvent.VK_P || key == KeyEvent.VK_SPACE) {
            paused = !paused;
            if (paused) {
                app.sounds.pauseMusic();
            } else {
                app.sounds.resumeMusic();
            }
            repaint();
            return;
        }

        if (paused) {
            return;
        }

        switch (key) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                if (dy == 0) {
                    nextDx = 0;
                    nextDy = -1;
                }
                break;

            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                if (dy == 0) {
                    nextDx = 0;
                    nextDy = 1;
                }
                break;

            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                if (dx == 0) {
                    nextDx = -1;
                    nextDy = 0;
                }
                break;

            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                if (dx == 0) {
                    nextDx = 1;
                    nextDy = 0;
                }
                break;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!running || paused) {
            return;
        }
        moveSnake();
        repaint();
    }

    void moveSnake() {

        dx = nextDx;
        dy = nextDy;

        Point head = snake.get(0);
        Point newHead = new Point(head.x + dx, head.y + dy);

        boolean ateFruit = newHead.equals(fruit);
        snake.add(0, newHead);
        if (!ateFruit) {
            snake.remove(snake.size() - 1);
        }

        if (hitsWall(newHead) || hitsBody(newHead) || hitsObstacle(newHead)) {
            endGame();
            return;
        }

        if (ateFruit) {
            eatFruit();
        }
    }

    boolean hitsWall(Point p) {
        return p.x < 0 || p.x >= SnakeFoodRush.COLS
                || p.y < 0 || p.y >= SnakeFoodRush.ROWS;
    }

    boolean hitsBody(Point p) {
        for (int i = 1; i < snake.size(); i++) {
            if (snake.get(i).equals(p)) {
                return true;
            }
        }
        return false;
    }

    boolean hitsObstacle(Point p) {
        return levelMode && obstacles.contains(p);
    }

    void eatFruit() {
        app.sounds.playEat();
        score++;
        fruitsEaten++;

        if (levelMode && fruitsEaten % FRUITS_PER_LEVEL == 0) {
            levelUp();
        }

        placeFruit();
    }

    void levelUp() {
        level++;
        placeObstacles();

        int newDelay = START_DELAY - (level - 1) * 7;
        timer.setDelay(Math.max(MIN_DELAY, newDelay));
    }

    void placeFruit() {
        while (true) {
            Point p = new Point(random.nextInt(SnakeFoodRush.COLS),
                    random.nextInt(SnakeFoodRush.ROWS));

            if (!snake.contains(p) && !obstacles.contains(p)) {
                fruit = p;
                return;
            }
        }
    }

    void placeObstacles() {
        obstacles.clear();
        int howMany = Math.min((level - 1) * 4, 45);
        int centerX = SnakeFoodRush.COLS / 2;
        int centerY = SnakeFoodRush.ROWS / 2;

        while (obstacles.size() < howMany) {
            Point p = new Point(random.nextInt(SnakeFoodRush.COLS),
                    random.nextInt(SnakeFoodRush.ROWS));

            boolean nearStart = Math.abs(p.x - centerX) <= 4 && Math.abs(p.y - centerY) <= 3;

            if (!nearStart && !snake.contains(p) && !obstacles.contains(p)) {
                obstacles.add(p);
            }
        }
    }

    void endGame() {
        running = false;
        timer.stop();

        app.sounds.stopMusic();
        app.sounds.playHit();

        app.gameOver(score, level, levelMode);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int cell = SnakeFoodRush.CELL;

        g.setColor(new Color(140, 90, 90));
        if (levelMode) {
            for (Point p : obstacles) {
                g.fillRect(p.x * cell + 1, p.y * cell + 1, cell - 2, cell - 2);
            }
        }

        if (fruit != null) {
            g.setColor(new Color(235, 65, 70));
            g.fillOval(fruit.x * cell + 2, fruit.y * cell + 2, cell - 4, cell - 4);
        }

        for (int i = 0; i < snake.size(); i++) {
            Point p = snake.get(i);

            if (i == 0) {
                g.setColor(new Color(100, 240, 130));
            } else {
                g.setColor(new Color(55, 185, 95));
            }
            g.fillRect(p.x * cell + 1, p.y * cell + 1, cell - 2, cell - 2);
        }

        g.setColor(new Color(0, 0, 0, 175));
        g.fillRect(0, 0, getWidth(), 30);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 13));

        String info = app.playerName + "   Score: " + score;
        if (levelMode) {
            info += "   Level: " + level
                    + "   Progress: " + (fruitsEaten % FRUITS_PER_LEVEL) + "/" + FRUITS_PER_LEVEL;
        }
        g.drawString(info, 8, 20);

        if (paused) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 34));
            drawCentered(g, "PAUSED", getHeight() / 2);

            g.setFont(new Font("Arial", Font.PLAIN, 15));
            drawCentered(g, "Press P or SPACE to resume", getHeight() / 2 + 30);
        }
    }

    void drawCentered(Graphics g, String text, int y) {
        int textWidth = g.getFontMetrics().stringWidth(text);
        g.drawString(text, (getWidth() - textWidth) / 2, y);
    }
}

class GameOverPanel extends JPanel {
    SnakeFoodRush app;
    boolean lastLevelMode;

    JLabel scoreLabel = SnakeFoodRush.makeLabel(" ", 16, false, Color.WHITE);
    JLabel levelLabel = SnakeFoodRush.makeLabel(" ", 16, false, Color.WHITE);
    JLabel previousLabel = SnakeFoodRush.makeLabel(" ", 16, false, Color.WHITE);
    JLabel highLabel = SnakeFoodRush.makeLabel(" ", 16, false, Color.WHITE);
    JLabel messageLabel = SnakeFoodRush.makeLabel(" ", 16, false, Color.WHITE);

    GameOverPanel(SnakeFoodRush app) {
        this.app = app;
        SnakeFoodRush.setupScreen(this);

        JLabel title = SnakeFoodRush.makeLabel("GAME OVER", 30, true, new Color(230, 70, 70));

        JButton retryButton = SnakeFoodRush.makeButton("Try Again");
        JButton menuButton = SnakeFoodRush.makeButton("Main Menu");

        retryButton.addActionListener(e -> app.gamePanel.startGame(lastLevelMode));
        menuButton.addActionListener(e -> app.showScreen("MENU"));

        JPanel box = SnakeFoodRush.makeBox();
        box.add(title);
        box.add(SnakeFoodRush.space(20));
        box.add(scoreLabel);
        box.add(levelLabel);
        box.add(SnakeFoodRush.space(10));
        box.add(previousLabel);
        box.add(highLabel);
        box.add(SnakeFoodRush.space(8));
        box.add(messageLabel);
        box.add(SnakeFoodRush.space(25));
        box.add(retryButton);
        box.add(SnakeFoodRush.space(10));
        box.add(menuButton);
        add(box);
    }

    void showResult(int score, int level, boolean levelMode, boolean newHighScore, int previousScore) {
        lastLevelMode = levelMode;

        scoreLabel.setText("Final Score: " + score);

        levelLabel.setVisible(levelMode);
        levelLabel.setText("Level Reached: " + level);

        if (previousScore < 0) {
            previousLabel.setText("Previous Score: -");
        } else {
            previousLabel.setText("Previous Score: " + previousScore);
        }

        highLabel.setText("Highest Score: " + app.highScore);

        if (newHighScore) {
            messageLabel.setText("NEW HIGH SCORE!");
            messageLabel.setForeground(new Color(255, 215, 0));
        } else {
            messageLabel.setText("Good game! Try to beat your best score.");
            messageLabel.setForeground(Color.WHITE);
        }
    }
}

class SoundPlayer {

    Clip eatSound = load("eatsfx.wav");
    Clip hitSound = load("Hitwallsfx.wav");
    Clip music = load("BGmusic.wav");

    Clip load(String fileName) {
        try (AudioInputStream audio = AudioSystem.getAudioInputStream(new File(fileName))) {
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Could not load " + fileName + ": " + e);
            return null;
        }
    }

    void play(Clip clip) {
        if (clip == null) {
            return;
        }
        clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    void playEat() {
        play(eatSound);
    }

    void playHit() {
        play(hitSound);
    }

    void startMusic() {
        if (music == null) {
            return;
        }
        music.stop();
        music.setFramePosition(0);
        music.loop(Clip.LOOP_CONTINUOUSLY);
    }

    void pauseMusic() {
        if (music != null) {
            music.stop();
        }
    }

    void resumeMusic() {
        if (music != null) {
            music.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    void stopMusic() {
        if (music != null) {
            music.stop();
            music.setFramePosition(0);
        }
    }
}
