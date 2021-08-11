package constructives;

import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.tools.RandomManager;
import structure.MOCDInstance;
import structure.MOCDSolution;
import structure.Pareto;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ConstructiveRandom implements Constructive<MOCDInstance, MOCDSolution> {
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
    public MOCDSolution constructSolution(MOCDInstance instance) {

        Random rnd = RandomManager.getRandom();
        MOCDSolution sol = new MOCDSolution(instance);
        MOCDSolution best = new MOCDSolution(instance);

        sol.startAgglomerative();
        Pareto.add(sol);
        List<Integer> cl = new ArrayList<>(sol.getClusters().keySet());

        while (cl.size() > 1) {
            int indexSelected = rnd.nextInt(cl.size());
            int c = cl.remove(indexSelected);

            int indexSelected2 = rnd.nextInt(cl.size());
            int c2 = cl.remove(indexSelected2);

            double previousNra = sol.getNra();
            sol.joinClusters(c, c2);

            cl = new ArrayList<>(sol.getClusters().keySet());;
            sol.getRc();
            Pareto.add(sol);
        }
        Pareto.add(best);
        return best;
    }


    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
