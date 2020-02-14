/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.puc.rj.jistar.model.generator;

import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author anamm
 */
public class JavaDynamicClassLoader extends ClassLoader{
     /**
   * Representa o arquivo e o bytecode 
   * Guiado pela chave e o valor.
   * Sendo que a chave e o qualified e o valor e o bytecode
   * @see JavaDinamicoBean
   */  
  private Map<String, JavaDynamicBean> classes = new HashMap<>();

  public JavaDynamicClassLoader(ClassLoader parentClassLoader) {
    super(parentClassLoader);
    
  }
  
  public void addClass(JavaDynamicBean compiledObj) {
    classes.put(compiledObj.getName(), compiledObj);
  }

  @Override
  public Class<?> findClass(String qualifiedClassName)
      throws ClassNotFoundException
  {
	JavaDynamicBean bean = classes.get(qualifiedClassName);
	
        if (bean == null) {
		return super.findClass(qualifiedClassName);
	}
    byte[] bytes = bean.getBytes();
    return defineClass(qualifiedClassName, bytes, 0, bytes.length);
  }
}
