/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package slepec.hra.planek.prostor.vec;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author gogo
 */
public class VecBatohTest {

    private VecBatoh batoh;
    private Vec svacina;
    private Vec sesit;
    private Vec skrinka;
    private Vec krabicka;
    private Vec sutrak;

    public VecBatohTest() {
    }

    @Before
    public void setUp() {
        //max.zatez 2kg, max.poc.veci 4
        batoh = new VecBatoh("batoh", "burton", 2000, 4);

        //1. uroven zanoreni
        svacina = new Vec("svacina", "rohlik s pastikou", 0);
        sesit = new Vec("sesit", "ukolnicek", 0);
        //3. uroven zanoreni
        skrinka = new Vec("skrinka", "", 1200, true);
        krabicka = new Vec("krabicka", "", 300, true);
        sutrak = new Vec("sutrak", "", 700);
        //2. uroven zanoreni
        krabicka.pridej(sutrak);
        //3. uroven zanoreni
        skrinka.pridej(krabicka);
    }

    @Test
    public void testPrekroceniVahy() {
        Vec kamen = new Vec("kamen", "mramor", 4000);
        assertFalse(batoh.getMaxZatez() >= kamen.getVaha());
        assertFalse(batoh.pridej(kamen));

        assertFalse(batoh.getMaxZatez() >= skrinka.getVaha());
        assertFalse(batoh.pridej(skrinka));
    }

    @Test
    public void testPrekroceniPoctu() {
        for (int i = 0; i < batoh.getMaxPocVeci(); i++) {
            assertTrue(batoh.pridej(new Vec("" + i, "", 1)));
        }
        assertFalse(batoh.pridej(new Vec("vec", "", 0)));
        //
        setUp();
        //
        assertTrue(0 == batoh.getPocVeci());
        batoh.pridej(krabicka, sesit, svacina);
        assertTrue(4 == batoh.getPocVeci());

        assertTrue(5 > batoh.getMaxPocVeci());
        assertFalse(batoh.pridej(new Vec("vecB", "becko", 0)));
    }

    @Test
    public void testGetPocVeci() {
        batoh.pridej(svacina);
        assertTrue(1 == batoh.getPocVeci());

        batoh.pridej(krabicka);
        assertTrue(3 == batoh.getPocVeci());
    }

    @Test
    public void testNelzeVlozitBatoh() {
        assertFalse(batoh.pridej(new VecBatoh("batuzek", "husky", 1, 1)));
        //sice uz nejde o instanci VecBatoh, ale ma stejny nazev s nasim batohem
        assertFalse(batoh.pridej(new Vec("batoh", "husky", 1)));
    }
}
