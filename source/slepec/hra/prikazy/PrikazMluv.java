package slepec.hra.prikazy;

import slepec.hra.Hra;
import slepec.hra.planek.HerniPlan;
import slepec.hra.planek.prostor.osoba.*;

/**
 * Vyvola metodu 'promluv' na urcite osobe (dle parametru)
 * 
 * @author Pavel Jurca, xjurp20@vse.cz
 * @version 2
 */
public class PrikazMluv extends Prikaz {

    public PrikazMluv(Hra hra, String popis) {
        super(hra, popis);
    }

    /**
     * Lze mluvit s osobou
     *
     * @param parametry
     * @return
     */
    @Override
    public boolean proved(String[] parametry) {
        if (null==parametry ||
                parametry.length < 1 || parametry[0].equals("")) {
            result = " err: dobre, mluv, ale s kym?";
            return false;
        }

        HerniPlan planek = hra.getHerniPlan();
        Osoba kdo = planek.kdeJsem().selectOsoba(parametry[0]);
        if (kdo != null) {
            switch (parametry.length) {
                case 1:
                    result = " " + kdo + ": \"" + kdo.promluv() + "\"";

                    return true;
                case 2:
                    result = " " + kdo + ": \""
                            + kdo.promluv(parametry[1]) + "\"";

                    return true;
                default:
                    result = " err: spatny pocet parametru"
                            + String.format("\n%6s%s", "", "tip: 'napovez'");
                    break;
            }
        } else {
            result = " err: " + parametry[0] + "? Neznam";
        }

        return false;
    }

    @Override
    public String toString() {
        return PrikazyVycet.MLUV.toString();
    }
}
