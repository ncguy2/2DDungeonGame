package net.ncguy.entity.aspect;

import java.util.Optional;

public interface IAspectProvider {

    default <T> Optional<Aspect<T>> GetAspect(AspectKey<T> key) {
        return Optional.ofNullable(ProvideAspect(key));
    }

    <T> Aspect<T> ProvideAspect(AspectKey<T> key);

}
