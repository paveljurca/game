package slepec.hra.prikazy;

import slepec.hra.Hra;

/**
 * Ukonci hru
 *
 * @author Pavel Jurca, xjurp20@vse.cz
 * @version 2
 */
public class PrikazKonec extends Prikaz {

    public PrikazKonec(Hra hra, String popis) {
        super(hra, popis);
    }

    @Override
    public boolean proved(String[] parametry) {
        hra.ukonciHru();
        return true;
    }

    @Override
    public String toString() {
        return PrikazyVycet.KONEC.toString();
    }
}
