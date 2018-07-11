var Color = Java.type("com.badlogic.gdx.graphics.Color");
var Damage = Java.type("net.ncguy.lib.dmg.Damage");
var DmgTypeHeal = Java.type("net.ncguy.damage.DmgTypeHeal");
var HealthComponent = Java.type("net.ncguy.entity.component.HealthComponent");

function OnActiveUpdate(delta) {
    var origin = this.Transform().translation.cpy();

    var range = 150;

    var dmg = Damage.Of(new DmgTypeHeal(), 10 * delta);

    var cls = HealthComponent.class;
    if(this.HasComponent(cls)) {
        var healthComp = this.GetComponent(cls, true);
        dmg.Apply(healthComp.health);
        print("Healing")
    }else {
        print("No healing")
    }


    // Debug rendering
    Utils.DebugCircle(origin, range, delta).colour = new Color(0, 1, 0, 1);
}

function OnEnabled() {
    var origin = this.Transform().translation.cpy();

    var range = 150;

    var dmg = Damage.Of(new DmgTypeHeal(), 50);

    var cls = HealthComponent.class;
    if(this.HasComponent(cls)) {
        var healthComp = this.GetComponent(cls, true);
        dmg.Apply(healthComp.health);
    }

    // Debug rendering
    Utils.DebugCircle(origin, range, 2).colour = new Color(1, 1, 0, 1);
}
