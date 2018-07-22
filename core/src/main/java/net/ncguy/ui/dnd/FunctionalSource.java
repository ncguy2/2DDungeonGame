package net.ncguy.ui.dnd;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

public class FunctionalSource extends DragAndDrop.Source {

    public FunctionalDragStart payloadFactory;

    public FunctionalSource(Actor actor) {
        super(actor);
    }

    @Override
    public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
        DragAndDrop.Payload payload = new DragAndDrop.Payload();
        if(payloadFactory != null)
            payloadFactory.PopulatePayload(payload, event, x, y, pointer);

        DnDManager.instance().DragStart(this);

        return payload;
    }

    @Override
    public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
        DnDManager.instance().DragStop();
    }

    @FunctionalInterface
    public interface FunctionalDragStart {
        void PopulatePayload(DragAndDrop.Payload payload, InputEvent event, float x, float y, int pointer);
    }


}
