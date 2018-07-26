package net.ncguy.material;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MaterialResolver implements IMaterialResolver {

    List<Material> materials;

    public MaterialResolver() {
        materials = new ArrayList<>();
    }

    public void Register(Material mtl) {
        materials.add(mtl);
    }

    @Override
    public Optional<EmptyMaterial> Resolve(String name) {
        return materials.stream()
                .filter(mtl -> mtl.name.equalsIgnoreCase(name))
                .findFirst()
                .map(e -> e);
    }
}
