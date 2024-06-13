package ru.practicum.shareit.util;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.TYPE_USE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EndStartDateValidator.class)
@Documented
public @interface EndStartDateConstraint {
    String message() default "End date cant be before start date.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
