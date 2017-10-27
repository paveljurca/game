package slepec.hra.prikazy;

import slepec.hra.Hra;
import slepec.hra.planek.HerniPlan;
import slepec.hra.planek.prostor.osoba.*;
import slepec.hra.planek.prostor.vec.*;

/**
 * Zakladni prikaz hry, ktery umozni pokladani veci
 * 
 * @author Pavel Jurca, xjurp20@vse.cz
 * @version 2
 */
public class PrikazPoloz extends Prikaz {

    public PrikazPoloz(Hra hra, String popis) {
        super(hra, popis);
    }

    @Override
    public boolean proved(String[] parametry) {
        if (null == parametry
                || parametry.length < 1 || parametry[0].equals("")) {
            result = " err: dobre, polozit, ale co?";
            return false;
        }

        HerniPlan planek = hra.getHerniPlan();
        
        String co;
        String kam;
        Vec poloz;
        Vec polozNa;
        switch (parametry.length) {
            case 1:
                co = parametry[0];
                poloz = planek.hlavniPostava().odeberVec(co);
                if (poloz != null) {
                    planek.kdeJsem().pridejVeci(poloz);
                    result = " USPECH: polozili jste " + poloz;

                    return true;
                } else {
                    result = " err: nemate zadnou vec \"" + co + "\""
                            + "\n" + String.format(
                            "%6s%s", "", "tip: 'popis me'");
                }

                break;
            case 2:
                co = parametry[0];
                kam = parametry[1];
                poloz = planek.hlavniPostava().odeberVec(co);
                if (poloz != null) {
                    polozNa = planek.kdeJsem().selectVec(kam);
                    if (polozNa != null) {
                        //do urcitych veci nelze vkladat dalsi predmety
                        if (polozNa.pridej(poloz)) {
                            result = " USPECH: vlozili jste " + poloz + " do " + polozNa;

                            return true;
                        } else {
                            //polozeni se nezdarilo, musime vec vratit zpet hlavni postave
                            planek.hlavniPostava().pridejVec(poloz);
                            //batoh ma specificka omezeni
                            if (polozNa instanceof VecBatoh) {
                                result = " neuspech: do " + polozNa
                                        + " uz nelze nic pridat"
                                        + "\n" + String.format(
                                        "%11s%s\n%11s%s", "",
                                        "vaha a/nebo pocet veci", "",
                                        "tip: 'popis " + polozNa + "' a 'popis "
                                        + poloz + "'");
                            } else {
                                //PRIPADNE maji predmety stejny nazev _
                                //viz trida VecTest
                                result = " neuspech: " + polozNa
                                        + " nemuze obsahovat dalsi veci";
                            }
                        }
                    } else {
                        for (Vec v : planek.kdeJsem().getVeci()) {
                            if (v.hasVec(kam)) {
                                //polozeni se nezdarilo, musime vec vratit zpet hlavni postave
                                planek.hlavniPostava().pridejVec(poloz);
                                result = " neuspech: " + v.selectVec(kam)
                                        + " neni primo v prostoru";

                                return false;
                            }
                        }
                        //jeste proverime osoby
                        Osoba dejKomu = planek.kdeJsem().selectOsoba(kam);
                        if (dejKomu != null) {
                            if (!(dejKomu.getBatoh()!=null && poloz instanceof VecBatoh)) {
                                if (dejKomu.pridejVec(poloz)) {
                                    result = " USPECH: predali jste " + poloz
                                            + " osobe " + dejKomu;

                                    return true;
                                } else {
                                    result = " neuspech: " + dejKomu + " odmita "
                                            + "prevzit vec " + poloz + "\n"
                                            + String.format(
                                            "%11s%s\n%11s%s", "",
                                            "zkuste se zeptat", "",
                                            "tip: 'popis " + dejKomu + "' a "
                                            + "'mluv " + dejKomu + " [monolog]'");
                                }
                            } else {
                                result = " neuspech: " + dejKomu
                                        + " pry nepotrebuje dalsi batoh";
                            }
                        } else {
                            result = " err: vec/osoba \"" + kam
                                    + "\" v prostoru rozhodne neni";
                        }
                        //polozeni se nezdarilo, musime vec vratit zpet hlavni postave
                        planek.hlavniPostava().pridejVec(poloz);
                    }
                } else {
                    result = " err: nemate zadnou vec \"" + co + "\""
                            + "\n" + String.format(
                            "%6s%s", "", "tip: 'popis me'");
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
        return PrikazyVycet.POLOZ.toString();
    }
}
