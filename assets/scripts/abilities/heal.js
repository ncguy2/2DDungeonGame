var Color = Java.type("com.badlogic.gdx.graphics.Color");
var Damage = Java.type("net.ncguy.lib.dmg.Damage");
var DmgTypeHeal = Java.type("net.ncguy.damage.DmgTypeHeal");
var HealthComponent = Java.type("net.ncguy.entity.component.HealthComponent");

function OnEnabled() {
    var origin = this.Transform().translation.cpy();

    var range = 150;

    var delta = 1.0 / 60.0;

    var dmg = Damage.Of(new DmgTypeHeal(), 50 * delta);

    var cls = HealthComponent.class;
    if(this.HasComponent(cls)) {
        var healthComp = this.GetComponent(cls, true);
        dmg.Apply(healthComp.health);
        print("Healed " + (50 * delta) + " health");
    }else {
        print("Unable to heal, no health component");
    }

    // Debug rendering
    Utils.DebugCircle(origin, range, 5).colour = new Color(0, 1, 0, 1);
}
