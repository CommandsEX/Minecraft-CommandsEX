package com.github.ikeirnez.commandsex.api.annotations;

/**
 * Class to handle various functionality with the Builder
 * @author iKeirNez
 */
public @interface Builder {
    boolean show() default true;
    boolean include() default false;
    String description() default "Placeholder text for command %b";
    String depends() default "";
}
