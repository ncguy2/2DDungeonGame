package net.ncguy.ui.dnd;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

public class FunctionalTarget extends DragAndDrop.Target {

    public final DnDManager.TagCondition condition;
    public FunctionalDrag drag;
    public FunctionalDrop drop;

    public FunctionalTarget(Actor actor, DnDManager.TagCondition condition) {
        super(actor);
        this.condition = condition;
    }

    @Override
    public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
        boolean valid = condition.Pass(DnDManager.instance().GetSourceTags(source));
        if(drag != null)
            valid &= drag.Drag(source, payload, x, y, pointer);
        return valid;
    }

    @Override
    public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
        if(drop != null)
            drop.Drop(source, payload, x, y, pointer);
    }

    @FunctionalInterface
    public interface FunctionalDrag {
        boolean Drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer);
    }

    @FunctionalInterface
    public interface FunctionalDrop {
        void Drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer);
    }

}
