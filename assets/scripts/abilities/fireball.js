var Color = Java.type("com.badlogic.gdx.graphics.Color");
var Damage = Java.type("net.ncguy.lib.dmg.Damage");
var DmgTypeFire = Java.type("net.ncguy.damage.DmgTypeFire");
var HealthComponent = Java.type("net.ncguy.entity.component.HealthComponent");
var SpriteComponent = Java.type("net.ncguy.entity.component.SpriteComponent");
var MovementComponent = Java.type("net.ncguy.entity.component.MovementComponent");
var BodyType = Java.type("com.badlogic.gdx.physics.box2d.BodyDef.BodyType");
var LightComponent = Java.type("net.ncguy.entity.component.LightComponent");


function ApplyDamage(target) {

    var dmg = Damage.Of(new DmgTypeFire(), 50);
    var cls = HealthComponent.class;

    if(target.HasComponent(cls)) {
        var healthComp = target.GetComponent(cls, true);
        dmg.Apply(healthComp.health);
    }
}

function OnEnabled() {
    print("Fireball 2");
    var origin = this.Transform().translation;
    var coords = Utils.GetMouseCoords();
    coords = Utils.UnprojectCoords(this, coords);

    var direction = coords.sub(origin.x, origin.y).nor();

    var radius = 32 * Utils.ScreenToPhysics();
    var that = this;

    print("Creating sensor")

    var world = Utils.GetPhysicsContainer("Overworld");

    Utils.CreateCircularSensor(world, origin.cpy().scl(Utils.ScreenToPhysics()), radius, BodyType.DynamicBody, function(body) {
        print("Sensor created")
        var userData = body.getUserData();
        userData.entity = that;
        if(userData.listener == null) {
            print("Null listener")
            return;
        }

        print("Creating entity");

        var entity = Utils.entityFactory.CreateCollisionEntity(body);
        entity.GetRootComponent().container = world;
        var sprite = entity.AddComponent(new SpriteComponent("Sprite"));
        sprite.spriteScaleOverride.set(64, 64);
        sprite.spriteRef = "textures/fireball.png";
        var movement = entity.AddComponent(new MovementComponent("Movement"));
        movement.resetAfterCheck = false;

        var light = entity.AddComponent(new LightComponent("Light"));
        light.radius = 64;
        light.colour.set(0xE25822FF);
        print(direction);
        movement.velocity.set(direction.x, direction.y).scl(750);
        print(movement.velocity);

        Utils.Engine().world.Add(entity);

        print("Entity created");

        userData.listener.BeginContact = function(contact) {
            print("Contact")
            var targetBody = Utils.GetOtherBody(contact, body);
            var tgtUserData = targetBody.getUserData();
            if(tgtUserData.bodyType.ordinal() == 2) {
                if (tgtUserData.entity != that && Utils.IsEntityAlive(tgtUserData.entity)) {
                    ApplyDamage.call(this, tgtUserData.entity);
                }
            }else {
                // Impact on wall
                if(entity != null) {
                    entity.Destroy();
                    entity = null;
                }
            }
        };

    });

}
