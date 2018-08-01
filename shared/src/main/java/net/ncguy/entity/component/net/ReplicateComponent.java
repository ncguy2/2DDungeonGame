package net.ncguy.entity.component.net;

import net.ncguy.entity.component.EntityComponent;
import net.ncguy.entity.component.EntityProperty;

public class ReplicateComponent extends EntityComponent {

    @EntityProperty(Type = Float.class, Name = "Update frequency", Description = "Seconds between updates for the attached entity", Category = "Replication")
    public float updateFreq = 0;

    public transient boolean replicationDue = false;

    protected transient float currentUpdateTime;

    public ReplicateComponent() {
        this("Unnamed replication component");
    }

    public ReplicateComponent(String name) {
        super(name);
    }

    @Override
    public void Update(float delta) {
        currentUpdateTime += delta;
        if(currentUpdateTime > updateFreq) {
            replicationDue = true;
            currentUpdateTime = 0;
        }
    }

    public boolean IsReplicationDue() {
        return replicationDue;
    }

    public ReplicateComponent Replication() {
        replicationDue = false;
        return this;
    }

}
