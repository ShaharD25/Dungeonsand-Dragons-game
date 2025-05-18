//Artiom Bondar:332692730
//Shahar Dahan: 207336355
package game.characters;
import game.Main;
import game.combat.CombatSystem;
import game.combat.RangedFighter;
import game.gui.GameFrame;
import game.gui.GameObserver;
import game.gui.PopupPanel;
import game.items.Treasure;
import game.map.Position;
import game.engine.GameWorld;
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

/**
 * Abstract class representing an enemy character on the map.
 * Enemies have loot value and drop a treasure upon defeat.
 */
public abstract class Enemy extends AbstractCharacter implements Runnable, GameObserver {
    private int loot;
    private AtomicBoolean active;
    private final AtomicBoolean running;
    private final ScheduledExecutorService exec;
    private final ReentrantLock boardLock;

    /**
     * Constructs an enemy with given loot and a fixed position.
     * Health is randomly initialized between 0–50.
     *
     * @param loot     The value of the treasure the enemy will drop
     * @param position The position of the enemy on the map
     */
    public Enemy(ScheduledExecutorService exec,AtomicBoolean running, ReentrantLock boardLock, int loot, Position position){
        super(position);
        setHealth(new Random().nextInt(51));
        this.loot = loot;
        this.exec = exec;
        this.boardLock = boardLock;
        this.running = running;
        active = new AtomicBoolean(false);
    }

    /**
     * Triggers upon the enemy's defeat.
     * A Treasure is created and added to the map at the enemy's position.
     */
    public void defeat()
    {
        Position p = this.getPosition();
        Treasure t = new Treasure(p,true,loot);
        GameWorld.getInstance().getMap().addEntity(p,t);
    }

    /**
     * Returns the amount of treasure (loot points) this enemy is holding.
     *
     * @return The loot value of the enemy.
     */
    public int getLoot() {
        return loot;
    }

    /**
     * Sets the loot value of the enemy.
     * Only accepts non-negative values (0 or more).
     *
     * @param loot The new loot value to assign.
     */
    public boolean setLoot(int loot) {
        if (loot >= 0) {
            this.loot = loot;
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        if(!running.get() || getHealth() <= 0){return;}
        if(active.get())
        {
            takeAction();
        }
        Random random = new Random();
        int delay = 500 + random.nextInt(1001); // 500–1500 ms
        exec.schedule(this,delay, TimeUnit.MILLISECONDS);
    }

    public void fightPlayer(GameWorld world, PlayerCharacter player)
    {
        GameFrame frame = GameWorld.getInstance().getGameFrame();
        frame.getMapPanel().flashCell(getPosition(), Color.RED);

        PopupPanel.showPopup("Enemy Encountered", "A " + getClass().getSimpleName() +" started a fight with you!"
                + "\nEnemy HP: " + getHealth() + "/50" + "\nYour HP: " + player.getHealth() + "/100");

        CombatSystem.resolveCombat(player, this);

        if (!isDead()) {
            PopupPanel.showPopup("After Combat",
                    getClass().getSimpleName() + " survived!\n" +
                            "Enemy HP: " + getHealth() + "/50\n" +
                            "Your HP: " + player.getHealth() + "/100");

        } else {
            PopupPanel.showPopup("Enemy Defeated",
                    "You defeated the " + getClass().getSimpleName() +
                            "!\nYour HP: " + player.getHealth() + "/100");

            world.getMap().removeEntity(getPosition(), this);
            world.getGameFrame().getMapPanel().updateMap();
            world.getGameFrame().getStatusPanel().updateStatus(player);
            // Notify that enemy was removed
            player.removeObserver(this);
            stopEnemy();
        }

        if (player.isDead()) {
            String message = "You have died in battle.";
            String title = "Game Over";

            // Custom button texts
            String[] options = {"Restart Game", "Exit Game"};

            int choice = JOptionPane.showOptionDialog(
                    null,
                    message,
                    title,
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            // Handle button click
            if (choice == 0) {
                world.closeGame();
                Main.restartGame(world);
            } else if (choice == 1) {
                System.exit(0);
            }
        }
    }

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

        return null; // no path found
    }

    private static List<Position> reconstructPath(Map<Position, Position> cameFrom, Position current) {
        List<Position> path = new LinkedList<>();
        path.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(0, current);
        }
        return path;
    }

    private static List<Position> getNeighbors(Position pos, GameMap map) {
        List<Position> neighbors = new ArrayList<>();
        int[][] directions = {{1,0},{-1,0},{0,1},{0,-1}};

        for (int[] d : directions) {
            Position next = new Position(pos.getRow() + d[0], pos.getCol() + d[1]);
            if (map.isValidPosition(next)) {
                neighbors.add(next);
            }
        }

        return neighbors;
    }

    public void moveToPlayer(GameWorld world, PlayerCharacter player) {
        Position start = getPosition();
        Position goal = player.getPosition();

        List<Position> path = findPath(world.getMap(), start, goal);

        if (path != null && path.size() > 1) {
            Position nextStep = path.get(1); // path[0] == current position

            synchronized (world.getMap()) {
                world.getMap().moveEntity(world,this, start, nextStep);
            }
        }
    }


    public void takeAction()
    {
        if(boardLock.tryLock())
        {
            try{
                GameWorld world = GameWorld.getInstance();
                PlayerCharacter player = world.getPlayers().get(0);

                int fightRange = 1;
                if(this instanceof RangedFighter)
                    fightRange = 2;
                if(Main.calcDistance(player.getPosition(), getPosition()) <= fightRange)
                {
                    fightPlayer(world, player);
                }
                else {
                    moveToPlayer(world, player);
                }
            }
            finally {
                boardLock.unlock();
            }
        }

    }


    public void stopEnemy()
    {
        running.set(false);
        exec.shutdownNow();
    }

    public void onPlayerMoved(Position pos) {
        active.set(getPosition().distanceTo(pos) <= 2);
    }
}
