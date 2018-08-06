package net.ncguy.lib.physics.detection;

import net.ncguy.lib.physics.data.AABB;

public class AABBDetection {

    public static boolean VsAABB(AABB a, AABB b) {
        if(a.max.x < b.min.x || a.min.x > b.max.x) return false;
        if(a.max.y < b.min.y || a.min.y > b.max.y) return false;

        // Overlap detected
        return true;
    }

}
