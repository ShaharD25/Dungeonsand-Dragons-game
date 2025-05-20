package game.gui;

import game.characters.PlayerCharacter;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

import static game.gui.MapPanel.getImagePath;

public class StatusPanel extends JPanel {

    // Status labels
    private final JLabel nameLabel = new JLabel("Name:");
    private final JLabel typeLabel = new JLabel("Type:");
    private final JLabel healthLabel = new JLabel("Health:");
    private final JLabel powerLabel = new JLabel("Power:");

    // Top row item icons + labels
    private final JLabel healthPotionLabel = new JLabel();
    private final JLabel powerPotionLabel = new JLabel();
    private final JLabel treasureLabel = new JLabel();

    public StatusPanel() {
        setLayout(new BorderLayout());

        // --- Top Item Panel ---
        JPanel itemPanel = new JPanel(new GridLayout(3, 1));

        // 1. Health Potion row
        JPanel healthRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        healthRow.add(new JLabel("Life Potion:       "));
        ImageIcon LPotionIcon = loadImageIcon(getImagePath("L"));
        healthRow.add(new JLabel(LPotionIcon));
        healthRow.add(healthPotionLabel);
        itemPanel.add(healthRow);

        // 2. Power Potion row
        JPanel powerRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        powerRow.add(new JLabel("Power Potion:      "));
        ImageIcon PPotionIcon = loadImageIcon(getImagePath("P"));
        //if (playerIcon != null) button.setIcon(playerIcon);
        powerRow.add(new JLabel(PPotionIcon));
        powerRow.add(powerPotionLabel);
        itemPanel.add(powerRow);

        // 3. Coins row
        JPanel treasureRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        treasureRow.add(new JLabel("Treasure Points: "));
        ImageIcon treasureIcon = loadImageIcon(getImagePath("T"));
        treasureRow.add(new JLabel(treasureIcon));
        treasureRow.add(treasureLabel);
        itemPanel.add(treasureRow);

        // --- Main status panel below ---
        JPanel statusInfoPanel = new JPanel(new GridLayout(4, 1));
        statusInfoPanel.add(nameLabel);
        statusInfoPanel.add(typeLabel);
        statusInfoPanel.add(healthLabel);
        statusInfoPanel.add(powerLabel);

        add(itemPanel, BorderLayout.NORTH);
        add(statusInfoPanel, BorderLayout.CENTER);
        setPreferredSize(new Dimension(200, 160));
    }

    private ImageIcon loadImageIcon(String path) {
        URL url = getClass().getResource(path);
        if (url != null) {
            ImageIcon icon = new ImageIcon(url);
            Image scaledImage = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } else {
            System.err.println("Image not found: " + path);
            return null;
        }
    }

    public void updateStatus(PlayerCharacter player) {
        nameLabel.setText("Name: " + player.getName());
        typeLabel.setText("Type: " + player.getClass().getSimpleName());
        healthLabel.setText("Health: " + player.getHealth());
        powerLabel.setText("Power: " + player.getPower());

        // Assuming the player has methods for these:

        healthPotionLabel.setText(String.valueOf(player.getLifePotionCount()));
        powerPotionLabel.setText(String.valueOf(player.getPowerPotionCount()));
        treasureLabel.setText(String.valueOf(player.getTreasurePoints()));
    }
}
