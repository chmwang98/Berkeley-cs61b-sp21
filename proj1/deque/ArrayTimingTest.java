package deque;

import edu.princeton.cs.algs4.Stopwatch;
/**
 * Created by hug.
 */
public class ArrayTimingTest {
    private static void printTimingTable(ArrayDeque<Integer> Ns, ArrayDeque<Double> times, ArrayDeque<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        adAddLast();
        adAddFirst();
    }

    public static void adAddLast() {
        ArrayDeque<Integer> Ns = new ArrayDeque<Integer>();
        ArrayDeque<Double> times = new ArrayDeque<Double>();
        ArrayDeque<Integer> opCounts = new ArrayDeque<Integer>();
        for (int N = 1024; N <= 16384; N *= 2) {
            Ns.addLast(N);

            Stopwatch sw = new Stopwatch();
            ArrayDeque<Integer> lst = new ArrayDeque<Integer>();
            for(int i = 0; i < N; i++) {
                lst.addLast(i);
            }
            double timeInSeconds = sw.elapsedTime();
            times.addLast(timeInSeconds);

            opCounts.addLast(N);
        }
        printTimingTable(Ns, times, opCounts);
    }

    public static void adAddFirst() {
        ArrayDeque<Integer> Ns = new ArrayDeque<Integer>();
        ArrayDeque<Double> times = new ArrayDeque<Double>();
        ArrayDeque<Integer> opCounts = new ArrayDeque<Integer>();
        for (int N = 1024; N <= 16384; N *= 2) {
            Ns.addLast(N);

            Stopwatch sw = new Stopwatch();
            ArrayDeque<Integer> lst = new ArrayDeque<Integer>();
            for(int i = 0; i < N; i++) {
                lst.addFirst(i);
            }
            double timeInSeconds = sw.elapsedTime();
            times.addLast(timeInSeconds);

            opCounts.addLast(N);
        }
        printTimingTable(Ns, times, opCounts);
    }
}
