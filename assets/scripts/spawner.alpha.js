var Entity = Java.type("net.ncguy.entity.Entity");
var CollisionComponent = Java.type("net.ncguy.entity.component.CollisionComponent");
var HealthComponent = Java.type("net.ncguy.entity.component.HealthComponent");
var PrimitiveCircleComponent = Java.type("net.ncguy.entity.component.PrimitiveCircleComponent");
var HealthUIComponent = Java.type("net.ncguy.entity.component.ui.HealthUIComponent");
var BodyDef = Java.type("com.badlogic.gdx.physics.box2d.BodyDef");
var CircleShape = Java.type("com.badlogic.gdx.physics.box2d.CircleShape");
var FixtureDef = Java.type("com.badlogic.gdx.physics.box2d.FixtureDef");
var SpawnEntityTask = Java.type("net.ncguy.physics.worker.SpawnEntityTask");

function Create(worldName, x, y) {
    var entity = new Entity();
    var collision = new CollisionComponent("Script/Collision");
    entity.SetRootComponent(collision);
    entity.AddComponent(new PrimitiveCircleComponent("Script/Circle")).colour.set(Utils.RandomColour()).a = 1.0;
    var healthComponent = HealthComponent.SingleHealthComponent("Script/Health", 250);
    entity.AddComponent(healthComponent);
    entity.AddComponent(new HealthUIComponent("UI/Health", healthComponent));

    var pos = Utils.RandomDirection().scl(Utils.RandomFloat()).scl(250).add(x, y);

    var bodyDef = new BodyDef();
    bodyDef.type = BodyDef.BodyType.DynamicBody;
    bodyDef.position.set(pos.x, pos.y).scl(screenToPhysics);

    var shape = new CircleShape();
    shape.setRadius(32 * screenToPhysics);
    var fixtureDef = new FixtureDef();
    fixtureDef.shape = shape;
    fixtureDef.density = 0;
    fixtureDef.friction = 0;
    fixtureDef.restitution = 0;

    var container = PhysicsSystem.GetContainer(worldName).orElse(null);

    var spawnTask = new SpawnEntityTask(bodyDef, fixtureDef);
    spawnTask.OnFinish(function(body) {
        body.getUserData().entity = entity;
        collision.body = body;
        collision.container = container;
    });
    container.foreman.Post(spawnTask);

    return entity;
}