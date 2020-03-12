/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.puc.rj.jistar.core.elements;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 *
 * @author anamm
 */
@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Repeatable(Softgoals.class)
@Target(value={ElementType.FIELD,ElementType.METHOD})
public @interface Softgoal {
    String name();
    String description() default "";
}
