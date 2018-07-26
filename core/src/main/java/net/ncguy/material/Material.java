package net.ncguy.material;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import net.ncguy.profile.ProfilerHost;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class Material extends EmptyMaterial {

    protected transient MaterialBindingContext bindingContext;

    protected List<MaterialAttribute> attributes;

    public Material(String name) {
        super(name);
        this.bindingContext = new MaterialBindingContext(2);
        this.attributes = new ArrayList<>();
    }

    public void Bind(ShaderProgram shader) {
        ProfilerHost.Start("Material::Bind");
        this.bindingContext.Reset();
        AttributeType.Handler.Bind(shader, this);
        ProfilerHost.End("Material::Bind");
    }

    public void Add(MaterialAttribute attr) {
        attributes.add(attr);
    }

    public boolean Has(AttributeType attrType) {
        return attributes.stream()
                .anyMatch(t -> t.type.equals(attrType));
    }

    public Optional<MaterialAttribute> Get(AttributeType attrType) {
        return attributes.stream().filter(t -> t.type.equals(attrType)).findFirst();
    }

    public void Using(AttributeType attrType, Consumer<MaterialAttribute> task) {
        attributes.stream().filter(t -> t.type.equals(attrType)).findFirst().ifPresent(task);
    }
}
