package net.ncguy.script;

import com.badlogic.gdx.math.Vector2;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import net.ncguy.entity.Entity;

import javax.script.Bindings;
import javax.script.ScriptContext;
import java.util.Optional;

public class SpawnerScriptObject extends ScriptObject {

    protected ScriptObjectMirror Function_Create;

    public SpawnerScriptObject(String source) {
        super(source);
    }

    @Override
    public void ReadFromContext(ScriptContext ctx) {
        super.ReadFromContext(ctx);
        Bindings bindings = ctx.getBindings(ScriptContext.ENGINE_SCOPE);
        Function_Create = ScriptHost.LoadFunctionMirror(bindings, "Create");
    }

    public Optional<Entity> InvokeCreate(Object thiz, String worldName, Vector2 position) {
        if(Function_Create != null) {
            Object obj = Function_Create.call(thiz, worldName, position.x, position.y);
            if(obj == null) {
                System.out.println(this + " produced a null object");
                return Optional.empty();
            }
            if(obj instanceof Entity)
                return Optional.of((Entity) obj);
            System.out.println(this + " produced an object of an incompatible type");
        }
        return Optional.empty();
    }

}
