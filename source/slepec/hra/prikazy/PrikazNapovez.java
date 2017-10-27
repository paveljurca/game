package slepec.hra.prikazy;

import slepec.hra.Hra;

/**
 * Vrati uzivateli seznam prikazu hry + malou napovedu, jaky je ucel hry
 *
 * @author Pavel Jurca, xjurp20@vse.cz
 * @version 2
 */
public class PrikazNapovez extends Prikaz {

    public PrikazNapovez(Hra hra, String popis) {
        super(hra, popis);
    }

    @Override
    public boolean proved(String[] parametry) {
        result = " .najdete vodu a prasek cepels\n"
                + " ..seberte je a pote\n"
                + " ...prasek snezte a vodu vypijte!\n\n";
        result += hra.getVypisPrikazu();

        return true;
    }

    @Override
    public String toString() {
        return PrikazyVycet.NAPOVEZ.toString();
    }
}
