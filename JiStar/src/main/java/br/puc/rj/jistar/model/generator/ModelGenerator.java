/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.puc.rj.jistar.model.generator;

import br.puc.rj.jistar.model.generator.JavaDynamicCompiler;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import br.puc.rj.jistar.core.elements.Actor;

/**
 *
 * @author anamm
 */
public class ModelGenerator {

    public static void main(String args[]) {
        try (Stream<Path> walk = Files.walk(Paths.get("C:\\class_test"))) {

            List<String> result = walk.map(x -> x.toString())
                    .filter(f -> f.endsWith(".java")).collect(Collectors.toList());

            result.forEach(javaFilePath -> {
                try {
                    listClasses(javaFilePath);
                } catch (Exception ex) {
                    Logger.getLogger(ModelGenerator.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private static void listClasses(String javaFilePath) throws Exception {
        Path path = Paths.get(javaFilePath);
        byte[] result = Files.readAllBytes(path);
        String fonte = new String(result, StandardCharsets.UTF_8);
        String nomeClasse = path.getFileName().toString().split("\\.")[0];
        JavaDynamicCompiler<Runnable> compiler = new JavaDynamicCompiler<Runnable>();
        
        
        Class<Runnable> clazz = compiler.compile(null, nomeClasse, fonte);
        System.out.print("\nAnotado? "+clazz.isAnnotationPresent(Actor.class)+"\n\n");
        Annotation[] annotations = clazz.getAnnotations();
        
        for (Annotation annotation : annotations) {
            if (annotation instanceof Actor) {
                Actor myAnnotation = (Actor) annotation;
                System.out.println("name: " + myAnnotation.name());
                System.out.println("value: " + myAnnotation.type().name());
            }
        }

        //tentativa com javaparser
        /*File javaFile = new File(javaFilePath);
        //List<ClassOrInterfaceDeclaration> classes = new ArrayList<ClassOrInterfaceDeclaration>();
        try {
            FileInputStream inputStream = new FileInputStream(javaFilePath);
                    CompilationUnit cu = JavaParser.parse(inputStream);
                    cu.findAll(ClassOrInterfaceDeclaration.class).forEach(be -> {
                        System.out.print("\n"+be.getName());
                        be.accept(new ClassVisitor(), null);
                    });
        }catch (FileNotFoundException fnfEx) {
            Logger.getLogger(ModelGenerator.class.getName()).log(Level.SEVERE, null, fnfEx);
        } catch (Exception ex) {
            Logger.getLogger(ModelGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }
}
