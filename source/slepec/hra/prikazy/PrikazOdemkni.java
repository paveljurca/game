package slepec.hra.prikazy;

import slepec.hra.Hra;
import slepec.hra.planek.HerniPlan;
import slepec.hra.planek.prostor.Prostor;
import slepec.hra.planek.prostor.vec.Vec;
import slepec.hra.planek.prostor.vec.VecKlic;

/**
 * Pomoci PrikazOdemkni lze odemknout danou mistnost, paklize ma od ni hrac klic
 * 
 * @author Pavel Jurca, xjurp20@vse.cz
 * @version 2
 */
public class PrikazOdemkni extends Prikaz {

    public PrikazOdemkni(Hra hra, String popis) {
        super(hra, popis);
    }

    @Override
    public boolean proved(String[] parametry) {
        if (null == parametry
                || parametry.length < 1 || parametry[0].equals("")) {
            result = " err: dobre, odemkni, ale co?";
            return false;
        }

        HerniPlan planek = hra.getHerniPlan();
        switch (parametry.length) {
            case 1:
                String co = parametry[0];
                Prostor prostor = planek.selectProstor(co);
                if (prostor != null) {
                    //lze odemknout i mistnost, ve ktere prave jste
                    if (planek.kdeJsem().isPruchoziS(prostor)
                            || planek.kdeJsem().equals(prostor)) {
                        if (prostor.lzeZamknout()) {
                            if (prostor.isZamknuto()) {
                                //takoveto pojmenovani klice je uz implicitne _
                                //definovano v konstruktoru
                                String klicNazev = "klic" + co.toUpperCase();
                                Vec klic = planek.hlavniPostava().selectVec(klicNazev);
                                if (klic != null) {
                                    if (klic instanceof VecKlic) {
                                        prostor.odemknout();
                                        result = " USPECH: odemkli jste "
                                                + prostor;

                                        return true;
                                    } else {
                                        result = " neuspech: klic je padelek!";
                                    }
                                } else {
                                    result = " neuspech: od prostoru " + prostor
                                            + " musite mit klic";
                                }
                            } else {
                                result = " neuspech: prostor " + prostor
                                        + " je uz odemceny";
                            }
                        } else {
                            result = " neuspech: prostor " + prostor
                                    + " nema zadne dvere!";
                        }
                    } else {
                        result = " neuspech: " + prostor
                                + " neni jednim z vychodu";
                    }
                } else {
                    result = " err: prostor \"" + co + "\" neexistuje";
                }
                break;
            default:
                result = " err: spatny pocet parametru"
                        + String.format("\n%6s%s", "", "tip: 'napovez'");
                break;
        }

        return false;
    }

    @Override
    public String toString() {
        return PrikazyVycet.ODEMKNI.toString();
    }
}
