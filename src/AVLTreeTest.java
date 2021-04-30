
import java.util.Random;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.System.nanoTime;

public class AVLTreeTest {
    public static void main(String[] args) {
        firstExperiment();
        System.out.println();
        secondExperiment();


    }

    public static void firstExperiment() {
        for (int i = 1; i <= 5; ++i) {
           AVLTree t = randomAVLTree(500 * i);
           int[] keys = t.keysToArray();
           long totalSlow = 0, totalFast = 0, start, stop;
            for (int j = 0; j < 100; j++) {
                start = nanoTime();
                t.prefixXor(keys[j]);
                stop = nanoTime();
                totalFast += stop - start;

                start = nanoTime();
                t.succPrefixXor(keys[j]);
                stop = nanoTime();
                totalSlow += stop - start;
            }
            double avgSlow100 = totalSlow / 100.0;
            double avgFast100 = totalFast / 100.0;

            for (int j = 100; j < i * 500; j++) {
                start = nanoTime();
                t.prefixXor(keys[j]);
                stop = nanoTime();
                totalFast += stop - start;

                start = nanoTime();
                t.succPrefixXor(keys[j]);
                stop = nanoTime();
                totalSlow += stop - start;
            }
            double avgSlow = totalSlow / (i * 500.0);
            double avgFast = totalFast / (i * 500.0);

            System.out.printf("i=%d | %f | %f | %f | %f\n",i, avgFast, avgSlow, avgFast100, avgSlow100);

            }

        }


    private static AVLTree randomAVLTree(int n) {
        AVLTree t = new AVLTree();
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            t.insert(random.nextInt(Integer.MAX_VALUE), true);
        }
        return t;
    }

    private static BSTree randomBSTTree(int n) {
        BSTree t = new BSTree();
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            t.insert(random.nextInt(100), true);
        }
        return t;
    }

    public static void secondExperiment() {
        for (int i = 1; i <= 5 ; i++) {
             double[] avgs = new double[6];
             avgs[0] = measureAvg(IntStream.iterate(0, n -> n + 1).limit(i * 1000), new AVLTree());
             avgs[1] = measureAvg(IntStream.iterate(0, n -> n + 1).limit(i * 1000), new BSTree());
             avgs[2] = measureAvg(IntStream.generate(new OptimalSequence(i * 1000)).limit(i * 1000), new AVLTree());
             avgs[3] = measureAvg(IntStream.generate(new OptimalSequence(i * 1000)).limit(i * 1000), new BSTree());
             avgs[4] = measureAvg(IntStream.generate(() -> new Random().nextInt(Integer.MAX_VALUE)).limit(i * 1000), new AVLTree());
             avgs[5] = measureAvg(IntStream.generate(() -> new Random().nextInt(Integer.MAX_VALUE)).limit(i * 1000), new BSTree());

            System.out.printf("i=%d | %f | %f | %f | %f | %f | %f\n", i, avgs[0], avgs[1], avgs[2], avgs[3], avgs[4], avgs[5]);
        }
    }


    private static double measureAvg(IntStream stream, IBSTree tree) {
        return stream.mapToLong(i -> timeInsert(tree, i)).average().getAsDouble();
    }

    public static long timeInsert(IBSTree t, int i) {
        long start = nanoTime();
        t.insert(i, true);
        long end = nanoTime();
        return end - start;
    }

    static class OptimalSequence implements IntSupplier {
        int n;
        int exp = 1;
        int i = -1;
        public OptimalSequence(int n) {this.n = n;}

        @Override
        public int getAsInt() {
            i += 2;
            if (i > Math.pow(2, exp)) {
                exp++;
                i = 1;
            }
            return  (int) Math.ceil((i * (n+1)) / (Math.pow(2, exp)));
        }
    }
}
