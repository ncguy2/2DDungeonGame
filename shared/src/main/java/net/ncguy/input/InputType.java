package net.ncguy.input;

import com.badlogic.gdx.Gdx;

import java.util.function.Function;

public enum InputType {
    Keyboard(k -> Gdx.input.isKeyPressed(k)),
    Mouse(k -> Gdx.input.isButtonPressed(k)),
    ;

    final Function<Integer, Boolean> isPressedFunc;

    InputType(Function<Integer, Boolean> isPressedFunc) {
        this.isPressedFunc = isPressedFunc;
    }

    public boolean Test(int id) {
        return isPressedFunc.apply(id);
    }

}
