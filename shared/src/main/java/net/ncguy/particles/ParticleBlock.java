package net.ncguy.particles;

import java.util.UUID;

public class ParticleBlock {

    public transient String methodName;

    public String name;
    public Type type;
    public String datumKey;
    public String[] uniforms;
    public String[] fragment;

    public String DatumKey() {
        return datumKey;
    }

    public String Uniforms() {
        StringBuilder s = new StringBuilder();
        for (String uniform : uniforms)
            s.append(uniform).append(";\n");
        return s.toString();
    }

    public String MethodSignature() {
        return "void " + MethodName() + "(inout ParticleData " + DatumKey() + ")";
    }

    public String MethodName() {
        if (methodName == null)
            methodName = name.replace(" ", "") + "_" + UUID.randomUUID()
                .toString()
                .replace("-", "");

        return methodName;
    }

    public String Fragment() {
        return Fragment("");
    }
    public String Fragment(String linePrefix) {
        StringBuilder s = new StringBuilder();
        for (String line : fragment)
            s.append(linePrefix).append(line).append("\n");
        String s1 = s.toString();
        String substring = s1.substring(0, s1.lastIndexOf("\n"));
        return substring;
    }

    public static enum Type {
        Spawn,
        Update
    }

    @Override
    public String toString() {
        return name;
    }
}
