package net.ncguy.script;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import net.ncguy.profile.ProfilerHost;

import javax.script.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ScriptHost {

    private static ScriptHost instance;
    public static ScriptHost instance() {
        if (instance == null)
            instance = new ScriptHost();
        return instance;
    }

    protected ScriptEngine engine;
    protected Map<Object, ScriptContext> contexts;
    protected Map<String, Object> globalBindings;
    protected ScriptContext originalContext;

    private ScriptHost() {
        ProfilerHost.Start("Script host initialization");
        engine = new ScriptEngineManager().getEngineByName("nashorn");
        originalContext = engine.getContext();
        contexts = new HashMap<>();
        globalBindings = new HashMap<>();
        ProfilerHost.End("Script host initialization");
    }

    public void _AddGlobalBinding(String key, Object value) {
        globalBindings.put(key, value);
    }

    public ScriptContext _GetContext(Object context) {
        if(context == null)
            return originalContext;

        if(contexts.containsKey(context))
            return contexts.get(context);

        SimpleScriptContext ctx = new SimpleScriptContext();
        Bindings bindings = engine.createBindings();
        bindings.put("CONTEXT", context);
        ctx.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
        contexts.put(context, ctx);
        return ctx;
    }

    public void _WithContext(Object context, Consumer<ScriptContext> task) {
        ScriptContext ctx = _GetContext(context);
        task.accept(ctx);
    }
    
    public void _UseContext(Object context, BiConsumer<ScriptEngine, ScriptContext> task) {
        ScriptContext ctx = _GetContext(context);
        engine.setContext(ctx);
        ctx.getBindings(ScriptContext.ENGINE_SCOPE).putAll(globalBindings);
        task.accept(engine, ctx);
        engine.setContext(originalContext);
    }
    
    public void _Invoke(Object context, String script) {
        _Invoke(context, script, null);
    }
    public void _Invoke(Object context, String script, Consumer<ScriptContext> afterEval) {
        _UseContext(context, (engine, ctx) -> {
            try {
//                Invocable invocable = (Invocable) engine;
                engine.eval(script);
                if(afterEval != null)
                    afterEval.accept(ctx);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    public static ScriptContext GetContext(Object context) {
        return instance()._GetContext(context);
    }

    public static void WithContext(Object context, Consumer<ScriptContext> task) {
        instance()._WithContext(context, task);
    }
    public static void UseContext(Object context, BiConsumer<ScriptEngine, ScriptContext> task) {
        instance()._UseContext(context, task);
    }
    public static void Invoke(Object context, String script) {
        instance()._Invoke(context, script);
    }
    public static void Invoke(Object context, String script, Consumer<ScriptContext> afterEval) {
        instance()._Invoke(context, script, afterEval);
    }

    public static void AddGlobalBinding(String key, Object value) {
        instance()._AddGlobalBinding(key, value);
    }

    public static ScriptObjectMirror LoadObjectMirror(Bindings bindings, String id, Predicate<ScriptObjectMirror> filter) {
        if(bindings.containsKey(id)) {
            Object o = bindings.get(id);
            if(o instanceof ScriptObjectMirror) {
                ScriptObjectMirror mirror = (ScriptObjectMirror) o;
                if(filter.test(mirror))
                    return mirror;
            }
        }
        return null;
    }

    public static ScriptObjectMirror LoadFunctionMirror(Bindings bindings, String id) {
        return LoadObjectMirror(bindings, id, ScriptObjectMirror::isFunction);
    }

}
