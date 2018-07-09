package net.ncguy.input;

public class InputManager {

    private static InputManager instance;
    public static InputManager instance() {
        if (instance == null)
            instance = new InputManager();
        return instance;
    }

    private InputManager() {}

    public float _Scale(float value, InputAction action) {
        return value * _Query(action);
    }

    public float _Scale(float value, InputAxis axis) {
        return value * _Query(axis);
    }

    public float _Query(InputAction action) {
        if(action.Test())
            return 1;
        return 0;
    }

    public float _Query(InputAxis axis) {
        return axis.Resolve();
    }

    public boolean _IsPressed(InputAction action) {
        return action.Test();
    }

    public boolean _IsPressed(InputAxis axis) {
        return axis.Resolve() != 0;
    }

    public static float Scale(float value, InputAction action) {
        return instance()._Scale(value, action);
    }

    public static float Scale(float value, InputAxis axis) {
        return instance()._Scale(value, axis);
    }

    public static float Query(InputAction action) {
        return instance()._Query(action);
    }

    public static float Query(InputAxis axis) {
        return instance()._Query(axis);
    }

    public static boolean IsPressed(InputAction action) {
        return instance()._IsPressed(action);
    }

    public static boolean IsPressed(InputAxis axis) {
        return instance()._IsPressed(axis);
    }


}
