var Color = Java.type("com.badlogic.gdx.graphics.Color");
var Damage = Java.type("net.ncguy.lib.dmg.Damage");
var DmgTypeHeal = Java.type("net.ncguy.damage.DmgTypeHeal");
var HealthComponent = Java.type("net.ncguy.entity.component.HealthComponent");
var LightComponent = Java.type("net.ncguy.entity.component.LightComponent");

var targets = [];
var lights = [];

// TODO add contact listener to watch for sensor overlaps

function AddTarget(targetEntity) {
    targets.push(targetEntity);
    var light = new LightComponent("TMP/Target/Light");
    light.radius = 32;
    light.colour.set(1, 0, 0, 1);
    lights.push(light);
    targetEntity.GetRootComponent().Add(light);
}

function GetSensorOriginEntity() {
    if(targets.length == 0)
        return this;
    return targets[targets.length - 1];
}

function GetSensorOrigin() {
    var entity = GetSensorOriginEntity.call(this);
    return entity.Transform().translation;
}

var sensor = null;

function OnEnabled() {
    var origin = this.Transform().translation;
    var range = 100 * Utils.ScreenToPhysics();
    var that = this;
    Utils.CreateCircularSensor(origin, range, function(body) {
        sensor = body;
        var userData = body.getUserData();
        userData.entity = that;
        if(userData.listener == null) {
            return;
        }

        userData.listener.BeginContact = function(contact) {
            var targetBody = Utils.GetOtherBody(contact, sensor);
            var tgtUserData = targetBody.getUserData();
            if(tgtUserData.entity != that && Utils.IsEntityAlive(tgtUserData.entity) && targets.indexOf(tgtUserData.entity) == -1)
                AddTarget.call(that, tgtUserData.entity);
        };

    });
}

function OnActiveUpdate(delta) {
    if(sensor == null)
        return;

    var target = Utils.GetMouseCoords();
    target = Utils.UnprojectCoords(this, target);
    var bodyPos = sensor.getPosition().cpy().scl(Utils.PhysicsToScreen());
    var range = 100;

    var origin = GetSensorOrigin.call(this);

    var tgt = target.cpy();

    var dst = tgt.dst(origin);
    if(dst > range)
        tgt.sub(origin).nor().scl(range).add(origin);

    bodyPos.set(tgt).scl(Utils.ScreenToPhysics());
    // sensor.setTransform(bodyPos, 0);
    Utils.SetBodyTransform(sensor, bodyPos, 0);

    // Debug rendering
    var radius = 100;
    // Utils.DebugCircle(bodyPos, radius, delta).colour = new Color(1, 0, 0, 1);
    Utils.DebugCircle(origin, range, delta).colour = new Color(0, 1, 0, 1);
    Utils.DebugPoint(tgt, delta).colour = new Color(0, 0, 1, 1);
    Utils.DebugPoint(origin, delta).colour = new Color(1, 0, 1, 1);

}

function GetTarget(idx) {
    if(idx < 0)
        return this;
    if(idx >= targets.length)
        return null;

    return targets[idx];
}

function GetTargetPos(idx) {
    var tgt = GetTarget.call(this, idx);
    if(tgt == null)
        return null;
    return tgt.Transform().translation;
}

function DamageEntity(target) {
    var dmg = Damage.Of(new DmgTypeHeal(), -40);
    var cls = HealthComponent.class;
    if(target.HasComponent(cls)) {
        var healthComp = target.GetComponent(cls, true);
        dmg.Apply(healthComp.health);
    }

    var org = this.Transform().translation;
    var tgt = target.Transform().translation;

    var vector = tgt.cpy().sub(org);
    var dst = vector.len() - 24;
    var dir = vector.cpy().nor();
    vector.set(dir).scl(dst).add(org);
    Utils.SetEntityRelativeLocation(this, vector);
    Utils.DebugPoint(vector, 5).colour = new Color(0, 1, 0, 1);
    Utils.DebugCircle(vector, 24, 5).colour = new Color(0, 1, 0, 1);
}

function OnDisabled() {
    if(sensor != null)
        Utils.DestroyBody(sensor);

    var max = targets.length;
    for(var i = 0; i < max; i++) {
        var start = GetTargetPos.call(this, i - 1);
        var end = GetTargetPos.call(this, i);

        Utils.DebugLine(start, end, 5).colour = new Color(1, 0, 0, 1);
    }

    var that = this;
    var delay = .05;
    targets.forEach(function(value, index) {
        Defer.Post(delay * index, function() {
            DamageEntity.call(that, value);
            var l = lights[index];
            if(l != null)
                l.RemoveFromParent();
            lights[index] = null;
        })
    });

    targets = [];
}