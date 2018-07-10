package net.ncguy.script;

import javax.script.ScriptContext;

public class ScriptObject {

    public Object context;
    public String source;

    public ScriptObject(String source) {
        this.context = this;
        this.source = source;
    }

    public void Parse() {
        ScriptHost.Invoke(this.context, this.source, this::ReadFromContext);
    }

    public void ReadFromContext(ScriptContext ctx) {}



}
