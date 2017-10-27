package slepec.hra.planek.prostor.osoba;

import java.util.List;
import slepec.hra.planek.prostor.vec.*;

/**
 * Verejne rozhrani pro ovladani osoby
 *
 * @author Pavel Jurca, xjurp20@vse.cz
 * @version 2
 */
public interface IOsoba extends slepec.util.Subject {
    
    //osoba muze prenaset veci jen pokud ma batoh
    boolean pridejVec(Vec vec);

    Vec odeberVec(String nazevVeci);

    boolean hasVec(String nazevVeci);

    Vec selectVec(String nazevVeci);

    VecBatoh getBatoh();

    void jez(Vec jidlo);

    void pij(Vec piti);

    List<Vec> getSnedeno();

    List<Vec> getVypito();

    String promluv();
    //kazda osoba by mela umet vice monologu

    String promluv(String oCem);

    String getPopis();

    String getMonology();

    String getSeznamVeci();

    void prejmenuj(String noveJmeno);
}
