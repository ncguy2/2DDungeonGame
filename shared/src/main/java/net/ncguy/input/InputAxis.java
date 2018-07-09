package net.ncguy.input;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class InputAxis {
    public List<AxisEntry> entries = new ArrayList<>();
    public CompositionRule rule = CompositionRule.AdditiveClamped;

    public float Resolve() {
        float value = 0;
        boolean first = true;
        for (AxisEntry entry : entries) {
            boolean test = entry.action.Test();
            if(test) {
                if(rule.equals(CompositionRule.First))
                    return entry.scale;

                if(first) value = entry.scale;
                else value = rule.Resolve(value, entry.scale);
                first = false;
            }
        }

        if(rule.shouldClamp)
            value = Math.max(-1, Math.min(value, 1));

        return value;
    }

    public static class AxisEntry {
        public InputAction action;
        public float scale;
    }

    public static enum CompositionRule {
        Additive((a, b) -> a + b),
        AdditiveClamped((a, b) -> a + b, true),
        Multiplicative((a, b) -> a * b),
        MultiplicativeClamped((a, b) -> a * b, true),
        First((a, b) -> b),
        ;

        final BiFunction<Float, Float, Float> function;
        final boolean shouldClamp;

        CompositionRule(BiFunction<Float, Float, Float> function) {
            this(function, false);
        }

        CompositionRule(BiFunction<Float, Float, Float> function, boolean shouldClamp) {
            this.function = function;
            this.shouldClamp = shouldClamp;
        }

        public float Resolve(float a, float b) {
            return function.apply(a, b);
        }

    }

}
