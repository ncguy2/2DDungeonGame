package net.ncguy.entity.component;

import com.badlogic.gdx.Input;
import net.ncguy.input.InputAction;

import java.util.Optional;

/**
 * Defines the input methods to be used by the input controller to drive the player entity
 */
public class InputComponent extends EntityComponent {

    public InputAction keyUp = InputAction.Key("Up", Input.Keys.W);
    public InputAction keyDown = InputAction.Key("Down", Input.Keys.S);
    public InputAction keyLeft = InputAction.Key("Left", Input.Keys.A);
    public InputAction keyRight = InputAction.Key("Right", Input.Keys.D);

    public InputAction keyAbility0 = InputAction.Key("Ability 1", Input.Keys.NUM_1);
    public InputAction keyAbility1 = InputAction.Key("Ability 2", Input.Keys.NUM_2);
    public InputAction keyAbility2 = InputAction.Key("Ability 3", Input.Keys.NUM_3);
    public InputAction keyAbility3 = InputAction.Key("Ability 4", Input.Keys.NUM_4);

    public InputAction keyInventory = InputAction.Key("Inventory", Input.Keys.TAB);
    public InputAction keyMenu = InputAction.Key("Menu", Input.Keys.ESCAPE);

    public InputAction keyInteract = InputAction.Key("Interact", Input.Keys.F);

    public InputAction keyAttackPrimary = InputAction.Button("Primary Attack", Input.Buttons.LEFT);
    public InputAction keyAttackSecondary = InputAction.Button("Secondary Attack", Input.Buttons.RIGHT);

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
