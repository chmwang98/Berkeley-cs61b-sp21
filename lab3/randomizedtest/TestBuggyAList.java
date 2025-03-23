package randomizedtest;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> alist = new AListNoResizing<Integer>();
        BuggyAList<Integer> blist = new BuggyAList<Integer>();
        for (int i = 4; i <= 6; i++) {
            alist.addLast(i);
            blist.addLast(i);
        }

        assertEquals(alist.size(), blist.size());
        while (alist.size() > 0) {
            assertEquals(alist.removeLast(), blist.removeLast());
        }
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> correct = new AListNoResizing<>();
        BuggyAList<Integer> broken = new BuggyAList<>();
        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 3);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                correct.addLast(randVal);
                broken.addLast(randVal);
//                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = correct.size();
                if (size == 0) {
                    continue;
                }
//                assertEquals(correct.getLast(), broken.getLast());
//                System.out.println("getLast(" + L.getLast() + ")");
            } else if (operationNumber == 2) {
                int size = correct.size();
                if (size == 0) {
                    continue;
                }
//                assertEquals(correct.removeLast(), broken.removeLast());
//                System.out.println("removeLast(" + L.removeLast() + ")");
            }
        }
    }
}
