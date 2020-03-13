/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.puc.rj.jistar.core.relationship;

import br.puc.rj.jistar.core.elements.Actor;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author anamm
 */
@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD,ElementType.TYPE_USE})
public @interface IsPartOf {
    Actor[] part();
}