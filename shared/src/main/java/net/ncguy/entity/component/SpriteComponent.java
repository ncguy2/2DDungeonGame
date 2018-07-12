package net.ncguy.entity.component;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Provides the reference used by the client renderer to identify which texture to use when rendering the component
 */
public class SpriteComponent extends SceneComponent {

    public transient Sprite sprite;
    public String spriteRef;

    public SpriteComponent(String name) {
        super(name);
        spriteRef = "";
    }

    @Override
    public void Update(float delta) {
        if(sprite == null) {
            sprite = new Sprite(new Texture(Gdx.files.internal(spriteRef)));
        }
        super.Update(delta);
    }
}
