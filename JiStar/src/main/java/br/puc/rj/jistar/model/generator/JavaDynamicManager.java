/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.puc.rj.jistar.model.generator;
import java.io.IOException;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
/**
 *
 * @author anamm
 */
public class JavaDynamicManager extends ForwardingJavaFileManager<JavaFileManager>{
     private JavaDynamicClassLoader classLoader;
  
  /**
   * codigo não compilado
   */
  private JavaDynamicBean codigoFonte;
  /**
   * código fonte compilado
   */
  private JavaDynamicBean arquivoCompilado;
  
  public JavaDynamicManager(JavaFileManager fileManager, JavaDynamicClassLoader classLoader)
  {
    super(fileManager);
    this.classLoader = classLoader;
  }

  public void setSources(JavaDynamicBean sourceObject, JavaDynamicBean compiledObject) {
    this.codigoFonte = sourceObject;
    this.arquivoCompilado = compiledObject;
    this.classLoader.addClass(compiledObject);
  }

  @Override
  public FileObject getFileForInput(Location location, String packageName,
      String relativeName) throws IOException
  {
    return codigoFonte;
  }

  @Override
  public JavaFileObject getJavaFileForOutput(Location location,
      String qualifiedName, Kind kind, FileObject outputFile)
      throws IOException
  {
    return arquivoCompilado;
  }

  @Override
  public ClassLoader getClassLoader(Location location) {
    return classLoader;
  }
}
