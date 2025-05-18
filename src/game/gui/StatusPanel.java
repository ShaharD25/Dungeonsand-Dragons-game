package game.gui;
import game.characters.PlayerCharacter;
import javax.swing.*;
import java.awt.*;
import game.gui.GameObserver;
import game.engine.GameWorld;



public class StatusPanel extends JPanel {

    private final JLabel nameLabel = new JLabel("Name:");
    private final JLabel typeLabel = new JLabel("Type:");
    private final JLabel healthLabel = new JLabel("Health:");
    private final JLabel powerLabel = new JLabel("Power:");

    public StatusPanel() {
        setLayout(new GridLayout(4, 1));
        add(nameLabel);
        add(typeLabel);
        add(healthLabel);
        add(powerLabel);
        setPreferredSize(new Dimension(200, 100));
    }

    public void updateStatus(PlayerCharacter player) {
        nameLabel.setText("Name: " + player.getName());
        typeLabel.setText("Type: " + player.getClass().getSimpleName());
        healthLabel.setText("Health: " + player.getHealth());
        powerLabel.setText("Power: " + player.getPower());
    }

}
