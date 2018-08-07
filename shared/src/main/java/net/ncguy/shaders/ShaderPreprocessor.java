package net.ncguy.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShaderPreprocessor {

    public static ThreadLocal<Set<ShaderInjectionBlock>> injectionBlocks = ThreadLocal.withInitial(HashSet::new);
    public static Optional<ShaderInjectionBlock> GetBlock(String blockKey) {
        return injectionBlocks.get().stream().filter(b -> b.name.equalsIgnoreCase(blockKey)).findFirst();
    }

    public static void Clear() {
        injectionBlocks.get().clear();
    }
    public static void RemoveBlock(String name) {
        GetBlock(name).ifPresent(ShaderPreprocessor::RemoveBlock);
    }
    public static void RemoveBlock(ShaderInjectionBlock block) {
        injectionBlocks.get().remove(block);
    }
    public static void SetBlock(ShaderInjectionBlock block) {
        RemoveBlock(block.name);
        injectionBlocks.get().add(block);
    }

    public static String ReadShader(FileHandle handle) {
        return ReadShader(handle, null, new HashMap<>());
    }

    public static String ReadShader(FileHandle handle, Map<String, String> macroParams) {
        return ReadShader(handle, null, macroParams);
    }

    private static String ReadShader(FileHandle handle, String outputFile, Map<String, String> macroParams) {
        StringBuilder sb = new StringBuilder();
        try {
            LoadShader_Impl(sb, handle, macroParams);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String s = sb.toString();

        if(outputFile != null && !outputFile.isEmpty()) {
            try {
                Files.write(new File(outputFile).toPath(), s.getBytes());
            }catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        return s;
    }

    public static void LoadShader_Impl(StringBuilder sb, FileHandle handle, Map<String, String> macroParams) throws IOException {

        String path = handle.parent().path() + "/";

        BufferedReader reader = new BufferedReader(new InputStreamReader(handle.read()));
        String line;

        while((line = reader.readLine()) != null) {
            for (Map.Entry<String, String> entry : macroParams.entrySet())
                line = line.replace(entry.getKey(), entry.getValue());

            if(line.trim().startsWith("#pragma"))
                HandlePragma(sb, path, line.trim(), macroParams);
            else sb.append(line).append("\n");
        }
    }

    public static void HandlePragma(StringBuilder sb, String currentPath, String line, Map<String, String> macroParams) {
        String cmd = line.substring("#pragma ".length());
        Pattern p = Pattern.compile("\"([^\"]*)\"");
        Matcher m = p.matcher(line);
        String arg = null;
        if(m.find())
            arg = m.group(1);
        if (arg == null)
            return;

        if (cmd.startsWith("include"))
            Include(sb, currentPath, arg, macroParams);
        else if (cmd.startsWith("inject"))
            Inject(sb, currentPath, arg, macroParams);

    }

    public static void Inject(StringBuilder sb, String currentPath, String injectKey) {
        Inject(sb, injectKey, currentPath, new HashMap<>());
    }
    public static void Inject(StringBuilder sb, String currentPath, String injectKey, Map<String, String> macroParams) {
        GetBlock(injectKey).map(ShaderInjectionBlock::Get).ifPresent(b -> {

            sb.append("// ").append(injectKey).append("\n");
            String[] lines = b.split("\n");
            for (String line : lines) {
                if(line.trim().startsWith("#pragma"))
                    HandlePragma(sb, currentPath, line, macroParams);
                else sb.append(line).append("\n");
            }
        });
    }

    public static void Include(StringBuilder sb, String currentPath, String includePath) {
        Include(sb, currentPath, includePath, new HashMap<>());
    }
    public static void Include(StringBuilder sb, String currentPath, String includePath, Map<String, String> macroParams) {
        String path = includePath;
        if(!path.startsWith("/")) {
            path = currentPath + path;
        }else
            path = path.substring(1);
        FileHandle handle = Gdx.files.internal(path);
        sb.append("// ").append(path).append("\n");
        if(handle.exists()) {
            try {
                LoadShader_Impl(sb, handle, macroParams);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            sb.append("// Not found\n");
        }
    }

    public static class ShaderInjectionBlock {
        public final String name;
        public Supplier<String> fragmentSupplier;

        public ShaderInjectionBlock(String name) {
            this.name = name;
        }

        public ShaderInjectionBlock(String name, Supplier<String> fragmentSupplier) {
            this.name = name;
            this.fragmentSupplier = fragmentSupplier;
        }

        public String Get() {
            if(fragmentSupplier != null)
                return fragmentSupplier.get();
            return "// No fragment supplier provided";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ShaderInjectionBlock that = (ShaderInjectionBlock) o;
            return Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }

}
