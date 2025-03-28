package flik;

import org.junit.Test;
import static org.junit.Assert.*;

public class FlikTest {
    @Test
    public void test128() {
        assertTrue(Flik.isSameNumber(128, 128));
    }
}
