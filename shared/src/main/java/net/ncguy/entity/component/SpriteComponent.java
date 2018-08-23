package net.ncguy.entity.component;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import net.ncguy.assets.TextureResolver;
import net.ncguy.construct.CTSprite;
import net.ncguy.construct.ConnectedBuilder;

/**
 * Provides the reference used by the client renderer to identify which texture to use when rendering the component
 */
public class SpriteComponent extends RenderComponent {

    public transient Sprite sprite;
    public transient CTSprite connectedSprite;
    public transient static ConnectedBuilder ctBuilder = new ConnectedBuilder();

    @EntityProperty(Type = Boolean.class, Category = "Sprite", Description = "Does this sprite cast a shadow", Name = "Cast shadow")
    public boolean castShadow = true;

    @EntityProperty(Type = Boolean.class, Category = "Sprite", Description = "Should this sprite use a connected texture. Does not support rotations", Name = "Connected")
    public boolean connected = false;

    public transient boolean[][] worldMap;
    public transient int worldX;
    public transient int worldY;

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
        if(isBuildingSprite)
            return;

        isBuildingSprite = true;
        TextureResolver.GetTextureAsync(spriteRef, tex -> {
            isBuildingSprite = false;
            if(connected) {
                connectedSprite = ctBuilder.build(tex, worldX, worldY, worldMap);
                connectedSprite.setSize(1, 1);
                return;
            }
            sprite = new Sprite(tex);
            sprite.setSize(1, 1);
        });
    }

    public boolean IsNull() {
        if(connected)
            return connectedSprite == null;
        return sprite == null;
    }

    @Override
    public void Update(float delta) {
        if(IsNull()) {
            Build();
        }else {
            Matrix3 transform = this.transform.Predict(delta).WorldTransform();
            Vector2 pos = new Vector2();
            Vector2 size = new Vector2();
            transform.getTranslation(pos);
            transform.getScale(size);

            if(spriteScaleOverride.x > -1 && spriteScaleOverride.y > -1)
                size.set(spriteScaleOverride);

            pos.sub(size.cpy().scl(.5f));

            if(connected) {
                connectedSprite.setPosition(pos.x, pos.y);
//                connectedSprite.setRotation(transform.getRotation());
                connectedSprite.setSize(size.x, size.y);
            }else {
                float rotation = transform.getRotation();
                sprite.setOrigin(size.x * .5f, size.y * .5f);
                sprite.setPosition(pos.x, pos.y);
                sprite.setRotation(rotation);
                sprite.setSize(size.x, size.y);
            }
        }
        super.Update(delta);
    }

    @Override
    public void Render(SpriteBatch batch) {
        batch.getShader().setUniformi("u_castShadow", castShadow ? 1 : 0);
        if(connected) {
            if(connectedSprite != null)
                connectedSprite.draw(batch);
        }else {
            if (sprite != null)
                sprite.draw(batch);
        }
        batch.flush();
    }

    @Override
    public void RenderShadow(SpriteBatch batch) {
        if(castShadow)
            super.RenderShadow(batch);
    }

    public Vector2 GetWorldSize() {
        Vector2 size = new Vector2();
        transform.WorldTransform().getScale(size);

        if(spriteScaleOverride.x > -1 && spriteScaleOverride.y > -1)
            size.set(spriteScaleOverride);

        return size;
    }

    public Texture GetTexture() {

        if(IsNull())
            return null;

        if(connected)
            return connectedSprite.getTexture();
        return sprite.getTexture();
    }

}
