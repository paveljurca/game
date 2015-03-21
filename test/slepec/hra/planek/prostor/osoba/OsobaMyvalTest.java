/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package slepec.hra.planek.prostor.osoba;

import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import slepec.hra.planek.prostor.vec.*;

/**
 *
 * @author
 */
public class OsobaMyvalTest {

    private OsobaMyval myval;
    private VecBatoh batuzek;
    private Vec mydlo;
    private Vec kolac;

    public OsobaMyvalTest() {
    }

    @Before
    public void setUp() {
        mydlo = new Vec("mydlo", "mejdlo", 80);
        kolac = new Vec("kolac", "makovy", 60);
        myval = new OsobaMyval("myval", "chlupate zviratko",
                new HashMap<String, String>());
        batuzek = new VecBatoh("batuzek", "myvaluv batuzek", 5000, 10);

        batuzek.pridej(kolac);
        myval.pridejVec(batuzek);
    }

    @Test
    public void testOdeberBatoh() {
        assertNull(myval.odeberVec(batuzek.toString()));
    }
    
    @Test
    public void testOdeberMydlo() {
        myval.pridejVec(mydlo);
        assertNull(myval.odeberVec(mydlo.toString()));
    }
    
    /**
     * Otestuje vymenny obchod mezi myvalem a nekym dalsim
     */
    @Test
    public void testBarter() {
        assertNull(myval.odeberVec(kolac.toString()));
        
        myval.pridejVec(mydlo);
        
        assertNotNull(myval.odeberVec(kolac.toString()));
    }
}
