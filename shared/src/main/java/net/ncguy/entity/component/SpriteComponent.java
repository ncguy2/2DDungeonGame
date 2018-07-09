package net.ncguy.entity.component;

/**
 * Provides the reference used by the client renderer to identify which texture to use when rendering the component
 */
public class SpriteComponent extends SceneComponent {

    public String spriteRef;

    public SpriteComponent(String name) {
        super(name);
        spriteRef = "";
    }

}
