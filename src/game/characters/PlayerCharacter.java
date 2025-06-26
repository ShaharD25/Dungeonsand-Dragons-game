//Artiom Bondar:332692730
//Shahar Dahan: 207336355
package game.characters;
import game.Main;
import game.audio.SoundPlayer;
import game.combat.CombatSystem;
import game.core.GameEntity;
import game.core.Inventory;
import game.engine.EnemyPool;
import game.engine.GameWorld;
import game.gui.GameEventSource;
import game.gui.GameFrame;
import game.gui.GameObserver;
import game.gui.PopupPanel;
import game.items.*;
import game.map.GameMap;
import game.map.Position;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import static game.Main.restartGame;
import static game.engine.GameWorld.BOARD_LOCK;

/**
 * Abstract class representing a player-controlled character.
 * It extends AbstractCharacter and adds name, inventory, and treasure point tracking.
 */
public abstract class PlayerCharacter extends AbstractCharacter implements GameEventSource {
    private String name;
    private Inventory inventory;
    private int treasurePoints;
    private final List<GameObserver> observers = new CopyOnWriteArrayList<>();
    /**
     * Constructs a new PlayerCharacter with a given name and random starting position on the board.
     */
    public PlayerCharacter(String name) {
        super(new Position(new Random().nextInt(10),new Random().nextInt(10)));
        this.name = name;
        this.inventory = new Inventory();
        this.treasurePoints = 0;
        this.setVisible(true);
    }


    	public String getClassSimpleName()
    	{
    		return getClass().getSimpleName();
    	}


    public boolean moveToPosition(Position newPos) {
        GameWorld world = GameWorld.getInstance();// Singleton instance of the game world
        GameMap map = world.getMap();
        int size = map.getMapSize();

        if (newPos.getRow() < 0 || newPos.getRow() >= size || newPos.getCol() < 0 || newPos.getCol() >= size) {
            PopupPanel.showPopup("Warning", "Cannot move outside the board!");
            return false;
        }

        List<GameEntity> entitiesAtNewPos = map.getEntitiesAt(newPos);
        boolean hasWall = entitiesAtNewPos.stream().anyMatch(e -> e instanceof Wall);

        if (hasWall) {
            SoundPlayer.playSound("hit.wav");
            PopupPanel.showPopup("Warning", "Blocked by wall!");
            return false;
        }
        
        boolean isemptyNext = entitiesAtNewPos.isEmpty();
        
        // Remove player from the current cell
        map.removeEntity(getPosition(), this);

        // Set new position
        setPosition(newPos);

        // Add player to the new cell
        map.addEntity(newPos, this);


        if (!isemptyNext) {
            handleInteractions(newPos);
        }


        world.getGameFrame().getMapPanel().updateMap();
        world.getGameFrame().getStatusPanel().updateStatus(this);
        notifyObservers();

        return true;

    }



    /**
     * Handles all interactions between the player and entities located in the given position.
     * Interactions can be with Enemies, Potions, or Treasures.
     * If the player dies during combat, further interactions are skipped.
     */
    public void handleInteractions(Position pos) {
        GameWorld world = GameWorld.getInstance();// Singleton instance of the game world
        if(BOARD_LOCK.tryLock())
        {
            try{
                List<GameEntity> entities = new ArrayList<>(world.getMap().getEntitiesAt(pos));

                // For debugging: print all entities at this position
                System.out.println("Entities at your current cell:");
                for (GameEntity e : entities) {
                    if (e != null)
                        System.out.println(" - " + e.getClass().getSimpleName());
                }

                for (GameEntity entity : entities) {
                    if (entity == null || entity == this ) continue;

                    // --- Enemy Interaction ---
                    if (entity instanceof Enemy enemy) {
                        GameFrame frame = GameWorld.getInstance().getGameFrame();
                        frame.getMapPanel().flashCell(pos, Color.RED);

                        PopupPanel.showPopup("Enemy Encountered", "You encountered a" + enemy.getClass().getSimpleName()
                                + "\nEnemy HP: " + enemy.getHealth() + "/50" + "\nYour HP: " + getHealth() + "/100");

                        CombatSystem.resolveCombat(this, enemy,true);


                        if (!enemy.isDead()) {
                            PopupPanel.showPopup("After Combat",
                                    enemy.getClass().getSimpleName() + " survived!\n" +
                                            "Enemy HP: " + enemy.getHealth() + "/50\n" +
                                            "Your HP: " + getHealth() + "/100");

                        } else {
                            PopupPanel.showPopup("Enemy Defeated",
                                    "You defeated the " + enemy.getClass().getSimpleName() +
                                            "!\nYour HP: " + getHealth() + "/100");

                            world.getMap().removeEntity(pos, enemy);
                            world.getGameFrame().getMapPanel().updateMap();
                            world.getGameFrame().getStatusPanel().updateStatus(this);
                            // Notify that enemy was removed
                            notifyObservers();
                        }

                        if (isDead()) {
                            JOptionPane.showMessageDialog(null, "You have died in battle.\nGame Over.");
                            EnemyPool.instance().shutdown();
                            System.exit(0);
                        }
                    }

                    // --- Potion Interaction ---
                    else if (entity instanceof Potion potion) {
                        //System.out.println("\nYou found a potion!");
                        GameFrame frame = GameWorld.getInstance().getGameFrame();
                        frame.getMapPanel().flashCell(pos, Color.GREEN);
                        if(potion instanceof PowerPotion)
                        {
                            int oldPower = getPower();
                            potion.interact(this);
                            SoundPlayer.playSound("life-spell.wav");
                            PopupPanel.showPopup("Power Potion Found",
                                    "You found a power potion!\nPower Before: " + oldPower +
                                            "\nPower After: " + getPower());
                        }
                        else {
                            int oldHp = getHealth();
                            potion.interact(this);
                            SoundPlayer.playSound("life-spell.wav");
                            PopupPanel.showPopup("Life Potion Found",
                                    "You found a Life potion!\nHP Before: " + oldHp +
                                            "\nHP After: " + getHealth());
                        }

                        world.getMap().removeEntity(pos, potion);

                    }

                    // --- Treasure Interaction ---
                    else if (entity instanceof Treasure treasure) {
                        treasure.interact(this);
                        SoundPlayer.playSound("coin.wav");
                        PopupPanel.showPopup("Treasure Found", "You found a treasure!");
                        world.getMap().removeEntity(pos, treasure);
                    }

                    // --- Other Entities ---
                    else {
                        System.out.println("\nNo interaction possible with: " + entity.getClass().getSimpleName());
                    }
                }
            }
            finally {
                BOARD_LOCK.unlock();
            }
        }

    }

    /**
     * Returns the name of the player.
     */
    public String getName() {return name;}


    public Inventory getInventory() {return inventory;}
    
    /**
     * Adds a GameItem to the player's inventory.
     * @param item the item to add
     * @return true (always succeeds for now)
     */
    public boolean addToInventory(GameItem item){
        inventory.addItem(item);
        return true;
    }

    /**
     * Uses the first regular Potion in the inventory (if any).
     * Removes it after use.
     * @return true if a potion was used, false otherwise
     */
    public boolean usePotion() {
        if(getHealth() == 100)
            return false;
        for (GameItem item : inventory.getItems()) {
            if (item instanceof Potion && !(item instanceof PowerPotion)) {
                ((Potion)item).interact(this);
                inventory.removeItem(item);
                return true;
            }
        }
        return false;
    }

    /**
     * Uses the first PowerPotion in the inventory (if any).
     * Removes it after use.
     * @return true if a power potion was used, false otherwise
     */
    public boolean usePowerPotion(){
        for (GameItem item : inventory.getItems()) {
            if (item instanceof PowerPotion) {
                ((PowerPotion)item).interact(this);
                inventory.removeItem(item);
                return true;
            }
        }
        return false;
    }

    /**
     * Adds treasure points to the player.
     * @param amount the number of points to add
     * @return true (always succeeds)
     */
    public boolean updateTreasurePoint(int amount){
        this.treasurePoints += amount;
        return true;
    }

    /**
     * Gets the total number of treasure points the player has earned.
     */
    public int getTreasurePoints(){return treasurePoints;}

    public String getDisplaySymbol() {
        return name.substring(0, 1).toUpperCase();
    }

    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }
    
    public void removeObserver(GameObserver observer){observers.remove(observer);}

    
    public void notifyObservers() {
        for (GameObserver observer : observers) {
            observer.onPlayerMoved(getPosition());
        }
    }

    
    public void clearObservers() {
        observers.clear();
    }

    
    public int getPowerPotionCount() {
        int counter = 0;
        for (GameItem i :inventory.getItems())
        {
            if(i instanceof PowerPotion)
                counter++;
        }
        return counter;
    }

    
    public int getLifePotionCount() {
        int counter = 0;
        for (GameItem i :inventory.getItems())
        {
            if(i instanceof Potion && !(i instanceof PowerPotion))
                counter++;
        }
        return counter;
    }


    
    public String getImagePath() {
        return "/game/resources/images/" + this.getClass().getSimpleName() + ".png";
    }




}
