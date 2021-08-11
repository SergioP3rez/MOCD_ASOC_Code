package experiments;

import structure.MOCDInstance;
import structure.MOCDSolution;

public class TestSolution {
    public static void main(String[] args){
        MOCDInstance instance = new MOCDInstance("instances/test/karate.txt");
        MOCDSolution sol = new MOCDSolution(instance);
        sol.addNewCluster(0);
        sol.addNodeToCluster(0, 0);
        sol.addNodeToCluster(1, 0);
        sol.addNodeToCluster(2, 0);
        sol.addNodeToCluster(3, 0);
        sol.addNodeToCluster(4, 0);
        sol.addNodeToCluster(5, 0);
        sol.addNodeToCluster(6, 0);
        sol.addNodeToCluster(7, 0);
        sol.addNodeToCluster(10, 0);
        sol.addNodeToCluster(11, 0);
        sol.addNodeToCluster(12, 0);
        sol.addNodeToCluster(13, 0);
        sol.addNodeToCluster(16, 0);
        sol.addNodeToCluster(17, 0);
        sol.addNodeToCluster(19, 0);
        sol.addNodeToCluster(21, 0);

        sol.addNodeToCluster(8, 1);
        sol.addNodeToCluster(9, 1);
        sol.addNodeToCluster(14, 1);
        sol.addNodeToCluster(15, 1);
        sol.addNodeToCluster(18, 1);
        sol.addNodeToCluster(20, 1);
        sol.addNodeToCluster(22, 1);
        sol.addNodeToCluster(23, 1);
        sol.addNodeToCluster(24, 1);
        sol.addNodeToCluster(25, 1);
        sol.addNodeToCluster(26, 1);
        sol.addNodeToCluster(27, 1);
        sol.addNodeToCluster(28, 1);
        sol.addNodeToCluster(29, 1);
        sol.addNodeToCluster(30, 1);
        sol.addNodeToCluster(31, 1);
        sol.addNodeToCluster(32, 1);
        sol.addNodeToCluster(33, 1);

        sol.updateEdges();
        sol.calculateNRA();
        sol.calculateRC();
        System.out.println(sol.getNra());
        System.out.println(sol.getRc());
        System.out.println(sol.getModularity());
        System.out.println(sol.getClusters());
    }
}
