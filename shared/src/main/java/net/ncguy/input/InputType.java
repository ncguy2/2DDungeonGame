package net.ncguy.input;

import com.badlogic.gdx.Gdx;

import java.util.function.Function;

public enum InputType {
    Keyboard(Gdx.input::isKeyPressed),
    Mouse(Gdx.input::isButtonPressed),
    Scroll(ScrollInputHelper::Resolve),
    ;

    final Function<Integer, Boolean> isPressedFunc;

    InputType(Function<Integer, Boolean> isPressedFunc) {
        this.isPressedFunc = isPressedFunc;
    }

    public boolean Test(int id) {
        return isPressedFunc.apply(id);
    }

}
