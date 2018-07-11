package net.ncguy.physics;

import com.badlogic.gdx.physics.box2d.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class PhysicsService {

    List<ServiceListener<?>> listeners = new ArrayList<>();

    public <T> ServiceListener<T> AddListener(Class<T> type, Consumer<ServiceListener<T>> task) {
        ServiceListener<T> listener = new ServiceListener<>();
        listener.type = type;
        listener.task = task;
        listeners.add(listener);
        return listener;
    }

    public <T> List<ServiceListener<T>> ListenersOfIdAndType(int id, Class<T> type) {
        return listeners.stream()
                .filter(l -> l.type.equals(type))
                .filter(l -> l.id == id)
                .map(l -> (ServiceListener<T>) l)
                .collect(Collectors.toList());
    }

    public abstract int QueueCreateBody(BodyDef def);

    public abstract int QueueCreateFixture(Body body, FixtureDef def);

    public abstract int QueueCreateFixtures(Body body, FixtureDef... def);

    public abstract int QueueCreateJoint(JointDef def);

    public abstract Body ObtainBody(int id);

    public abstract Fixture ObtainFixture(int id);

    public abstract Fixture[] ObtainFixtures(int id);

    public abstract Joint ObtainJoint(int id);

    public abstract void QueueRemoveBody(Body body);
    public abstract void QueueRemoveFixture(Body body, Fixture fixture);
    public abstract void QueueRemoveFixtures(Body body, Fixture... fixture);
    public abstract void QueueRemoveJoint(Joint joint);

    public abstract void Produce();
    public abstract void Remove();

    public static class ServiceListener<T> {
        public int id;
        public Class<T> type;
        public Consumer<ServiceListener<T>> task;

        public void Run() {
            task.accept(this);
        }

    }

}
