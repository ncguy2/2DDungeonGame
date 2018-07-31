package net.ncguy.network;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import net.ncguy.entity.Entity;
import net.ncguy.entity.Transform2D;
import net.ncguy.entity.component.*;
import net.ncguy.lib.dmg.hp.Health;
import net.ncguy.lib.dmg.status.StatusEffect;
import net.ncguy.lib.net.Network;
import net.ncguy.network.packet.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PacketClasses {

    public static List<Network.Entry> GetPacketClasses() {
        List<Network.Entry> list = new ArrayList<>(Network.GetPacketClasses());

        // Data classes
        list.add(new Network.Entry(Entity.class));
        list.add(new Network.Entry<>(UUID.class, new Serializer<UUID>() {
            @Override
            public void write(Kryo kryo, Output output, UUID o) {
                kryo.writeObject(output, o.toString());
            }

            @Override
            public UUID read(Kryo kryo, Input input, Class aClass) {
                String uuid = kryo.readObject(input, String.class);
                return UUID.fromString(uuid);
            }
        }));

        list.add(new Network.Entry(EntityComponent.class));
        list.add(new Network.Entry(SceneComponent.class));
        list.add(new Network.Entry(DistortionComponent.class));
        list.add(new Network.Entry(RenderComponent.class));
        list.add(new Network.Entry(SpriteComponent.class));
        list.add(new Network.Entry(MaterialSpriteComponent.class));
        list.add(new Network.Entry(CollisionComponent.class));
        list.add(new Network.Entry(PrimitiveCircleComponent.class));
        list.add(new Network.Entry(LightComponent.class));
        list.add(new Network.Entry(LaggingArmComponent.class));
        list.add(new Network.Entry(LinearLaggingArmComponent.class));
//        list.addnew Entry<(CameraComponent.class>());
        // TODO synchronize
//        list.addnew Entry<(EntitySpawnerComponent.class>());
        list.add(new Network.Entry(MovementComponent.class));
        list.add(new Network.Entry(AbilityComponent.class));
//        list.addnew Entry<(InputComponent.class>());
        list.add(new Network.Entry(HealthComponent.class));
        list.add(new Network.Entry(InventoryComponent.class));
        list.add(new Network.Entry(AbilitiesComponent.class));
        list.add(new Network.Entry(GeneratorComponent.class));

        list.add(new Network.Entry(Transform2D.class));
        list.add(new Network.Entry(Vector2.class));
        list.add(new Network.Entry(Matrix3.class));
        list.add(new Network.Entry(Color.class));
        list.add(new Network.Entry(AbilityComponent.AbilityState.class));
        list.add(new Network.Entry(Health.class));
        list.add(new Network.Entry(StatusEffect.class));

        // Physics classes
        list.add(new Network.Entry(BodyDef.class));
        list.add(new Network.Entry(BodyDef.BodyType.class));
        list.add(new Network.Entry(FixtureDef.class));
        list.add(new Network.Entry(Shape.class));
        list.add(new Network.Entry(Shape.Type.class));
        list.add(new Network.Entry(Filter.class));

        // Packet classes
        list.add(new Network.Entry(DungeonPacket.class));
        list.add(new Network.Entry(TransformPacket.class));
        list.add(new Network.Entry(WorldPacket.class));
        list.add(new Network.Entry(WorldRequestPacket.class));
        list.add(new Network.Entry(EntityPacket.class));

        return list;
    }


}
