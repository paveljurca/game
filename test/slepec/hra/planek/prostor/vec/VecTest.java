/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package slepec.hra.planek.prostor.vec;

import java.util.Iterator;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author
 */
public class VecTest {

    private Vec vecA;
    private Vec vecB;

    public VecTest() {
    }

    @Before
    public void setUp() {
        //true, muze obsahovat dalsi veci
        vecA = new Vec("a", "vec A", 10, true);
        //nemuze obsahovat dalsi veci
        vecB = new Vec("b", "vec B", 20);
    }

    @Test
    public void testPridejOdeber() {
        vecA.pridej(vecB);
        Vec result = vecA.odeber(vecB.toString());

        assertEquals("Ocekavana vecB", vecB, result);
        assertTrue(vecA.getVnitrek().isEmpty());
    }

    @Test
    public void testOdeberZeZanoreni() {
        Vec c = new Vec("c", "cecko", 1, true);
        Vec d = new Vec("d", "decko", 1);
        c.pridej(d);
        assertFalse(c.getVnitrek().isEmpty());

        vecA.pridej(c);
        assertTrue(vecA.hasVec("d"));
        assertSame(1,vecA.getVnitrek().size());
        
        assertEquals(d,vecA.odeber("d"));
        assertTrue(c.getVnitrek().isEmpty());
        
        assertFalse(vecA.hasVec("d"));
    }

    @Test
    public void testLzePridat() {
        assertTrue("Nemuze obsahovat dalsi veci", vecA.isSchranka());
        assertTrue("Nemuze obsahovat dalsi veci",
                vecA.pridej(new Vec("c", "d", 20)));
    }

    @Test
    public void testNelzePridat() {
        assertSame(vecB.isSchranka(), vecB.pridej(vecA));
        assertTrue(vecB.getVnitrek().isEmpty());
    }

    @Test
    public void testNelzePridatNull() {
        Vec[] veci = new Vec[4];
        veci[2] = new Vec("neco", "neco", 10);

        assertTrue(vecA.getVnitrek().isEmpty());
        vecA.pridej(veci);
        assertSame(1, vecA.getVnitrek().size());
    }

    @Test
    public void testNelzePridatStejnouVec() {
        assertFalse(vecA.pridej(vecA));

        //to plati i o zanorenych vecech
        Vec vec = new Vec("obal", "papirovy", 1, true);
        vec.pridej(new Vec("a", "parodie na vec A", 4));

        assertFalse(vecA.pridej(vec));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnmodifiableVnitrek() {
        //veci lze odebrat jen pres metodu odeber(String nazevVeci)
        vecA.pridej(vecB);
        assertSame(1, vecA.getVnitrek().size());

        Iterator<Vec> iter = vecA.getVnitrek().iterator();
        while (iter.hasNext()) {
            iter.remove();
        }
    }

    @Test
    public void testSeznamVeci() {
        Vec c = new Vec("c", "cecko", 1, true);
        Vec d = new Vec("d", "decko", 1, true);
        d.pridej(new Vec("e", "ecko", 1));
        c.pridej(d);

        vecA.pridej(c, vecB);
        //HashSet pro ukladani veci neni trideny
        assertTrue("c(d(e)), b".equals(vecA.getSeznamVeci())
                || "b, c(d(e))".equals(vecA.getSeznamVeci()));
    }

    @Test
    public void testZapornaVaha() {
        Vec vec = new Vec("c", "d", -298520);
        assertSame(0, vec.getVaha());
    }

    @Test
    public void testZmenaVahy() {
        vecA.pridej(vecB);
        assertSame(30, vecA.getVaha());
    }

    @Test
    public void testToString() {
        assertEquals("a", vecA.toString());
    }

    @Test
    public void testIsEqual() {
        Vec novaVec = new Vec("A", "cokoli", 12, false);
        assertTrue("VecA se nejmenuje 'a' nebo 'A'", novaVec.equals(vecA));
    }
}
