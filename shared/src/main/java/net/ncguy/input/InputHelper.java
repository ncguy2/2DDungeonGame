package net.ncguy.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;

public class InputHelper {

    public static InputMultiplexer GetProcessor() {
        InputProcessor current = Gdx.input.getInputProcessor();
        InputMultiplexer multiplexer;

        if(current instanceof InputMultiplexer)
            multiplexer = (InputMultiplexer) current;
        else {
            multiplexer = new InputMultiplexer();
            if(current != null)
                multiplexer.addProcessor(current);
            Gdx.input.setInputProcessor(multiplexer);
        }

        return multiplexer;
    }

    public static void AddProcessors(InputProcessor... processors) {
        InputMultiplexer multiplexer = GetProcessor();
        for (InputProcessor processor : processors)
            multiplexer.addProcessor(processor);
    }

    public static void RemoveProcessors(InputProcessor... processors) {
        InputMultiplexer multiplexer = GetProcessor();
        for (InputProcessor processor : processors)
            multiplexer.removeProcessor(processor);
    }

}
