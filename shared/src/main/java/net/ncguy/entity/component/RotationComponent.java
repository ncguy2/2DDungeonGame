package net.ncguy.entity.component;

public class RotationComponent extends SceneComponent {

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
        transform.rotationDegrees += rotationSpeed * delta;
        transform.rotationDegrees %= 360;
        super.Update(delta);
    }
}
