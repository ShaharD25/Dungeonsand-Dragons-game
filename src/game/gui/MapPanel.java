package game.gui;

import game.characters.Enemy;
import game.core.GameEntity;
import game.engine.GameWorld;
import game.items.*;
import game.map.GameMap;
import game.map.Position;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.List;

import static game.Main.calcDistance;

public class MapPanel extends JPanel {
    private JButton[][] gridButtons;
    private int mapSize;

    public MapPanel() {
        mapSize = GameWorld.getInstance().getMap().getMapSize();
        setLayout(new GridLayout(mapSize, mapSize));
        setPreferredSize(new Dimension(64 * mapSize, 64 * mapSize));
        gridButtons = new JButton[mapSize][mapSize];
        for (int row = 0; row < mapSize; row++) {
            for (int col = 0; col < mapSize; col++) {
                JButton button = new JButton();
                button.setEnabled(false);
                button.setFocusable(false);
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

                    if (entity != null && entity.isVisible()) {
                        ImageIcon icon = loadImageIcon(getImagePath(entity.getDisplaySymbol()));
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
}
