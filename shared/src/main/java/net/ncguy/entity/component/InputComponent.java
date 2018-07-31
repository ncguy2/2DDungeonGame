package net.ncguy.entity.component;

import com.badlogic.gdx.Input;
import net.ncguy.input.InputAction;

import java.util.Optional;

/**
 * Defines the input methods to be used by the input controller to drive the player entity
 */
public class InputComponent extends EntityComponent {

    @EntityProperty(Type = InputAction.class, Category = "Input", Description = "Key to move up", Name = "Key Up")
    public InputAction keyUp = InputAction.Key("Up", Input.Keys.W);
    @EntityProperty(Type = InputAction.class, Category = "Input", Description = "Key to move down", Name = "Key Down")
    public InputAction keyDown = InputAction.Key("Down", Input.Keys.S);
    @EntityProperty(Type = InputAction.class, Category = "Input", Description = "Key to move left", Name = "Key Left")
    public InputAction keyLeft = InputAction.Key("Left", Input.Keys.A);
    @EntityProperty(Type = InputAction.class, Category = "Input", Description = "Key to move right", Name = "Key Right")
    public InputAction keyRight = InputAction.Key("Right", Input.Keys.D);

    @EntityProperty(Type = InputAction.class, Category = "Input", Description = "Key to use the ability in slot 1", Name = "Key ability 1")
    public InputAction keyAbility0 = InputAction.Key("Ability 1", Input.Keys.NUM_1);
    @EntityProperty(Type = InputAction.class, Category = "Input", Description = "Key to use the ability in slot 2", Name = "Key ability 2")
    public InputAction keyAbility1 = InputAction.Key("Ability 2", Input.Keys.NUM_2);
    @EntityProperty(Type = InputAction.class, Category = "Input", Description = "Key to use the ability in slot 3", Name = "Key ability 3")
    public InputAction keyAbility2 = InputAction.Key("Ability 3", Input.Keys.NUM_3);
    @EntityProperty(Type = InputAction.class, Category = "Input", Description = "Key to use the ability in slot 4", Name = "Key ability 4")
    public InputAction keyAbility3 = InputAction.Key("Ability 4", Input.Keys.NUM_4);

    @EntityProperty(Type = InputAction.class, Category = "Input", Description = "Key to open the inventory", Name = "Key Inventory")
    public InputAction keyInventory = InputAction.Key("Inventory", Input.Keys.TAB);
    @EntityProperty(Type = InputAction.class, Category = "Input", Description = "Key to open the menu", Name = "Key Menu")
    public InputAction keyMenu = InputAction.Key("Menu", Input.Keys.ESCAPE);

    @EntityProperty(Type = InputAction.class, Category = "Input", Description = "Key to interact", Name = "Key Interact")
    public InputAction keyInteract = InputAction.Key("Interact", Input.Keys.F);

    @EntityProperty(Type = InputAction.class, Category = "Input", Description = "Key to use the equipped primary attack", Name = "Key Primary Attack")
    public InputAction keyAttackPrimary = InputAction.Button("Primary Attack", Input.Buttons.LEFT);
    @EntityProperty(Type = InputAction.class, Category = "Input", Description = "Key to use the equipped secondary attack", Name = "Key Secondary Attack")
    public InputAction keyAttackSecondary = InputAction.Button("Secondary Attack", Input.Buttons.RIGHT);

    public InputComponent() {
        this("Unnamed Scene component");
    }

    public InputComponent(String name) {
        super(name);
    }

    @Override
    public boolean CanReplicate() {
        return false;
    }

    public Optional<InputAction> GetAbilityKey(int idx) {
        switch(idx) {
            case 0: return Optional.of(keyAbility0);
            case 1: return Optional.of(keyAbility1);
            case 2: return Optional.of(keyAbility2);
            case 3: return Optional.of(keyAbility3);
        }
        return Optional.empty();
    }

}
