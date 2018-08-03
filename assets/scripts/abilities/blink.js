var Color = Java.type("com.badlogic.gdx.graphics.Color");
var ParticleProfileComponent = Java.type("net.ncguy.entity.component.ParticleProfileComponent");
var Gdx = Java.type("com.badlogic.gdx.Gdx");

function OnEnabled() {
    var origin = this.Transform().translation.cpy();
    var target = Utils.GetMouseCoords();

    target = Utils.UnprojectCoords(this, target);
    var origTarget = target.cpy();

    var direction = Utils.ToDirection(origin, target);

    var range = 500;
    var distanceToTarget = target.dst(origin);
    if(range > distanceToTarget)
        range = distanceToTarget;

    // target.set(origin).add(direction.scl(range));
    target.set(direction).scl(range).add(origin);

    var intersection = Utils.LineTrace(origin, target);

    if(intersection.hit === false)
        intersection.point.set(target);

    var offsetPoint = intersection.normal.cpy().scl(50).add(intersection.point);
    Utils.SetEntityRelativeLocation(this, offsetPoint);

    var p = new ParticleProfileComponent("Blink particles");
    p.systemName = "Blink";
    p.onInit = function(comp, sys) {
        sys.Bind("u_curve", comp.profile.curve);
        sys.AddUniform("u_spawnPoint", function(loc) {
            Gdx.gl.glUniform2f(loc, origin.x, origin.y);
        });
        sys.AddUniform("attractionPoint", function(loc) {
            Gdx.gl.glUniform2f(loc, offsetPoint.x, offsetPoint.y);
        });
        sys.AddUniform("u_simSpeed", function(loc) {
            Gdx.gl.glUniform1f(loc, 5);
        });
        sys.AddUniform("u_devianceRadius", function (loc) {
            Gdx.gl.glUniform1f(loc, 32);
        });
    };
    this.AddComponent(p);

    // Debug rendering
    // Utils.DebugPoint(intersection.point, 5).colour = new Color(1, 0, 0, 1);
    // Utils.DebugPoint(origTarget, 5).colour = new Color(1, 1, 1, 1);
    // Utils.DebugLine(intersection.point, offsetPoint, 5).colour = new Color(1, 0, 1, 1);
    // Utils.DebugLine(origin, intersection.point, 5).colour = new Color(0, 1, 0, 1);
    // Utils.DebugLine(intersection.point, target, 5).colour = new Color(0, 0, 1, 1);

}