package net.ncguy.input;

import com.badlogic.gdx.InputAdapter;

public class ScrollInputHelper extends InputAdapter {

    private static ScrollInputHelper instance;
    public static ScrollInputHelper instance() {
        if (instance == null)
            instance = new ScrollInputHelper();
        return instance;
    }

    protected int scrollAmt;

    private ScrollInputHelper() {
        InputHelper.AddProcessors(this);
    }

    public void Update(float delta) {
        scrollAmt = 0;
    }

    public int GetScrollAmount() {
        return scrollAmt;
    }

    public static boolean Resolve(int typeId) {
        return instance()._Resolve(ScrollType.values()[typeId]);
    }
    public boolean _Resolve(ScrollType type) {
        switch(type) {
            case ANY:
                return scrollAmt != 0;
            case UP:
                return scrollAmt > 0;
            case DOWN:
                return scrollAmt < 0;
        }
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        scrollAmt = amount;
        return super.scrolled(amount);
    }

    public static enum ScrollType {
        ANY,    // Any scroll amount
        UP,     // Positive scroll amount
        DOWN,   // Negative scroll amount
    }

}
