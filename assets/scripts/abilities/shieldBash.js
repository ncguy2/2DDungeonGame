var Color = Java.type("com.badlogic.gdx.graphics.Color");
var Damage = Java.type("net.ncguy.lib.dmg.Damage");
var DmgTypeHeal = Java.type("net.ncguy.damage.DmgTypeHeal");
var HealthComponent = Java.type("net.ncguy.entity.component.HealthComponent");
var CollisionComponent = Java.type("net.ncguy.entity.component.CollisionComponent");
var InputComponent = Java.type("net.ncguy.entity.component.InputComponent");
var BodyType = Java.type("com.badlogic.gdx.physics.box2d.BodyDef.BodyType");

function OnEnabled() {
    print("ShieldBash");
    var origin = this.Transform().WorldTranslation();

    var input = this.GetComponent(InputComponent.class, true);
    var initialDelay = .1;
    input.enabled = false;
    var that = this;
    Defer.Post(initialDelay, function() {
        var coords = Utils.GetMouseCoords();
        coords = Utils.UnprojectCoords(that, coords);
        var direction = coords.sub(origin.x, origin.y).nor();

        // var range = 500;
        // var target = direction.cpy().scl(range).add(origin);
        // var intersection = Utils.LineTrace(origin, direction);
        //
        // if(intersection.hit === true) {
        //     target.set(intersection.point);
        // }

        var duration = .2;
        var radius = 50;

        var world = Utils.GetPhysicsContainer("Overworld");
        Utils.CreateCircularSensor(world, origin.cpy().scl(Utils.ScreenToPhysics()), radius, BodyType.DynamicBody, function(body) {
            var collision = that.AddComponent(new CollisionComponent("TMP/Knockback"));
            collision.useComponentTransform = true;
            collision.body = body;

            var userData = body.getUserData();
            userData.entity = that;
            if(userData.listener == null) {
                return;
            }

            userData.listener.BeginContact = function(contact) {
                var targetBody = Utils.GetOtherBody(contact, body);
                var tgtUserData = targetBody.getUserData();
                if(tgtUserData.entity != that && Utils.IsEntityAlive(tgtUserData.entity)) {
                    var tgtPos = tgtUserData.entity.Transform().WorldTranslation();
                    var selfPos = body.getPosition().cpy().scl(Utils.PhysicsToScreen());
                    var dst = tgtPos.dst(selfPos);
                    var str = (radius * 2) - dst;
                    str = str * 100;
                    var dir = tgtPos.cpy().sub(selfPos).scl(str);
                    print("Knockback: " + dir);
                    targetBody.setLinearDamping(0);
                    Utils.SetBodyVelocity(tgtUserData.entity, dir, duration);
                    Defer.Post(duration, function() {
                        targetBody.setLinearDamping(100);
                    });
                    // TODO add stun damage type (should block the AI processing while in effect)
                }
            };

            Defer.Post(duration, function() {
                collision.RemoveFromParent();
            });
        });
        Utils.SetBodyVelocity(that, direction.scl(1250), duration);
        // Utils.MoveBodyOverTime(that, direction, .5);
        Defer.Post(duration, function() {
            input.enabled = true;
        })
    });
}