package net.ncguy.system;

import net.ncguy.entity.Entity;
import net.ncguy.entity.component.InputComponent;
import net.ncguy.entity.component.MovementComponent;
import net.ncguy.input.InputManager;
import net.ncguy.world.EntityWorld;

import java.util.List;

public class InputSystem extends BaseSystem {

    public InputSystem(EntityWorld operatingWorld) {
        super(operatingWorld);
    }

    @Override
    public void Startup() {

    }

    @Override
    public void Update(float delta) {
        //noinspection unchecked
        List<Entity> entities = operatingWorld.GetFlattenedEntitiesWithComponents(InputComponent.class, MovementComponent.class);
        entities.forEach(e -> {
            InputComponent input = e.GetComponent(InputComponent.class, true);
            MovementComponent movement = e.GetComponent(MovementComponent.class, true);

            movement.velocity.x += InputManager.Scale(movement.movementSpeed, input.keyRight);
            movement.velocity.x -= InputManager.Scale(movement.movementSpeed, input.keyLeft);

            movement.velocity.y += InputManager.Scale(movement.movementSpeed, input.keyUp);
            movement.velocity.y -= InputManager.Scale(movement.movementSpeed, input.keyDown);

        });
    }

    @Override
    public void Shutdown() {

    }
}
