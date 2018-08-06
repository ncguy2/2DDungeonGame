package net.ncguy.input;

public class InputAction {
    public String name;
    public int id;
    public InputType type;

    public static InputAction Key(String name, int id) {
        return new InputAction(name, id, InputType.Keyboard);
    }
    public static InputAction Button(String name, int id) {
        return new InputAction(name, id, InputType.Mouse);
    }
    public static InputAction Scroll(String name, ScrollInputHelper.ScrollType type) {
        return new InputAction(name, type.ordinal(), InputType.Scroll);
    }

    public InputAction(String name, int id, InputType type) {
        this.name = name;
        this.id = id;
        this.type = type;
    }

    public boolean Test() {
        return type.Test(id);
    }
}
