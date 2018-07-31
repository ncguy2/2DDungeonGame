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
    @EntityProperty(Type = Boolean.class, Category = "Sprite", Description = "Does this sprite cast a shadow", Name = "Cast shadow")
    public boolean castShadow = true;
    @EntityProperty(Type = String.class, Category = "Sprite", Description = "Reference of the sprite to use", Name = "Sprite reference")
    public String spriteRef;
    @EntityProperty(Type = Vector2.class, Category = "Sprite", Description = "The override for the sprite size, ignored if either axis is -1", Name = "Sprite scale override")
    public final Vector2 spriteScaleOverride = new Vector2(-1, -1);
    protected transient boolean isBuildingSprite = false;

    public SpriteComponent() {
        this("Unnamed Scene component");
    }

    public SpriteComponent(String name) {
        super(name);
        spriteRef = "";
    }

    public void Build() {
        Texture texture = new Texture(Gdx.files.internal(spriteRef));
        sprite = new Sprite(texture);
        sprite.setSize(1, 1);
    }

    @Override
    public void Update(float delta) {
        if(sprite == null) {
            Build();
        }else {
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

    @Override
    public void Render(SpriteBatch batch) {
        if(sprite != null)
            sprite.draw(batch);
    }

    @Override
    public void RenderShadow(SpriteBatch batch) {
        if(castShadow)
            super.RenderShadow(batch);
    }
}
