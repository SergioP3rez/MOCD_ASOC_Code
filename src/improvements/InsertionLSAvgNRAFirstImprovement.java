package improvements;

import grafo.optilib.metaheuristics.Improvement;
import grafo.optilib.tools.Timer;
import structure.MOCDSolution;
import structure.Pareto;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class InsertionLSAvgNRAFirstImprovement implements Improvement<MOCDSolution> {

    private class Candidate {
        private int node;
        private int cluster;
        private int value;

        public Candidate(int node, int cluster, int value) {
            this.node = node;
            this.cluster = cluster;
            this.value = value;
        }

        public int getCluster() {
            return cluster;
        }

        public int getNode() {
            return node;
        }

        @Override
        public String toString() {
            return node+" "+cluster+" "+value;
        }
    }

    @Override
    public void improve(MOCDSolution sol) {
        boolean improved = true;
        int n = sol.getInstance().getN();
        List<Candidate> cl = createCandidateList(sol, n);

        while(improved && !Timer.timeReached()){
            improved = false;
            for (Candidate c : cl) {
                int node = c.node;
                int cluster = c.cluster;
                int clusterForNode = sol.getClusterOfNode(node);
                if (clusterForNode == cluster || !sol.getClusters().containsKey(cluster)) continue;
                double actValueCluster1 = sol.getNra(clusterForNode);
                double actValueCluster2 = sol.getNra(cluster);
                double prevAvg = (actValueCluster1 + actValueCluster2) / 2;
                sol.removeNodeFromCluster(node, clusterForNode);
                sol.addNodeToCluster(node, cluster);
                Pareto.add(sol);
                double newValueCluster1 = sol.getNra(cluster);
                double newValueCluster2;
                if(sol.getClusters().containsKey(clusterForNode)){
                    newValueCluster2 = sol.getNra(clusterForNode);
                }else{
                    newValueCluster2 = 0.0;
                }
                double postAvg = (newValueCluster1 + newValueCluster2) / 2;
//                if (Utils.compareDouble2decs(postAvg, prevAvg) < 0) {
                if (Utils.compareDouble(postAvg, prevAvg) < 0) {
                    improved = true;
                    updateCandidateList(c, sol, cl);
                    break;
                } else {
                    sol.removeNodeFromCluster(node, cluster);
                    sol.addNodeToCluster(node, clusterForNode);
                }
                if(Timer.timeReached()){
                    break;
                }
            }

        }

    }

    private List<Candidate> createCandidateList(MOCDSolution sol, int n) {
        List<Candidate> toRet = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            int[] bestClusterAndValue = sol.getBestClusterToNode(i);
            if(sol.getClusterOfNode(i) != bestClusterAndValue[0]){
                toRet.add(new Candidate(i, bestClusterAndValue[0], bestClusterAndValue[1]));
            }
        }
        toRet.sort((c1, c2) -> Integer.compare(c2.value, c1.value));
        return toRet;
    }

    private void updateCandidateList(Candidate c, MOCDSolution sol, List<Candidate> cl) {

        List<Candidate> candidateAux = new ArrayList<>(cl);
        for (Candidate candidate : candidateAux) {
            if(candidate.cluster == c.cluster && sol.getInstance().areAdjacents(candidate.node, c.node)){
                cl.remove(candidate);
                int[] newBest = sol.getBestClusterToNode(candidate.node);
                if(newBest[0] != sol.getClusterOfNode(candidate.node)){
                    cl.add(new Candidate(candidate.node, newBest[0], newBest[1]));
                }
            }
        }
        cl.remove(c);
        cl.sort((c1, c2) -> Double.compare(c2.value, c1.value));
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
