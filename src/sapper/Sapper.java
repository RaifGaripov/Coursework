package sapper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.String;

public class Sapper extends JFrame {
    private final Game game;

    private JPanel panel;
    private JLabel label;

    private int COLS;
    private int ROWS;
    private int BOMBS;
    private final int IMAGE_SIZE = 50;

    public static void main(String[] args) {
        new Sapper().setVisible(true);
    }

    private Sapper() {
        initParameters();
        game = new Game(COLS, ROWS, BOMBS);
        game.start();
        setImage();
        initLabel();
        initPanel();
        initFrame();
    }

    private void initParameters() {
        try {
            String input = JOptionPane.showInputDialog("Enter the number of columns:");
            COLS = Integer.parseInt(input);
            input = JOptionPane.showInputDialog("Enter the number of rows:");
            ROWS = Integer.parseInt(input);
            input = JOptionPane.showInputDialog("Enter the number of bombs:");
            BOMBS = Integer.parseInt(input);
        }
        catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null,"Incorrect parameters entered");
            System.exit(0);
        }
    }

    private void initLabel() {
        label = new JLabel("Click any cell to start");
        add(label, BorderLayout.NORTH);
    }

    private void initPanel() {
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                for (Coordinate coordinate : Ranges.getAllCoordinates()) {
                    g.drawImage((Image) game.getBox(coordinate).image,
                            coordinate.x * IMAGE_SIZE, coordinate.y * IMAGE_SIZE, this);
                }
            }
        };

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX() / IMAGE_SIZE;
                int y = e.getY() / IMAGE_SIZE;
                Coordinate coordinate = new Coordinate(x, y);
                if (e.getButton() == MouseEvent.BUTTON1)
                    game.pressLeftButton(coordinate);
                if (e.getButton() == MouseEvent.BUTTON3)
                    game.pressRightButton(coordinate);
                if (e.getButton() == MouseEvent.BUTTON2)
                    game.start();
                label.setText(getMessage());
                panel.repaint();
            }
        });
        panel.setPreferredSize(new Dimension(
                Ranges.getSize().x * IMAGE_SIZE,
                Ranges.getSize().y * IMAGE_SIZE));
        add(panel);

    }

    private String getMessage() {
        switch (game.getGameState()) {
            case PLAYED:
                return "Choose the next cell";
            case BOMBED:
                return "YOU LOSE!";
            case WINNER:
                return "YOU WON! CONGRATULATIONS!";
            default:
                return "Click any cell to start";
        }
    }

    private void initFrame() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Sapper Game");
        setResizable(false);
        setVisible(true);
        pack();
        setLocationRelativeTo(null);
        setIconImage(getImage("icon"));
    }

    private void setImage() {
        for (Box box : Box.values()) {
            box.image = getImage(box.name().toLowerCase());
        }
    }

    private Image getImage(String name) {
        String filename = "/img/" + name + ".png";
        ImageIcon icon = new ImageIcon(getClass().getResource(filename));
        return icon.getImage();
    }
}