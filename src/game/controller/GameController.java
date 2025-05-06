package game.controller;
import game.engine.GameWorld;
import game.characters.PlayerCharacter;
import game.gui.GameFrame;
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

        actionMap.put("moveUp", new MoveAction(-1, 0));
        actionMap.put("moveDown", new MoveAction(1, 0));
        actionMap.put("moveLeft", new MoveAction(0, -1));
        actionMap.put("moveRight", new MoveAction(0, 1));
    }

    private class MoveAction extends AbstractAction {
        private final int dRow, dCol;

        public MoveAction(int dRow, int dCol) {
            this.dRow = dRow;
            this.dCol = dCol;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Position newPos = new Position(player.getPosition().getRow() + dRow, player.getPosition().getCol() + dCol);
            boolean moved = Main.moveToPosition(world, player, newPos);
            if (moved) {
                Main.updateVisibility(world, player);
                GameFrame frame = world.getGameFrame();
                frame.getMapPanel().updateMap();
                frame.getStatusPanel().updateStatus(player);
            }
        }
    }
}
