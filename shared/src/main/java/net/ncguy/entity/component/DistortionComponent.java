package net.ncguy.entity.component;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;

/**
 * Provides the reference used by the client renderer to identify which texture to use when rendering the component
 */
public class DistortionComponent extends SceneComponent {

    public transient Sprite sprite;
    public String spriteRef;
    public final Vector2 spriteScaleOverride = new Vector2(-1, -1);

    public DistortionComponent() {
        this("Unnamed Scene component");
    }

    public DistortionComponent(String name) {
        super(name);
        spriteRef = "";
    }

    @Override
    public void Update(float delta) {
        if (sprite == null) {
            sprite = new Sprite(new Texture(Gdx.files.internal(spriteRef)));
        } else {
            Matrix3 transform = this.transform.WorldTransform();
            Vector2 pos = new Vector2();
            Vector2 size = new Vector2();
            transform.getTranslation(pos);
            transform.getScale(size);

            if(spriteScaleOverride.x > -1 && spriteScaleOverride.y > -1)
                size.set(spriteScaleOverride);

            pos.sub(size.cpy().scl(.5f));
            sprite.setPosition(pos.x, pos.y);
            sprite.setRotation(transform.getRotation());
            sprite.setSize(size.x, size.y);
        }
        super.Update(delta);
    }

}