package net.ncguy.entity.component;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import net.ncguy.lib.gen.tile.CaveTileWorldGenerator;
import net.ncguy.lib.gen.tile.TileWorldElement;
import net.ncguy.physics.worker.SpawnEntityTask;
import net.ncguy.system.PhysicsContainer;
import net.ncguy.system.PhysicsSystem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static net.ncguy.system.PhysicsSystem.screenToPhysics;

public class GeneratorComponent extends SceneComponent {

    @EntityProperty(Type = Float.class, Name = "Tile width", Category = "Generator", Description = "Width of tiles")
    public float tileWidth = 64;
    @EntityProperty(Type = Float.class, Name = "Tile height", Category = "Generator", Description = "Height of tiles")
    public float tileHeight = 64;

    @EntityProperty(Type = Integer.class, Name = "World width", Category = "Generator", Description = "Width of the world")
    public int worldWidth = 4;
    @EntityProperty(Type = Integer.class, Name = "World height", Category = "Generator", Description = "Height of the world")
    public int worldHeight = 4;

    @EntityProperty(Type = Integer.class, Name = "Seed", Category = "Generator", Description = "Seed for the generator")
    public int generatorSeed = 8;

    @EntityProperty(Type = Float.class, Name = "Chance To Start Alive", Category = "Generator")
    public float chanceToStartAlive = 0.25f;
    @EntityProperty(Type = Integer.class, Name = "Death Limit", Category = "Generator")
    public int deathLimit = 6;
    @EntityProperty(Type = Integer.class, Name = "Birth Limit", Category = "Generator")
    public int birthLimit = 3;
    @EntityProperty(Type = Integer.class, Name = "Iterations", Category = "Generator")
    public int iterations = 8;

    protected transient final List<SceneComponent> generatedEntities;

    public GeneratorComponent() {
        this("Unnamed Scene component");
    }

    public GeneratorComponent(String name) {
        super(name);
        generatedEntities = new ArrayList<>();
    }

    @EntityFunction(Name = "Generate", Description = "Regenerates the world", Category = "Generator")
    public void Generate() {

        synchronized (generatedEntities) {
            generatedEntities.forEach(SceneComponent::Destroy);
            generatedEntities.clear();
        }

        String wallTexPath = "textures/wall.jpg";
        String floorTexPath = "textures/wood.png";

        float width = tileWidth;
        float height = tileHeight;

        float halfWidth = width * .5f;
        float halfHeight = height * .5f;

        CaveTileWorldGenerator generator = new CaveTileWorldGenerator();
        generator.seed = generatorSeed;
        generator.width = worldWidth;
        generator.height = worldHeight;
        generator.iterations = iterations;
        generator.chanceToStartAlive = chanceToStartAlive;
        generator.deathLimit = deathLimit;
        generator.birthLimit = birthLimit;

        Collection<TileWorldElement> elements = generator.GetElements();

        PhysicsContainer container = PhysicsSystem.GetContainerByName("Overworld").orElse(null);

        long start = System.nanoTime();
        synchronized (generatedEntities) {
            for (TileWorldElement element : elements) {

                int x = element.x;
                int y = element.y;
                boolean solid = element.solid;

                MaterialSpriteComponent mapEntity = new MaterialSpriteComponent("Sprite");
                mapEntity.materialRef = "default";
                if(element.texRef == null || element.texRef.isEmpty())
                    mapEntity.spriteRef = (solid ? wallTexPath : floorTexPath);
                else mapEntity.spriteRef = element.texRef;
                mapEntity.connected = element.texConnected;
                mapEntity.worldX = element.x;
                mapEntity.worldY = element.y;
                mapEntity.worldMap = element.texMap;
                mapEntity.castShadow = solid;
                mapEntity.transform.translation.set(width * x, height * y);
                mapEntity.transform.scale.set(width, height);

                if (solid) {
                    if(container != null) {
                        BodyDef def = new BodyDef();
                        def.type = BodyDef.BodyType.StaticBody;
                        def.position.set((x * width), (y * height))
                                .scl(screenToPhysics);

                        PolygonShape shape = new PolygonShape();
                        shape.setAsBox(halfWidth * screenToPhysics, halfHeight * screenToPhysics);

                        FixtureDef fixDef = new FixtureDef();
                        fixDef.shape = shape;
                        fixDef.density = 0;
                        fixDef.friction = 0f;
                        fixDef.restitution = 0.0f;

                        SpawnEntityTask task = new SpawnEntityTask(def, fixDef);
                        task.OnFinish(body -> {
                            CollisionComponent collision = mapEntity.Add(new CollisionComponent("Collision"));
                            collision.body = body;
                            collision.container = container;
                            shape.dispose();
                        });

                        container.foreman.Post(task);
                    }else System.out.println("No container, physics construction cancelled");
                }

                mapEntity.transform.parent = this.transform;
                generatedEntities.add(mapEntity);
            }
        }
//        generatedEntities.forEach(owningComponent::Add);
        long end = System.nanoTime();
        System.out.printf("World composition finished: took %fms\n", (end - start) / 1000000f);
    }

    @Override
    public void PostReplicate() {
        Generate();
    }

    @Override
    public Set<EntityComponent> GetComponents() {
        Set<EntityComponent> set = super.GetComponents();
        synchronized (generatedEntities) {
            set.addAll(generatedEntities);
        }
        return set;
    }
}
