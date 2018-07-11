var Color = Java.type("com.badlogic.gdx.graphics.Color");
var Damage = Java.type("net.ncguy.lib.dmg.Damage");
var DmgTypeHeal = Java.type("net.ncguy.damage.DmgTypeHeal");
var HealthComponent = Java.type("net.ncguy.entity.component.HealthComponent");

var targets = [];

// TODO add contact listener to watch for sensor overlaps

function GetSensorOriginEntity() {
    print("Targets.length = " + targets.length);
    print("This: " + this);
    if(targets.length == 0)
        return this;
    return targets[targets.length - 1];
}

function GetSensorOrigin() {
    var entity = GetSensorOriginEntity.call(this);
    print(entity.toString());
    return entity.Transform().translation;
}

var sensor = null;

function OnEnabled() {
    var origin = this.Transform().translation;
    var range = 150;
    Utils.CreateCircularSensor(origin, range, function(body) {
        sensor = body;
    });
}

function OnActiveUpdate(delta) {
    if(sensor == null)
        return;

    var target = Utils.GetMouseCoords();
    target = Utils.UnprojectCoords(this, target);
    var bodyPos = sensor.getPosition().cpy();
    var range = 150;

    bodyPos.lerp(target, delta * 5);
    sensor.setTransform(bodyPos, 0);

    // Debug rendering
    Utils.DebugCircle(bodyPos, range, delta).colour = new Color(1, 0, 0, 1);
    Utils.DebugPoint(GetSensorOrigin.call(this), delta).colour = new Color(1, 0, 1, 1);
}


function OnDisabled() {
    if(sensor != null)
        Utils.DestroyBody(sensor);
}