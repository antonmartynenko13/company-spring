package com.martynenko.anton.company.openapi;

import io.swagger.v3.oas.annotations.Operation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)

@CrudGetAll
@Operation(summary = "Get available",
    description = "Retrive list of available employees for assigning onto the new project. "
        + "Employee is available if he/she doesn’t have active projects. "
        + "Accept period (integer, days) as a parameter and return list "
        + "of employees available now or within the period. "
        + "If parameter not provided - return only available on the current moment"
)
public @interface GetAvailable {

}
