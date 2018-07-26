package net.ncguy.assets;

import com.badlogic.gdx.graphics.Texture;

import java.util.function.Consumer;

public class TextureResolver {

    public static Texture GetTexture(String ref) {
        AssetHandler handler = AssetHandler.instance();
        if (!handler.IsLoaded(ref, Texture.class)) {
            handler.GetAsync(ref, Texture.class, t -> {});
            return Sprites.Default().getTexture();
        }
        return handler.Get(ref, Texture.class);
    }

    public static void GetTextureAsync(String ref, Consumer<Texture> task) {
        AssetHandler.instance().GetAsync(ref, Texture.class, task);
    }

}
