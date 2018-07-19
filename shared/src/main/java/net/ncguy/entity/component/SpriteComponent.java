package net.ncguy.entity.component;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;

/**
 * Provides the reference used by the client renderer to identify which texture to use when rendering the component
 */
public class SpriteComponent extends RenderComponent {

    public transient Sprite sprite;
    public boolean castShadow = true;
    public String spriteRef;

    public SpriteComponent(String name) {
        super(name);
        spriteRef = "";
    }

    @Override
    public void Update(float delta) {
        if(sprite == null) {
            sprite = new Sprite(new Texture(Gdx.files.internal(spriteRef)));
        }else {
            Matrix3 transform = this.transform.WorldTransform();
            Vector2 vec = new Vector2();
            transform.getTranslation(vec);
            sprite.setPosition(vec.x, vec.y);
            sprite.setRotation(transform.getRotation());
            transform.getScale(vec);
            sprite.setSize(vec.x, vec.y);
        }
        super.Update(delta);
    }

    @Override
    public void Render(SpriteBatch batch) {
        batch.getShader().setUniformi("u_castShadow", castShadow ? 1 : 0);
        if(sprite != null)
            sprite.draw(batch);
        // TODO remove when possible
        batch.flush();
    }
}
