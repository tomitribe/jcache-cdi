package org.tomitribe.jcache.cdi;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Qualifier
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface DeclarativeCache
{
    @Nonbinding String value() default "";

    @Nonbinding Class<?> keyType() default Object.class;

    @Nonbinding Class<?> valueType() default Object.class;
}
