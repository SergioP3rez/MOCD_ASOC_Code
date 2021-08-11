package structure;

import grafo.optilib.structure.Instance;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

public class MOCDInstance implements Instance {


    private String name;
    private Set<Integer>[] graph;

    private int n;
    private int m;

    public MOCDInstance(String path) {
        readInstance(path);
    }

    @Override
    public void readInstance(String path) {
        try {
            name = path.substring(path.lastIndexOf('/') + 1);
            BufferedReader bf = new BufferedReader(new FileReader(path));
            String line = bf.readLine();
            String[] tokens = line.split("\\s+");
            n = Integer.parseInt(tokens[0]);
            m = Integer.parseInt(tokens[1]);
            graph = (HashSet<Integer>[]) new HashSet[n];
            for (int i = 0; i < n; i++) {
                graph[i] = new HashSet<>(n);
            }
            while ((line = bf.readLine()) != null) {
                tokens = line.split("\\s+");
                int src;
                int dst;
                if (!name.contains("previous")) {
                    // TO READ PREVIOUS INSTANCES
                    src = Integer.parseInt(tokens[0]);
                    dst = Integer.parseInt(tokens[1]);
                }else{
                    // TO READ OURS
                    src = Integer.parseInt(tokens[0]) - 1;
                    dst = Integer.parseInt(tokens[1]) - 1;
                }

                if (src == dst) continue;
                graph[src].add(dst);
                graph[dst].add(src);
            }
            bf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getN() {
        return n;
    }

    public int getM() {
        return m;
    }

    public String getName() {
        return name;
    }

    public boolean areAdjacents(int node, int i) {
        return (this.graph[node].contains(i) || this.graph[i].contains(node));
    }

    public Set<Integer> getAdjacents(int node) {
        return this.graph[node];
    }

    public double getDegree(int node) {
        return this.graph[node].size();
    }
}
