package net.ncguy.particles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import net.ncguy.buffer.ShaderStorageBufferObject;
import net.ncguy.shaders.ComputeShader;
import net.ncguy.shaders.ShaderPreprocessor;
import net.ncguy.util.ReloadableComputeShader;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ParticleShader extends ReloadableComputeShader {

    protected int location;
    protected transient ShaderStorageBufferObject particleBuffer;

    protected List<ParticleBlock> blocks;

    public static Map<ParticleBlock.Type, ParticleShader> FromProfile(ParticleProfile profile, boolean register) {
        HashMap<ParticleBlock.Type, ParticleShader> map = new HashMap<>();

        FileHandle handle = Gdx.files.internal("particles/compute/framework.comp");
        map.put(ParticleBlock.Type.Spawn, new ParticleShader(profile.name + ": Spawn", handle, new HashMap<>(), register));
        map.put(ParticleBlock.Type.Update, new ParticleShader(profile.name + ": Update", handle, new HashMap<>(), register));

        for (String block : profile.blocks)
            ParticleManager.instance()
                    .GetParticleBlock(block)
                    .ifPresent(b -> {
                        ParticleShader ps = map.get(b.type);
                        if(ps != null)
                            ps.AddBlock(b);
                    });

        if(register)
            map.values().forEach(ParticleShader::ReloadImmediate);

        return map;
    }

    public ParticleShader(String name, FileHandle handle) {
        this(name, handle, new HashMap<>());
    }

    public ParticleShader(String name, FileHandle handle, Map<String, String> macroParams) {
        this(name, handle, macroParams, true);
    }
    public ParticleShader(String name, FileHandle handle, Map<String, String> macroParams, boolean register) {
        super(name, handle, macroParams, register);
    }

    public void AddBlock(ParticleBlock block) {
        GetBlocks().add(block);
    }

    public List<ParticleBlock> GetBlocks() {
        if (blocks == null)
            blocks = new ArrayList<>();
        return blocks;
    }

    int GetTypeScore(String type) {

        if(type.equalsIgnoreCase("float"))
            return 1;
        if(type.equalsIgnoreCase("vec2"))
            return 2;
        if(type.equalsIgnoreCase("vec3"))
            return 3;
        if(type.equalsIgnoreCase("vec4"))
            return 4;

        return 0;
    }

    boolean HasDefault(String u) {
        return u.contains("=");
    }

    public String GetUniforms() {
        List<String> allUniforms = new ArrayList<>();
        for (ParticleBlock block : GetBlocks()) {
            Collections.addAll(allUniforms, block.uniforms);
        }

        Map<String, List<String>> collect = allUniforms.stream()
                .collect(Collectors.groupingBy(s -> s.split(" ")[1]));

        allUniforms.clear();

        collect.forEach((name, entries) -> {
            if(entries.size() == 1) {
                allUniforms.add(entries.get(0));
                return;
            }

            System.out.println("Duplicate uniform detected: ");
            for (String entry : entries)
                System.out.println("\t" + entry);

            entries.sort((a, b) -> {
                String aType = a.split(" ")[0];
                String bType = b.split(" ")[0];
                int compare = Integer.compare(GetTypeScore(aType), GetTypeScore(bType));
                if(compare == 0)
                    return Boolean.compare(HasDefault(a), HasDefault(b));
                return compare;
            });

            String bestFit = entries.get(entries.size() - 1);
            System.out.println();
            System.out.println("\tUsing " + bestFit);
            allUniforms.add(bestFit);
        });

        String join = String.join("\n", allUniforms.stream().map(s -> {
            if(s.endsWith(";"))
                return s;
            return s + ";";
        }).map(s -> "uniform " + s).collect(Collectors.toList()));
        return join;
    }

    public String GetDeclarations() {
        StringBuilder sb = new StringBuilder();
        for (ParticleBlock block : GetBlocks())
            sb.append(block.MethodSignature())
                    .append(";\n");
        return sb.toString();
    }

    public String GetInvocations() {
        StringBuilder sb = new StringBuilder();
        for (ParticleBlock block : GetBlocks())
            sb.append(block.MethodName())
                    .append("(")
                    .append(block.DatumKey())
                    .append(");\n");
        return sb.toString();
    }

    public String GetDefinitions() {
        StringBuilder sb = new StringBuilder();
        for (ParticleBlock block : GetBlocks()) {
            sb.append(block.MethodSignature())
                    .append(" {\n")
                    .append(block.Fragment("\t"))
                    .append("\n}\n");
        }
        return sb.toString();
    }

    public String GetScript() {
        ShaderPreprocessor.SetBlock(new ShaderPreprocessor.ShaderInjectionBlock("uniforms", ParticleShader.this::GetUniforms));
        ShaderPreprocessor.SetBlock(new ShaderPreprocessor.ShaderInjectionBlock("declarations", ParticleShader.this::GetDeclarations));
        ShaderPreprocessor.SetBlock(new ShaderPreprocessor.ShaderInjectionBlock("invocations", ParticleShader.this::GetInvocations));
        ShaderPreprocessor.SetBlock(new ShaderPreprocessor.ShaderInjectionBlock("definitions", ParticleShader.this::GetDefinitions));
        return ShaderPreprocessor.ReadShader(handle, macroParams);
    }

    @Override
    public ComputeShader Create() {
        return new ComputeShader(handle, macroParams) {
            @Override
            public void Compile() {
                ShaderPreprocessor.SetBlock(new ShaderPreprocessor.ShaderInjectionBlock("uniforms", ParticleShader.this::GetUniforms));
                ShaderPreprocessor.SetBlock(new ShaderPreprocessor.ShaderInjectionBlock("declarations", ParticleShader.this::GetDeclarations));
                ShaderPreprocessor.SetBlock(new ShaderPreprocessor.ShaderInjectionBlock("invocations", ParticleShader.this::GetInvocations));
                ShaderPreprocessor.SetBlock(new ShaderPreprocessor.ShaderInjectionBlock("definitions", ParticleShader.this::GetDefinitions));
                super.Compile();
            }
        };
    }

    public void SetParticleBuffer(int location, ShaderStorageBufferObject buffer) {
        this.location = location;
        this.particleBuffer = buffer;
    }

    public void Dispatch() {
        Dispatch(1);
    }

    public void Dispatch(int x) {
        Dispatch(x, 1);
    }

    public void Dispatch(int x, int y) {
        Dispatch(x, y, 1);
    }

    public void Dispatch(int x, int y, int z) {
        if (program != null)
            program.Dispatch(x, y, z);
    }

    public void SetUniform(String uniform, Consumer<Integer> setter) {
        if (program != null)
            program.SetUniform(uniform, setter);
    }

}
