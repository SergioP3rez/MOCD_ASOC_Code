package structure;

import grafo.optilib.structure.Solution;
import grafo.optilib.tools.RandomManager;
import utils.Utils;

import java.util.*;

public class MOCDSolutionUnionFind implements Solution {

    private MOCDInstance instance;
    private UnionFind clusters;
    private Map<Integer, Integer> edgesIntraCluster;
    private Map<Integer, Integer> edgesInterCluster;
    private int[] clusterToNode;

    private double nra;
    private double rc;

    public MOCDSolutionUnionFind(MOCDInstance instance) {
        int n = instance.getN();
        this.instance = instance;
        this.clusters = new UnionFind(n);
        clusterToNode = new int[instance.getN()];
        Arrays.fill(clusterToNode, -1);
        this.nra = 0.0;
        this.rc = 0.0;
        edgesInterCluster = new HashMap<>();
        edgesIntraCluster = new HashMap<>();
    }

    public MOCDSolutionUnionFind(MOCDSolutionUnionFind sol) {
        this.instance = sol.instance;
        this.clusters = new UnionFind(sol.getClusters());
        this.clusterToNode = sol.clusterToNode.clone();
        this.nra = Utils.round(sol.nra);
        this.rc = Utils.round(sol.rc);
        this.edgesInterCluster = new HashMap<>(sol.edgesInterCluster);
        this.edgesIntraCluster = new HashMap<>(sol.edgesIntraCluster);
    }

    public void startAgglomerative() {
        int n = instance.getN();
        this.nra = 0.0;
        this.rc = 0.0;
        for (int i = 0; i < n; i++) {
            clusterToNode[i] = i;
            int degree = this.instance.getAdjacents(i).size();
            this.rc += degree;
            edgesInterCluster.put(i, degree);
            edgesIntraCluster.put(i, 0);
        }
    }

    public double calculateNRA() {
        double toSum = 0;

        for (int i : clusters.getExistingClusters()) {
            toSum += getNra(i);
        }

        this.nra = Utils.round(toSum);
        return -this.nra;
    }

    public double calculateRC() {
        double toSum = 0;
        for (int i : clusters.getExistingClusters()) {
            toSum += getRc(i);
        }

        this.rc = Utils.round(toSum);
        return this.rc;
    }

    public double getEdgesInterCluster(int community) {
        return edgesInterCluster.get(community);
    }


    public double getEdgesIntraCluster(int community) {
        return edgesIntraCluster.get(community);
    }

    public void updateEdges(int community, int id1, int id2) {
        int toSetToZero = (community == id1) ? id2 : id1;
        int counterInter = 0;
        int counterIntra = 0;
        for (int node : clusters.getAllElementsOfSet(community)) {
            for (int adjacent : instance.getAdjacents(node)) {
                if (!clusters.isSameSet(node, adjacent)) {
                    counterInter++;
                } else {
                    counterIntra++;
                }
            }
        }

        edgesInterCluster.put(community, counterInter);
        edgesIntraCluster.put(community, counterIntra / 2);
        edgesInterCluster.put(toSetToZero, 0);
        edgesIntraCluster.put(toSetToZero, 0);
    }

    private void updateEdgesAddedNode(int node, int previousCluster, int cluster) {
        int newInterClusterEdgesForPrevious = 0;
        int newInterClusterEdgesForNew = 0;
        int newIntraClusterEdgesForNew = 0;

        for (int adjacent : instance.getAdjacents(node)) {
            int adjacentCluster = clusters.findSet(adjacent);
            if (adjacentCluster == previousCluster) {
                newInterClusterEdgesForPrevious++; //Restar de intracluster previous
            } else if (adjacentCluster == cluster) {
                newIntraClusterEdgesForNew++;
                newInterClusterEdgesForPrevious++;
            } else {
                newInterClusterEdgesForNew++;
            }
        }

        edgesInterCluster.put(previousCluster, edgesInterCluster.get(previousCluster) + newInterClusterEdgesForPrevious - newInterClusterEdgesForNew);
        edgesIntraCluster.put(previousCluster, edgesIntraCluster.get(previousCluster) - newInterClusterEdgesForPrevious);
        edgesInterCluster.put(cluster, edgesInterCluster.get(cluster) + newInterClusterEdgesForNew - newIntraClusterEdgesForNew);
        edgesIntraCluster.put(cluster, edgesIntraCluster.get(cluster) + newIntraClusterEdgesForNew);
    }

    public MOCDInstance getInstance() {
        return this.instance;
    }

    public double getNra() {
        calculateNRA();
        return -nra;
    }

    public double getNra(int i) {
        double intraClusterEdges = getEdgesIntraCluster(i);
        double communitySize = clusters.sizeOfSet(i);
        return intraClusterEdges / communitySize;
    }

    public double getNraToPrint() {
        return -nra;
    }

    public double getRc() {
        calculateRC();
        return rc;
    }

    public double getRc(int i) {
        double interClusterEdges = getEdgesInterCluster(i);
        double communitySize = clusters.sizeOfSet(i);
        return interClusterEdges / communitySize;
    }

    public double getRcToPrint() {
        return rc;
    }

    public UnionFind getClusters() {
        return clusters;
    }

    public int getNClusters() {
        return clusters.numDisjointSets();
    }


    public int getClusterOfNode(int i) {
        return clusters.findSet(i);
    }

    public double getFusionMetric(int c1, int c2) {

        if (clusters.isSameSet(c1, c2)) return Double.MIN_VALUE;
        double toRet = 0.0;
        MOCDInstance inst = this.getInstance();
        for (int v1 : this.clusters.getAllElementsOfSet(c1)) {
            for (int v2 : this.clusters.getAllElementsOfSet(c2)) {
                if (inst.areAdjacents(v1, v2)) toRet++;
            }
        }
        return (toRet / (this.clusters.sizeOfSet(c1) + this.clusters.sizeOfSet(c2)));
    }

    public double getFusionMetricSergio(int c1, int c2) {
        if (clusters.isSameSet(c1, c2)) return Double.MIN_VALUE;
        double toRet = 0.0;
        MOCDInstance inst = this.getInstance();
        for (int v1 : this.clusters.getAllElementsOfSet(c1)) {
            for (int v2 : this.clusters.getAllElementsOfSet(c2)) {
                if (inst.areAdjacents(v1, v2)) toRet+= instance.getDegree(v1) + instance.getDegree(v2);
            }
        }

        return (toRet / (this.clusters.sizeOfSet(c1) + this.clusters.sizeOfSet(c2)));
    }

    public void joinClusters(int id1, int id2) {
        int commToUpdate = clusters.unionSet(id1, id2);
        updateEdges(commToUpdate, id1, id2);
    }

    public void addNodeToCluster(int i, int cluster) {
        int previousCluster = clusters.findSet(i);
        clusters.unionElement(i, cluster);
        updateEdgesAddedNode(i, previousCluster, cluster);
    }

    public void shake(double k) {
        Random rnd = RandomManager.getRandom();
        ArrayList<Integer> factibleClusters = new ArrayList<>(clusters.getExistingClusters());
        for (int i = 0; i < k; i++) {
            int node = rnd.nextInt(this.instance.getN());

            int clusterPrev = clusterToNode[node];
            factibleClusters.remove(Integer.valueOf(clusterPrev));
            int clusterToMove;

            clusterToMove = rnd.nextInt(factibleClusters.size());
            addNodeToCluster(node, factibleClusters.get(clusterToMove));
            factibleClusters.add(clusterPrev);
        }
    }

    public int[] getClusterToNodeArray() {
        return clusterToNode;
    }

    public Map<Integer, Integer> getEdgesIntraCluster() {
        return edgesIntraCluster;
    }

    public Map<Integer, Integer> getEdgesInterCluster() {
        return edgesInterCluster;
    }

    //    public double getModularity() {
//        double modularity = 0.;
//        double nEdges = instance.getM();
//        for (int i : clusters.getExistingClusters()) {
//            int intra = edgesIntraCluster.get(i);
//            int inter = edgesInterCluster.get(i);
//            modularity += (intra / nEdges - Math.pow((inter + intra) / nEdges, 2));
//        }
//        return modularity;
//    }
    public double getModularity() {
        double modularity = 0.;
        double nEdges = instance.getM();
        int n = instance.getN();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) continue;
                if (getClusterOfNode(i) == getClusterOfNode(j)) {
                    if (instance.areAdjacents(i, j)) {
                        modularity += 1 - (instance.getAdjacents(i).size() + instance.getAdjacents(j).size()) / (2 * nEdges);
                    } else {
                        modularity += 0 - (instance.getAdjacents(i).size() + instance.getAdjacents(j).size()) / (2 * nEdges);
                    }
                }
            }
        }
        return (1 / (2 * nEdges)) * modularity;
    }

    public boolean isRemovedCluster(int cl) {
        return clusters.isRemoved(cl);
    }

    public int[] getBestClusterToNode(int i) {
        int[] toRet = new int[2];
        int highestValue = Integer.MIN_VALUE;
        for (int cl : clusters.getExistingClusters()) {
            int counter = 0;
            for (int node : clusters.getAllElementsOfSet(cl)) {
                if (instance.areAdjacents(i, node)) {
//                    counter++;
                    counter+=instance.getDegree(node)/clusters.sizeOfSet(cl);
                }
            }
            if (counter > highestValue) {
                toRet[0] = cl;
//                toRet[1] = counter;
                toRet[1] = counter/clusters.sizeOfSet(cl);;
//                highestValue = counter;
                highestValue = counter/clusters.sizeOfSet(cl);;
            }
        }
        return toRet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MOCDSolutionUnionFind that = (MOCDSolutionUnionFind) o;
        return Utils.compareDouble(that.nra, nra) == 0 &&
                Utils.compareDouble(that.rc, rc) == 0 &&
                instance.equals(that.instance) &&
                clusters.equals(that.clusters);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(instance, clusters, nra, rc);
        return result;
    }

    @Override
    public String toString() {
        return clusters.toString();
    }


}
