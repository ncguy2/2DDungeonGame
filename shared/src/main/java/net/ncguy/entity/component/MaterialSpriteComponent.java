package net.ncguy.entity.component;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import net.ncguy.material.EmptyMaterial;
import net.ncguy.material.IMaterialResolver;

import java.util.Optional;

public class MaterialSpriteComponent extends SpriteComponent {

    public static IMaterialResolver resolver;

    public String materialRef;

    public String MaterialRef() {
        if(materialRef == null || materialRef.isEmpty())
            return "Default";
        return materialRef;
    }

    public MaterialSpriteComponent(String name) {
        super(name);
    }

    public void Resolve(SpriteBatch batch) {
        if(resolver == null)
            return;

        Optional<EmptyMaterial> resolve = resolver.Resolve(materialRef);
        resolve.ifPresent(mtl -> mtl.Bind(batch.getShader()));
    }

}
