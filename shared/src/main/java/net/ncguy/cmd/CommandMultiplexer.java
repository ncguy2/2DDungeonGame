package net.ncguy.cmd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandMultiplexer extends CommandExecutor {

    List<CommandExecutor> executors;

    public CommandMultiplexer() {
        executors = new ArrayList<>();
    }

    public CommandMultiplexer(CommandExecutor... executors) {
        this.executors = Arrays.asList(executors);
    }

    public void AddExecutor(CommandExecutor executor) {
        executors.add(executor);
    }

    public void RemoveExecutor(CommandExecutor executor) {
        executors.remove(executor);
    }

    @Override
    public void Commands(final List<Command> cmds) {
        executors.forEach(executor -> executor.Commands(cmds));
    }
}
