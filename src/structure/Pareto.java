package structure;

import utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Pareto {

    private static List<MOCDSolution> front;
    private static boolean modifiedSinceLastAsk;

    public static void reset() {
        front = new ArrayList<>(1000);
        modifiedSinceLastAsk = false;
    }

    public synchronized static boolean add(MOCDSolution newSol) {
        //System.out.println("newSol: minDistanceInOut ->  "+newSol.getMinDistanceOutToIn()+" maxDistanceBetween -> "+newSol.getMaxDistanceBetweenSelected());
        List<Integer> dominated = new ArrayList<>();
        boolean enter = true;
        int idx = 0;
        for (MOCDSolution frontSol : front) {

            int compMin1 = Utils.compareDouble(newSol.getNraToPrint(), frontSol.getNraToPrint());
            int compMin2 = Utils.compareDouble(newSol.getRcToPrint(), frontSol.getRcToPrint());

            if (compMin1 >= 0 && compMin2 >= 0) {
                // newSol esta dominada por una ya incluida en el frente
                enter = false;
                break;
            } else if ((compMin1 < 0 && compMin2 <= 0) || (compMin1 <= 0 && compMin2 < 0)) {
                // newSol domina a la incluida
                dominated.add(idx);
            }
            idx++;
        }
        int removed = 0;
        for (int idRem : dominated) {
            front.remove(idRem - removed);
            removed++;
        }
        if (enter) {
            front.add(new MOCDSolution(newSol));
            modifiedSinceLastAsk = true;
        }

        return enter;
    }

    public static synchronized boolean isModifiedSinceLastAsk() {
        boolean ret = modifiedSinceLastAsk;
        modifiedSinceLastAsk = false;
        return ret;
    }

    public static synchronized List<MOCDSolution> getFront() {
        return front;
    }

    public static String toText() {
        StringBuilder stb = new StringBuilder();
        for (MOCDSolution sol : front) {
            stb.append(sol.getClusters()).append("\t").append("MinNRA: ").append(sol.getNraToPrint()).append("\t").append("MinRC: ").append(sol.getRc()).append("\n").append("Modularity: ").append(sol.getModularity()).append("\n");
        }
        return stb.toString();
    }

    public static String toText2() {
        StringBuilder stb = new StringBuilder();
        for (MOCDSolution sol : front) {
            stb.append(sol.getNraToPrint()).append("\t").append(sol.getRc()).append("\n");
        }
        return stb.toString();
    }

    public static String toText3() {
        StringBuilder stb = new StringBuilder();
        for (MOCDSolution sol : front) {
            int n = sol.getInstance().getN();
            int[] communityToNode = new int[n];
            for (int i = 0; i < n; i++) {
                communityToNode[i] = sol.getClusterOfNode(i);
            }
            stb.append(Arrays.toString(communityToNode)).append("\n");
        }
        return stb.toString();
    }


    public static void saveToFile(String path) {
        if (path.lastIndexOf('/') > 0) {
            File folder = new File(path.substring(0, path.lastIndexOf('/')));
            if (!folder.exists()) {
                folder.mkdirs();
            }
        }
        try {
            PrintWriter pw = new PrintWriter(path);
            pw.print(toText());
            pw.close();
            PrintWriter pw2 = new PrintWriter(path.replaceAll("\\.txt","_onlyPareto.txt"));
            pw2.print(toText2());
            pw2.close();
            PrintWriter pw3 = new PrintWriter(path.replaceAll("\\.txt","network.txt"));
            pw3.print(toText3());
            pw3.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
