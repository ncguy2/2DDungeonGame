package net.ncguy.material;

import java.util.Optional;

public class MaterialResolverWithDefault extends MaterialResolver {

    public Material defaultMaterial;

    public MaterialResolverWithDefault(Material defaultMaterial) {
        this.defaultMaterial = defaultMaterial;
    }

    @Override
    public Optional<EmptyMaterial> Resolve(String name) {
        Optional<EmptyMaterial> resolve = super.Resolve(name);
        if (resolve.isPresent())
            return resolve;
        return Optional.of(defaultMaterial);
    }
}
