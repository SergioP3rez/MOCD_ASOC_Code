package structure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UnionFind {
    private ArrayList<Integer> p, rank, setSize;
    private int numSets;
    private int initialSets;
    private Set<Integer> existingClusters;

    public UnionFind(int n) {
        p = new ArrayList<>(n);
        rank = new ArrayList<>(n);
        setSize = new ArrayList<>(n);
        numSets = n;
        initialSets = n;
        existingClusters = new HashSet<>(n);
        for (int i = 0; i < n; i++) {
            p.add(i);
            rank.add(0);
            setSize.add(1);
            existingClusters.add(i);
        }
    }

    public UnionFind(UnionFind uf) {
        p = new ArrayList<>(uf.p);
        rank = new ArrayList<>(uf.rank);
        setSize = new ArrayList<>(uf.setSize);
        numSets = uf.numSets;
        initialSets = uf.initialSets;
        existingClusters = new HashSet<>(uf.existingClusters);
    }

    public int findSet(int i) {
//        if (p.get(i) == i) return i;
//        else {
//            int ret = findSet(p.get(i));
//            p.set(i, ret);
//            return ret;
//        }
        while (i != p.get(i)) {
            i = p.get(i);
        }
        p.set(i, i);
        return i;
    }

    public Boolean isSameSet(int i, int j) {
        return findSet(i) == findSet(j);
    }

    public int unionSet(int i, int j) {
        if (!isSameSet(i, j)) {
            numSets--;
            int x = findSet(i), y = findSet(j);
            if (rank.get(x) > rank.get(y)) {
                p.set(y, x);
                setSize.set(x, setSize.get(x) + setSize.get(y));
                existingClusters.remove(y);
                return x;
            } else {
                p.set(x, y);
                setSize.set(y, setSize.get(y) + setSize.get(x));
                existingClusters.remove(x);
                if (rank.get(x).equals(rank.get(y))) rank.set(y, rank.get(y) + 1);
                return y;
            }
        }
        return findSet(i);
    }

    //Union method to Insertion Local Search Procedures
    public int unionElement(int node, int cluster) {

        int x = findSet(node);

        p.set(node, cluster);
        setSize.set(cluster, setSize.get(cluster) + 1);
        if (getAllElementsOfSet(x).size() == 0) {
            existingClusters.remove(x);
            numSets--;
        } else if (!existingClusters.contains(cluster)) {
            existingClusters.add(cluster);
            numSets++;
        }

        if (rank.get(x).equals(rank.get(cluster))) rank.set(cluster, rank.get(cluster) + 1);

        return cluster;

    }

    public int numDisjointSets() {
        return numSets;
    }

    public int sizeOfSet(int i) {
        return setSize.get(findSet(i));
    }

    public boolean isRemoved(int cl) {
        return !existingClusters.contains(cl);
    }

    public List<Integer> getAllElementsOfSet(int i) {
        List<Integer> toRet = new ArrayList<>(numSets);
        for (int j = 0; j < initialSets; j++) {
            int conjunto = findSet(j);
            if (conjunto == i) {
                toRet.add(j);
            }
        }
        setSize.set(i, toRet.size());
        return toRet;
    }

    public int getInitialSets() {
        return initialSets;
    }

    public Set<Integer> getExistingClusters() {
        return existingClusters;
    }

    @Override
    public String toString() {
        StringBuilder toRet = new StringBuilder();
        for (int i = 0; i < initialSets; i++) {
            if (!existingClusters.contains(i)) continue;
            toRet.append(" ").append(i).append(": [");
            for (int node : getAllElementsOfSet(i)) {
                toRet.append(node).append(" ");
            }
            toRet.deleteCharAt(toRet.length() - 1).append("]");
        }
        return toRet.toString();
    }
}
