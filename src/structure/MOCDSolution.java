package structure;

import grafo.optilib.structure.Solution;
import grafo.optilib.tools.RandomManager;
import utils.Utils;

import java.util.*;

public class MOCDSolution implements Solution {

    private MOCDInstance instance;
    private Map<Integer, Set<Integer>> clusters;
    private Map<Integer, Double> nraToCluster;
    private Map<Integer, Double> rcToCluster;
    private Map<Integer, Integer> edgesIntraCluster;
    private Map<Integer, Integer> edgesInterCluster;
    private int[] clusterToNode;

    private double nra;
    private double rc;

    public MOCDSolution(MOCDInstance instance) {
        this.instance = instance;
        this.clusters = new HashMap<>(instance.getN());
        this.nraToCluster = new HashMap<>(instance.getN());
        this.rcToCluster = new HashMap<>(instance.getN());
        clusterToNode = new int[instance.getN()];
        Arrays.fill(clusterToNode, -1);
        this.nra = 0.0;
        this.rc = 0.0;
        edgesInterCluster = new HashMap<>();
        edgesIntraCluster = new HashMap<>();
    }

    public MOCDSolution(MOCDSolution sol) {
        this.instance = sol.instance;
        this.clusters = new HashMap<>(sol.getInstance().getN());
        Map<Integer, Set<Integer>> solClusters = sol.getClusters();
        this.rcToCluster = new HashMap<>();
        this.nraToCluster = new HashMap<>();
        for (int cluster : solClusters.keySet()) {
            Set<Integer> aux = solClusters.get(cluster);
            this.clusters.put(cluster, new HashSet<>(aux));
            this.rcToCluster.put(cluster, Utils.round(sol.rcToCluster.get(cluster)));
            this.nraToCluster.put(cluster, Utils.round(sol.nraToCluster.get(cluster)));
        }

        this.clusterToNode = sol.clusterToNode.clone();
        this.nra = Utils.round(sol.nra);
        this.rc = Utils.round(sol.rc);
        this.edgesInterCluster = new HashMap<>(sol.edgesInterCluster);
        this.edgesIntraCluster = new HashMap<>(sol.edgesIntraCluster);
    }

    public MOCDSolution(MOCDSolutionUnionFind sol) {
        this.instance = sol.getInstance();
        this.clusters = new HashMap<>(sol.getInstance().getN());
        this.rcToCluster = new HashMap<>();
        this.nraToCluster = new HashMap<>();
        this.clusterToNode = sol.getClusterToNodeArray().clone();
        this.edgesInterCluster = new HashMap<>(sol.getEdgesInterCluster());
        this.edgesIntraCluster = new HashMap<>(sol.getEdgesIntraCluster());
        for (int cluster : sol.getClusters().getExistingClusters()) {
            List<Integer> aux = sol.getClusters().getAllElementsOfSet(cluster);
            for (int node : aux) {
                clusterToNode[node] = cluster;
            }
            this.clusters.put(cluster, new HashSet<>(aux));
            double rc = getRc(cluster);
            double nra = getNra(cluster);
            this.rcToCluster.put(cluster, rc);
            this.nraToCluster.put(cluster, nra);
            this.rc += rc;
            this.nra += nra;
        }
    }

    public void startAgglomerative() {
        int n = instance.getN();
        this.nra = 0.0;
        this.rc = 0.0;
        for (int i = 0; i < n; i++) {
            HashSet<Integer> clusterAux = new HashSet<>();
            clusterAux.add(i);
            this.clusters.put(i, clusterAux);
            clusterToNode[i] = i;
            int degree = this.instance.getAdjacents(i).size();
            rcToCluster.put(i, (double) degree);
            this.rc += degree;
            nraToCluster.put(i, 0.0);
            edgesInterCluster.put(i, degree);
            edgesIntraCluster.put(i, 0);
        }

    }

    public double calculateNRA() {
        double toSum = 0;

        for (int i : clusters.keySet()) {
            toSum += getNra(i);
        }

        this.nra = Utils.round(toSum);
        return -this.nra;
    }

    public double calculateRC() {
        double toSum = 0;
        for (int i : clusters.keySet()) {
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


    public MOCDInstance getInstance() {
        return this.instance;
    }

    public double getNra() {
//        calculateNRA(); // To evaluate previous paper solutions is necessary uncomment this line
        return -nra;
    }

    public double getNra(int i) {
        double intraClusterEdges = getEdgesIntraCluster(i);
        double communitySize = clusters.get(i).size();
        return intraClusterEdges / communitySize;
    }

    public double getRc() {
//        calculateRC(); // To evaluate previous paper solutions is necessary uncomment this line
        return rc;
    }

    public double getRc(int i) {
        double interClusterEdges = getEdgesInterCluster(i);
        double communitySize = clusters.get(i).size();
        return interClusterEdges / communitySize;
    }

    public double getNraToPrint() {
        return -nra;
    }

    public double getRcToPrint() {
        return rc;
    }

    public Map<Integer, Set<Integer>> getClusters() {
        return clusters;
    }

    public int getNClusters() {
        return clusters.size();
    }


    public int getClusterOfNode(int i) {
        return clusterToNode[i];
    }

    public void addNodeToCluster(int node, int cluster) {

        Set<Integer> clusterBefore = this.clusters.get(cluster);
        double oldNra;
        double oldRc;

        if (clusterBefore == null) {
            oldNra = 0.0;
            oldRc = 0.0;
        } else {
            oldNra = nraToCluster.get(cluster);
            oldRc = Utils.round(rcToCluster.get(cluster));
        }

        this.nra -= Utils.round(oldNra);


        this.rc -= Utils.round(oldRc);

        if (!clusters.containsKey(cluster)) {
            Set<Integer> aux = new HashSet<>();
            aux.add(node);
            clusters.put(cluster, aux);
        } else {
            clusters.get(cluster).add(node);
        }

        clusterBefore = clusters.get(cluster);
        oldNra = oldNra * (clusterBefore.size() - 1);
        oldRc = oldRc * (clusterBefore.size() - 1);

        int newIntracluster = 0;
        int newIntercluster = 0;

        Set<Integer> adjacents = instance.getAdjacents(node);
        for (int n : adjacents) {
            if (clusterBefore.contains(n)) {
                newIntracluster++;
            } else if (clusterToNode[n] >= 0) {
                newIntercluster++;
            }
        }

        if (edgesIntraCluster.containsKey(cluster)) {
            edgesIntraCluster.put(cluster, edgesIntraCluster.get(cluster) + newIntracluster);
        } else {
            edgesIntraCluster.put(cluster, newIntracluster);
        }

        if (edgesInterCluster.containsKey(cluster)) {
            edgesInterCluster.put(cluster, edgesInterCluster.get(cluster) + newIntercluster - newIntracluster);
        } else {
            edgesInterCluster.put(cluster, newIntercluster);
        }

        double newNra = (newIntracluster + oldNra) / clusters.get(cluster).size();

        nraToCluster.put(cluster, Utils.round(newNra));
        this.nra += Utils.round(newNra);

        double newRc = (newIntercluster + oldRc - newIntracluster) / clusters.get(cluster).size();

        rcToCluster.put(cluster, Utils.round(newRc));
        this.rc += Utils.round(newRc);

        this.clusterToNode[node] = cluster;
    }

    public void removeNodeFromCluster(int node, int cluster) {
        Set<Integer> clusterBefore = this.clusters.get(cluster);

        double oldNra = nraToCluster.get(cluster);
        this.nra -= Utils.round(oldNra);

        double oldRc = rcToCluster.get(cluster);
        this.rc -= Utils.round(oldRc);

        oldNra = oldNra * clusterBefore.size();
        oldRc = oldRc * clusterBefore.size();

        int newIntercluster = 0;
        int notLongerInCluster = 0;
        Set<Integer> adjacents = instance.getAdjacents(node);
        for (int n : adjacents) {
            if (clusterBefore.contains(n)) {
                newIntercluster++;
            } else {
                notLongerInCluster++;
            }
        }

        int auxIntra = edgesIntraCluster.get(cluster);
        edgesIntraCluster.put(cluster, auxIntra-newIntercluster);

        int auxInter = edgesInterCluster.get(cluster);
        edgesInterCluster.put(cluster, auxInter-notLongerInCluster+newIntercluster);

        clusters.get(cluster).remove(node);
        if (clusters.get(cluster).isEmpty()) {
            clusters.remove(cluster);
            nraToCluster.remove(cluster);
            rcToCluster.remove(cluster);
        } else {
            double newNra = (oldNra - newIntercluster) / clusters.get(cluster).size();

            nraToCluster.put(cluster, newNra);
            this.nra += Utils.round(newNra);

            double newRc = (oldRc + newIntercluster - notLongerInCluster) / clusters.get(cluster).size();
            rcToCluster.put(cluster, newRc);
            this.rc += Utils.round(newRc);
        }


        clusterToNode[node] = -1;

    }

    public void updateNodeFromCluster(int node, int clusterInNewSol, int clusterReal) {
        Set<Integer> clusterBefore = this.clusters.get(clusterInNewSol);

        double oldNra = nraToCluster.get(clusterInNewSol);
        this.nra -= Utils.round(oldNra);

        double oldRc = rcToCluster.get(clusterInNewSol);
        this.rc -= Utils.round(oldRc);

        oldNra = oldNra * clusterBefore.size();
        oldRc = oldRc * clusterBefore.size();

        int newIntercluster = 0;

        Set<Integer> adjacents = instance.getAdjacents(node);
        for (int n : adjacents) {
            if (clusterBefore.contains(n)) {
                newIntercluster++;
            }
        }
        clusters.get(clusterInNewSol).remove(node);
        if (clusters.get(clusterInNewSol).isEmpty()) {
            clusters.remove(clusterInNewSol);
            nraToCluster.remove(clusterInNewSol);
            rcToCluster.remove(clusterInNewSol);
        } else {
            double newNra = (oldNra - newIntercluster) / clusters.get(clusterInNewSol).size();

            nraToCluster.put(clusterInNewSol, newNra);
            this.nra += Utils.round(newNra);

            double newRc = (oldRc + newIntercluster) / clusters.get(clusterInNewSol).size();
            rcToCluster.put(clusterInNewSol, newRc);
            this.rc += Utils.round(newRc);
        }
        // ANTES clusters.get(clusterInNewSol).remove(node);
        /*if (clusters.get(clusterInNewSol).isEmpty()) {
            clusters.remove(clusterInNewSol);
        }*/
        clusterToNode[node] = clusterReal;

    }

    public double getFusionMetric(int c1, int c2) {

        double toRet = 0.0;
        MOCDInstance inst = this.getInstance();
        for (int v1 : this.clusters.get(c1)) {
            for (int v2 : this.clusters.get(c2)) {
                if (inst.areAdjacents(v1, v2)) toRet++;
            }
        }

        return (toRet / (this.clusters.get(c1).size() + this.clusters.get(c2).size()));
    }

    public void joinClusters(int id1, int id2) {
        Set<Integer> c1 = this.clusters.get(id1);

        Set<Integer> c1AuxCopy = new HashSet<>(c1);
        for (int v : c1AuxCopy) {
            addNodeToCluster(v, id2);
            removeNodeFromCluster(v, id1);
            clusterToNode[v] = id2;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MOCDSolution that = (MOCDSolution) o;
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

    public boolean areEquals(MOCDSolution solGuide) {
        boolean sameClusters = false;

        for (int clSolGuide : solGuide.clusters.keySet()) {
            for (int clNewSol : this.clusters.keySet()) {
                if (this.clusters.get(clNewSol).containsAll(solGuide.clusters.get(clSolGuide))) {
                    sameClusters = true;
                    break;
                }
            }
            if (!sameClusters) return false;
        }
        return true;
    }

    public void insertClusterFrom(MOCDSolution solGuide, int clIniToSelect, int clFinToSelect, List<Integer> clustersSolIni) {
        Set<Integer> clusterFin = solGuide.clusters.get(clFinToSelect);
        Random rnd = RandomManager.getRandom();

        for (int node : clusterFin) {
            removeNodeFromCluster(node, this.clusterToNode[node]);
            addNodeToCluster(node, clIniToSelect);
        }

        Set<Integer> clusterIni = this.clusters.get(clIniToSelect);

        List<Integer> toRedistribute = getDifferentNodes(clusterFin, clusterIni);

        for (int node : toRedistribute) {
            int idClToMove = rnd.nextInt(clustersSolIni.size());
            int clusterToMove = clustersSolIni.get(idClToMove);
            removeNodeFromCluster(node, this.clusterToNode[node]);
            addNodeToCluster(node, clusterToMove);
        }


    }

    private List<Integer> getDifferentNodes(Set<Integer> clusterIni, Set<Integer> clusterFin) {

        List<Integer> toRet = new ArrayList<>();
        for (int node : clusterFin) {
            if (!clusterIni.contains(node)) toRet.add(node);
        }
        return toRet;
    }

    public void addNewCluster(int idClusterExtra) {
        this.clusters.put(idClusterExtra, new HashSet<>());
        this.nraToCluster.put(idClusterExtra, 0.0);
        this.rcToCluster.put(idClusterExtra, (double) instance.getAdjacents(idClusterExtra).size());
    }

    public boolean hasBeenDeleted(int clusterPrevFori) {
        return clusters.get(clusterPrevFori) == null;
    }

    public void insertNodesInEmptyCluster(MOCDSolution solGuide, int cl, int remove) {
        Set<Integer> clusterFin = solGuide.clusters.get(remove);

        for (int node : clusterFin) {
            removeNodeFromCluster(node, this.clusterToNode[node]);
            addNodeToCluster(node, cl);
        }

    }

    public double getCandidateMetric(int node) {
        double metrica = 0;
        for (int adjacent : this.instance.getAdjacents(node)) {
            metrica += (clusterToNode[adjacent]!=clusterToNode[node]) ? 0 : 1;
        }
        return metrica/this.instance.getAdjacents(node).size();
    }

    public Map<Integer, Integer> getCandidateCluster(int candidate) {
        Map<Integer, Integer> toRet = new HashMap<>(clusters.size());
        for (int adjacent : instance.getAdjacents(candidate)) {
            for (int cluster : clusters.keySet()) {
                if(clusters.get(cluster).contains(adjacent)){
                    if(toRet.get(cluster)==null){
                        toRet.put(cluster, 1);
                    }else{
                        int aux = toRet.get(cluster);
                        aux++;
                        toRet.put(cluster, aux);
                    }
                }
            }
        }
        return toRet;
    }

    public void shake(double k) {
        Random rnd = RandomManager.getRandom();
        ArrayList<Integer> factibleClusters = new ArrayList<>(clusters.size()-1);
        factibleClusters.addAll(clusters.keySet());
        for (int i = 0; i < k; i++) {
            int node = rnd.nextInt(this.instance.getN());

            int clusterPrev = clusterToNode[node];
            factibleClusters.remove(Integer.valueOf(clusterPrev));
            removeNodeFromCluster(node, clusterPrev);
            int clusterToMove;

            clusterToMove = rnd.nextInt(factibleClusters.size()+1);
            addNodeToCluster(node, clusterToMove);
            factibleClusters.add(clusterPrev);
        }
    }

    public void updateEdges() {
        for (int cl : clusters.keySet()) {
            int edgesInter = 0;
            int edgesIntra = 0;
            Set<Integer> visited = new HashSet<>();
            for (int node : clusters.get(cl)) {
                visited.add(node);
                for (int adjacent : instance.getAdjacents(node)) {
                    if(!visited.contains(adjacent)){
                        if(clusters.get(cl).contains(adjacent)){
                            edgesIntra++;
                        }else{
                            edgesInter++;
                        }
                    }
                }
                edgesInterCluster.put(cl, edgesInter);
                edgesIntraCluster.put(cl, edgesIntra);
            }
        }
    }

    public double getModularity(){
        double modularity = 0.;
        double nEdges = instance.getM();
        int n = instance.getN();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if(clusterToNode[i]==clusterToNode[j]){
                   if(instance.areAdjacents(i, j)){
                       modularity += 1 - (instance.getAdjacents(i).size() * instance.getAdjacents(j).size())/(2*nEdges);
                   }else{
                       modularity += 0 - (instance.getAdjacents(i).size() * instance.getAdjacents(j).size())/(2*nEdges);
                   }
                }
            }
        }
        return modularity/(2*nEdges);
    }

    @Override
    public String toString() {
        return clusters.toString();
    }

    public int[] getBestClusterToNode(int i) {
        int[] toRet = new int[2];
        int highestValue = Integer.MIN_VALUE;
        for (int cl : clusters.keySet()) {
            int counter = 0;
            for (int node : clusters.get(cl)) {
                if (instance.areAdjacents(i, node)) {
                    counter++;
                }
            }
            if (counter > highestValue) {
                toRet[0] = cl;
                toRet[1] = counter;
                highestValue = counter;
            }
        }
        return toRet;
    }
}
