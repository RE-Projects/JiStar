/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.puc.rj.jistar.model.generator;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import javax.tools.SimpleJavaFileObject;
/**
 *
 * @author anamm
 */
public class JavaDynamicBean extends SimpleJavaFileObject{
    
  
  private String source;
  
  private ByteArrayOutputStream byteCode = new ByteArrayOutputStream();
  
  
  public JavaDynamicBean(String baseName, String source) {
    super(JavaDynamicUtils.INSTANCE.createURI
     (JavaDynamicUtils.INSTANCE.getClassNameWithExt
     (baseName)), Kind.SOURCE);
    this.source = source;
  }
              
  public JavaDynamicBean(String name) {
    super(JavaDynamicUtils.INSTANCE.createURI(name), Kind.CLASS);
  }
  
  @Override
  public String getCharContent(boolean ignoreEncodingErrors) {
    return source;
  }
  
  @Override
  public OutputStream openOutputStream() {
    return byteCode;
  }
  
  public byte[] getBytes() {
    return byteCode.toByteArray();
  }

}
