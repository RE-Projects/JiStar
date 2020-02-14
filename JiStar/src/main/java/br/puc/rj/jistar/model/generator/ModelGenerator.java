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
import br.puc.rj.jistar.core.elements.Goal;
import java.io.FileWriter;

/**
 *
 * @author anamm
 */
public class ModelGenerator {
    
    public static void main(String args[]) {
        try {
            List<Class<Runnable>> classList = listClasses(args[0]);
            switch (args[1]) {
                case "-html": {
                    htmlResult(classList, args[0]);
                    break;
                }
                
            }
        } catch (Exception ex) {
            Logger.getLogger(ModelGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void htmlResult(List<Class<Runnable>> classList, String projectPath) {
        String fileName = projectPath + "\\index.html";
        String index = "<html>";
        classList.forEach(clazz -> {            
            String htmlText="<html>";            
            String htmlName=projectPath + "\\";
            String htmlHead="";
            String htmlBody="<body>";
            for (Actor a : clazz.getAnnotationsByType(Actor.class)) {
                htmlName+=a.name()+".html";
                htmlHead+="<head><title>"+a.name()+"</title></head>";
                htmlBody+="<h1>"+a.name()+"</h1><br><hr>";
                htmlBody+="<h2>Actor Type: "+a.type().name()+"<h2><br><br>";
            }
            htmlBody+="Goals:<br>";
            for (Goal g : clazz.getAnnotationsByType(Goal.class)) {                                
                htmlBody+="- "+g.description()+"<br>";
                
            }
            htmlBody+="</body>";
            htmlText+=htmlHead+htmlBody+"</html>";
            save(htmlText,htmlName);
            
        });
        index += "</html>";
        save(index, fileName);
    }

    public static void save(String texto, String fileName) {
        try {
            FileWriter fw = new FileWriter(new File(fileName));
            fw.write(texto);
            fw.flush();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static List<Class<Runnable>> listClasses(String javaFilesPath) throws Exception {
        List<Class<Runnable>> classList = new ArrayList<Class<Runnable>>();
        JavaDynamicCompiler<Runnable> compiler = new JavaDynamicCompiler<Runnable>();
        try (Stream<Path> walk = Files.walk(Paths.get(javaFilesPath))) {
            
            List<String> result = walk.map(x -> x.toString())
                    .filter(f -> f.endsWith(".java")).collect(Collectors.toList());
            
            result.forEach(javaFilePath -> {
                try {
                    Path path = Paths.get(javaFilePath);
                    byte[] bytes = Files.readAllBytes(path);
                    String fonte = new String(bytes, StandardCharsets.UTF_8);
                    String nomeClasse = path.getFileName().toString().split("\\.")[0];
                    Class<Runnable> clazz = compiler.compile(null, nomeClasse, fonte);
                    classList.add(clazz);
                } catch (Exception ex) {
                    Logger.getLogger(ModelGenerator.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return classList;
    }
}
