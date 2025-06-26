package game.memento;

import game.core.Inventory;
import game.map.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameMemento {

    private final List<CharacterSnapshot> playerSnapshots;
    private final List<CharacterSnapshot> enemySnapshots;
    private final List<ItemSnapshot> itemSnapshots;

 // Button states
    private boolean boostActive = false;
    private boolean shieldActive = false;
    private boolean boostButtonEnabled = true;
    private boolean shieldButtonEnabled = true;
    
    public GameMemento(List<CharacterSnapshot> players, List<CharacterSnapshot> enemies, List<ItemSnapshot> items) {
        this.playerSnapshots = new ArrayList<>(players);
        this.enemySnapshots = new ArrayList<>(enemies);
        this.itemSnapshots = new ArrayList<>(items);
    }

    public List<CharacterSnapshot> getPlayerSnapshots() {
        return playerSnapshots;
    }

    public List<CharacterSnapshot> getEnemySnapshots() {
        return enemySnapshots;
    }

    public List<ItemSnapshot> getItemSnapshots() {
        return itemSnapshots;
    }
    
 // Button state getters and setters
    public boolean isBoostActive() {
        return boostActive;
    }
    
    public void setBoostActive(boolean boostActive) {
        this.boostActive = boostActive;
    }
    
    public boolean isShieldActive() {
        return shieldActive;
    }
    
    public void setShieldActive(boolean shieldActive) {
        this.shieldActive = shieldActive;
    }
    
    public boolean isBoostButtonEnabled() {
        return boostButtonEnabled;
    }
    
    public void setBoostButtonEnabled(boolean enabled) {
        this.boostButtonEnabled = enabled;
    }
    
    public boolean isShieldButtonEnabled() {
        return shieldButtonEnabled;
    }
    
    public void setShieldButtonEnabled(boolean enabled) {
        this.shieldButtonEnabled = enabled;
    }
    

    // Enhanced CharacterSnapshot to handle decorators
    public static class CharacterSnapshot {
        public final String baseType;           // Base class (Warrior, Goblin, etc.)
        public final int health;
        public final int power;
        public final Position position;
        public final boolean isVisible;
        public final int loot;                  // For enemies
        
        // Player-specific fields
        public final String name;
        public final Inventory inventory;
        public final int treasurePoints;
        
        // Decorator chain information
        public final List<DecoratorInfo> decorators;
        
        // Constructor for enemies
        public CharacterSnapshot(String baseType, int health, int power, Position position, 
                               boolean isVisible, int loot, List<DecoratorInfo> decorators) {
            this.baseType = baseType;
            this.health = health;
            this.power = power;
            this.position = position;
            this.isVisible = isVisible;
            this.loot = loot;
            this.decorators = decorators;
            
            // Enemy-specific defaults
            this.name = null;
            this.inventory = null;
            this.treasurePoints = 0;
        }
        
        // Constructor for players
        public CharacterSnapshot(String baseType, int health, int power, Position position, 
                               boolean isVisible, String name, Inventory inventory, 
                               int treasurePoints, List<DecoratorInfo> decorators) {
            this.baseType = baseType;
            this.health = health;
            this.power = power;
            this.position = position;
            this.isVisible = isVisible;
            this.name = name;
            // Deep copy the inventory
            this.inventory = (inventory != null) ? inventory.deepCopy() : null;
            this.treasurePoints = treasurePoints;
            this.decorators = decorators;
            
            // Player-specific defaults
            this.loot = 0;
        }
    }
    
    // Class to store decorator information
    public static class DecoratorInfo {
        public final String decoratorType;
        public final Map<String, Object> state;
        
        public DecoratorInfo(String decoratorType, Map<String, Object> state) {
            this.decoratorType = decoratorType;
            this.state = new HashMap<>(state);
        }
    }

    public static class ItemSnapshot {
        public final String type;
        public final Position position;
        public final int value;  // For treasures

        public ItemSnapshot(String type, Position position, int value) {
            this.type = type;
            this.position = position;
            this.value = value;
        }
        
        // Convenience constructor for non-treasure items
        public ItemSnapshot(String type, Position position) {
            this(type, position, 0);
        }
    }
}