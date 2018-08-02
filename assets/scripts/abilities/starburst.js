var LightComponent = Java.type("net.ncguy.entity.component.LightComponent");
var ParticleComponent = Java.type("net.ncguy.entity.component.ParticleComponent");
var RotationComponent = Java.type("net.ncguy.entity.component.RotationComponent");
var Vector2 = Java.type("com.badlogic.gdx.math.Vector2");
var Entity = Java.type("net.ncguy.entity.Entity");
var SystemType = Java.type("net.ncguy.particles.AbstractParticleSystem.SystemType");
var Maths = Java.type("java.lang.Math");
var Gdx = Java.type("com.badlogic.gdx.Gdx");

var curveSteps = 3;
var curveMax = 15.0;

var entities = [];
var particles = {};

function Create(caster, point) {
    var e = new Entity();
    entities.push(e);
    var p = new ParticleComponent();
    particles[e] = p;
    e.SetRootComponent(p);
    e.Transform().translation.set(point);

    var l = new LightComponent();
    l.colour = Utils.RandomColour();
    l.radius = 256;

    p.duration = 15.0;
    p.particleCount = 200000;
    p.spawnOverTime = 10.0;
    p.systemType = SystemType.Temporal;
    p.curve = Utils.RandomColourCurve(curveSteps, curveMax);

    p.onInit = function(particles) {
        particles.Bind("u_curve", p.curve);
        particles.AddUniform("u_spawnPoint", function(loc) {
            var pos = p.transform.WorldTranslation();
            Gdx.gl.glUniform2f(loc, pos.x, pos.y);
        });
        particles.AddUniform("attractionPoint", function(loc) {
            var pos = caster.Transform().WorldTranslation();
            Gdx.gl.glUniform2f(loc, pos.x, pos.y);
        });
    };

    Utils.entityFactory.AddToWorld(e);
    Defer.Post(15.0, function() {
        e.Destroy();
    })
}

function OnEnabled() {
    var origin = this.Transform().translation;
    var range = 500;
    var caster = this;

    var dirs = [];
    var pointCount = 8;
    var segSize = 360.0 / pointCount;
    var i;

    for(i = 0; i < pointCount; i++) {
        var seg = Maths.toRadians((segSize * i));
        dirs[i] = new Vector2(Maths.cos(seg), Maths.sin(seg));
    }

    var anchor = new RotationComponent();
    anchor.rotationSpeed = 30.0;
    anchor.transform.translation.set(0.0, 0.0);
    caster.AddComponent(anchor);

    for(i = 0; i < pointCount; i++) {
        var point = dirs[i].scl(range);

        var p = new ParticleComponent("Particle " + i);
        p.transform.translation.set(point);

        var l = new LightComponent();
        l.colour = Utils.RandomColour();
        l.colour.r = l.colour.r * 0.5 + 0.5;
        l.colour.g = l.colour.g * 0.5 + 0.5;
        l.colour.b = l.colour.b * 0.5 + 0.5;
        l.colour.a = 1.0;
        l.radius = 512;
        p.Add(l);

        p.duration = 40.0;
        p.particleCount = 20000;
        p.spawnOverTime = 30.0;
        p.systemType = SystemType.Temporal;
        p.curve = Utils.RandomColourCurve(curveSteps, curveMax);

        p.onInit = function(comp, particles) {
            particles.Bind("u_curve", comp.curve);
            particles.AddUniform("u_spawnPoint", function(loc) {
                var pos = comp.transform.WorldTranslation();
                Gdx.gl.glUniform2f(loc, pos.x, pos.y);
            });
            particles.AddUniform("attractionPoint", function(loc) {
                var pos = caster.Transform().WorldTranslation();
                Gdx.gl.glUniform2f(loc, pos.x, pos.y);
            });
        };

        p.onFinish = function(comp) {
            comp.particleSystem.Finish();
            comp.Destroy();
        };
        anchor.Add(p);
    }

}
