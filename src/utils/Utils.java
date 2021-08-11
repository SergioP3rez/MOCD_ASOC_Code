package utils;

public class Utils {
//    public static int compareDouble(double f1, double f2) {
//        return (int) (f1 * 10) - (int) (f2 * 10);
////        return (int) (f1) - (int) (f2);
//    }

    public static double round(double number) {
        int pow = 100;
        double tmp = number * pow;
        return ( (double) ( Math.round(tmp) ) )/ pow;
    }

//    public static int compareDouble2decs(double f1, double f2) {
//        return (int) (f1 * 100) - (int) (f2 * 100);
//    }
    public static int compareDouble(double f1, double f2) {
        if (Math.abs(f1 - f2) < 0.001) {
            return 0;
        } else if (f1 < f2) {
            return -1;
        } else {
            return 1;
        }
    }
}
