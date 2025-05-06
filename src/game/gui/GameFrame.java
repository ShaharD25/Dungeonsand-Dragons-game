package game.gui;
import javax.swing.*;
import java.awt.*;
import game.characters.PlayerCharacter;
import game.engine.GameWorld;

public class GameFrame extends JFrame {
    private final MapPanel mapPanel;
    private final StatusPanel statusPanel;
    public GameFrame(PlayerCharacter player) {
        this.mapPanel = new MapPanel();
        this.statusPanel = new StatusPanel();

        setTitle("Fantasy Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(true);//changing the window size
        statusPanel.updateStatus(player);

        add(mapPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.EAST);
        GameWorld.getInstance().setGameFrame(this);//עדכון של GAME WORLD

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public MapPanel getMapPanel() {return mapPanel;}

    public StatusPanel getStatusPanel() {return statusPanel;}

}
