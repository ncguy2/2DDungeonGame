package net.ncguy.material;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import net.ncguy.entity.component.EntityProperty;
import net.ncguy.entity.component.FieldPropertyDescriptorLite;
import net.ncguy.entity.component.IPropertyProvider;

import java.util.Collection;

public class ColourAttribute extends MaterialAttribute<ColourAttribute.ColourType> implements IPropertyProvider {

    @EntityProperty(Type = Color.class, Name = "Colour", Description = "Colour value for this attribute", Category = "Material|Colour")
    public final Color colour;

    public ColourAttribute(ColourType colourType, Color colour) {
        super(colourType);
        this.colour = colour;
    }

    @Override
    public void Init() {

    }

    @Override
    public void BindToShader(MaterialBindingContext context, ShaderProgram shader) {
        shader.setUniformf(type.UniformName(), colour);
    }

    @Override
    public void Provide(Collection<FieldPropertyDescriptorLite> descriptors) {
        FieldPropertyDescriptorLite.OfClass(this).forEach(d -> {
            d.name = type.name() + " " + d.name;
            descriptors.add(d);
        });
    }

    public enum ColourType implements AttributeType {
        Diffuse("u_diffuseColour"),
        Emissive("u_emissiveColour"),
        ;

        final String uName;

        ColourType(String uName) {
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
            program.setUniformf(UniformName(), Color.WHITE);
        }
    }

}
