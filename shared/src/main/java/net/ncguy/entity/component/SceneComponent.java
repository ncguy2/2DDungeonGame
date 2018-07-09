package net.ncguy.entity.component;

import net.ncguy.entity.Transform2D;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Similar to the {@link EntityComponent}, the Scene component provides a 2d transform and functionality to allow for child components
 */
public class SceneComponent extends EntityComponent {

    public Transform2D transform;
    public Set<EntityComponent> childrenComponents;

    public SceneComponent(String name) {
        super(name);
        transform = new Transform2D();
        childrenComponents = new LinkedHashSet<>();
    }

    public void Add(EntityComponent component) {
        childrenComponents.add(component);
    }

    public void Remove(EntityComponent component) {
        childrenComponents.remove(component);
    }

}
