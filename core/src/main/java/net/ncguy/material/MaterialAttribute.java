package net.ncguy.material;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import net.ncguy.profile.ProfilerHost;

public abstract class MaterialAttribute<TYPE extends AttributeType> {

    public TYPE type;

    public MaterialAttribute(TYPE type) {
        this.type = type;
    }

    public abstract void Init();
    public abstract void BindToShader(MaterialBindingContext context, ShaderProgram shader);

    // Internal
    public void _BindToShader(MaterialBindingContext context, ShaderProgram shader) {
        ProfilerHost.Start(getClass().getSimpleName() + "::_BindToShader");
        context.subroutineId |= type.Mask();
        BindToShader(context, shader);
        ProfilerHost.End("MaterialAttribute::_BindToShader");
    }

}
