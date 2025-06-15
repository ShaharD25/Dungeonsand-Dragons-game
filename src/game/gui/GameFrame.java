package game.gui;

import javax.swing.*;
import java.awt.*;

import game.characters.PlayerCharacter;
import game.decorators.BoostedAttackDecorator;
import game.decorators.ShieldedPlayerDecorator;
import game.decorators.RegenerationDecorator;
import game.engine.GameWorld;
import game.memento.GameMemento;

public class GameFrame extends JFrame {
    private final MapPanel mapPanel;
    private final StatusPanel statusPanel;

    public GameFrame(PlayerCharacter player) {
        this.mapPanel = new MapPanel();
        this.statusPanel = new StatusPanel();

        setTitle("Fantasy Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(true);
        statusPanel.updateStatus(player);

        add(mapPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.EAST);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        // Save button
        JButton saveButton = new JButton("Save Game");
        saveButton.addActionListener(e -> {
            GameMemento memento = GameWorld.getInstance().saveState();
            GameWorld.getInstance().getCaretaker().save(memento);
            JOptionPane.showMessageDialog(this, "Game state saved!");
        });
        buttonPanel.add(saveButton);

        // Load button
        JButton loadButton = new JButton("Load Game");
        loadButton.addActionListener(e -> {
            GameMemento memento = GameWorld.getInstance().getCaretaker().restore();
            if (memento != null) {
                GameWorld.getInstance().restoreFromMemento(memento);
                mapPanel.updateMap();
                JOptionPane.showMessageDialog(this, "Game state loaded!");
            } else {
                JOptionPane.showMessageDialog(this, "No saved state found.");
            }
        });
        buttonPanel.add(loadButton);

        // ==== BOOST BUTTON ====
        JButton boostButton = new JButton("Boost Attack (3 min cooldown)");
        JButton boostStatus = new JButton("Boost Inactive");
        boostStatus.setEnabled(false);
        boostStatus.setBackground(Color.LIGHT_GRAY);
        final Timer[] boostCooldown = new Timer[1];

        boostButton.addActionListener(e -> {
            boostButton.setEnabled(false);
            PlayerCharacter current = GameWorld.getInstance().getPlayers().get(0);
            BoostedAttackDecorator boosted = new BoostedAttackDecorator(current, 10);
            GameWorld.getInstance().getPlayers().set(0, boosted);

            mapPanel.updateMap();
            statusPanel.updateStatus(boosted);
            boostStatus.setBackground(Color.GREEN);
            boostStatus.setText("Boost Active");

            Timer endBoost = new Timer(15_000, ev -> {
                GameWorld.getInstance().getPlayers().set(0, current);
                mapPanel.updateMap();
                statusPanel.updateStatus(current);
                boostStatus.setBackground(Color.LIGHT_GRAY);
                boostStatus.setText("Boost Ended");

                boostCooldown[0] = new Timer(180_000, ev2 -> {
                    boostButton.setEnabled(true);
                    boostStatus.setText("Boost Ready");
                });
                boostCooldown[0].setRepeats(false);
                boostCooldown[0].start();
            });
            endBoost.setRepeats(false);
            endBoost.start();
        });

        buttonPanel.add(boostButton);
        buttonPanel.add(boostStatus);

        // ==== SHIELD BUTTON ====
        JButton shieldButton = new JButton("Activate Shield (3 min cooldown)");
        JButton shieldStatus = new JButton("Shield Inactive");
        shieldStatus.setEnabled(false);
        shieldStatus.setBackground(Color.LIGHT_GRAY);
        final Timer[] shieldCooldown = new Timer[1];

        shieldButton.addActionListener(e -> {
            shieldButton.setEnabled(false);
            PlayerCharacter current = GameWorld.getInstance().getPlayers().get(0);
            ShieldedPlayerDecorator shielded = new ShieldedPlayerDecorator(current);
            GameWorld.getInstance().getPlayers().set(0, shielded);

            mapPanel.updateMap();
            statusPanel.updateStatus(shielded);
            shieldStatus.setBackground(Color.CYAN);
            shieldStatus.setText("Shield Active");

            Timer endShield = new Timer(15_000, ev -> {
                GameWorld.getInstance().getPlayers().set(0, current);
                mapPanel.updateMap();
                statusPanel.updateStatus(current);
                shieldStatus.setBackground(Color.LIGHT_GRAY);
                shieldStatus.setText("Shield Ended");

                shieldCooldown[0] = new Timer(180_000, ev2 -> {
                    shieldButton.setEnabled(true);
                    shieldStatus.setText("Shield Ready");
                });
                shieldCooldown[0].setRepeats(false);
                shieldCooldown[0].start();
            });
            endShield.setRepeats(false);
            endShield.start();
        });

        buttonPanel.add(shieldButton);
        buttonPanel.add(shieldStatus);

        // ==== REGENERATION BUTTON ====
        JButton regenButton = new JButton("Activate Regeneration");
        JButton regenStatus = new JButton("Regen Inactive");
        regenStatus.setEnabled(false);
        regenStatus.setBackground(Color.LIGHT_GRAY);

        regenButton.addActionListener(e -> {
            PlayerCharacter current = GameWorld.getInstance().getPlayers().get(0);

            if (current instanceof RegenerationDecorator) {
                JOptionPane.showMessageDialog(this, "Regeneration is already active!");
                return;
            }

            RegenerationDecorator regen = new RegenerationDecorator(current);
            GameWorld.getInstance().getPlayers().set(0, regen);
            statusPanel.updateStatus(regen);
            mapPanel.updateMap();
            regenStatus.setBackground(Color.PINK);
            regenStatus.setText("Regen Active");

            JOptionPane.showMessageDialog(this, "Regeneration activated. Heals 2 HP every 10 seconds.");
        });

        buttonPanel.add(regenButton);
        buttonPanel.add(regenStatus);

        add(buttonPanel, BorderLayout.NORTH);
        GameWorld.getInstance().setGameFrame(this);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public MapPanel getMapPanel() {
        return mapPanel;
    }

    public StatusPanel getStatusPanel() {
        return statusPanel;
    }
}
