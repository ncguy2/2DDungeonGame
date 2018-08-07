var Color = Java.type("com.badlogic.gdx.graphics.Color");
var ParticleProfileComponent = Java.type("net.ncguy.entity.component.ParticleProfileComponent");
var TextureBurstParticleSystem = Java.type("net.ncguy.particles.TextureBurstParticleSystem");
var SpriteComponent = Java.type("net.ncguy.entity.component.SpriteComponent");
var Gdx = Java.type("com.badlogic.gdx.Gdx");

function OnEnabled() {
    var origin = this.Transform().translation.cpy();
    var target = Utils.GetMouseCoords();
    var caster = this;

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

    var trail = new ParticleProfileComponent("Blink particles");
    trail.systemName = "Blink Trail";
    trail.onInit = function(comp, sys) {
        sys.Bind("u_curve", comp.profile.curve);
        sys.AddUniform("u_spawnPoint", function(loc) {
            Gdx.gl.glUniform2f(loc, origin.x, origin.y);
        });
        sys.AddUniform("u_initialScale", function(loc) {
            Gdx.gl.glUniform2f(loc, 1, 1);
        });
        sys.AddUniform("u_attractionPoint", function(loc) {
            Gdx.gl.glUniform2f(loc, offsetPoint.x, offsetPoint.y);
        });
        sys.AddUniform("u_simSpeed", function(loc) {
            Gdx.gl.glUniform1f(loc, 10);
        });
        sys.AddUniform("u_devianceRadius", function (loc) {
            Gdx.gl.glUniform1f(loc, 32);
        });
    };
    this.AddComponent(trail);

    // var burst = new ParticleProfileComponent("Blink Burst particles");
    // burst.systemName = "Blink Burst";
    // burst.onInit = function(comp, sys) {
    //     sys.Bind("u_curve", comp.profile.curve);
    //     sys.AddUniform("u_spawnPoint", function(loc) {
    //         Gdx.gl.glUniform2f(loc, origin.x, origin.y);
    //     });
    //     sys.AddUniform("attractionPoint", function(loc) {
    //         Gdx.gl.glUniform2f(loc, offsetPoint.x, offsetPoint.y);
    //     });
    //     sys.AddUniform("u_simSpeed", function(loc) {
    //         Gdx.gl.glUniform1f(loc, 1);
    //     });
    //     sys.AddUniform("u_devianceRadius", function (loc) {
    //         Gdx.gl.glUniform1f(loc, 32);
    //     });
    //     sys.AddUniform("u_followCurve", function (loc) {
    //         Gdx.gl.glUniform1i(loc, 0);
    //     });
    //     if(sys instanceof TextureBurstParticleSystem) {
    //         var sprite = caster.GetComponent(SpriteComponent.class, true);
    //         if(sprite != null) {
    //             sys.size = sprite.GetWorldSize();
    //             print(sys.size);
    //             sys.SetAmount(sys.size.x * sys.size.y);
    //             sys.colourTexture = sprite.GetTexture();
    //             sys.maskTexture = sprite.GetTexture();
    //             sys.maskChannel = 3;
    //         }
    //     }
    // };
    // this.AddComponent(burst);

    // Debug rendering
    // Utils.DebugPoint(intersection.point, 5).colour = new Color(1, 0, 0, 1);
    // Utils.DebugPoint(origTarget, 5).colour = new Color(1, 1, 1, 1);
    // Utils.DebugLine(intersection.point, offsetPoint, 5).colour = new Color(1, 0, 1, 1);
    // Utils.DebugLine(origin, intersection.point, 5).colour = new Color(0, 1, 0, 1);
    // Utils.DebugLine(intersection.point, target, 5).colour = new Color(0, 0, 1, 1);

}