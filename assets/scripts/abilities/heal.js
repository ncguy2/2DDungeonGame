var Color = Java.type("com.badlogic.gdx.graphics.Color");
var Damage = Java.type("net.ncguy.lib.dmg.Damage");
var DmgTypeHeal = Java.type("net.ncguy.damage.DmgTypeHeal");
var HealthComponent = Java.type("net.ncguy.entity.component.HealthComponent");
var LightComponent = Java.type("net.ncguy.entity.component.LightComponent");
var ParticleProfileComponent = Java.type("net.ncguy.entity.component.ParticleProfileComponent");
var Gdx = Java.type("com.badlogic.gdx.Gdx");

var light = null;

function OnActiveUpdate(delta) {
    var origin = this.Transform().translation.cpy();

    var range = 150;

    var dmg = Damage.Of(new DmgTypeHeal(), 10 * delta);

    var cls = HealthComponent.class;
    if(this.HasComponent(cls)) {
        var healthComp = this.GetComponent(cls, true);
        dmg.Apply(healthComp.health);
        print("Healing");
    }else {
        print("No healing")
    }


    // Debug rendering
    Utils.DebugCircle(origin, range, delta).colour = new Color(0, 1, 0, 1);
}

function OnEnabled() {
    var origin = this.Transform().translation;
    var caster = this;

    var range = 150;

    var dmg = Damage.Of(new DmgTypeHeal(), 50);

    var cls = HealthComponent.class;
    if(this.HasComponent(cls)) {
        var healthComp = this.GetComponent(cls, true);
        dmg.Apply(healthComp.health);
        light = new LightComponent("TMP/Health/Light");
        healthComp.owningComponent.Add(light);
        light.radius = 64;
        light.colour.set(0, 1, 0, 1);
    }


    var trail = new ParticleProfileComponent("Stream particles");
    trail.systemName = "Heal";
    trail.onInit = function(comp, sys) {

        sys.renderer.textureRef = "textures/icons/health.png";

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
        sys.AddUniform("u_baseScale", function(loc) {
            Gdx.gl.glUniform2f(loc, 4, 4);
        });
    };
    this.AddComponent(trail);

    // Debug rendering
    Utils.DebugCircle(origin, range, 2).colour = new Color(1, 1, 0, 1);
}

function OnDisabled() {

    var caster = this;

    var streamParticles = caster.GetComponent("Stream particles", ParticleProfileComponent.class, true);
    print(streamParticles);
    streamParticles.Destroy();

    if(light == null)
        return;
    light.RemoveFromParent();
    light = null;
}