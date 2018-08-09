var Color = Java.type("com.badlogic.gdx.graphics.Color");
var Damage = Java.type("net.ncguy.lib.dmg.Damage");
var DmgTypeHeal = Java.type("net.ncguy.damage.DmgTypeHeal");
var HealthComponent = Java.type("net.ncguy.entity.component.HealthComponent");
var ParticleProfileComponent = Java.type("net.ncguy.entity.component.ParticleProfileComponent");
var Gdx = Java.type("com.badlogic.gdx.Gdx");

function OnEnabled() {
    var origin = this.Transform().translation.cpy();

    var range = 150;

    var delta = 1.0 / 60.0;

    var dmg = Damage.Of(new DmgTypeHeal(), -25);

    var cls = HealthComponent.class;
    if(this.HasComponent(cls)) {
        var healthComp = this.GetComponent(cls, true);
        dmg.Apply(healthComp.health);
        print("Healed " + (50 * delta) + " health");
    }else {
        print("Unable to heal, no health component");
    }

    var trail = new ParticleProfileComponent("Stream particles");
    trail.systemName = "Hurt";
    trail.onInit = function(comp, sys) {
        sys.Bind("u_curve", comp.profile.curve);
        sys.AddUniform("u_spawnPoint", function(loc) {
            Gdx.gl.glUniform2f(loc, origin.x, origin.y);
        });
        sys.AddUniform("u_initialScale", function(loc) {
            Gdx.gl.glUniform2f(loc, 1, 1);
        });
        sys.AddUniform("u_simSpeed", function(loc) {
            Gdx.gl.glUniform1f(loc, 1);
        });
        sys.AddUniform("u_devianceRadius", function (loc) {
            Gdx.gl.glUniform1f(loc, range);
        });
    };
    this.AddComponent(trail);

    // Debug rendering
    Utils.DebugCircle(origin, range, 5).colour = new Color(0, 1, 0, 1);
}


function OnDisabled() {

    var caster = this;

    var streamParticles = caster.GetComponent("Stream particles", ParticleProfileComponent.class, true);
    print(streamParticles);
    streamParticles.Destroy();
}