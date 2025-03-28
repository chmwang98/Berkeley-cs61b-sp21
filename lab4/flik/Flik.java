package flik;

/** An Integer tester created by Flik Enterprises.
 * @author Josh Hug
 * */
public class Flik {
    /** @param a Value 1
     *  @param b Value 2
     *  @return Whether a and b are the same */
    public static boolean isSameNumber(Integer a, Integer b) {
        /**
         * To compare two Integers (not ints), the addresses of each other are compared
         * Java caches the values in [-128, 127], true because they point to the same address
         * Out of the range, new objects will be created, for example: new Integer(128). So addresses are different
         */

        return a.equals(b);
    }
}
