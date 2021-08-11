package constructives;

import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.tools.RandomManager;
import structure.MOCDInstance;
import structure.MOCDSolutionUnionFind;
import structure.ParetoUF;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ConstructiveRandomUF implements Constructive<MOCDInstance, MOCDSolutionUnionFind> {
    private class Candidate {
        int id1;
        int id2;
        double cost;

        public Candidate(int id1, int id2, double cost) {
            this.id1 = id1;
            this.id2 = id2;
            this.cost = cost;
        }
    }

    @Override
    public MOCDSolutionUnionFind constructSolution(MOCDInstance instance) {

        Random rnd = RandomManager.getRandom();
        MOCDSolutionUnionFind sol = new MOCDSolutionUnionFind(instance);
        MOCDSolutionUnionFind best = new MOCDSolutionUnionFind(instance);

        sol.startAgglomerative();
        ParetoUF.add(sol);
        List<Integer> cl = new ArrayList<>(sol.getNClusters());
        for (int i = 0; i < sol.getNClusters(); i++) {
            cl.add(i);
        }
        while (cl.size() > 1) {
            int indexSelected = rnd.nextInt(cl.size());
            int c = cl.remove(indexSelected);

            int indexSelected2 = rnd.nextInt(cl.size());
            int c2 = cl.remove(indexSelected2);

            sol.joinClusters(c, c2);

            cl = new ArrayList<>(sol.getClusters().getExistingClusters());

            sol.getRc();
            sol.getNra();
            ParetoUF.add(sol);
        }
        return sol;
    }


    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
