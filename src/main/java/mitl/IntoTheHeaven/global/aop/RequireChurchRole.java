package mitl.IntoTheHeaven.global.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import mitl.IntoTheHeaven.domain.enums.ChurchRole;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireChurchRole {
    ChurchRole value();
}
