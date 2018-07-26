package net.ncguy.material;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import net.ncguy.profile.ProfilerHost;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface AttributeType {

    String UniformName();
    int Mask();
    void Reset(ShaderProgram program);

    default int Offset() {
        return Handler.attrTypes.indexOf(getClass());
    }

    class Handler {

        public static void Register(AttributeType type) {
            attrTypes.add(type);
        }

        public static List<AttributeType> attrTypes;
        static {
            attrTypes = new ArrayList<>();
        }

        public static void Bind(ShaderProgram program, Material material) {
            ProfilerHost.Start("Handler::Bind");
            for (AttributeType attrType : attrTypes) {
                ProfilerHost.Start("Handler::Bind [" + attrType.getClass().getSimpleName() + ": " + attrType.UniformName() + "]");
                ProfilerHost.Start("Fetch attribute");
                Optional<MaterialAttribute> attr = material.Get(attrType);
                ProfilerHost.End("Fetch attribute");

                ProfilerHost.Start("Conditional uniform population");
                if(attr.isPresent())
                    attr.get()._BindToShader(material.bindingContext, program);
                else attrType.Reset(program);
                ProfilerHost.End("Conditional uniform population");
                ProfilerHost.End("Handler::Bind");
            }
            ProfilerHost.End("Handler::Bind");
        }

    }

}
