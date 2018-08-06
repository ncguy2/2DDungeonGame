package net.ncguy.lib.physics.detection;

import net.ncguy.lib.physics.data.Circle;

public class CircleDetection {

    public static boolean VsCircle(Circle a, Circle b) {
        float r = a.radius + b.radius;
        r *= r;
        return r < a.position.Distance2(b.position);
    }

}
