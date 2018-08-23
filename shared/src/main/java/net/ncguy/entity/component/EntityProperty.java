package net.ncguy.entity.component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EntityProperty {

    Class<?> Type();

    String Name() default "";
    String Description() default "";
    String Category() default "";
    boolean Editable() default true;

    Class<?> ElementType() default Object.class;

}
