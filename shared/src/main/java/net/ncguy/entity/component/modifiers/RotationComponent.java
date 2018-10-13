package net.ncguy.entity.component.modifiers;

import net.ncguy.entity.Transform2D;
import net.ncguy.entity.aspect.Aspect;
import net.ncguy.entity.aspect.CommonAspectKeys;
import net.ncguy.entity.component.EntityProperty;

public class RotationComponent extends ModifierComponent {

    @EntityProperty(Type = Float.class, Name = "Rotation speed", Category = "Dynamics", Description = "Speed of rotation")
    public float rotationSpeed;

    public RotationComponent() {
        this(null);
    }

    public RotationComponent(String name) {
        super(name);
    }

    @Override
    public void Update(float delta) {
        Aspect.of(owningComponent, CommonAspectKeys.TRANSFORM).ifPresent(aspect -> {
            Transform2D transform = aspect.getObject();
            transform.rotationDegrees += rotationSpeed * delta;
            transform.rotationDegrees %= 360;
        });
        super.Update(delta);
    }
}
