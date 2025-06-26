package game.gui;
import game.characters.Enemy;
import game.characters.PlayerCharacter;
import game.core.GameEntity;
import game.decorators.EnemyDecorator;
import game.engine.GameWorld;
import game.items.Potion;
import game.items.PowerPotion;
import game.items.Treasure;
import game.items.Wall;
import game.map.Position;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class PopupPanel {
    static Integer enemyHealth = null;
    public static void showPopup(String title, String message) {
        JTextArea textArea = new JTextArea(message);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);

        textArea.setEditable(false);
        textArea.setFocusable(false);
        textArea.setBackground(UIManager.getColor("Label.background"));
        textArea.setFont(UIManager.getFont("Label.font"));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(100, 100));
        JOptionPane.showMessageDialog(null, scrollPane, title, JOptionPane.INFORMATION_MESSAGE);

    }

    public static void quickPopup( GameWorld world, Position pos)
    {
        List<GameEntity> entities = world.getMap().getEntitiesAt(pos);
        String message = "Nothing here.";
        enemyHealth = null;
        Integer heroHealth = null;
        for (GameEntity entity : entities) {
            if (entity == null) continue;

            if (entity instanceof Enemy enemy) {
            		// Get the base enemy type for display
                String enemyType = enemy.getClass().getSimpleName();
                if (enemy instanceof EnemyDecorator decorator) {
                    // Get the wrapped enemy's type
                    Enemy base = decorator.getWrapped();
                    while (base instanceof EnemyDecorator) {
                        base = ((EnemyDecorator) base).getWrapped();
                    }
                    enemyType = decorator.getClass().getSimpleName().replace("Decorator", "") + 
                               " " + base.getClass().getSimpleName();
                }
                enemyHealth = enemy.getHealth();
                message = "You encountered a " + enemy.getClass().getSimpleName()
                        + "\nEnemy HP: " + enemyHealth + "/50";
                break; // stop at first enemy
            } else if (entity instanceof Potion potion) {
                message = (potion instanceof PowerPotion) ?
                        "You found a power potion!" : "You found a Life potion!";
            } else if (entity instanceof Treasure) {
                message = "You found a treasure!";
            } else if (entity instanceof Wall) {
                message = "It is a wall";
            }
            else if(entity instanceof PlayerCharacter)
            {
                message = "It is I!! the Hero!";
                heroHealth = ((PlayerCharacter) entity).getHealth();
            }

        }

        final String popupText = message;
        final Integer health = enemyHealth == null ? heroHealth : enemyHealth;

        SwingUtilities.invokeLater(() -> {
            JPopupMenu popupMenu = new JPopupMenu();

            // Custom panel for content
            JPanel content = new JPanel();
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setBackground(new Color(40, 40, 40));
            content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Text area
            JTextArea textArea = new JTextArea(popupText);
            textArea.setEditable(false);
            textArea.setOpaque(false);
            textArea.setForeground(Color.WHITE);
            textArea.setFont(new Font("Arial", Font.PLAIN, 14));
            content.add(textArea);

            // Only add health bar if the entity is an enemy
            if (health != null) {
                JProgressBar healthBar = enemyHealth == null ? new JProgressBar(0, 100) : new JProgressBar(0, 50);
                healthBar.setValue(health);
                healthBar.setStringPainted(false);
                healthBar.setPreferredSize(new Dimension(200, 15));
                healthBar.setBorderPainted(false);

                float ratio = enemyHealth == null ? health / 100f: health / 50f;
                if (ratio <= 0.3f)
                    healthBar.setForeground(Color.RED);
                else if (ratio <= 0.7f)
                    healthBar.setForeground(new Color(176, 121, 54)); // brown/orange
                else
                    healthBar.setForeground(new Color(63, 119, 76)); // green

                content.add(Box.createVerticalStrut(5));
                content.add(healthBar);
            }

            popupMenu.setLayout(new BorderLayout());
            popupMenu.add(content, BorderLayout.CENTER);

            // Auto-hide on mouse exit
            Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
                if (!(event instanceof MouseEvent)) return;
                MouseEvent me = (MouseEvent) event;
                if (popupMenu.isVisible()) {
                    Point screenPoint = me.getLocationOnScreen();
                    SwingUtilities.convertPointFromScreen(screenPoint, popupMenu);
                    if (!popupMenu.contains(screenPoint)) {
                        popupMenu.setVisible(false);
                    }
                }
            }, AWTEvent.MOUSE_MOTION_EVENT_MASK);

            // Show popup at mouse position
            PointerInfo pointerInfo = MouseInfo.getPointerInfo();
            Point mouseLocation = pointerInfo.getLocation();

            Component focusComponent = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            if (focusComponent != null) {
                SwingUtilities.convertPointFromScreen(mouseLocation, focusComponent);
                popupMenu.show(focusComponent, mouseLocation.x, mouseLocation.y);
            }
        });

    }
}
