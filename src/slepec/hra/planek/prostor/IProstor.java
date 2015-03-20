package slepec.hra.planek.prostor;

import java.util.Set;
import slepec.hra.planek.prostor.osoba.Osoba;
import slepec.hra.planek.prostor.vec.Vec;

/**
 * Definice verejnych metod pro ovladani prostoru hry
 *
 * @author Pavel Jurca, xjurp20@vse.cz
 * @version 2
 */
public interface IProstor {

    String getInfo();

    void setVychody(Prostor... vychody);

    boolean pridejVeci(Vec... veci);

    boolean pridejOsoby(Osoba... osoby);

    Vec selectVec(String nazevVeci);

    Osoba selectOsoba(String jmenoOsoby);

    Vec odeberVec(String nazevVeci);

    Osoba odeberOsoba(String jmenoOsoby);

    Set<Vec> getVeci();

    Set<Osoba> getOsoby();

    boolean isPruchoziS(Prostor vychod);

    boolean zamknout();

    boolean odemknout();

    boolean isZamknuto();

    boolean lzeZamknout();
}
