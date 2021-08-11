package experiments;


import algorithms.Paralell_MO_RSVNS_UF_2LS;
import constructives.ConstructiveGRASP;
import grafo.optilib.metaheuristics.Algorithm;
import grafo.optilib.metaheuristics.Improvement;
import grafo.optilib.results.Experiment;
import improvements.InsertionLSAvgNRAFirstImprovement;
import improvements.InsertionLSAvgRCFirstImprovement;
import structure.MOCDInstance;
import structure.MOCDInstanceFactory;
import structure.MOCDSolution;

import java.io.File;
import java.util.Calendar;

public class ExperimentAlgorithm {

    public static void main(String[] args) {
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);

        String date = String.format("%04d-%02d-%02d", year, month, day);

        MOCDInstanceFactory factory = new MOCDInstanceFactory();


        String dir = ((args.length == 0) ? "instances/" : (args[1] + "/"));

        String outDir = "results/" + date;
        File outDirCreator = new File(outDir);
        outDirCreator.mkdirs();
        String[] extensions = new String[]{".txt"};
        Improvement<MOCDSolution>[] lsArray = new Improvement[]{new InsertionLSAvgNRAFirstImprovement(), new InsertionLSAvgRCFirstImprovement()};

        Algorithm<MOCDInstance>[] execution = new Algorithm[]{
                new Paralell_MO_RSVNS_UF_2LS(new ConstructiveGRASP(0.25), lsArray, 100, 0.05, 0.3),
        };

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < execution.length; j++) {
                String outputFile = outDir + "/" + execution[j].toString() + "_execution_" + i + ".xlsx";
                Experiment<MOCDInstance, MOCDInstanceFactory> experiment = new Experiment<>(execution[j], factory);
                experiment.launch(dir, outputFile, extensions);
            }
        }
    }
}
