package com.github.ikeirnez.commandsex.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

/**
 * Required for any command class
 */
public @interface ACommand {
    
    /**
     * @return The primary command
     */
    String command();
    
    /**
     * @return A brief description of the command
     */
    String description();
    
    /**
     * @return A comma separated list of command aliases, cex_<command> will be automagically added
     */
    String aliases() default "";
    
    /**
     * @return The permission node required for the command, don't forget to add this permission in init()
     */
    String permission() default "";
    
    /**
     * @return This only needs to be used if you have 1 or more paremeters for the command, /<command> is added automagically
     */
    String usage() default "";
    
}
