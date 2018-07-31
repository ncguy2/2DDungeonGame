package net.ncguy.entity.component;

public class LinearLaggingArmComponent extends LaggingArmComponent {

    @EntityProperty(Type = Float.class, Category = "Lagging arm", Description = "The interpolation time in seconds", Name = "Interpolation seconds")
    public float delaySeconds = 1;

    public LinearLaggingArmComponent() {
        this("Unnamed Scene component");
    }

    public LinearLaggingArmComponent(String name) {
        super(name);
    }

    @Override
    public void Step(float delta) {
        spoofedTransform.LerpLocalToWorld(transform, delta / delaySeconds);
    }
}
