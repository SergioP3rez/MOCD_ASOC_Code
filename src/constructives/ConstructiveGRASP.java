package constructives;

import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.tools.Timer;
import structure.MOCDInstance;
import structure.MOCDSolutionUnionFind;
import structure.ParetoUF;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class ConstructiveGRASP implements Constructive<MOCDInstance, MOCDSolutionUnionFind> {
    private class Candidate {
        int id1;
        int id2;
        double cost;

        public Candidate(int id1, int id2, double cost) {
            this.id1 = id1;
            this.id2 = id2;
            this.cost = cost;
        }

        public int getCluster(){
            return id1;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Candidate candidate = (Candidate) o;
            return id1 == candidate.id1 ||
                    id2 == candidate.id2;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id1, id2);
        }
    }

    private double alpha;

    public ConstructiveGRASP(double alpha) {
        this.alpha = alpha;
    }

    @Override
    public MOCDSolutionUnionFind constructSolution(MOCDInstance instance) {

//        Random rnd = RandomManager.getRandom();
        Random rnd = new Random();
        MOCDSolutionUnionFind sol = new MOCDSolutionUnionFind(instance);
        MOCDSolutionUnionFind best = new MOCDSolutionUnionFind(instance);

        double realAlpha = ((alpha >= 0) ? alpha : rnd.nextDouble());
        sol.startAgglomerative();
        ParetoUF.add(sol);
        //Añadido intentando acelerar
        int n = sol.getInstance().getN();
        for (int cl1 = 0; cl1 < n / 2; cl1++) {
            if (sol.isRemovedCluster(cl1)) continue;

            double bestFusionMetric = Integer.MIN_VALUE;
            int bestClusterToFusion = -1;

            for (int cl2 = cl1 + 1; cl2 < n; cl2++) {
                if (sol.isRemovedCluster(cl2)) continue;
                double fusionMetric = sol.getFusionMetric(cl1, cl2);
                if (Double.compare(bestFusionMetric, fusionMetric) < 0) {
                    bestFusionMetric = fusionMetric;
                    bestClusterToFusion = cl2;
                }
            }

            if (bestClusterToFusion != -1 && !sol.getClusters().isSameSet(cl1, bestClusterToFusion)
                    && Double.compare(0.0, bestFusionMetric) < 0) {
                sol.joinClusters(cl1, bestClusterToFusion);
                sol.getNra();
                sol.getRc();
//                System.out.println("Last joint: "+cl1+" "+bestClusterToFusion+" "+Timer.getTime() / 1000);
                ParetoUF.add(sol);
            }
        }
//        System.out.println("fin init");
        //Fin añadido intentando acelerar
        List<Candidate> cl = createCandidates(sol);
//        System.out.println("Candidates created");
        while (cl.size() > 1 && !Timer.timeReached()) {
            double gmin = cl.get(cl.size() - 1).cost;
            double gmax = cl.get(0).cost;
            double th = gmax - realAlpha * (gmax - gmin);
            int limit = 0;

            while (limit < cl.size() && cl.get(limit).cost >= th) {
                limit++;
            }

            int selected = rnd.nextInt(limit);
            Candidate c = cl.remove(selected);
            sol.joinClusters(c.id1, c.id2);
            sol.getNra();
            sol.getRc();
            ParetoUF.add(sol);
            if (ParetoUF.isModifiedSinceLastAsk()) {
                best = sol;
            }
            //REVISAR
            updateCandidateList(c, sol, cl);

        }

        return best;
    }

    private List<Candidate> createCandidates(MOCDSolutionUnionFind sol) {

        int n = sol.getInstance().getN();
        List<Candidate> cl = new ArrayList<>(n);

        for (int cl1 = 0; cl1 < n; cl1++) {
            if (sol.isRemovedCluster(cl1)) continue;

            double bestFusionMetric = Integer.MIN_VALUE;
            int bestClusterToFusion = -1;

            for (int cl2 = cl1 + 1; cl2 < n; cl2++) {
                if (sol.isRemovedCluster(cl2)) continue;
                double fusionMetric = sol.getFusionMetric(cl1, cl2);
                if (Double.compare(bestFusionMetric, fusionMetric) < 0) {
                    bestFusionMetric = fusionMetric;
                    bestClusterToFusion = cl2;
                }
            }

            if (bestClusterToFusion != -1 && !sol.getClusters().isSameSet(cl1, bestClusterToFusion)
                    && Double.compare(0.0, bestFusionMetric) < 0) {
                Candidate c = new Candidate(cl1, bestClusterToFusion, bestFusionMetric);
//                System.out.println("Last calculated fmetric: "+cl1+" "+bestClusterToFusion+" "+Timer.getTime() / 1000);
                cl.add(c);
            }
        }

        cl.sort((c1, c2) -> Double.compare(c2.cost, c1.cost));
        return cl;
    }

    private void updateCandidateList(Candidate c, MOCDSolutionUnionFind sol, List<Candidate> cl) {

        int lastCandidate = sol.getClusterOfNode(c.getCluster());
        List<Candidate> candidateAux = new ArrayList<>(cl);
        for (Candidate candidate : candidateAux) {
            if(candidate.id1==c.id1 || candidate.id2==c.id1 || candidate.id2 == c.id2 || candidate.id1 == c.id2) cl.remove(candidate);
        }
        double bestFusionMetric = Integer.MIN_VALUE;
        int bestClusterToFusion = -1;
        for (int i : sol.getClusters().getExistingClusters()) {
            if (i==lastCandidate || sol.getClusters().isSameSet(lastCandidate, i)) continue;
            double fusionMetric = sol.getFusionMetric(i, lastCandidate);
            if (Double.compare(bestFusionMetric, fusionMetric) < 0) {
                bestFusionMetric = fusionMetric;
                bestClusterToFusion = i;
            }
        }
//        if (bestClusterToFusion != -1 && !sol.getClusters().isSameSet(lastCandidate, bestClusterToFusion) creo que esta cond me la puedo ahorrar
        if (bestClusterToFusion != -1
                && Double.compare(0.0, bestFusionMetric) < 0) {
            Candidate cand = new Candidate(lastCandidate, bestClusterToFusion, bestFusionMetric);
            cl.add(cand);
        }
        cl.sort((c1, c2) -> Double.compare(c2.cost, c1.cost));
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(" + alpha + ")";
    }
}
