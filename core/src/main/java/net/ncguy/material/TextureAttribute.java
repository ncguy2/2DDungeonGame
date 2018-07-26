package net.ncguy.material;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import net.ncguy.assets.TextureResolver;

public class TextureAttribute extends MaterialAttribute<TextureAttribute.TextureType> {

    String textureRef;
    transient Texture texture;

    public TextureAttribute(TextureAttribute.TextureType type) {
        super(type);
    }

    public boolean HasTexture() {
        return texture != null;
    }

    public void SetTexture(Texture tex) {
        this.texture = tex;
    }

    @Override
    public void Init() {
        TextureResolver.GetTextureAsync(this.textureRef, this::SetTexture);
    }

    @Override
    public void BindToShader(MaterialBindingContext context, ShaderProgram shader) {
        if(!HasTexture())
            return;

        int unit = context.Bind(texture);
        shader.setUniformi(type.UniformName(), unit);
    }

    public enum TextureType implements AttributeType {
        Emissive("u_emissiveTexture"),
        ;
        final String uName;


        TextureType(String uName) {
            this.uName = uName;
            Handler.Register(this);
        }

        @Override
        public String UniformName() {
            return uName;
        }

        @Override
        public int Mask() {
            return 1 << (ordinal() + Offset());
        }

        @Override
        public void Reset(ShaderProgram program) {

        }
    }


}
