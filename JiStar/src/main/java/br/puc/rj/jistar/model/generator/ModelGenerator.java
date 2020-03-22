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
import br.puc.rj.jistar.core.relationship.Contribution;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 *
 * @author anamm
 */
public class ModelGenerator {

    static StringBuffer outputModel = new StringBuffer();
    static StringBuffer outputOrphans = new StringBuffer("\n\"orphans\": [],");
    static StringBuffer outputDependencies = new StringBuffer("\n\"dependencies\": [],");
    static StringBuffer outputLinks = new StringBuffer("\n\"links\": [");
    static StringBuffer outputDisplay = new StringBuffer("\n\"display\": [],");
    static StringBuffer outputActors = new StringBuffer("");
    static int x = 10, y = 10;

    public static void main(String args[]) {
        try {
            List<Class<Runnable>> classList = listClasses(args[0]);
            switch (args[1]) {
                case "-html": {
                    htmlResult(classList, args[0]);
                    break;
                }
                case "-pistar": {
                    pistarResult(classList, args[0]);
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
            String link = "";
            for (Actor a : clazz.getAnnotationsByType(Actor.class)) {
                htmlName += a.name() + ".html";
                link = "<a href=\'" + a.name() + ".html\'>" + a.name() + " " + a.type().name() + "</a>";
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
            addContentIndex(link, projectPath);

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
        File f = new File(projectPath + "\\index.html");

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
            sb.append("<br>" + content);
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
                    System.out.print("\njavaFilePath: " + javaFilePath);
                    byte[] bytes = Files.readAllBytes(path);
                    String fonte = new String(bytes, StandardCharsets.UTF_8);
                    System.out.print("\nFonte: " + fonte);
                    String nomeClasse = path.getFileName().toString().split("\\.")[0];
                    Class<Runnable> clazz = compiler.compile("testejistar", nomeClasse, fonte);
                    System.out.print("\n" + clazz.getDeclaredAnnotation(Actor.class));
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

    private static void pistarResult(List<Class<Runnable>> classList, String projectPath) {

        String fileName = projectPath + "\\goal_model.txt";
        outputModel.append("{\n"
                + "  \"actors\": [\n");
        System.out.print("Qtde classes: " + classList.size());
        classList.forEach(clazz -> {

            for (Actor a : clazz.getAnnotationsByType(Actor.class)) {
                String actorType;
                switch (a.type().name()) {
                    case "AGENT": {
                        actorType = "Agent";
                        break;
                    }
                    case "ROLE": {
                        actorType = "Role";
                        break;
                    }
                    default: {
                        actorType = "Actor";
                    }
                }
                outputActors.append("\n{\n"
                        + "      \"id\": \"" + UUID.randomUUID() + "\",\n"
                        + "      \"text\": \"" + a.name() + "\",\n"
                        + "      \"type\": \"istar." + actorType + "\",\n"
                        + "      \"x\": " + x + ",\n"
                        + "      \"y\": " + y + ",\n"
                        + "\"customProperties\": {\n"
                        + "        \"Description\": \"\"\n"
                        + "      },"
                        + "\n\"nodes\": [\n");
                x += 50;
                y += 50;
            }

            for (Goal g : clazz.getAnnotationsByType(Goal.class)) {

                outputActors.append("\n{\n"
                        + "      \"id\": \"" + UUID.randomUUID() + "\",\n"
                        + "      \"text\": \"" + g.name() + "\",\n"
                        + "      \"type\": \"istar." + "Goal" + "\",\n"
                        + "      \"x\": " + x + ",\n"
                        + "      \"y\": " + y + ",\n"
                        + "\"customProperties\": {\n"
                        + "        \"Description\": \"" + g.description() + "\"\n"
                        + "      }},");
                x += 5;
                y += 5;
            }

            for (Softgoal s : clazz.getAnnotationsByType(Softgoal.class)) {
                outputActors.append("\n{\n"
                        + "      \"id\": \"" + UUID.randomUUID() + "\",\n"
                        + "      \"text\": \"" + s.name() + "\",\n"
                        + "      \"type\": \"istar." + "Softgoal" + "\",\n"
                        + "      \"x\": " + x + ",\n"
                        + "      \"y\": " + y + ",\n"
                        + "\"customProperties\": {\n"
                        + "        \"Description\": \"" + s.description() + "\"\n"
                        + "      }},");
                x += 5;
                y += 5;
            }
            for (Resource r : clazz.getAnnotationsByType(Resource.class)) {
                outputActors.append("\n{\n"
                        + "      \"id\": \"" + UUID.randomUUID() + "\",\n"
                        + "      \"text\": \"" + r.name() + "\",\n"
                        + "      \"type\": \"istar." + "Resource" + "\",\n"
                        + "      \"x\": " + x + ",\n"
                        + "      \"y\": " + y + ",\n"
                        + "\"customProperties\": {\n"
                        + "        \"Description\": \"\"\n"
                        + "      }},");
                x += 5;
                y += 5;
            }

            for (Field f : clazz.getFields()) {
                for (Resource r : f.getAnnotationsByType(Resource.class)) {
                    outputActors.append("\n{\n"
                            + "      \"id\": \"" + UUID.randomUUID() + "\",\n"
                            + "      \"text\": \"" + r.name() + "\",\n"
                            + "      \"type\": \"istar." + "Resource" + "\",\n"
                            + "      \"x\": " + x + ",\n"
                            + "      \"y\": " + y + ",\n"
                            + "\"customProperties\": {\n"
                            + "        \"Description\": \"\"\n"
                            + "      }},");
                    x += 5;
                    y += 5;
                }
                for (Softgoal s : f.getAnnotationsByType(Softgoal.class)) {
                    outputActors.append("\n{\n"
                            + "      \"id\": \"" + UUID.randomUUID() + "\",\n"
                            + "      \"text\": \"" + s.name() + "\",\n"
                            + "      \"type\": \"istar." + "Quality" + "\",\n"
                            + "      \"x\": " + x + ",\n"
                            + "      \"y\": " + y + ",\n"
                            + "\"customProperties\": {\n"
                            + "        \"Description\": \"" + s.description() + "\"\n"
                            + "      }},");
                    x += 5;
                    y += 5;
                }
            }

            for (Method m : clazz.getMethods()) {
                UUID taskUUID = null;
                UUID softgoalUUID = null;
                for (Task t : m.getAnnotationsByType(Task.class)) {
                    taskUUID = UUID.randomUUID();
                    outputActors.append("\n{\n"
                            + "      \"id\": \"" + taskUUID + "\",\n"
                            + "      \"text\": \"" + t.name() + "\",\n"
                            + "      \"type\": \"istar." + "Task" + "\",\n"
                            + "      \"x\": " + x + ",\n"
                            + "      \"y\": " + y + ",\n"
                            + "\"customProperties\": {\n"
                            + "        \"Description\": \"" + t.description() + "\"\n"
                            + "      }},");
                    x += 5;
                    y += 5;
                }
                for (Contribution c : m.getAnnotationsByType(Contribution.class)) {
                    softgoalUUID = UUID.randomUUID();
                    outputActors.append("\n{\n"
                            + "      \"id\": \"" + softgoalUUID + "\",\n"
                            + "      \"text\": \"" + c.softgoal().name() + "\",\n"
                            + "      \"type\": \"istar." + "Quality" + "\",\n"
                            + "      \"x\": " + x + ",\n"
                            + "      \"y\": " + y + ",\n"
                            + "\"customProperties\": {\n"
                            + "        \"Description\": \"" + c.softgoal().description() + "\"\n"
                            + "      }},");
                    outputLinks.append("\n{\n"
                            + "      \"id\": \"" + UUID.randomUUID() + "\",\n"
                            + "      \"type\": \"istar.ContributionLink\",\n"
                            + "      \"source\": \"" + taskUUID + "\",\n"
                            + "      \"target\": \"" + softgoalUUID + "\",\n"
                            + "\"label\": \""+c.value().toString().toLowerCase()+"\"},");
                    x += 5;
                    y += 5;
                }
            }
            outputActors.deleteCharAt(outputActors.length() - 1);
            outputActors.append("]},");

        });
        outputActors.deleteCharAt(outputActors.length() - 1);
        outputActors.append(" ],\n");
        outputModel.append(outputActors);
        outputModel.append(outputOrphans);
        outputModel.append(outputDependencies);
        outputLinks.deleteCharAt(outputLinks.length() - 1);
        outputLinks.append(" ],\n");
        outputModel.append(outputLinks);
        outputModel.append(outputDisplay);
        DateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
        Date date = new Date();
        StringBuffer outputConfModelo = new StringBuffer("\n"
                + "  \"tool\": \"pistar.2.0.0\",\n"
                + "  \"istar\": \"2.0\",\n"
                + "  \"saveDate\": \"" + dateFormat.format(date) + "\",\n"
                + "  \"diagram\": {\n"
                + "    \"width\": 2000,\n"
                + "    \"height\": 1300,\n"
                + "      \"name\": \" \",\n"
                + "    \"customProperties\": {\n"
                + "      \"Description\": \" \" \n"
                + "    }\n"
                + "  }\n"
                + "} ");
        outputModel.append(outputConfModelo);
        save(outputModel.toString(), fileName);

        /*
        for (Node no : modelo.getNodes()) {
            if (no instanceof Actor) {
                Actor ator = (Actor) no;
                String tipoAtor = (ator.getType().name().equals("General")) ? "Actor" : ator.getType().name();
                saida.append("    \n{\n"
                        + "      \"id\": \"" + ator.getId() + "\",\n"
                        + "      \"text\": \"" + ator.getName() + "\",\n"
                        + "      \"type\": \"istar." + tipoAtor + "\",\n"
                        + "      \"x\": " + x + ",\n"
                        + "      \"y\": " + y + ",\n"
                        + "      \"nodes\": [\n");

                for (SRElement element : ator.getSrElements()) {
                    String istarType = element.getType().name();
                    switch (element.getType().name()) {
                        case "SoftGoal":
                            istarType = "Quality";
                            break;
                        default:
                            istarType = element.getType().name();
                    }
                    saida.append("    \n{\n"
                            + "      \"id\": \"" + element.getId() + "\",\n"
                            + "      \"text\": \"" + element.getName() + "\",\n"
                            + "      \"type\": \"istar." + istarType + "\",\n"
                            + "      \"x\": " + x + ",\n"
                            + "      \"y\": " + y + "},");

                    x += 20;

                }
                saida = saida.deleteCharAt(saida.length() - 1);
                saida.append("]},");
                x += 50;
                y += 50;

            }
        }
        saida = saida.deleteCharAt(saida.length() - 1);
        saida.append("  ],\n"
                + " \"dependencies\": [\n");
        //Realiza o mapeamento dos links
        for (Node no : modelo.getNodes()) {
            if (no instanceof Dependum) {
                Dependum d = (Dependum) no;

                String istarType = d.getType().name();
                switch (d.getType().name()) {
                    case "SoftGoal":
                        istarType = "Quality";
                        break;
                    default:
                        istarType = d.getType().name();
                }

                for (Link link : d.getLinks()) {

                    saida.append("    \n{\n"
                            + "      \"id\": \"" + link.getId() + "\",\n"
                            + "      \"text\": \"" + d.getName() + "\",\n"
                            + "      \"type\": \"istar." + istarType + "\",\n"
                            + "      \"x\": " + x + ",\n"
                            + "      \"y\": " + y + ",\n"
                            + "      \"source\": \"" + link.getFrom().getId() + "\",\n"
                            + "      \"target\": \"" + link.getTo().getId() + "\"},");
                    x += 5;
                    y += 5;
                }
            }
        }
        saida = saida.deleteCharAt(saida.length() - 1);
        saida.append(" ],\n\"links\": [\n");
        //Realiza o mapeamento dos links
        for (Node no : modelo.getNodes()) {
            if (no instanceof Actor) {
                Actor ator = (Actor) no;
                for (SRElement element : ator.getSrElements()) {
                    for (Link link : element.getLinks()) {

                        String type = "";
                        if (link instanceof DependumLink) {
                            type = "DependencyLink";
                        }
                        if (link instanceof MeansEnd) {
                            type = "OrRefinementLink";
                        }
                        if ((link instanceof TaskDecomposition) && (((SRElement) link.getTo()).getType() == IntentionalType.Task)) {
                            type = "AndRefinementLink";

                        }
                        if ((link instanceof TaskDecomposition) && (element.getType() == IntentionalType.Resource)) {
                            type = "NeededByLink";
                        }
                        if ((link instanceof Contribution)) {
                            type = "ContributionLink";
                        }
                        System.out.println("\n"+link.getId()+" : "+element+" -> "+type+" -> "+link.getTo().getName());
                        saida.append("    \n{\n"
                                + "      \"id\": \"" + link.getId() + "\",\n"
                                + "      \"type\": \"istar." + type + "\",\n"
                                + (link instanceof Contribution ? "\"label\": \"help\"," : "")
                                + "      \"source\": \"" + element.getId() + "\",\n"
                                + "      \"target\": \"" + link.getTo().getId() + "\"},");
                    }
                }
            }
        }
        saida = saida.deleteCharAt(saida.length() - 1);
        DateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
        Date date = new Date();

        saida.append("],\n"
                + "\"display\": { ");
        for (Node no : modelo.getNodes()) {
            if (no instanceof Actor) {
                if (no.getProperty("selected") == null) {
                    saida.append("\n\""+no.getId()+"\": {"
                            + "      \"collapsed\": true"
                            + "    },");
                }
            }
        }
        saida = saida.deleteCharAt(saida.length() - 1);
        saida.append("},\n"
                + "  \"tool\": \"pistar.1.0.0\",\n"
                + "  \"istar\": \"2.0\",\n"
                + "  \"saveDate\": \"" + dateFormat.format(date) + "\",\n"
                + "  \"diagram\": {\n"
                + "    \"width\": 2806.5,\n"
                + "    \"height\": 1172\n"
                + "  }\n"
                + "} ");
        return saida.toString();
        
         */
    }
}
