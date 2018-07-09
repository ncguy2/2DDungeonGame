package net.ncguy.cmd;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class CommandExecutor {

    public List<Command> Commands() {
        List<Command> cmds = new ArrayList<>();
        Commands(cmds);
        return cmds;
    }
    public abstract void Commands(final List<Command> cmds);

    public void Parse(String cmd) {
        List<String> sections = new ArrayList<>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(cmd);
        while(m.find())
            sections.add(m.group(1));

        final String name = sections.remove(0);

        Commands().stream().filter(c -> c.name.equalsIgnoreCase(name)).findFirst().ifPresent(c -> {
            c.Invoke(sections.toArray(new String[0]));
        });
    }

}
