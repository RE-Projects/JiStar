/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.puc.rj.jistar.model.generator;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
/**
 *
 * @author anamm
 */
public class JavaDynamicException extends RuntimeException{
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * O coletor de informações da compilação
	 */
	private DiagnosticCollector<JavaFileObject> collector;

	public JavaDynamicException(String message) {
		super(message);
	}

	public JavaDynamicException(String message, DiagnosticCollector<JavaFileObject> collector) {
		super(message);
		this.collector = collector;
	}
	
	public JavaDynamicException(Throwable e, DiagnosticCollector<JavaFileObject> collector) {
		super(e);
		this.collector = collector;
	}

	public String getCompilationError() {
		StringBuilder sb = new StringBuilder();
		for (Diagnostic<? extends JavaFileObject> diagnostic : collector.getDiagnostics()) {
			sb.append(diagnostic.getMessage(null));
		}
		return sb.toString();
	}
	
	@Override
	public String toString() {
	  return getCompilationError();
	}
}
