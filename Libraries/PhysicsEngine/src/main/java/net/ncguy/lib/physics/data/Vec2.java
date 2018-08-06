package net.ncguy.lib.physics.data;

public class Vec2 {

    public float x;
    public float y;

    public float Distance2(Vec2 other) {
        float diffX = this.x - other.x;
        float diffY = this.y - other.y;
        return (diffX * diffX) + (diffY * diffY);
    }

    public float Distance(Vec2 other) {
        return (float) Math.sqrt(Distance2(other));
    }

}
