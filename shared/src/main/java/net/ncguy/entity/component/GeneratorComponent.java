package net.ncguy.entity.component;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import net.ncguy.entity.Entity;
import net.ncguy.lib.gen.tile.TileWorldElement;
import net.ncguy.lib.gen.tile.TileWorldGenerator;
import net.ncguy.physics.worker.SpawnEntityTask;
import net.ncguy.profile.ProfilerHost;
import net.ncguy.system.PhysicsContainer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static net.ncguy.system.PhysicsSystem.screenToPhysics;

public class GeneratorComponent extends EntityComponent {

    @EntityProperty(Type = Float.class, Name = "Tile width", Category = "Generator", Description = "Width of tiles")
    public float tileWidth = 64;
    @EntityProperty(Type = Float.class, Name = "Tile height", Category = "Generator", Description = "Height of tiles")
    public float tileHeight = 64;

    @EntityProperty(Type = Integer.class, Name = "World width", Category = "Generator", Description = "Width of the world")
    public int worldWidth = 8;
    @EntityProperty(Type = Integer.class, Name = "World height", Category = "Generator", Description = "Height of the word")
    public int worldHeight = 8;

    public PhysicsContainer container;
    protected final List<Entity> generatedEntities;

    public GeneratorComponent(String name) {
        super(name);
        generatedEntities = new ArrayList<>();
    }

    @EntityFunction(Name = "Generate", Description = "Regenerates the world", Category = "Generator")
    public void Generate() {

        generatedEntities.forEach(Entity::Destroy);
        generatedEntities.clear();

        String wallTexPath = "textures/wall.jpg";
        String floorTexPath = "textures/wood.png";

        ProfilerHost.Start("Map");
        float width = tileWidth;
        float height = tileHeight;

        float halfWidth = width * .5f;
        float halfHeight = height * .5f;

        ProfilerHost.Start("World generation");
        TileWorldGenerator generator = new TileWorldGenerator();
        generator.width = worldWidth;
        generator.height = worldHeight;
        Collection<TileWorldElement> elements = generator.GetElements();
        ProfilerHost.End("World generation");

        ProfilerHost.Start("World composition [" + elements.size() + "]");
        for (TileWorldElement element : elements) {

            int x = element.x;
            int y = element.y;
            boolean solid = element.solid;

            Entity mapEntity = new Entity();
            mapEntity.SetRootComponent(new MaterialSpriteComponent("Sprite")).spriteRef = (solid ? wallTexPath : floorTexPath);
            ((MaterialSpriteComponent) mapEntity.GetRootComponent()).castShadow = solid;
            mapEntity.Transform().translation.set(width * x, height * y);
            mapEntity.Transform().scale.set(width, height);

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
                        CollisionComponent collision = mapEntity.AddComponent(new CollisionComponent("Collision"));
                        collision.body = body;
                        collision.container = container;
                        shape.dispose();
                    });

                    container.foreman.Post(task);
                }else System.out.println("No container, physics construction cancelled");
            }

            generatedEntities.add(mapEntity);

        }
        GetOwningEntity().AddEntities(generatedEntities);
        ProfilerHost.End("World composition");
        ProfilerHost.End("Map");

    }

}
