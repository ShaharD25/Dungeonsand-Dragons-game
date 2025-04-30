package game.gui;
import game.engine.GameWorld;
import game.map.GameMap;
import game.map.Position;
import game.core.GameEntity;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MapPanel extends JPanel{
    private final JButton[][] gridButtons = new JButton[10][10];

    public MapPanel(){
        setLayout(new GridLayout(10,10));
        setPreferredSize(new Dimension(800,700));

        for(int row = 0; row < 10; row++){
            for(int col = 0; col < 10; col++){
                JButton button = new JButton();
                button.setEnabled(false);
                button.setFocusable(false);
                gridButtons[row][col] = button;
                add(button);
            }
        }
    }

    public void updateMap(){
        GameMap map = GameWorld.getInstance().getMap();
        Position playerPos = GameWorld.getInstance().getPlayers().get(0).getPosition();

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                Position pos = new Position(row,col);
                List <GameEntity> entities = map.getEntitiesAt(pos);
                JButton button = gridButtons[row][col];
                button.setIcon(null);

                if (pos.equals(playerPos)) {
                    button.setText("P");
                    continue;
                }

                for (GameEntity entity : entities) {
                    if (entity != null && entity.isVisible()){
                        button.setText(entity.getDisplaySymbol());
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
