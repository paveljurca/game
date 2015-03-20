package slepec.hra.prikazy;

import slepec.hra.Hra;
import slepec.hra.planek.HerniPlan;
import slepec.hra.planek.prostor.osoba.*;
import slepec.hra.planek.prostor.vec.*;

/**
 * Zakladni prikaz hry, ktery umoznuje sbirani a diky tomu i prenaseni veci
 *
 * @author Pavel Jurca, xjurp20@vse.cz
 * @version 2
 */
public class PrikazSeber extends Prikaz {

    public PrikazSeber(Hra hra, String popis) {
        super(hra, popis);
    }

    @Override
    public boolean proved(String[] parametry) {
        if (null == parametry
                || parametry.length < 1 || parametry[0].equals("")) {
            result = " err: dobre, sebrat, ale co?";
            return false;
        }

        HerniPlan planek = hra.getHerniPlan();

        String co;
        String odkud;
        Vec seber;
        Vec seberZ;
        switch (parametry.length) {
            case 1:
                co = parametry[0];
                seber = planek.kdeJsem().selectVec(co);
                if (seber != null) {
                    if (planek.hlavniPostava().getBatoh() != null
                            || (seber instanceof VecBatoh)) {

                        if (planek.hlavniPostava().pridejVec(seber)) {
                            planek.kdeJsem().odeberVec(co);
                            result = " USPECH: sebrali jste " + seber;

                            return true;
                        } else {
                            if (seber instanceof VecBatoh) {
                                result = " neuspech: nelze mit vice batohu!";
                            } else {
                                result = " neuspech: nemuzete sebrat "
                                        + seber + "\n"
                                        + String.format(
                                        "%11s%s\n%11s%s", "",
                                        "vaha a/nebo pocet veci", "",
                                        "tip: 'batoh' a 'popis "
                                        + seber + "'");
                            }
                        }
                    } else {
                        result = " neuspech: nejdrive potrebujete nejaky batoh!";
                    }
                } else {
                    for (Vec v : planek.kdeJsem().getVeci()) {
                        if (v.hasVec(co)) {
                            result = " neuspech: " + v.selectVec(co)
                                    + " neni primo v prostoru";

                            return false;
                        }
                    }
                    result = " err: vec \"" + co + "\" tady rozhodne neni";
                }

                break;
            case 2:
                co = parametry[0];
                odkud = parametry[1];
                seberZ = planek.kdeJsem().selectVec(odkud);
                if (seberZ != null) {
                    seber = seberZ.selectVec(co);
                    if (seber != null) {
                        if (planek.hlavniPostava().getBatoh() != null
                                || (seber instanceof VecBatoh)) {
                            if (planek.hlavniPostava().pridejVec(seber)) {
                                seberZ.odeber(co);
                                result = " USPECH: sebrali jste " + seber
                                        + " z " + seberZ;

                                return true;
                            } else {
                                if (seber instanceof VecBatoh) {
                                    result = " neuspech: nelze mit vice batohu!";
                                } else {
                                    result = " neuspech: nemuzete sebrat "
                                            + seber + "\n"
                                            + String.format(
                                            "%11s%s\n%11s%s", "",
                                            "vaha a/nebo pocet veci", "",
                                            "tip: 'batoh' a 'popis "
                                            + seber + "'");
                                }
                            }
                        } else {
                            result = " neuspech: nejdrive "
                                    + "potrebujete nejaky batoh!";
                        }
                    } else {
                        result = " neuspech: " + seberZ
                                + " neobsahuje predmet \"" + co + "\"";
                    }
                } else {
                    for (Vec v : planek.kdeJsem().getVeci()) {
                        if (v.hasVec(odkud)) {
                            result = " neuspech: " + v.selectVec(odkud)
                                    + " neni primo v prostoru";

                            return false;
                        }
                    }
                    //jeste proverime osoby
                    Osoba seberOd = planek.kdeJsem().selectOsoba(odkud);
                    if (seberOd != null) {
                        seber = seberOd.selectVec(co);
                        if (seber != null) {
                            if (planek.hlavniPostava().getBatoh() != null
                                    || seber instanceof VecBatoh) {

                                if (seberOd.odeberVec(seber.toString()) != null) {
                                    if (planek.hlavniPostava().pridejVec(seber)) {
                                        result = " USPECH: sebrali jste "
                                                + seber + " od " + seberOd;

                                        return true;
                                    } else {
                                        //osobe danou vec zase vratime
                                        seberOd.pridejVec(seber);

                                        if (seber instanceof VecBatoh) {
                                            result = " neuspech: nelze mit vice batohu!";
                                        } else {
                                            result = " neuspech: nemuzete sebrat "
                                                    + seber + "\n"
                                                    + String.format(
                                                    "%11s%s\n%11s%s", "",
                                                    "vaha a/nebo pocet veci", "",
                                                    "tip: 'batoh' a 'popis "
                                                    + seber + "'");
                                        }
                                    }
                                } else {
                                    result = " neuspech: " + seberOd
                                            + " odmita " + "vydat "
                                            + seber + "\n" + String.format(
                                            "%11s%s\n%11s%s", "",
                                            "zkuste se zeptat", "",
                                            "tip: 'popis " + seberOd + "' a "
                                            + "'mluv "
                                            + seberOd + " [monolog]'");
                                }
                            } else {
                                result = " neuspech: nejdrive "
                                        + "potrebujete nejaky batoh!";
                            }
                        } else {
                            result = " neuspech: osoba "
                                    + seberOd + " nevlastni zadny predmet \""
                                    + co + "\"";
                        }
                    } else {
                        result = " err: vec/osoba \"" + odkud
                                + "\" tady rozhodne neni";
                    }
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
        return PrikazyVycet.SEBER.toString();
    }
}
