/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.puc.rj.jistar.model.generator;

import java.util.Arrays;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 *
 * @author anamm
 */
public class JavaDynamicCompiler<T> {
    
  
  private JavaCompiler compiler;
  
  private JavaDynamicManager javaDinamicoManager;
  
  private JavaDynamicClassLoader classLoader;
  
  private DiagnosticCollector<JavaFileObject> diagnostics;
  
  public JavaDynamicCompiler() throws JavaDynamicException {
    compiler = ToolProvider.getSystemJavaCompiler();
    if (compiler == null) { 
            throw new JavaDynamicException("Cmpiler no found.");
    }
  
    classLoader = new JavaDynamicClassLoader
     (getClass().getClassLoader());
    diagnostics = new DiagnosticCollector<JavaFileObject>();
  
      StandardJavaFileManager standardFileManager = compiler
        .getStandardFileManager(diagnostics, null, null);
    javaDinamicoManager = new JavaDynamicManager
    (standardFileManager, classLoader);
  }
  
  @SuppressWarnings("unchecked")
  public synchronized Class<T> compile(String packageName, 
      String className,
      String javaSource) throws JavaDynamicException
  {
    try {
      String qualifiedClassName = 
        JavaDynamicUtils.INSTANCE.getQualifiedClassName(
          packageName, className);
      JavaDynamicBean sourceObj = new JavaDynamicBean
        (className, javaSource);
      JavaDynamicBean compiledObj = new JavaDynamicBean
       (qualifiedClassName);
      javaDinamicoManager.setSources(sourceObj, compiledObj);
  
        JavaCompiler.CompilationTask task = compiler.getTask
       (null, javaDinamicoManager, diagnostics,
          null, null, Arrays.asList(sourceObj));
      boolean result = task.call();
  
      if (!result) { 
          throw new JavaDynamicException
          ("A compilação falhou", diagnostics); 
    }
      
      Class<T> newClass = (Class<T>) 
       classLoader.loadClass(qualifiedClassName);
      return newClass;
  
    }
    catch (Exception exception) {
      throw new JavaDynamicException(exception, diagnostics);
    }
  }

}
