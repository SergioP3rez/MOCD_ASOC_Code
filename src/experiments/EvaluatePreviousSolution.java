package experiments;

import structure.MOCDInstance;
import structure.MOCDSolution;
import structure.Pareto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class EvaluatePreviousSolution {
    public static void main(String[] args) {
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);

        String date = String.format("%04d-%02d-%02d", year, month, day);

        String[] dirsToEvaluate = new String[]{"500_0.1", "500_0.2", "500_0.3", "500_0.4", "500_0.5", "500_0.6", "500_0.7", "500_0.8", "1000_0.1", "1000_0.2", "1000_0.3", "1000_0.4", "1000_0.5", "1000_0.6", "1000_0.7", "1000_0.8"};
        for (String nameDir : dirsToEvaluate) {
            Pareto.reset();
            String[] extensions = new String[]{"_1.txt", "_2.txt", "_3.txt", "_4.txt", "_5.txt", "_6.txt", "_7.txt", "_8.txt", "_9.txt", "_10.txt"};
            List<String> files = readFilesInFolder("/Users/sergio/OneDrive - Universidad Rey Juan Carlos/Investigacion URJC/MOCD/instances/ground_truth_real/resultados_chinos_paraNMI/" + nameDir +"/", extensions);

            for (String file : files) {
                File fileReaded = new File(file);
                BufferedReader br;
                try {
                    br = new BufferedReader(new FileReader(fileReaded));
                    String line;
                    MOCDInstance instance = new MOCDInstance("instances/finalversusprevious/"+nameDir+"_chinos.txt");
                    MOCDSolution sol = new MOCDSolution(instance);
                    while ((line = br.readLine()) != null) {
                        line = line.replaceAll("\\[","").replaceAll("]", "");
                        String[] aux = line.split(",\\s+\\t*");
                        for (int i = 0; i < aux.length; i++) {
                            sol.addNodeToCluster(i, Integer.parseInt(aux[i]));
                        }
                        sol.updateEdges();
                        Pareto.add(sol);
//                        System.out.println("Fichero " + fileReaded + " procesado.");
                    }

//                System.out.println("Para el fichero " + fileReaded + " el menor Nra es " + minNra + " y el menor Rc es " + minRc);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Fichero " + fileReaded + " procesado.");
            }
            String outDir = "experiments/"+date+"/resultadosChinos/";
            File folder = new File(outDir.substring(0, outDir.lastIndexOf('/')));
            if (!folder.exists()) folder.mkdirs();
            Pareto.saveToFile(outDir + "/"+nameDir+"/pareto_"+nameDir+".txt");
        }
    }

    private static List<String> readFilesInFolder(String folder, String[] extensions) {
        List files = null;

        try {
            files = Files.list(Paths.get(folder)).map(String::valueOf).filter((path) -> {
                int var3 = extensions.length;

                for (int var4 = 0; var4 < var3; ++var4) {
                    String ext = extensions[var4];
                    if (path.substring(Math.max(path.lastIndexOf("/"), path.lastIndexOf("\\")) + 1).startsWith(".")) {
                        return false;
                    }

                    if (path.endsWith(ext) || path.matches(ext)) {
                        return true;
                    }
                }

                return false;
            }).sorted().collect(Collectors.toList());
        } catch (IOException var5) {
            var5.printStackTrace();
        }

        return files;
    }
}
