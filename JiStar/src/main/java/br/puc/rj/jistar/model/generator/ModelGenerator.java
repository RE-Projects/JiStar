/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jistar.model.generator;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author anamm
 */
public class ModelGenerator {
    
    public static void main(String args[]){
    try (Stream<Path> walk = Files.walk(Paths.get("C:\\class_test"))) {

		      List<String> result = walk.map(x -> x.toString())
				.filter(f -> f.endsWith(".java")).collect(Collectors.toList());

		result.forEach(System.out::println);
                Reflections reflections = new Reflections("my.project.prefix");

 Set<Class<? extends Object>> allClasses = 
     reflections.getSubTypesOf(Object.class);

	} catch (IOException e) {
		e.printStackTrace();
	}    
    }
}
