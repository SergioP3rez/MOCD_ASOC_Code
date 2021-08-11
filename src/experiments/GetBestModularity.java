package experiments;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class GetBestModularity {
    public static void main(String[] args) {
        String[] dirsToEvaluate = new String[]{"experiments/exec1/2021-07-03/Paralell_MO_RSVNS_UF_2LS(ConstructiveGRASPAggNEW(0.25),[Lgrafo.optilib.metaheuristics.Improvement;@21b8d17c, 0.3)", "experiments/exec2/2021-07-03/Paralell_MO_RSVNS_UF_2LS(ConstructiveGRASPAggNEW(0.25),[Lgrafo.optilib.metaheuristics.Improvement;@21b8d17c, 0.3)", "experiments/exec2/2021-07-04/Paralell_MO_RSVNS_UF_2LS(ConstructiveGRASPAggNEW(0.25),[Lgrafo.optilib.metaheuristics.Improvement;@21b8d17c, 0.3)"};

        for (String nameDir : dirsToEvaluate) {
            String[] extensions = new String[]{".txt"};
            //Previous
//            List<String> files = readFilesInFolder("experiments/2021-06-09/resultadosPrevious1800sec_rev1SUYOS/" + nameDir + "/", extensions);
            List<String> files = readFilesInFolder(nameDir + "/", extensions);

            for (String file : files) {
                if(file.contains("onlyPareto") || file.contains("edgesnetworknetwork.txt") || file.contains("previosnetwork") || file.contains("bestModularity")) continue;
                File fileReaded = new File(file);
                BufferedReader br;
                int bestSol = 1;
                double bestModularity = Double.MIN_VALUE;
                try {
                    br = new BufferedReader(new FileReader(fileReaded));
                    String line;
                    int lineCounter = 1;
                    while ((line = br.readLine()) != null) {
                        line = br.readLine();
                        if (line==null) break;
                        lineCounter++;
                        // Previous
//                        if (lineCounter==2) continue;
                        String[] aux = line.split(": ");
                        double actMod = Double.parseDouble(aux[1]);
                        /* Previous

                        String[] aux = line.split("Modularity: ");
                        String mod = aux[1].split("\\{")[0];
                        double actMod = Double.parseDouble(mod);
*/

                        if (Double.compare(bestModularity, actMod) < 0){
                            bestModularity = actMod;
                            bestSol = lineCounter-1;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                File fileNetwork;
                if(!file.contains("musae")){
                    fileNetwork = new File(file.split("\\.txt")[0]+"network.txt");
                }else{
                    fileNetwork = new File(file.split("\\.txt")[0]+".txt");
                }
                BufferedReader br2;
                String line = "";
                try {
                    br2 = new BufferedReader(new FileReader(fileNetwork));
                    for (int i = 1; i < bestSol; i++) {
                        br2.readLine();
                    }
                    line = br2.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Previous
//                String outDir = "experiments/2021-06-09/resultadosPrevios1800sec_rev1SUYOS/"+nameDir+"/";
                String outDir = nameDir+"/";

                try{
                    // Previous
//                    if(!nameDir.contains("musae")){
////                        PrintWriter pw = new PrintWriter(outDir+nameDir.replace(".txt","")+"_bestModularity.txt");
//                        PrintWriter pw = new PrintWriter((outDir+file.substring(file.lastIndexOf('/')).replace(".txt",""))+"_bestModularity.txt");
//                        pw.print(line+" "+bestModularity);
//                        pw.close();
//                    }else{
////                        PrintWriter pw = new PrintWriter(outDir+file.replace(".txt","").substring(file.lastIndexOf('/'))+"_bestModularity.txt");
//                        PrintWriter pw = new PrintWriter((outDir+file.substring(file.lastIndexOf('/')).replace(".txt",""))+"_bestModularity.txt");
//                        pw.print(line+" "+bestModularity);
//                        pw.close();
//                    }

                    PrintWriter pw = new PrintWriter(outDir+file.substring(file.lastIndexOf('/'))+"_bestModularity.txt");
                    pw.print(line+" "+bestModularity);
                    pw.close();
                }catch(IOException e){
                    e.printStackTrace();
                }

            }
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
