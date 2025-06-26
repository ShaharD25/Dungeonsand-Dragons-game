package game.gui;

import javax.swing.*;
import java.awt.*;
import game.characters.PlayerCharacter;
import game.decorators.BoostedAttackDecorator;
import game.decorators.ShieldedPlayerDecorator;
import game.decorators.RegenerationDecorator;
import game.decorators.PlayerDecorator;
import game.engine.GameWorld;
import game.memento.GameMemento;
import game.map.Position;

public class GameFrame extends JFrame {
    private final MapPanel mapPanel;
    private final StatusPanel statusPanel;
    
    // Track decorator states
    private Timer boostTimer = null;
    private Timer boostCooldownTimer = null;
    private Timer shieldTimer = null;
    private Timer shieldCooldownTimer = null;
    private boolean boostActive = false;
    private boolean shieldActive = false;
    
    // UI Components
    private JButton boostButton;
    private JButton boostStatus;
    private JButton shieldButton;
    private JButton shieldStatus;

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
            // Save button states
            memento.setBoostActive(boostActive);
            memento.setShieldActive(shieldActive);
            memento.setBoostButtonEnabled(boostButton.isEnabled());
            memento.setShieldButtonEnabled(shieldButton.isEnabled());
            
            GameWorld.getInstance().getCaretaker().save(memento);
            JOptionPane.showMessageDialog(this, "Game state saved!");
        });
        buttonPanel.add(saveButton);

        // Load button
        JButton loadButton = new JButton("Load Game");
        loadButton.addActionListener(e -> {
            GameMemento memento = GameWorld.getInstance().getCaretaker().restore();
            if (memento != null) {
                // Cancel all active timers before loading
                cancelAllTimers();
                
                GameWorld.getInstance().restoreFromMemento(memento);
                mapPanel.updateMap();
                
                // Restore button states
                restoreButtonStates(memento);
                
                JOptionPane.showMessageDialog(this, "Game state loaded!");
            } else {
                JOptionPane.showMessageDialog(this, "No saved state found.");
            }
        });
        buttonPanel.add(loadButton);

        // ==== BOOST BUTTON ====
        boostButton = new JButton("Boost Attack (3 min cooldown)");
        boostStatus = new JButton("Boost Inactive");
        boostStatus.setEnabled(false);
        boostStatus.setBackground(Color.LIGHT_GRAY);

        boostButton.addActionListener(e -> {
            boostButton.setEnabled(false);
            boostActive = true;
            
            PlayerCharacter current = GameWorld.getInstance().getPlayers().get(0);
            // Get the base player without decorators but preserve current position
            Position currentPos = current.getPosition();
            PlayerCharacter basePlayer = getBasePlayer(current);
            basePlayer.setPosition(currentPos);
            
            BoostedAttackDecorator boosted = new BoostedAttackDecorator(basePlayer, 10);
            GameWorld.getInstance().getPlayers().set(0, boosted);

            mapPanel.updateMap();
            statusPanel.updateStatus(boosted);
            boostStatus.setBackground(Color.GREEN);
            boostStatus.setText("Boost Active");

            boostTimer = new Timer(15_000, ev -> {
                boostActive = false;
                // Get current position before removing decorator
                Position pos = GameWorld.getInstance().getPlayers().get(0).getPosition();
                PlayerCharacter unwrapped = removeBoostDecorator(GameWorld.getInstance().getPlayers().get(0));
                unwrapped.setPosition(pos);
                GameWorld.getInstance().getPlayers().set(0, unwrapped);
                
                mapPanel.updateMap();
                statusPanel.updateStatus(unwrapped);
                boostStatus.setBackground(Color.LIGHT_GRAY);
                boostStatus.setText("Boost Ended");

                boostCooldownTimer = new Timer(180_000, ev2 -> {
                    boostButton.setEnabled(true);
                    boostStatus.setText("Boost Ready");
                });
                boostCooldownTimer.setRepeats(false);
                boostCooldownTimer.start();
            });
            boostTimer.setRepeats(false);
            boostTimer.start();
        });

        buttonPanel.add(boostButton);
        buttonPanel.add(boostStatus);

        // ==== SHIELD BUTTON ====
        shieldButton = new JButton("Activate Shield (3 min cooldown)");
        shieldStatus = new JButton("Shield Inactive");
        shieldStatus.setEnabled(false);
        shieldStatus.setBackground(Color.LIGHT_GRAY);

        shieldButton.addActionListener(e -> {
            shieldButton.setEnabled(false);
            shieldActive = true;
            
            PlayerCharacter current = GameWorld.getInstance().getPlayers().get(0);
            // Get the base player without decorators but preserve current position
            Position currentPos = current.getPosition();
            PlayerCharacter basePlayer = getBasePlayer(current);
            basePlayer.setPosition(currentPos);
            
            ShieldedPlayerDecorator shielded = new ShieldedPlayerDecorator(basePlayer);
            GameWorld.getInstance().getPlayers().set(0, shielded);

            mapPanel.updateMap();
            statusPanel.updateStatus(shielded);
            shieldStatus.setBackground(Color.CYAN);
            shieldStatus.setText("Shield Active");

            shieldTimer = new Timer(15_000, ev -> {
                shieldActive = false;
                // Get current position before removing decorator
                Position pos = GameWorld.getInstance().getPlayers().get(0).getPosition();
                PlayerCharacter unwrapped = removeShieldDecorator(GameWorld.getInstance().getPlayers().get(0));
                unwrapped.setPosition(pos);
                GameWorld.getInstance().getPlayers().set(0, unwrapped);
                
                mapPanel.updateMap();
                statusPanel.updateStatus(unwrapped);
                shieldStatus.setBackground(Color.LIGHT_GRAY);
                shieldStatus.setText("Shield Ended");

                shieldCooldownTimer = new Timer(180_000, ev2 -> {
                    shieldButton.setEnabled(true);
                    shieldStatus.setText("Shield Ready");
                });
                shieldCooldownTimer.setRepeats(false);
                shieldCooldownTimer.start();
            });
            shieldTimer.setRepeats(false);
            shieldTimer.start();
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
            
            if (hasRegenerationDecorator(current)) {
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
    
    // Helper method to get base player without decorators
    private PlayerCharacter getBasePlayer(PlayerCharacter player) {
        while (player instanceof PlayerDecorator) {
            player = ((PlayerDecorator) player).getWrapped();
        }
        return player;
    }
    
    // Helper method to remove boost decorator while preserving others
    private PlayerCharacter removeBoostDecorator(PlayerCharacter player) {
        if (!(player instanceof PlayerDecorator)) {
            return player;
        }
        
        if (player instanceof BoostedAttackDecorator) {
            return ((BoostedAttackDecorator) player).getWrapped();
        }
        
        // Recursively check wrapped players
        PlayerDecorator decorator = (PlayerDecorator) player;
        PlayerCharacter wrapped = removeBoostDecorator(decorator.getWrapped());
        decorator.setWrapped(wrapped);
        return player;
    }
    
    // Helper method to remove shield decorator while preserving others
    private PlayerCharacter removeShieldDecorator(PlayerCharacter player) {
        if (!(player instanceof PlayerDecorator)) {
            return player;
        }
        
        if (player instanceof ShieldedPlayerDecorator) {
            return ((ShieldedPlayerDecorator) player).getWrapped();
        }
        
        // Recursively check wrapped players
        PlayerDecorator decorator = (PlayerDecorator) player;
        PlayerCharacter wrapped = removeShieldDecorator(decorator.getWrapped());
        decorator.setWrapped(wrapped);
        return player;
    }
    
    // Check if player has regeneration decorator
    private boolean hasRegenerationDecorator(PlayerCharacter player) {
        while (player instanceof PlayerDecorator) {
            if (player instanceof RegenerationDecorator) {
                return true;
            }
            player = ((PlayerDecorator) player).getWrapped();
        }
        return false;
    }
    
    // Cancel all active timers
    private void cancelAllTimers() {
        if (boostTimer != null && boostTimer.isRunning()) {
            boostTimer.stop();
        }
        if (boostCooldownTimer != null && boostCooldownTimer.isRunning()) {
            boostCooldownTimer.stop();
        }
        if (shieldTimer != null && shieldTimer.isRunning()) {
            shieldTimer.stop();
        }
        if (shieldCooldownTimer != null && shieldCooldownTimer.isRunning()) {
            shieldCooldownTimer.stop();
        }
    }
    
    // Restore button states from memento
    private void restoreButtonStates(GameMemento memento) {
        boostActive = memento.isBoostActive();
        shieldActive = memento.isShieldActive();
        
        boostButton.setEnabled(memento.isBoostButtonEnabled());
        shieldButton.setEnabled(memento.isShieldButtonEnabled());
        
        // Update status displays
        if (boostActive) {
            boostStatus.setBackground(Color.GREEN);
            boostStatus.setText("Boost Active");
        } else if (!memento.isBoostButtonEnabled()) {
            boostStatus.setBackground(Color.LIGHT_GRAY);
            boostStatus.setText("Boost Cooldown");
        } else {
            boostStatus.setBackground(Color.LIGHT_GRAY);
            boostStatus.setText("Boost Ready");
        }
        
        if (shieldActive) {
            shieldStatus.setBackground(Color.CYAN);
            shieldStatus.setText("Shield Active");
        } else if (!memento.isShieldButtonEnabled()) {
            shieldStatus.setBackground(Color.LIGHT_GRAY);
            shieldStatus.setText("Shield Cooldown");
        } else {
            shieldStatus.setBackground(Color.LIGHT_GRAY);
            shieldStatus.setText("Shield Ready");
        }
    }

    public MapPanel getMapPanel() {
        return mapPanel;
    }

    public StatusPanel getStatusPanel() {
        return statusPanel;
    }
}