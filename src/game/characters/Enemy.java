//Artiom Bondar:332692730
//Shahar Dahan: 207336355
package game.characters;

import game.combat.CombatSystem;
import game.combat.RangedFighter;
import game.core.GameEntity;
import game.decorators.EnemyDecorator;
import game.engine.EnemyPool;
import game.engine.GameWorld;
import game.factory.EnemyFactory;
import game.gui.GameFrame;
import game.gui.GameObserver;
import game.items.Treasure;
import game.logging.LogManager;
import game.map.GameMap;
import game.map.Position;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import static game.map.GameMap.calcDistance;

public abstract class Enemy extends AbstractCharacter implements Runnable, GameObserver {
    private int loot;
    protected final AtomicBoolean running;
    protected final ScheduledExecutorService exec;
    protected final ReentrantLock boardLock;
    public AtomicBoolean active;
    private volatile boolean replacementSpawned = false;
    private volatile boolean stopped = false;
    
    public Enemy(ScheduledExecutorService exec, AtomicBoolean running, ReentrantLock boardLock, int loot, Position position) {
        super(position);
        this.loot = loot;
        this.exec = exec;
        this.running = running;
        this.boardLock = boardLock;
        this.active = new AtomicBoolean(false);
        // Health is set separately via setInitialRandomHealth or builder
    }

    public ScheduledExecutorService getExec()
    {
    		return exec;
    }
    
    public AtomicBoolean getRunning()
    {
    		return running;
    }
    
    public ReentrantLock getBoardLock()
    {
    		return boardLock;
    }
    

    // Used when we want randomized health on creation
    public void setInitialRandomHealth() {
        setHealth(new Random().nextInt(50) + 1);
    }

    public int getLoot() {
        return loot;
    }

    
    public boolean setLoot(int loot) {
        if (loot >= 0) {
            this.loot = loot;
            return true;
        }
        return false;
    }

    // Called when enemy dies: drops treasure on its position
    public void defeat() {
        Position p = this.getPosition();
        Treasure t = new Treasure(p, true, loot);
        GameWorld.getInstance().getMap().addEntity(p, t);

    }

    
    public void stopEnemy() {
        running.set(false);
        exec.shutdownNow();
    }

    @Override
    public void run() {
        if (!running.get() || stopped) return;

        if (isDead()) {
        		// Only spawn replacement once
            if (!replacementSpawned) {
                replacementSpawned = true;
                stopped = true; // Stop this enemy's thread
                
                GameWorld world = GameWorld.getInstance();
                Enemy replacement = EnemyFactory.createRandomEnemy();
                world.addEnemy(replacement);
                EnemyPool.instance().scheduleEnemy(replacement);
            }
            return; // Don't reschedule - this enemy is done
        }
        
        if (active.get()) {
            takeAction();
        }

        int delay = 500 + new Random().nextInt(1001);
        exec.schedule(this, delay, TimeUnit.MILLISECONDS);
    }
    

    // Determines whether the player is near enough to activate the enemy
    public void onPlayerMoved(Position pos) {
        active.set(getPosition().distanceTo(pos) <= 2);
    }

    // Called when enemy is active and takes its turn
    public void takeAction() {
        if (boardLock.tryLock()) {
            try {
                GameWorld world = GameWorld.getInstance();
                PlayerCharacter player = world.getPlayers().get(0);

                int fightRange = (this instanceof RangedFighter) ? 2 : 1;

                if (calcDistance(player.getPosition(), getPosition()) <= fightRange) {
                    fightPlayer(player);
                } else {
                    moveToPlayer(player);
                }
            } finally {
                boardLock.unlock();
            }
        }
    }

    // Handles combat logic between this enemy and the player
    public void fightPlayer(PlayerCharacter player) {
        GameWorld world = GameWorld.getInstance();
        GameFrame frame = GameWorld.getInstance().getGameFrame();
        frame.getMapPanel().flashCell(getPosition(), Color.RED);

        LogManager.log("Enemy Encountered! A " + getClass().getSimpleName() + " started a fight with you!"
                + "\nEnemy HP: " + getHealth() + "/50" + "\nYour HP: " + player.getHealth() + "/100");

        Enemy decoratedEnemy = this;
        List<GameEntity> entitiesAtPos = world.getMap().getEntitiesAt(getPosition());
        for(GameEntity ge : entitiesAtPos)
        {
        		if(ge instanceof EnemyDecorator decEnemy)
        		{
        			if(decEnemy.getWrapped() == this)
        				decoratedEnemy = decEnemy;
        		}
        }
              
        
        CombatSystem.resolveCombat(decoratedEnemy, player, false);

        if (!isDead()) {
        		if(player.isDead())
        		{
        			LogManager.log("After Combat, " + getClass().getSimpleName() + " survived!\n" +
                            "Enemy HP: " + getHealth() + "/50\n" +
                            "Your HP: " + player.getHealth() + "/100");
        		}           
        } else {
            LogManager.log("Enemy Defeated, You defeated the " + getClass().getSimpleName() +
                    "!\nYour HP: " + player.getHealth() + "/100");
            


            if(decoratedEnemy != null) {
	            	world.getMap().removeEntity(getPosition(), decoratedEnemy);
	        		world.removeEnemy(decoratedEnemy);
            }

            world.getMap().removeEntity(getPosition(), this);
            world.removeEnemy(this);
            world.getGameFrame().getMapPanel().updateMap();
            world.getGameFrame().getStatusPanel().updateStatus(player);
            player.removeObserver(this);
        }

        if (player.isDead()) {
            JOptionPane.showMessageDialog(null, "You have died in battle.\nGame Over.");
            EnemyPool.instance().shutdown();
            System.exit(0);
        }
    }

    // Moves the enemy one step closer to the player
    public void moveToPlayer(PlayerCharacter player) {
        GameWorld world = GameWorld.getInstance();
        Position start = getPosition();
        Position goal = player.getPosition();

        List<Position> path = findPath(world.getMap(), start, goal);
        if (path != null && path.size() > 1) {
            Position nextStep = path.get(1);
            synchronized (world.getMap()) {
                world.getMap().moveEntity(world, this, start, nextStep);
            }
        }
    }

    // Pathfinding algorithm: A* search (simplified)
    public static List<Position> findPath(GameMap map, Position start, Position goal) {
        Set<Position> closedSet = new HashSet<>();
        Map<Position, Position> cameFrom = new HashMap<>();
        Map<Position, Integer> gScore = new HashMap<>();
        Map<Position, Integer> fScore = new HashMap<>();

        PriorityQueue<Position> openSet = new PriorityQueue<>(Comparator.comparingInt(fScore::get));

        gScore.put(start, 0);
        fScore.put(start, start.distanceTo(goal));
        openSet.add(start);

        while (!openSet.isEmpty()) {
            Position current = openSet.poll();

            if (current.equals(goal)) {
                return reconstructPath(cameFrom, current);
            }

            closedSet.add(current);

            for (Position neighbor : getNeighbors(current, map)) {
                if (closedSet.contains(neighbor)) continue;

                int tentativeG = gScore.getOrDefault(current, Integer.MAX_VALUE) + 1;

                if (tentativeG < gScore.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeG);
                    fScore.put(neighbor, tentativeG + neighbor.distanceTo(goal));
                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        return null;
    }

    // Reconstructs the path from start to goal
    private static List<Position> reconstructPath(Map<Position, Position> cameFrom, Position current) {
        List<Position> path = new LinkedList<>();
        path.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(0, current);
        }
        return path;
    }

    // Returns all 4 valid neighboring positions (up/down/left/right)
    private static List<Position> getNeighbors(Position pos, GameMap map) {
        List<Position> neighbors = new ArrayList<>();
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        for (int[] d : directions) {
            Position next = new Position(pos.getRow() + d[0], pos.getCol() + d[1]);
            if (map.isValidPosition(next) && !map.isWall(next)) {
                neighbors.add(next);
            }
        }

        return neighbors;
    }

    public String getOriginalName() {
        return getClass().getSimpleName();
    }

}
