package net.ncguy.entity.component;

import com.badlogic.gdx.Gdx;
import net.ncguy.ability.Ability;
import net.ncguy.ability.AbilityRegistry;
import net.ncguy.ability.AbilityScript;

import java.util.Optional;
import java.util.function.Consumer;

public class AbilityComponent extends EntityComponent {

    public transient Ability ability;
    public transient AbilityScript script;
    @EntityProperty(Type = String.class, Category = "Ability", Description = "Reference to the ability stored in this component", Name = "Ability reference")
    public String abilityRef;
    @EntityProperty(Type = Integer.class, Category = "Ability", Description = "Slot index for the ability stored in this component", Name = "Slot index")
    public int slotIdx;
    public AbilityState state = AbilityState.Disabled;

    public AbilityComponent(String name) {
        super(name);
    }

    public AbilityComponent SlotIdx(int slotIdx) {
        this.slotIdx = slotIdx;
        return this;
    }

    public void SetAbility(Ability ability) {
        this.ability = ability;
        if(ability != null) {
            script = new AbilityScript(Gdx.files.internal(this.ability.scriptPath)
                    .readString());
            script.Parse();
        }else script = null;
    }

    public Optional<Ability> GetAbility() {
        if(ability != null)
            return Optional.of(ability);

        Optional<Ability> opt = AbilityRegistry.instance()
                .Get(abilityRef);

        opt.ifPresent(this::SetAbility);
        return opt;
    }

    public void WithAbility(Consumer<Ability> task) {
        GetAbility().ifPresent(task);
    }

    public boolean IsState(AbilityState queryState) {
        return state.equals(queryState);
    }

    public AbilityState State() {
        return state;
    }

    public void SetState(AbilityState state) {
        this.state = state;
    }

    public void SetEnabled(boolean enabled) {
        if(this.state.enabled == enabled)
            return;

        if(enabled)
            SetState(AbilityState.JustEnabled);
        else SetState(AbilityState.JustDisabled);
    }

    public static enum AbilityState {
        Enabled(true, false),
        JustEnabled(true, true),
        Disabled(false, false),
        JustDisabled(false, true),
        ;

        public final boolean enabled;
        public final boolean just;
        AbilityState(boolean enabled, boolean just) {
            this.enabled = enabled;
            this.just = just;
        }
    }

}
