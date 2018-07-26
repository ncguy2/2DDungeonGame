package net.ncguy.entity.component;

import net.ncguy.entity.Transform2D;

public abstract class LaggingArmComponent extends SceneComponent {

    @EntityProperty(Type = Transform2D.class, Category = "Lagging arm", Description = "The transform parent of child components", Name = "Spoofed transform")
    public Transform2D spoofedTransform;

    public LaggingArmComponent(String name) {
        super(name);
        spoofedTransform = new Transform2D();
    }

    @Override
    public void Update(float delta) {
        super.Update(delta);
        Step(delta);
    }

    public abstract void Step(float delta);

    @Override
    public void Add(EntityComponent component) {
        super.Add(component);
        if(component instanceof SceneComponent)
            ((SceneComponent) component).transform.parent = spoofedTransform;
    }
}
