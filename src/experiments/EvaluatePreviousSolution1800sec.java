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

public class EvaluatePreviousSolution1800sec {
    public static void main(String[] args) {
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);

        String date = String.format("%04d-%02d-%02d", year, month, day);

//        String[] dirsToEvaluate = new String[]{"500_0.1", "500_0.2", "500_0.3", "500_0.4", "500_0.5", "500_0.6", "500_0.7", "500_0.8", "1000_0.1", "1000_0.2", "1000_0.3", "1000_0.4", "1000_0.5", "1000_0.6", "1000_0.7", "1000_0.8"};
//        String[] dirsToEvaluate = new String[]{"2021-06-01", "2021-06-02", "2021-06-07"};
//        String[] dirsToEvaluate = new String[]{"5000_0.1", "5000_0.2", "5000_0.3", "5000_0.4", "5000_0.5", "5500_0.1", "5500_0.2", "5500_0.3", "6000_0.1", "6000_0.2", "6000_0.3", "7500_0.1", "7500_0.2", "7500_0.3", "7000_0.1", "7000_0.2", "7000_0.3", "musae_DE", "musae_ES", "musae_FR"};
//        String[] dirsToEvaluate = new String[]{"musae_DE", "musae_ES", "musae_FR"};
        String[] dirsToEvaluate = new String[]{"6500_0.1", "6500_0.2", "6500_0.3"};

//        String[] dirsToEvaluate = new String[]{"RealWorld"};
        for (String nameDir : dirsToEvaluate) {
            Pareto.reset();
            String[] extensions = new String[]{".txt"};
//            List<String> files = readFilesInFolder("/Users/sergio/OneDrive - Universidad Rey Juan Carlos/Investigacion URJC/MOCD/instances/ground_truth_real/resultados_chinos_1800sec/" + nameDir +"/", extensions);
//            List<String> files = readFilesInFolder("/Users/sergio/Downloads/output_chinos/"+nameDir, extensions);
//            List<String> files = readFilesInFolder("experiments/"+nameDir+"/Paralell_MO_RSVNS_UF_2LS(ConstructiveGRASPAggNEW(0.25),[Lgrafo.optilib.metaheuristics.Improvement;@21b8d17c, 0.3)", extensions);
            List<String> files = readFilesInFolder("/Users/sergio/OneDrive - Universidad Rey Juan Carlos/Investigacion URJC/MOCD/instances/ground_truth_real/resultados_chinos_paraNMI_REVISION/" + nameDir + "/", extensions);

            for (String file : files) {
                File fileReaded = new File(file);
                BufferedReader br;
                try {
                    br = new BufferedReader(new FileReader(fileReaded));
                    String line;
//                    MOCDInstance instance = new MOCDInstance("instances/finalversusprevious/"+nameDir+"_chinos.txt");
//                    MOCDInstance instance = new MOCDInstance("instances/finalversusprevious/"+nameDir+"network.txt");
                    MOCDInstance instance;
                    if (!nameDir.contains("musae")) {
                        instance = new MOCDInstance("/Users/sergio/OneDrive - Universidad Rey Juan Carlos/Investigacion URJC/MOCD/instances/lfr_rev1/lfr_traducidas_rev1/" + nameDir +".txt");
                    } else {
                        instance = new MOCDInstance("/Users/sergio/OneDrive - Universidad Rey Juan Carlos/Investigacion URJC/MOCD/instances/snap_rev1/snap_traducidas_rev1/" + nameDir + "_edgesnetwork.txt");
                    }
//                    MOCDInstance instance = new MOCDInstance("instances/finalversusprevious/"+file.split("/")[10]);
                    MOCDSolution sol = new MOCDSolution(instance);
                    while ((line = br.readLine()) != null) {
                        line = line.replaceAll("\\[", "").replaceAll("]", "");
                        String[] aux = line.split(", ");
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
//                String outDir = "experiments/"+date+"/resultadosChinos1800sec/";
//                File folder = new File(outDir.substring(0, outDir.lastIndexOf('/')));
//                if (!folder.exists()) folder.mkdirs();
//                Pareto.saveToFile(outDir + "/"+nameDir+"/pareto_"+file.split("/")[10]+".txt");
            }
            String outDir = "experiments/" + date + "/resultadosChinos1800sec_rev1/";
            File folder = new File(outDir.substring(0, outDir.lastIndexOf('/')));
            if (!folder.exists()) folder.mkdirs();
            Pareto.saveToFile(outDir + "/" + nameDir + "/pareto_" + nameDir + ".txt");
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
