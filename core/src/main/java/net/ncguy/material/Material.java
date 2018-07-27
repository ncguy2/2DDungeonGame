package net.ncguy.material;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import net.ncguy.entity.component.FieldPropertyDescriptorLite;
import net.ncguy.entity.component.IPropertyProvider;
import net.ncguy.profile.ProfilerHost;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class Material extends EmptyMaterial implements IPropertyProvider {

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
        return attributes.stream()
                .filter(t -> t.type.equals(attrType))
                .findFirst();
    }

    public void Using(AttributeType attrType, Consumer<MaterialAttribute> task) {
        attributes.stream()
                .filter(t -> t.type.equals(attrType))
                .findFirst()
                .ifPresent(task);
    }

    @Override
    public void Provide(Collection<FieldPropertyDescriptorLite> descriptors) {
        attributes.stream()
                .filter(a -> a instanceof IPropertyProvider)
                .map(a -> (IPropertyProvider) a)
                .forEach(a -> a.Provide(descriptors));
    }
}
