package net.ncguy.tools.debug.view.component.editors.nodes;

import javafx.beans.binding.NumberExpression;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.control.IndexRange;
import javafx.scene.control.TextField;

import java.math.BigInteger;

public class NumericField extends TextField {

    private final NumericField.NumericValidator<? extends Number> value;

    public NumericField(Class<? extends Number> cls) {

        if (cls == byte.class || cls == Byte.class || cls == short.class || cls == Short.class ||
                cls == int.class || cls == Integer.class || cls == long.class || cls == Long.class ||
                cls == BigInteger.class) {
            value = new NumericField.LongValidator(this);
        } else {
            value = new NumericField.DoubleValidator(this);
        }

        focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                value.setValue(value.toNumber(getText()));
            }
        });
    }

    public final Property<Number> valueProperty() {
        return value.GetProperty();
    }

    @Override
    public void replaceText(int start, int end, String text) {
        if (replaceValid(start, end, text)) {
            super.replaceText(start, end, text);
        }
    }

    @Override
    public void replaceSelection(String text) {
        IndexRange range = getSelection();
        if (replaceValid(range.getStart(), range.getEnd(), text)) {
            super.replaceSelection(text);
        }
    }

    private Boolean replaceValid(int start, int end, String fragment) {
        try {
            String newText = getText().substring(0, start) + fragment + getText().substring(end);
            if (newText.isEmpty()) return true;
            value.toNumber(newText);
            return true;
        } catch (Throwable ex) {
            return false;
        }
    }


    private interface NumericValidator<T extends Number> extends NumberExpression {
        void setValue(Number num);

        T toNumber(String s);

        Property<Number> GetProperty();
    }

    static class DoubleValidator extends SimpleDoubleProperty implements NumericField.NumericValidator<Double> {

        private NumericField field;

        public DoubleValidator(NumericField field) {
            super(field, "value", 0.0); //$NON-NLS-1$
            this.field = field;
            invalidated();
        }

        @Override
        protected void invalidated() {
            double d = get();
            String value = Double.toString(d);
            field.setText(value);
        }

        @Override
        public Double toNumber(String s) {
            if (s == null || s.trim()
                    .isEmpty()) return 0d;
            String d = s.trim();
            if (d.endsWith("f") || d.endsWith("d") || d.endsWith("F") || d.endsWith("D")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                throw new NumberFormatException("There should be no alpha symbols"); //$NON-NLS-1$
            }
            return new Double(d);
        }

        @Override
        public Property<Number> GetProperty() {
            return this;
        }

    }


    static class LongValidator extends SimpleLongProperty implements NumericField.NumericValidator<Long> {

        private NumericField field;

        public LongValidator(NumericField field) {
            super(field, "value", 0L); //$NON-NLS-1$
            this.field = field;
            invalidated();
        }

        @Override
        protected void invalidated() {
            long i = get();
            String value = Long.toString(i);
            field.setText(value);
        }

        @Override
        public Long toNumber(String s) {
            if (s == null || s.trim()
                    .isEmpty()) return 0L;
            String d = s.trim();
            return new Long(d);
        }

        @Override
        public Property<Number> GetProperty() {
            return this;
        }

    }


}