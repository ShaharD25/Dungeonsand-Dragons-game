package game.factory;
import game.builder.EnemyBuilder;
import game.characters.Enemy;
import game.characters.Goblin;
import game.characters.Orc;
import game.characters.Dragon;
import game.engine.GameWorld;
import game.map.Position;

import java.util.*;
public class EnemyFactory {
    private static final Random random = new Random();
    private static final Map<String, Class<? extends Enemy>> enemyTypes = new HashMap<>();
    static{
        enemyTypes.put("Goblin", Goblin.class);
        enemyTypes.put("Orc", Orc.class);
        enemyTypes.put("Dragon", Dragon.class);
    }

    public static Enemy createRandomEnemy(){
        GameWorld gw = GameWorld.getInstance();
        Position position = gw.getFreeRandomPosition();
        List<String> missing = gw.getMissingEnemies();
        String selectedKey;
        if(missing.isEmpty()){
            List<String> keys =new ArrayList<>(enemyTypes.keySet());
            selectedKey = keys.get(random.nextInt(keys.size()));
        }
        else {
            selectedKey = missing.get(random.nextInt(missing.size()));
        }
        Class<? extends Enemy> enemyClass = enemyTypes.get(selectedKey);

        return new EnemyBuilder()
                .setRandom()
                .setType(enemyClass)
                .setPosition(position)
                .build();
    }

    public static Set<String> getSupportedEnemyTypes() {
        return enemyTypes.keySet();
    }

}

