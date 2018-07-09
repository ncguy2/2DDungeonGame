package net.ncguy.input;

import java.util.ArrayList;
import java.util.List;

public class InputAxis {
    public List<AxisEntry> entries = new ArrayList<>();

    public static class AxisEntry {
        public InputAction action;
        public float scale;
    }
}
