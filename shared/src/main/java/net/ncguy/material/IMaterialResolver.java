package net.ncguy.material;

import java.util.Optional;

public interface IMaterialResolver {

    Optional<EmptyMaterial> Resolve(String name);

}
