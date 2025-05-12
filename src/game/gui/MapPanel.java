package game.gui;

import game.Main;
import game.characters.Enemy;
import game.characters.PlayerCharacter;
import game.combat.RangedFighter;
import game.core.GameEntity;
import game.engine.GameWorld;
import game.items.*;
import game.map.GameMap;
import game.map.Position;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.List;
import game.gui.GameObserver;


import static game.Main.calcDistance;
import static game.gui.PopupPanel.quickPopup;

public class MapPanel extends JPanel implements GameObserver {

    private JButton[][] gridButtons;
    private int mapSize;

    public MapPanel() {
        mapSize = GameWorld.getInstance().getMap().getMapSize();
        setLayout(new GridLayout(mapSize, mapSize));
        setPreferredSize(new Dimension(64 * mapSize, 64 * mapSize));
        gridButtons = new JButton[mapSize][mapSize];

        for (int row = 0; row < mapSize; row++) {
            for (int col = 0; col < mapSize; col++) {
                final int r = row;
                final int c = col;

                JButton button = new JButton();
                button.setEnabled(false);
                button.setFocusable(false);


                button.addActionListener(e -> {
                    GameWorld world = GameWorld.getInstance();
                    PlayerCharacter player = world.getPlayers().get(0);
                    Position clickedPos = new Position(r, c);
                    int fightRange = 1;
                    if(player instanceof RangedFighter)
                        fightRange = 2;
                    if (Main.calcDistance(player.getPosition(), clickedPos) <= fightRange) {
                        Main.moveToPosition(world, player, clickedPos);
                    }

                });


                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (SwingUtilities.isRightMouseButton(e)) {
                            GameWorld world = GameWorld.getInstance();
                            PlayerCharacter player = world.getPlayers().get(0);
                            Position clickedPos = new Position(r, c);
                            if (Main.calcDistance(player.getPosition(), clickedPos) <= 2) {
                                quickPopup(world, clickedPos);

                                //Main.handleInteractions(world, player, clickedPos);
                                //world.getGameFrame().getMapPanel().updateMap();
                                //world.getGameFrame().getStatusPanel().updateStatus(player);
                            }
                        }
                    }
                });

                gridButtons[row][col] = button;
                add(button);
            }
        }

        updateMap();
    }

    public String getImagePath(String type) {
        String base = "/game/resources/images/";
        switch (type) {
            case "player":
                String playerType = GameWorld.getInstance().getPlayers().get(0).getClass().getSimpleName();
                return base + playerType + ".png";
            case "D":
                return base + "Dragon.png";
            case "G":
                return base + "Goblin.png";
            case "O":
                return base + "Orc.png";
            case "T":
                return base + "Treasure.png";
            case "W":
                return base + "Wall.png";
            case "L":
                return base + "Life_potion.png";
            case "P":
                return base + "Power_potion.png";
            default:
                return "";
        }
    }

    private ImageIcon loadImageIcon(String path) {
        URL url = getClass().getResource(path);
        if (url != null) {
            ImageIcon icon = new ImageIcon(url);
            Image scaledImage = icon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } else {
            System.err.println("Image not found: " + path);
            return null;
        }
    }

    public void updateMap() {
        GameMap map = GameWorld.getInstance().getMap();
        Position playerPos = GameWorld.getInstance().getPlayers().get(0).getPosition();

        for (int row = 0; row < mapSize; row++) {
            for (int col = 0; col < mapSize; col++) {
                Position pos = new Position(row, col);
                List<GameEntity> entities = map.getEntitiesAt(pos);
                JButton button = gridButtons[row][col];
                button.setIcon(null);
                button.setEnabled(true);

                if (pos.equals(playerPos)) {
                    ImageIcon playerIcon = loadImageIcon(getImagePath("player"));
                    if (playerIcon != null) button.setIcon(playerIcon);
                    continue;
                }

                for (GameEntity entity : entities) {
                    if (entity != null) {
                        boolean isClose = calcDistance(playerPos, entity.getPosition()) <= 2;
                        entity.setVisible(isClose);
                    }
                    else
                        continue;

                    if (entity.isVisible()) {
                        String type = "";
                        if(entity instanceof PowerPotion)
                            type = "P";
                        else if(entity instanceof Potion)
                            type = "L";

                        ImageIcon icon = loadImageIcon(getImagePath(type == "" ? entity.getDisplaySymbol() : type));
                        if (icon != null) button.setIcon(icon);
                        break;
                    } else {
                        button.setText("");
                    }
                }
            }
        }
        revalidate();
        repaint();
    }

    public JButton getCellButton(int row, int col) {
        return gridButtons[row][col];
    }

    public void flashCell(Position pos, Color color)
    {
        int row = pos.getRow();
        int col = pos.getCol();
        JButton button = gridButtons[row][col];
        Color originalColor = button.getBackground();
        final boolean[] toggle = {true}; // to switch between colors
        final int[] count = {0};         // how many ticks happened (max 10)

        Timer timer = new Timer(100, e -> {
            if (toggle[0]) {
                button.setBackground(color);
            } else {
                button.setBackground(originalColor);
            }
            toggle[0] = !toggle[0];  // flip color
            count[0]++;

            if (count[0] >= 10) { // 5 full blinks = 10 color switches
                ((Timer) e.getSource()).stop();
                button.setBackground(originalColor); // ensure it ends on original color
            }
        });

        timer.setRepeats(true);
        timer.start();
    }
    @Override
    public void update() {
        repaint();
    }

}
