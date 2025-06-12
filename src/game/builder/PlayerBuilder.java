package game.builder;
import game.characters.*;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Random;

public class PlayerBuilder {
    private String name;
    private int health = 100;
    private int power ;
    private int defence ;
    private Class<? extends PlayerCharacter> type;
    private double accuracy;
    private String magicElement;

    public PlayerBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public PlayerBuilder setPower(int power) {
        this.power = power;
        return this;
    }

    public PlayerBuilder setType(Class<? extends PlayerCharacter> type) {
        this.type = type;
        return this;
    }

    private PlayerBuilder setHealth(int health) {
        this.health = health;
        return this;
    }

    private void setDefence(int defence) {
        this.defence = defence;
    }

    private void setAccuracy(double adjustedAccuracy) {
        this.accuracy = adjustedAccuracy;
    }

    private void setMagicElement(String selectedItem) {
        this.magicElement = selectedItem;
    }


    public PlayerCharacter build() {
        try {
            if (name == null || type == null )
                throw new IllegalStateException("Missing parameters in PlayerBuilder");

            PlayerCharacter player = type.getDeclaredConstructor(String.class).newInstance(name);
            player.setHealth(health); // תמיד 100
            player.setPower(power);
            return player;

        } catch (Exception e) {
            throw new RuntimeException("Failed to build player", e);
        }
    }


    public static PlayerCharacter showDialog() {
        JPanel panel = new JPanel(new GridLayout(0, 2));

        Random rand = new Random();

        // Default values
        int defaultPower = rand.nextInt(4, 15);
        int defaultHealth = 100;
        int defaultDefence = rand.nextInt(121);
        double defaultAccuracy = Math.random() * 0.8;

        int totalPoints = defaultPower + defaultHealth;

        JTextField nameField = new JTextField("Player");

        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Warrior", "Mage", "Archer"});

        JSpinner powerSpinner = new JSpinner(new SpinnerNumberModel(defaultPower, defaultPower-2, defaultPower+3, 1));
        JSpinner healthSpinner = new JSpinner(new SpinnerNumberModel(defaultHealth, defaultHealth-2, defaultHealth+3, 1));

        JSpinner defenceSpinner = new JSpinner(new SpinnerNumberModel(defaultDefence, defaultDefence-2, defaultDefence+3, 1));
        defenceSpinner.setVisible(true);

        JComboBox<String> elementCombo = new JComboBox<>(new String[]{"FIRE", "ICE", "LIGHTNING", "ACID"});
        elementCombo.setVisible(false);

        Double[] accuracyOptions = {-0.2, -0.1, 0.0, 0.1, 0.2, 0.3};
        JComboBox<Double> accuracyCombo = new JComboBox<>(accuracyOptions);
        accuracyCombo.setSelectedItem(0.0);
        accuracyCombo.setVisible(false);

        // UI Layout
        panel.add(new JLabel("Name:"));
        panel.add(nameField);

        panel.add(new JLabel("Class:"));
        panel.add(typeCombo);

        panel.add(new JLabel("Health:"));
        panel.add(healthSpinner);

        panel.add(new JLabel("Power:"));
        panel.add(powerSpinner);

        JLabel defLab = new JLabel("Defence:");
        panel.add(defLab);
        panel.add(defenceSpinner);

        JLabel magicLab = new JLabel("Magic Type:");
        panel.add(magicLab);
        panel.add(elementCombo);
        magicLab.setVisible(false);

        JLabel accLab = new JLabel("Accuracy Change:");
        panel.add(accLab);
        panel.add(accuracyCombo);
        accLab.setVisible(false);

        panel.add(new JLabel()); // filler

        /* Listeners to update visibility */
        typeCombo.addActionListener(e -> {
            String type = (String) typeCombo.getSelectedItem();
            if (type.equals("Warrior")) {
                defenceSpinner.setVisible(true);
                defLab.setVisible(true);
                elementCombo.setVisible(false);
                magicLab.setVisible(false);
                accuracyCombo.setVisible(false);
                accLab.setVisible(false);
            } else if (type.equals("Mage")) {
                defenceSpinner.setVisible(false);
                defLab.setVisible(false);
                elementCombo.setVisible(true);
                magicLab.setVisible(true);
                accuracyCombo.setVisible(false);
                accLab.setVisible(false);
            } else if (type.equals("Archer")) {
                defenceSpinner.setVisible(false);
                defLab.setVisible(false);
                elementCombo.setVisible(false);
                magicLab.setVisible(false);
                accuracyCombo.setVisible(true);
                accLab.setVisible(true);
            }
            panel.revalidate();
            panel.repaint();
        });

        // Show dialog
        while (true) {
            int result = JOptionPane.showConfirmDialog(null, panel, "Create Your Player", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) return null;

            String name = nameField.getText();
            String typeStr = (String) typeCombo.getSelectedItem();
            int power = (int) powerSpinner.getValue();
            int health = (int) healthSpinner.getValue();

            int sumUsed = power + health;
            int defaultTotal = totalPoints;
            if ("Archer".equals(typeStr)) {
                double accChange = (Double) accuracyCombo.getSelectedItem();
                int accCost = (int) Math.round(accChange * 10);  // +10% costs 1 pt, etc.
                sumUsed += accCost;
            }
            if ("Warrior".equals(typeStr)) {
                int defChange = (int) defenceSpinner.getValue();
                //int defCost = Math.abs(defaultDefence - defChange);
                defaultTotal += defaultDefence;
                sumUsed += defChange;
            }

            // Compare to total
            if (sumUsed != defaultTotal) {
                JOptionPane.showMessageDialog(null,
                        "The sum of attributes must equal " + defaultTotal + ". You used " + sumUsed + ".",
                        "Invalid Attributes", JOptionPane.ERROR_MESSAGE);
                continue;  // re-show the dialog
            }

            // Build the correct class
            Class<? extends PlayerCharacter> typeClass = switch (typeStr) {
                case "Mage" -> Mage.class;
                case "Archer" -> Archer.class;
                default -> Warrior.class;
            };

            PlayerBuilder builder = new PlayerBuilder()
                    .setName(name)
                    .setType(typeClass)
                    .setPower(power)
                    .setHealth(health);

            if ("Warrior".equals(typeStr)) {
                builder.setDefence((int) defenceSpinner.getValue());
            } else if ("Mage".equals(typeStr)) {
                builder.setMagicElement((String) elementCombo.getSelectedItem());
            } else if ("Archer".equals(typeStr)) {
                double accChange = (Double) accuracyCombo.getSelectedItem();
                double adjustedAccuracy = defaultAccuracy * (1.0 + accChange);
                builder.setAccuracy(adjustedAccuracy);
            }

            return builder.build();
        }
    }




}
