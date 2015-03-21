package slepec.hra;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import slepec.hra.planek.HerniPlan;
import slepec.hra.planek.prostor.Prostor;
import slepec.hra.planek.prostor.osoba.*;
import slepec.hra.planek.prostor.vec.*;

/**
 *
 * @author
 */
public class HraTest {

    private Hra hra;
    private HerniPlan herniPlan;

    @Before
    public void setUp() {
        hra = new Hra(false);
        herniPlan = hra.getHerniPlan();
    }

    @Test
    public void testNaVyhru() {
        Vec b = null;
        for (Prostor p : herniPlan.getProstory()) {
            b = p.selectVec("bagl");
            if (b!=null) {
                break;
            }
        }
        if (b != null && b instanceof VecBatoh) {
            herniPlan.hlavniPostava().pridejVec((VecBatoh)b);
        }
            
        assertNotNull(herniPlan.hlavniPostava().getBatoh());
        //postup
        String[] postup = {
            "toaleta",
            "seber mydlo skrinka",
            "jdi predsin",
            "jdi zahrada",
            "poloz mydlo myval",
            "seber klicKOMORA myval",
            "jdi predsin",
            "jdi pracovna",
            "poloz klickomora",
            "seber suplik stolek",
            "poloz suplik",
            "seber cepels suplik",
            "seber klickomora",
            "jdi predsin",
            "jdi chodba",
            "odemkni komora",
            "jdi komora",
            "seber voda",
            "jez cepels",
            "pij voda"};
        hra.nactiStavHry(postup);

        assertTrue(hra.isVyhra());
    }

    @Test
    public void testIsGameOver() {
        boolean expResult = hra.isCasovac();
        boolean result = hra.isGameOver();

        assertNotSame(expResult, result);
    }

    @Test
    public void testNactiStavHry() {
        String[] prikazy = new String[]{
            "obyvak", "jdi zahrada", "jdi predsin"};
        hra.nactiStavHry(prikazy);

        assertEquals(herniPlan.selectProstor("predsin"),
                hra.getHerniPlan().kdeJsem());
    }

    @Test
    public void testHerniPlanSlepec() {
        Prostor komora = herniPlan.selectProstor("komora");
        assertNotNull(komora);
        assertTrue(komora.isZamknuto());
        assertNotNull(komora.selectVec("voda"));

        Vec batoh = null;
        Osoba myval = null;
        for (Prostor p : herniPlan.getProstory()) {
            if (p.selectVec("bagl") != null) {
                batoh = p.selectVec("bagl");
            }
            if (p.selectOsoba("myval") != null) {
                myval = p.selectOsoba("myval");
            }
        }

        assertNotNull(batoh);
        assertTrue(batoh instanceof VecBatoh);
        assertNotNull(myval);
        assertTrue(myval.hasVec("klicKOMORA"));

        Prostor chodba = herniPlan.selectProstor("chodba");
        assertNotNull(chodba);
        assertNotNull(chodba.selectVec("bagl"));
        assertTrue(chodba.selectVec("bagl").getVnitrek().isEmpty());

        Prostor pracovna = herniPlan.selectProstor("pracovna");
        assertNotNull(pracovna);
        assertNotNull(pracovna.selectVec("stolek"));
        assertTrue(pracovna.selectVec("stolek").hasVec("cepels"));
    }
}
