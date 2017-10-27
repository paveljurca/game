package slepec.hra.prikazy;

import slepec.hra.Hra;
import slepec.hra.planek.HerniPlan;
import slepec.hra.planek.prostor.Prostor;

/**
 * PrikazJdi umoznuje uzivatelovi prochazet mistnostmi
 *
 * @author Pavel Jurca, xjurp20@vse.cz
 * @version 2
 */
public class PrikazJdi extends Prikaz {

    public PrikazJdi(Hra hra, String popis) {
        super(hra, popis);
    }

    @Override
    public boolean proved(String[] parametry) {
        if (null == parametry
                || parametry.length < 1 || parametry[0].equals("")) {
            result = " err: dobre, jdi, ale kam?";
            return false;
        }

        HerniPlan planek = hra.getHerniPlan();
        Prostor kam = planek.selectProstor(parametry[0]);

        if (kam != null) {
            if (!kam.equals(planek.kdeJsem())) {
                if (planek.kdeJsem().isPruchoziS(kam)) {
                    if (!planek.kdeJsem().isZamknuto()) {
                        if (!kam.isZamknuto()) {
                            if (planek.move(kam.toString())) {
                                result = planek.kdeJsem().getInfo();

                                return true;
                            } else {
                                result = " fatal err: nelze vstoupit do " + kam;
                            }

                        } else {
                            result = " neuspech: prostor "
                                    + kam + " je zamceny";
                        }
                    } else {
                        result = " neuspech: jste v zamcenem prostoru";
                    }
                } else {
                    result = " neuspech: " + planek.kdeJsem() + " a "
                            + kam + " nejsou pruchozi";
                }
            } else {
                result = " neuspech: v prostoru " + kam + " prave jste";
            }
        } else {
            result = " err: takovy prostor neexistuje";
        }

        return false;
    }

    @Override
    public String toString() {
        return PrikazyVycet.JDI.toString();
    }
}
