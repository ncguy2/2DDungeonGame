package net.ncguy.ability;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import net.ncguy.lib.foundation.utils.ExceptionUtils;
import net.ncguy.script.ScriptHost;
import net.ncguy.script.ScriptObject;

import javax.script.Bindings;
import javax.script.ScriptContext;

public class AbilityScript extends ScriptObject {

    public AbilityScript(String source) {
        super(source);
    }

    protected ScriptObjectMirror Function_OnUpdate;
    protected ScriptObjectMirror Function_OnActiveUpdate;
    protected ScriptObjectMirror Function_OnInactiveUpdate;

    protected ScriptObjectMirror Function_OnEnabled;
    protected ScriptObjectMirror Function_OnDisabled;

    @Override
    public void ReadFromContext(ScriptContext ctx) {
        super.ReadFromContext(ctx);
        Bindings bindings = ctx.getBindings(ScriptContext.ENGINE_SCOPE);
        Function_OnUpdate = ScriptHost.LoadFunctionMirror(bindings, "OnUpdate");
        Function_OnActiveUpdate = ScriptHost.LoadFunctionMirror(bindings, "OnActiveUpdate");
        Function_OnInactiveUpdate = ScriptHost.LoadFunctionMirror(bindings, "OnInactiveUpdate");
        Function_OnEnabled = ScriptHost.LoadFunctionMirror(bindings, "OnEnabled");
        Function_OnDisabled = ScriptHost.LoadFunctionMirror(bindings, "OnDisabled");
    }
    
    public void InvokeUpdate(Object thiz, float delta) {
        if(Function_OnUpdate != null)
            ExceptionUtils.ContainException(() -> Function_OnUpdate.call(thiz, delta));
    }

    public void InvokeActiveUpdate(Object thiz, float delta) {
        if(Function_OnActiveUpdate != null)
            ExceptionUtils.ContainException(() -> Function_OnActiveUpdate.call(thiz, delta));
    }

    public void InvokeInactiveUpdate(Object thiz, float delta) {
        if(Function_OnInactiveUpdate != null)
            ExceptionUtils.ContainException(() -> Function_OnInactiveUpdate.call(thiz, delta));
    }

    public void InvokeEnabled(Object thiz) {
        if(Function_OnEnabled != null)
            ExceptionUtils.ContainException(() -> Function_OnEnabled.call(thiz));
    }

    public void InvokeDisabled(Object thiz) {
        if(Function_OnDisabled != null)
            ExceptionUtils.ContainException(() -> Function_OnDisabled.call(thiz));
    }
    
}
