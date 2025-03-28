package tester;

import static org.junit.Assert.*;

import edu.princeton.cs.introcs.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

import java.util.ArrayList;

public class TestArrayDequeEC {
    @Test
    public void randomTest() {
        StudentArrayDeque<Integer> sad1 = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> ads1 = new ArrayDequeSolution<>();
        ArrayList<String> callHistory = new ArrayList<>();

        for (int i = 0; i < 999; i++) {
            int operation = StdRandom.uniform(4);

            switch (operation) {
                case 0: // addFirst
                    int numToAddFirst = StdRandom.uniform(666);
                    sad1.addFirst(numToAddFirst);
                    ads1.addFirst(numToAddFirst);
                    callHistory.add("addFirst(" + numToAddFirst + ")");
                    break;

                case 1: // addLast
                    int numToAddLast = StdRandom.uniform(666);
                    sad1.addLast(numToAddLast);
                    ads1.addLast(numToAddLast);
                    callHistory.add("addLast(" + numToAddLast + ")");
                    break;

                case 2: // removeFirst
                    if (!sad1.isEmpty() && !ads1.isEmpty()) {
                        Integer expected = ads1.removeFirst();
                        Integer actual = sad1.removeFirst();
                        callHistory.add("removeFirst()");
                        assertEquals(String.join("\n", callHistory), expected, actual);
                    }
                    break;

                case 3: // removeLast
                    if (!sad1.isEmpty() && !ads1.isEmpty()) {
                        Integer expected = ads1.removeLast();
                        Integer actual = sad1.removeLast();
                        callHistory.add("removeLast()");
                        assertEquals(String.join("\n", callHistory), expected, actual);
                    }
                    break;
            }
        }
    }
}