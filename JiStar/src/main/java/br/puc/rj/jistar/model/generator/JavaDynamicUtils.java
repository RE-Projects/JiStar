/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.puc.rj.jistar.model.generator;

import java.net.URI;
import java.net.URISyntaxException;
import javax.tools.JavaFileObject.Kind;
//import org.apache.commons.lang.StringUtils;

/**
 *
 * @author anamm
 */
public enum JavaDynamicUtils {
    INSTANCE;

    /**
     * cria um objeto URI a partir de uma String
     *
     * @param str - caminho da classe
     * @return
     */
    public URI createURI(String str) {
        try {
            return new URI(str);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * retorna o nome completo da classe
     *
     * @param packageName - pacote java
     * @param className - nome da Classe
     * @return nome completo da Classe
     */
    public String getQualifiedClassName(String packageName,
            String className) {        
        if (packageName==null||packageName.isEmpty()) {
            return className;
        } else {
            return packageName + "." + className;
        }
    }

    public String getClassNameWithExt(String className) {
        return className + Kind.SOURCE.extension;
    }

}
