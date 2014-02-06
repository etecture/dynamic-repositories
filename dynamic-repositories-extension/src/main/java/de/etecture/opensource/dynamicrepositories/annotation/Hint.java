package de.etecture.opensource.dynamicrepositories.annotation;

/**
 *
 * @author rhk
 * @version
 * @since
 */
public @interface Hint {

    String name();

    String value() default "";

    Class<? extends HintValueGenerator> generator() default DefaultHintValueGenerator.class;
}
