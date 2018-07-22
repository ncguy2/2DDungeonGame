package net.ncguy.lib.dmg.status;

import net.ncguy.lib.dmg.hp.Health;
import net.ncguy.lib.dmg.types.DamageType;

public abstract class StatusEffect {

    protected Health target;
    protected DamageType type;
    public int stackCount;
    public float life;
    public float maxLife;
    protected float secondTimer;

    /**
     * Creates a status effect and attaches itself to the provided Health object
     * @param target The target to attach the effect to
     * @param life The life of the status effect
     */
    public StatusEffect(Health target, float life) {
        stackCount = 1;
        this.maxLife = life;
        SetLife(life);
        Attach(target);
    }

    public void OnAttach(Health target) {}
    public void OnRemove(Health target) {}

    /**
     * Attaches the status effect to the provided target, detaching itself from the current target if it exists
     * @param target The new target to attach to
     */
    public void Attach(Health target) {
        if(this.target != null)
            _Remove();
        this.target = target;
        if(this.target != null) {
            this.target._AddStatusEffect(this);
            OnAttach(target);
        }
    }

    /**
     * Combines the stacks of 2 status effects
     * @param other The other status effect, must be targeting the same Health object
     */
    public void Combine(StatusEffect other) {
        if(this.target != other.target) {
            System.err.println("StatusEffect.Combine");
            return;
        }
        _ChangeStack(other.stackCount);
    }

    /**
     * Sets the stack count
     * @param stackCount The stack count
     * @return This instance for chaining
     */
    public StatusEffect Stacks(int stackCount) {
        _SetStack(stackCount);
        return this;
    }

    /**
     * Increments the stack count by 1
     */
    public void IncrementStack() {
        IncrementStack(1);
    }

    /**
     * Increments the stack count by the provided amount
     * @param amt The amount to increment the stack count by
     */
    public void IncrementStack(int amt) {
        _ChangeStack(amt);
    }

    /**
     * Decrements the stack count by 1
     */
    public void DecrementStack() {
        DecrementStack(1);
    }

    /**
     * Decrements the stack count by the provided amount
     * @param amt The amount to decrement the stack count by
     */
    public void DecrementStack(int amt) {
        _ChangeStack(-amt);
    }

    /**
     * Sets the life of the status effect, can be used to reset the duration on stack change
     * @param life The new life for this effect
     */
    public void SetLife(float life) {
        this.life = life;
    }

    /**
     * Event fired when the stack count is changed
     * @param oldCount The old stack count
     * @param newCount The new stack count
     * @param incrementCount The amount of stacks added
     */
    public abstract void OnStackChange(int oldCount, int newCount, int incrementCount);

    /**
     * Event fired every iteration
     * <br>
     * Should be called regularly
     * @param delta The time between invocations
     */
    public abstract void OnUpdate(float delta);

    /**
     * Event fired every second the effect is active
     */
    public abstract void OnSecond();

    /**
     * @return The status effect display name
     */
    public abstract String GetName();

    /**
     * Provides the life value when losing a stack
     * @return The max life value
     */
    public float GetMaxLife() {
        return maxLife;
    }

    public boolean IsStackAlive() {
        return life > 0;
    }
    public boolean IsEffectAlive() {
        return stackCount > 0;
    }

    // Internal API

    public void _Update(float delta) {
        life -= delta;
        if(!IsStackAlive()) {
            DecrementStack();
            life = GetMaxLife();
            if(!IsEffectAlive())
                _Remove();
        }

        if((secondTimer += delta) > 1) {
            secondTimer -= 1;
            OnSecond();
        }

        OnUpdate(delta);
    }

    public void _Remove() {
        if(target != null) {
            OnRemove(target);
            target._Remove(this);
        }
        target = null;
    }

    public void _SetDamageType(DamageType type) {
        this.type = type;
    }

    /**
     * Modifies the stack count by the provided amount
     * @param amt The amount to add to the stack count
     */
    protected void _ChangeStack(int amt) {
        int oldCount = this.stackCount;
        this.stackCount += amt;
        OnStackChange(oldCount, this.stackCount, amt);
    }

    /**
     * Overrides the stack count by setting it to the provided amount
     * @param amt The amount to set the stack count to
     */
    protected void _SetStack(int amt) {
        int oldCount = this.stackCount;
        this.stackCount = amt;
        OnStackChange(oldCount, this.stackCount, this.stackCount - oldCount);
    }


}
