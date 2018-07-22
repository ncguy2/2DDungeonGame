package net.ncguy.util;

public class StringUtils {

    public static <T extends Enum<T>> String Name(T val) {
        if(val == null)
            return "";
        return val.name();
    }

    public static String GlEscape(String s) {
        return s.replace(".", "_").replace(" ", "_").replace("-", "_");
    }

    public static String ToDisplayCase(String s) {

        if(s == null || s.isEmpty())
            return "";

        if(s.length() == 1)
            return s.toUpperCase();

        s = s.replace("_", " ");
        StringBuilder sb = new StringBuilder();
        char[] chars = s.toCharArray();
        boolean nextCap = true;
        for(char c : chars) {
            if(nextCap) {
                sb.append(Character.toUpperCase(c));
                nextCap = false;
                continue;
            }
            sb.append(c);
            if(c == ' ')
                nextCap = true;
        }

        String s1 = sb.toString();
        sb = new StringBuilder();
        chars = s1.toCharArray();
        sb.append(Character.toUpperCase(chars[0]));
        for (int i = 1; i < chars.length - 1; i++) {
            sb.append(chars[i]);
            if(chars[i] != ' ' && Character.isUpperCase(chars[i + 1]))
                sb.append(" ");
        }
        sb.append(chars[chars.length - 1]);

        return sb.toString();
    }

    public static boolean IsNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

}
