package net.ncguy.tools.debug.view.component.editors;

import com.badlogic.gdx.graphics.Color;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ColorPicker;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.AbstractPropertyEditor;

public class ColourEditor extends AbstractPropertyEditor<Color, ColorPicker> {

    SimpleObjectProperty<Color> val;

    public ColourEditor(PropertySheet.Item property) {
        this(property, new ColorPicker());
        Object value = property.getValue();
        if(value instanceof Color)
            setValue((Color) value);
    }

    public ColourEditor(PropertySheet.Item property, ColorPicker control) {
        super(property, control);
        Init();
    }

    public ColourEditor(PropertySheet.Item property, ColorPicker control, boolean readonly) {
        super(property, control, readonly);
        Init();
    }

    void Init() {
        getEditor().valueProperty().addListener((observable, oldValue, newValue) -> {
            val.set(new Color((float) newValue.getRed(), (float) newValue.getGreen(), (float) newValue.getBlue(), (float) newValue.getOpacity()));
        });
    }

    @Override
    protected ObservableValue<Color> getObservableValue() {
        if(val == null)
            val = new SimpleObjectProperty<>();
        return val;
    }

    @Override
    public void setValue(Color value) {
        getEditor().valueProperty().set(new javafx.scene.paint.Color(value.r, value.g, value.b, value.a));
    }
}
