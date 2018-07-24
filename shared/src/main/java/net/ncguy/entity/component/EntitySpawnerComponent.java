package net.ncguy.entity.component;

import com.badlogic.gdx.math.Vector2;
import net.ncguy.entity.Entity;
import net.ncguy.script.SpawnerScriptObject;
import net.ncguy.world.EntityWorld;

public class EntitySpawnerComponent extends SceneComponent {

    public SpawnerScriptObject spawnerScript;
    public float spawnInterval;
    public int spawnAmount;

    float currentInterval;

    public EntitySpawnerComponent(String name) {
        super(name);
        spawnInterval = .5f;
        spawnAmount = 1;
    }

    @Override
    public void Update(float delta) {
        super.Update(delta);
        Vector2 pos = new Vector2();
        transform.WorldTransform().getTranslation(pos);

        currentInterval += delta;
        if(currentInterval > spawnInterval) {
            currentInterval -= spawnInterval;
            Entity entity = GetOwningEntity();
            EntityWorld entityWorld = entity.GetWorld();
            for (int i = 0; i < spawnAmount; i++) {
//                spawnerScript.InvokeCreate(entity, "Overworld", pos).ifPresent(e -> {
//                    entityWorld.PostRunnable(() -> entityWorld.Add(e));
//                });
            }
        }
    }

}
