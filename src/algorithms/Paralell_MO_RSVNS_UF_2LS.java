package algorithms;

import grafo.optilib.metaheuristics.Algorithm;
import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.metaheuristics.Improvement;
import grafo.optilib.results.Result;
import grafo.optilib.structure.Solution;
import grafo.optilib.tools.RandomManager;
import grafo.optilib.tools.Timer;
import structure.*;
import utils.Utils;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Paralell_MO_RSVNS_UF_2LS implements Algorithm<MOCDInstance> {
    private Constructive<MOCDInstance, MOCDSolutionUnionFind> c;
    private Improvement<MOCDSolution>[] ls;
    private int nConstructions;
    private int nShakes;
    private double kStep;
    private double kMax;
    private MOCDSolutionUnionFind best;
    private CountDownLatch latch;
    ExecutorService pool;

    public Paralell_MO_RSVNS_UF_2LS(Constructive<MOCDInstance, MOCDSolutionUnionFind> c, Improvement<MOCDSolution>[] ls, int nConstructions, double kStep, double kMax) {
        this.c = c;
        this.ls = ls;
        this.nConstructions = nConstructions;
        this.nShakes = Runtime.getRuntime().availableProcessors();
        this.kStep = kStep;
        this.kMax = kMax;
    }

    @Override
    public Result execute(MOCDInstance instance) {
        best = null;
        System.out.print(instance.getName() + "\t");
        Result r = new Result(instance.getName());
        ParetoUF.reset();
        pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        int iters = 0;
//        pool = Executors.newFixedThreadPool(1);

        Timer.initTimer(1800 * 1000);
        latch = new CountDownLatch(nConstructions);

        for (int i = 0; i < nConstructions; i++) {
            pool.submit(() -> {
                c.constructSolution(instance);
                latch.countDown();
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        System.out.println("CONSTRUCTED");
        Pareto.reset();
        for (MOCDSolutionUnionFind sol : ParetoUF.getFront()) {
            Pareto.add(new MOCDSolution(sol));
        }
        if (!Timer.timeReached()) {
            ArrayList<MOCDSolution> paretoCons = new ArrayList<>(Pareto.getFront());
            latch = new CountDownLatch(paretoCons.size());
            //Comentar para ver qué aporta la LS

            for (MOCDSolution sol : paretoCons) {
                pool.submit(() -> {
                    ls[0].improve(new MOCDSolution(sol));
                    ls[1].improve(new MOCDSolution(sol));
                    latch.countDown();
                });
            }
//            System.out.println("Termino LS después de construir");
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            List<MOCDSolution> E = new ArrayList<>(Pareto.getFront());

            double k = kStep;

            while (Utils.compareDouble(k, kMax) <= 0 && !Timer.timeReached()) { // k <= kMax
//            System.out.println("Mejora k: "+k);
                Pareto.reset();
                double k_final = k;
                List<MOCDSolution> E_final = E;
                latch = new CountDownLatch(nShakes);
                for (int i = 0; i < nShakes; i++) {
                    pool.submit(() -> {
                        this.shake(E_final, k_final);
                        latch.countDown();
                    });
                }
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//            System.out.println("ACABO EL SHAKE");
                List<MOCDSolution> E_prime = new ArrayList<>(Pareto.getFront());
                //Comentar para ver qué aporta la LS

                latch = new CountDownLatch(E_prime.size());
                for (MOCDSolution solution : E_prime) {
                    pool.submit(() -> {
                        ls[0].improve(new MOCDSolution(solution));
                        ls[1].improve(new MOCDSolution(solution));
                        latch.countDown();
                    });
                }
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


//            System.out.println("ACABO LA LS");
                List<MOCDSolution> E_second_prime = new ArrayList<>(Pareto.getFront());
                k = NC(k, E, E_second_prime);
//            System.out.println("ACABO LA NC");
                E = new ArrayList<>(Pareto.getFront());
                iters++;
//                if (Timer.timeReached()) {
////                System.err.println("TIEMPO LIMITE ALCANZADO: " + Timer.getTime() / 1000.0 + " (s)");
//                    break;
//                }
            }
        }

        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);

        String date = String.format("%04d-%02d-%02d", year, month, day);
//        ParetoUF.saveToFile("experiments/" + date + "/" + this.toString() + "/pareto_only_constLS" + instance.getName());

        double secs = Timer.getTime() / 1000.0;
//        System.out.println("Termino VNS");
        Random rnd2 = RandomManager.getRandom();
        ParetoUF.saveToFile("experiments/" + date + "/" + this.toString() + "/pareto_"+rnd2.nextInt()+"_" + instance.getName());
        r.add("Time (s)", secs);
        r.add("VNS_Time (s)", secs);
        r.add("VNS_Iters", iters);
        r.add("VNS_k_step", kStep);
        r.add("VNS_k_max", kMax);
        double maxMod = Integer.MIN_VALUE;

        for (MOCDSolution sol : Pareto.getFront()) {
            double actualMod = sol.getModularity();
            maxMod = (Double.compare(actualMod, maxMod) > 0) ? actualMod : maxMod;
        }
        r.add("Max. Modularity", maxMod);
        System.out.println("Time (s): " + secs);
        pool.shutdown();
        try {
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return r;
    }

    private double NC(double k, List<MOCDSolution> e_prime, List<MOCDSolution> e_second_prime) {
        Pareto.reset();
        for (MOCDSolution sol : e_prime) {
            Pareto.add(sol);
        }
        Pareto.isModifiedSinceLastAsk();
        for (MOCDSolution solution : e_second_prime) {
            Pareto.add(solution);
        }
        double toRet;
        toRet = (ParetoUF.isModifiedSinceLastAsk()) ? kStep : k + kStep;
        return toRet;
    }

    private void shake(List<MOCDSolution> paretoFront, double k) {

        for (MOCDSolution sol : paretoFront) {
            MOCDSolution aux = new MOCDSolution(sol);
            aux.shake(k);
            Pareto.add(aux);
        }

    }

    @Override
    public Solution getBestSolution() {
        return best;
    }

    private class MyEntry implements Map.Entry<Integer, Integer> {
        Integer key;
        Integer value;

        public MyEntry(Integer key, Integer value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public Integer getKey() {
            return key;
        }

        @Override
        public Integer getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "myentry{" +
                    "key=" + key +
                    ", value=" + value +
                    '}';
        }

        @Override
        public Integer setValue(Integer value) {
            Integer aux = this.value;
            this.value = value;
            return aux;
        }
    }

    private class MyEntryComparator implements Comparator<MyEntry> {
        @Override
        public int compare(MyEntry o1, MyEntry o2) {
            return o2.getValue().compareTo(o1.getValue());
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(" + c + "," + ls + ", " + kMax + ")";
    }

}
