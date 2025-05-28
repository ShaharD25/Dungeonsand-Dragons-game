package game.controller;
import game.engine.GameWorld;
import game.characters.PlayerCharacter;
import game.gui.GameFrame;
import game.items.PowerPotion;
import game.map.Position;
import game.Main;
import javax.swing.*;
import java.awt.event.ActionEvent;

public class GameController {
    private final GameWorld world;
    private final PlayerCharacter player;
    private final JPanel panel;

    public GameController(GameWorld world, PlayerCharacter player, JComponent component, JPanel panel) {
        this.world = world;
        this.player = player;
        this.panel = panel;
        setupKeyBindings(component);
    }

    private void setupKeyBindings(JComponent component) {
        InputMap inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = component.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("UP"), "moveUp");
        inputMap.put(KeyStroke.getKeyStroke("DOWN"), "moveDown");
        inputMap.put(KeyStroke.getKeyStroke("LEFT"), "moveLeft");
        inputMap.put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");
        inputMap.put(KeyStroke.getKeyStroke("p"), "powerPotion");
        inputMap.put(KeyStroke.getKeyStroke("P"), "powerPotion");
        inputMap.put(KeyStroke.getKeyStroke("l"), "lifePotion");
        inputMap.put(KeyStroke.getKeyStroke("L"), "lifePotion");

        actionMap.put("moveUp", new MoveAction(-1, 0));
        actionMap.put("moveDown", new MoveAction(1, 0));
        actionMap.put("moveLeft", new MoveAction(0, -1));
        actionMap.put("moveRight", new MoveAction(0, 1));
        actionMap.put("lifePotion", new usePotion("life"));
        actionMap.put("powerPotion", new usePotion("power"));
        ;
    }

    private class usePotion extends AbstractAction {
        private final String type;

        public usePotion(String type) {
            this.type = type;
        }

        //try run with and without
        @Override
        public void actionPerformed(ActionEvent e) {
            boolean used;
            if(type.equals("power")) {
                used = player.usePowerPotion();
            }
            else
                used = player.usePotion();

            if(used)
            {
                GameFrame frame = world.getGameFrame();
                frame.getStatusPanel().updateStatus(player);
            }
        }
    }

    private class MoveAction extends AbstractAction {
        private final int dRow, dCol;

        public MoveAction(int dRow, int dCol) {
            this.dRow = dRow;
            this.dCol = dCol;
        }

        //try run with and without
        @Override
        public void actionPerformed(ActionEvent e) {
            Position newPos = new Position(player.getPosition().getRow() + dRow, player.getPosition().getCol() + dCol);
            boolean moved = player.moveToPosition(newPos);
            if (moved) {
                Main.updateVisibility(world, player);
                GameFrame frame = world.getGameFrame();
                frame.getMapPanel().updateMap();
                frame.getStatusPanel().updateStatus(player);
            }
        }
    }



}
