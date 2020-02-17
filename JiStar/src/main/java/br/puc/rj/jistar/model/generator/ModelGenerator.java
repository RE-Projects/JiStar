/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.puc.rj.jistar.model.generator;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import br.puc.rj.jistar.core.elements.Actor;
import br.puc.rj.jistar.core.elements.Goal;
import br.puc.rj.jistar.core.elements.Resource;
import br.puc.rj.jistar.core.elements.Softgoal;
import br.puc.rj.jistar.core.elements.Task;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

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
        String index = "<html>\n<head>\n<title>Index</title>\n</head>\n<body>";        
        save(index, fileName);
        classList.forEach(clazz -> {
            String htmlText = "<html>";
            String htmlName = projectPath + "\\";
            String htmlHead = "";
            String htmlBody = "<body>";
            String link="";
            for (Actor a : clazz.getAnnotationsByType(Actor.class)) {
                htmlName += a.name() + ".html";
                link = "<a href=\'" + a.name() + ".html\'>" + a.name() +" "+ a.type().name()+ "</a>";
                htmlHead += "<head><title>" + a.name() + "</title></head>";
                htmlBody += "<h1>" + a.name() + "</h1><br><hr>";
                htmlBody += "<h2>Actor Type: " + a.type().name() + "<h2><br><br>";
            }
            htmlBody += "<div style=\"background-color:#ffffe6;color:black;padding:20px;\">Goals:<br>";
            for (Goal g : clazz.getAnnotationsByType(Goal.class)) {
                htmlBody += g.name() + " - " + g.description() + "<br>";
            }
            htmlBody += "</div>";
            htmlBody += "<div style=\"background-color:#e6f2ff;color:black;padding:20px;\">SoftGoals:<br>";
            for (Softgoal s : clazz.getAnnotationsByType(Softgoal.class)) {
                htmlBody += s.name() + " - " + s.description() + "<br>";
            }
            htmlBody += "</div>";
            htmlBody += "<div style=\"background-color:#e6ffe6;color:black;padding:20px;\">Resources:<br>";
            for (Field f : clazz.getFields()) {
                for (Resource r : f.getAnnotationsByType(Resource.class)) {
                    htmlBody += " - " + r.name() + "<br>";
                }
            }
            htmlBody += "</div>";
            htmlBody += "<div style=\"background-color:#ffe6f7;color:black;padding:20px;\">Tasks:<br>";
            for (Method m : clazz.getMethods()) {
                for (Task t : m.getAnnotationsByType(Task.class)) {
                    htmlBody += " - " + t.name() + "<br>";
                }
            }
            htmlBody += "</div>";
            htmlBody += "</body>";
            htmlText += htmlHead + htmlBody + "</html>";
            save(htmlText, htmlName);
            //atualiza o index            
            addContentIndex(link,projectPath);

        });
        addContentIndex("\n</body>\n</html>", projectPath);
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

    public static void addContentIndex(String content, String projectPath) {
        File f = new File(projectPath+"\\index.html");

        FileInputStream fs = null;
        InputStreamReader in = null;
        BufferedReader br = null;

        StringBuffer sb = new StringBuffer();

        String textinLine;

        try {
            fs = new FileInputStream(f);
            in = new InputStreamReader(fs);
            br = new BufferedReader(in);

            while (true) {
                textinLine = br.readLine();
                if (textinLine == null) {
                    break;
                }
                sb.append(textinLine);
            }
            int cnt1 = sb.indexOf("<body>");
            //sb.replace(cnt1,cnt1+textToEdit1.length(),"New Append text");
            sb.append("<br>"+content);
            fs.close();
            in.close();
            br.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileWriter fstream = new FileWriter(f);
            BufferedWriter outobj = new BufferedWriter(fstream);
            outobj.write(sb.toString());
            outobj.close();

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
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
