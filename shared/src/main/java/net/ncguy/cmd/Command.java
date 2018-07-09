package net.ncguy.cmd;

public abstract class Command {

    public String name;
    public String desc;

    public abstract void Invoke(String... args);

}
