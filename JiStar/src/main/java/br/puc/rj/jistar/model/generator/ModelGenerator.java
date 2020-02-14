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
        try {
            listClasses(args[0]);
        } catch (Exception ex) {
            Logger.getLogger(ModelGenerator.class.getName()).log(Level.SEVERE, null, ex);
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
                    Path path = Paths.get(javaFilesPath);
                    byte[] bytes = Files.readAllBytes(path);
                    String fonte = new String(bytes, StandardCharsets.UTF_8);
                    String nomeClasse = path.getFileName().toString().split("\\.")[0];
                    Class<Runnable> clazz = compiler.compile(null, nomeClasse, fonte);

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
