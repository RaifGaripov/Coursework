package sapper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.String;

import static java.awt.event.KeyEvent.*;

public class Sapper extends JFrame {
    private Game game;

    private JPanel panel;
    private JLabel label;
    private JButton saveGameButton;
    private JFileChooser fileChooser;

    private int COLS;
    private int ROWS;
    private int BOMBS;
    private final int IMAGE_SIZE = 50;

    public static void main(String[] args) {
        new Sapper().setVisible(true);
    }

    private Sapper() {
        initFileChooser();
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
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Incorrect parameters entered");
            System.exit(0);
        }
    }

    private void initLabel() {
        label = new JLabel("Click any cell to start");
        add(label, BorderLayout.SOUTH);
    }

    private void initFileChooser()
    {
        fileChooser = new JFileChooser();
        int ret = fileChooser.showDialog(null, "Открыть сохранение");
        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            FileReader load = null;
            try {
                load = new FileReader(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            BufferedReader reader = new BufferedReader(load);

            String currentLine;
            String[] line = new String[10];
            int i = 0;

            while(line != null && i < 1) {
                try {
                    line = reader.readLine().split(" ");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ROWS = Integer.parseInt(line[0]);
                COLS = Integer.parseInt(line[1]);
                BOMBS = Integer.parseInt(line[2]);
                i++;
            }

            game = new Game(COLS, ROWS, BOMBS);
            game.start();
            Coordinate coordinate = new Coordinate(0,0);


            while(true) {
                try {
                    line = reader.readLine().split(" ");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                currentLine = line[0];
                if(currentLine.equals("@")) break;

                for(int j = 0; j < COLS; j++) {
                    game.getFlagMap().set(coordinate, Box.valueOf(line[j]));
                    coordinate.incX();
                }

                coordinate.setX(0);
                coordinate.incY();

                i++;
            }

            coordinate.setX(0);
            coordinate.setY(0);

            line = new String[COLS];
            while(line != null) {
                try {
                    line = reader.readLine().split(" ");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException n){
                     break;
                }

                for(int j = 0; j < COLS; j++) {
                    game.getBombMap().set(coordinate, Box.valueOf(line[j]));
                    coordinate.incX();
                }

                coordinate.setX(0);
                coordinate.incY();
                i++;
            }
        }
        else {
            initParameters();
            game = new Game(COLS, ROWS, BOMBS);
            game.start();
        }
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

        saveGameButton = new JButton("Save game");
        add(saveGameButton, BorderLayout.BEFORE_FIRST_LINE);

        saveGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Coordinate coordinate = new Coordinate(0, 0);
                game.saveGame(coordinate,COLS,ROWS);
            }
        });

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

        Coordinate point = new Coordinate(0, 0);
        panel.setFocusable(true);
        panel.requestFocusInWindow();
        game.pointerOpen(point);
        panel.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                switch (key) {
                    case VK_UP:
                        if (point.getY() <= 0) break;
                        game.pointerClose(point);
                        point.decY();
                        game.pointerOpen(point);
                        break;
                    case VK_DOWN:
                        if (point.getY() >= COLS - 1) break;
                        game.pointerClose(point);
                        point.incY();
                        game.pointerOpen(point);
                        break;
                    case VK_RIGHT:
                        if (point.getX() >= ROWS - 1) break;
                        game.pointerClose(point);
                        point.incX();
                        game.pointerOpen(point);
                        break;
                    case VK_LEFT:
                        if (point.getX() <= 0) break;
                        game.pointerClose(point);
                        point.decX();
                        game.pointerOpen(point);
                        break;
                    case VK_Z:
                        if (game.getGameState() == GameState.BOMBED || game.getGameState() == GameState.WINNER) {
                            point.setX(0);
                            point.setY(0);
                            game.pointerOpen(point);
                        }
                        game.pressLeftButton(point);
                        if (game.getGameState() == GameState.BOMBED || game.getGameState() == GameState.WINNER) {
                            point.setX(0);
                            point.setY(0);
                            game.pointerOpen(point);
                        }
                        break;
                    case VK_X:
                        if (game.getGameState() == GameState.BOMBED || game.getGameState() == GameState.WINNER) {
                            point.setX(0);
                            point.setY(0);
                            game.pointerOpen(point);
                        }
                        game.pressRightButton(point);
                        break;
                    case VK_R:
                        game.start();
                        point.setX(0);
                        point.setY(0);
                        game.pointerOpen(point);
                        break;
                    default:
                        break;
                }
                panel.repaint();
            }

            @Override
            public void keyReleased(KeyEvent e) {

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