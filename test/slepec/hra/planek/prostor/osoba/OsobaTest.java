/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package slepec.hra.planek.prostor.osoba;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import slepec.hra.planek.prostor.vec.*;

/**
 *
 * @author gogo
 */
public class OsobaTest {
    
    private Osoba osobaBatoh;
    private Osoba osobaNotBatoh;
    private VecBatoh batoh;
    private Vec povidla;
    
    public OsobaTest() {
    }
    
    @Before
    public void setUp() {
        batoh = new VecBatoh("batoh","skolni",1000,2);
        povidla = new Vec("povidla","svestkova",125);
        
        osobaBatoh = new Osoba("osoba","s batohem");
        osobaNotBatoh = new Osoba("osoba","bez batohu");
        
        osobaBatoh.pridejVec(batoh);
    }
    
    @Test
    public void testSeberSBatohem() {
        assertTrue(osobaBatoh.pridejVec(povidla));
        assertFalse(batoh.getVnitrek().isEmpty());
        
        assertTrue(osobaBatoh.hasVec(povidla.toString()));
    }
    
    @Test
    public void testSeberBezBatohu() {
        assertFalse(osobaNotBatoh.pridejVec(povidla));
    }
    
    @Test
    public void testHasVec() {
        batoh.pridej(povidla);
        assertTrue(batoh.hasVec(povidla.toString()));
        
        assertFalse(osobaNotBatoh.hasVec("povidla"));
        osobaNotBatoh.pridejVec(batoh);
        assertTrue(osobaNotBatoh.hasVec("povidla"));
    }
    
    @Test
    public void testSeber_BatohLimitVaha() {
        assertSame(0,batoh.getVaha());
        
        int maxVaha = batoh.getMaxZatez();
        assertFalse(osobaBatoh.pridejVec(
                new Vec("kamen","zula",maxVaha+1)));
        assertTrue(osobaBatoh.pridejVec(
                new Vec("kamen","zula",maxVaha)));
        
        assertTrue(maxVaha == batoh.getVaha());
    }
    
    @Test
    public void testSeber_BatohLimitPocet() {
        int max = batoh.getMaxPocVeci();
        for (int i=0;i<=max;i++) {
            Vec pridat = new Vec(""+i,"",1);
            if (i==max) {
                assertFalse(osobaBatoh.pridejVec(pridat));
            } else {
                assertTrue(osobaBatoh.pridejVec(pridat));
            }
        }
    }
            
    @Test
    public void testOdlozSeberBatoh() {
        assertNotNull(osobaBatoh.getBatoh());
        assertEquals(batoh, osobaBatoh.odeberVec(batoh.toString()));
        assertNull(osobaBatoh.getBatoh());
        
        osobaBatoh.pridejVec(batoh);
        assertNotNull(osobaBatoh.getBatoh());
    }
    
    @Test
    public void testVypit() {
        Vec pepsi = new Vec("pepsi","black",500);
        osobaBatoh.pridejVec(null);
        assertTrue(osobaBatoh.getVypito().isEmpty());
        
        osobaBatoh.pij(pepsi);
        assertEquals(pepsi, osobaBatoh.getVypito().get(0));
    }
}
