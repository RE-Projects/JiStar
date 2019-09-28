/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jistar.core.elements;

import java.lang.annotation.Documented;

/**
 *
 * @author anamm
 */
@Documented
public @interface Goals {
    Goal[] value();
}
