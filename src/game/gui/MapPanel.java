package game.gui;
import game.characters.Enemy;
import game.engine.GameWorld;
import game.items.Potion;
import game.items.PowerPotion;
import game.items.Treasure;
import game.items.Wall;
import game.map.GameMap;
import game.map.Position;
import game.core.GameEntity;
import javax.swing.*;
import java.awt.*;
import java.util.List;

import static game.Main.calcDistance;

public class MapPanel extends JPanel{
    private JButton[][] gridButtons;
    private int mapSize;

    public MapPanel(){
        mapSize = GameWorld.getInstance().getMap().getMapSize();
        setLayout(new GridLayout(mapSize,mapSize));
        setPreferredSize(new Dimension(64*mapSize,64*mapSize));
        gridButtons = new JButton[mapSize][mapSize];
        for(int row = 0; row < mapSize; row++){
            for(int col = 0; col < mapSize; col++){
                JButton button = new JButton();
                button.setEnabled(false);
                button.setFocusable(false);
                gridButtons[row][col] = button;
                add(button);
            }
        }
        updateMap();
    }

    public String getImagePath(String type)
    {
        switch(type)
        {
            case "player":
                String playerType = GameWorld.getInstance().getPlayers().get(0).getClass().getSimpleName();
                return "C:\\Users\\Artiom\\IdeaProjects\\HW1Tamar\\src\\game\\images\\" + playerType + ".png";
            case "D":
                return "C:\\Users\\Artiom\\IdeaProjects\\HW1Tamar\\src\\game\\images\\Dragon.png";
            case "G":
                return "C:\\Users\\Artiom\\IdeaProjects\\HW1Tamar\\src\\game\\images\\Goblin.png";
            case "O":
                return "C:\\Users\\Artiom\\IdeaProjects\\HW1Tamar\\src\\game\\images\\Orc.png";
            case "T":
                return "C:\\Users\\Artiom\\IdeaProjects\\HW1Tamar\\src\\game\\images\\Treasure.png";
            case "W":
                return "C:\\Users\\Artiom\\IdeaProjects\\HW1Tamar\\src\\game\\images\\Wall.png";
            case "L":
                return "C:\\Users\\Artiom\\IdeaProjects\\HW1Tamar\\src\\game\\images\\Life_potion.png";
            case "P":
                return "C:\\Users\\Artiom\\IdeaProjects\\HW1Tamar\\src\\game\\images\\Power_potion.png";
        }
        return "";
    }

    public void updateMap(){
        GameMap map = GameWorld.getInstance().getMap();
        Position playerPos = GameWorld.getInstance().getPlayers().get(0).getPosition();

        for (int row = 0; row < mapSize; row++) {
            for (int col = 0; col < mapSize; col++) {
                Position pos = new Position(row,col);
                List <GameEntity> entities = map.getEntitiesAt(pos);

                JButton button = gridButtons[row][col];
                button.setIcon(null);
                button.setEnabled(true);

                if (pos.equals(playerPos)) {
                    ImageIcon icon = new ImageIcon(getImagePath("player"));
                    Image scaledImage = icon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                    ImageIcon scaledIcon = new ImageIcon(scaledImage);
                    button.setIcon(scaledIcon);
                    continue;
                }

                for (GameEntity entity : entities) {

                    if (entity != null) {
                        boolean isClose = calcDistance(playerPos, entity.getPosition()) <= 2;
                        if(isClose)
                        {
                            entity.setVisible(true);
                        }
                        else entity.setVisible(false);
                    }
                    if (entity != null && entity.isVisible()){
                        ImageIcon icon = new ImageIcon(getImagePath(entity.getDisplaySymbol()));
                        Image scaledImage = icon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                        ImageIcon scaledIcon = new ImageIcon(scaledImage);
                        button.setIcon(scaledIcon);
                        break;
                    }
                    else {
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
