package net.ncguy.lib.net.shared;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PacketInfo {

    String Name() default "";
    Class<? extends PacketReceivedHandler> Handler() default PacketReceivedHandler.class;
}
