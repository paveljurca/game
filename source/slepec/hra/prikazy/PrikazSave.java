package slepec.hra.prikazy;

import slepec.hra.Hra;

/**
 * Prikaz pro ulozeni dosavadniho postupu ve hre <i>diky tomu ji bude mozne pri
 * pristim spusteni hry obnovit</i>
 *
 * @author Pavel Jurca, xjurp20@vse.cz
 * @version 2
 */
public class PrikazSave extends Prikaz {

    public PrikazSave(Hra hra, String popis) {
        super(hra, popis);
    }

    @Override
    public boolean proved(String[] parametry) {
        if (hra.ulozAktualniHra()) {
            result = " USPECH: hra byla ulozena";

            return true;
        } else {
            result = " err: hru zatim nelze ulozit";

            return false;
        }
    }

    @Override
    public String toString() {
        return PrikazyVycet.SAVE.toString();
    }
}
