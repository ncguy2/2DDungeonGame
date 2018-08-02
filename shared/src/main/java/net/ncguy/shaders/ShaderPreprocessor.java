package net.ncguy.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShaderPreprocessor {

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

            if(line.startsWith("#pragma"))
                HandlePragma(sb, path, line);
            else sb.append(line).append("\n");
        }
    }

    public static void HandlePragma(StringBuilder sb, String currentPath, String line) {
        String cmd = line.substring("#pragma ".length());
        if(cmd.startsWith("include")) {
            Pattern p = Pattern.compile("\"([^\"]*)\"");
            Matcher m = p.matcher(line);
            String includePath = null;
            if(m.find())
                includePath = m.group(1);
            if(includePath != null)
                Include(sb, currentPath, includePath);
        }
    }


    public static void Include(StringBuilder sb, String currentPath, String includePath) {
        Include(sb, currentPath, includePath, new HashMap<>());
    }
    public static void Include(StringBuilder sb, String currentPath, String includePath, HashMap<String, String> macroParams) {
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

}
