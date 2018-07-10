var Color = Java.type("com.badlogic.gdx.graphics.Color");

function OnUpdate(delta) {
}

function OnActiveUpdate(delta) {
}
function OnInactiveUpdate(delta) {
}

function OnEnabled() {
    var origin = this.Transform().translation.cpy();
    var target = Utils.GetMouseCoords();

    target = Utils.UnprojectCoords(this, target);

    var direction = Utils.ToDirection(origin, target);

    var range = 500;
    // target.set(origin).add(direction.scl(range));
    target.set(direction).scl(range).add(origin);

    var intersection = Utils.LineTrace(origin, target);

    print("intersection.hit: " + intersection.hit);
    if(intersection.hit === false) {
        intersection.point.set(target);
    }

    var offsetPoint = intersection.normal.cpy().scl(50).add(intersection.point);
    Utils.SetEntityRelativeLocation(this, offsetPoint);

    // Debug rendering

    Utils.DebugPoint(intersection.point, 5).colour = new Color(1, 0, 0, 1);
    Utils.DebugLine(intersection.point, offsetPoint, 5).colour = new Color(1, 0, 1, 1);
    Utils.DebugLine(origin, intersection.point, 5).colour = new Color(0, 1, 0, 1);
    Utils.DebugLine(intersection.point, target, 5).colour = new Color(0, 0, 1, 1);

}
function OnDisabled() {
}