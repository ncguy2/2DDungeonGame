package net.ncguy.physics;

import com.badlogic.gdx.physics.box2d.*;
import net.ncguy.physics.data.BodyFixture;
import net.ncguy.physics.data.BodyFixtureDef;
import net.ncguy.physics.data.BodyFixturesDef;

import java.util.*;

public class PhysicsServiceImpl extends PhysicsService {

    protected int maxPerIteration = 16;
    protected World world;
    protected PhysicsFactory factory;
    protected final Map<Integer, Object> input;
    protected final Map<Integer, Object> output;
    protected final List<Object> recycleBin;
    protected int currentIdx = Integer.MIN_VALUE;

    public PhysicsServiceImpl(World world) {
        this.world = world;
        this.factory = new PhysicsFactoryImpl(world);
        input = new HashMap<>();
        output = new HashMap<>();
        recycleBin = new ArrayList<>();
    }

    public int Next() {
        return currentIdx++;
    }

    @Override
    public synchronized int QueueCreateBody(BodyDef def) {
        int id = Next();
        input.put(id, def);
        return id;
    }

    @Override
    public synchronized int QueueCreateFixture(Body body, FixtureDef def) {
        int id = Next();
        input.put(id, new BodyFixtureDef(body, def));
        return id;
    }

    @Override
    public synchronized int QueueCreateFixtures(Body body, FixtureDef... def) {
        int id = Next();
        input.put(id, new BodyFixturesDef(body, def));
        return id;
    }

    @Override
    public synchronized int QueueCreateJoint(JointDef def) {
        int id = Next();
        input.put(id, def);
        return id;
    }

    @Override
    public synchronized Body ObtainBody(int id) {
        Object o = output.get(id);
        if (o instanceof Body) {
            Body body = (Body) o;
            output.remove(id);
            return body;
        }
        return null;
    }

    @Override
    public synchronized Fixture ObtainFixture(int id) {
        Object o = output.get(id);
        if (o instanceof Fixture) {
            Fixture fixture = (Fixture) o;
            output.remove(id);
            return fixture;
        }
        return null;
    }

    @Override
    public synchronized Fixture[] ObtainFixtures(int id) {
        Object o = output.get(id);
        if (o instanceof Fixture[]) {
            Fixture[] fixture = (Fixture[]) o;
            output.remove(id);
            return fixture;
        }
        return null;
    }

    @Override
    public synchronized Joint ObtainJoint(int id) {
        Object o = output.get(id);
        if (o instanceof Joint) {
            Joint joint = (Joint) o;
            output.remove(id);
            return joint;
        }
        return null;
    }

    @Override
    public synchronized void QueueRemoveBody(Body body) {
        recycleBin.add(body);
    }

    @Override
    public synchronized void QueueRemoveFixture(Body body, Fixture fixture) {
        recycleBin.add(new BodyFixture(body, fixture));
    }

    @Override
    public synchronized void QueueRemoveFixtures(Body body, Fixture... fixtures) {
        for (Fixture fixture : fixtures)
            recycleBin.add(new BodyFixture(body, fixture));
    }

    @Override
    public synchronized void QueueRemoveJoint(Joint joint) {
        recycleBin.add(joint);
    }

    @Override
    public synchronized void Produce() {
        Set<Integer> keys = input.keySet();
        if (keys.isEmpty())
            return;
        synchronized (keys) {
            int amt = 0;
            Iterator<Integer> it = keys.iterator();
            while (it.hasNext() && amt < maxPerIteration) {
                amt++;
                int id = it.next();
                Object obj = input.get(id);
                Object out = null;
                Class<?> type = null;
                // TODO abstract
                if (obj instanceof BodyDef) {
                    out = factory.CreateBody((BodyDef) obj);
                    type = Body.class;
                } else if (obj instanceof JointDef) {
                    out = factory.CreateJoint((JointDef) obj);
                    type = Joint.class;
                } else if (obj instanceof BodyFixtureDef) {
                    BodyFixtureDef def = (BodyFixtureDef) obj;
                    out = factory.CreateFixture(def.body, def.definition);
                    type = Fixture.class;
                } else if (obj instanceof BodyFixturesDef) {
                    BodyFixturesDef def = (BodyFixturesDef) obj;
                    out = factory.CreateFixtures(def.body, def.definitions);
                    type = Fixture[].class;
                }

                if (out != null) {
                    output.put(id, out);
                    List<? extends ServiceListener<?>> listeners = ListenersOfIdAndType(id, type);
                    if (!listeners.isEmpty()) {
                        listeners.forEach(ServiceListener::Run);
                        this.listeners.removeAll(listeners);
                    }
                }
            }
            output.keySet()
                    .forEach(input::remove);
        }
    }

    @Override
    public void Execute() {
        if(taskList.isEmpty())
            return;

        final LinkedList<Runnable> queue;
        synchronized (taskList) {
            queue = new LinkedList<>(taskList);
            taskList.clear();
        }

        while(!queue.isEmpty())
            Optional.ofNullable(queue.remove()).ifPresent(Runnable::run);
    }

    @Override
    public void Remove() {
        List<Object> bin;
        synchronized (this.recycleBin) {
            bin = new ArrayList<>(this.recycleBin);
            recycleBin.clear();
        }

        bin.forEach(obj -> {
            if (obj instanceof Body) {
                world.destroyBody((Body) obj);
            } else if (obj instanceof Joint) {
                world.destroyJoint((Joint) obj);
            } else if (obj instanceof BodyFixture) {
                BodyFixture def = (BodyFixture) obj;
                def.body.destroyFixture(def.fixture);
            }
        });
    }
}
