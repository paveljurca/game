package slepec.hra.prikazy;

import slepec.hra.Hra;
import slepec.hra.planek.HerniPlan;
import slepec.hra.planek.prostor.vec.Vec;

/**
 * Pomoci PrikazPij lze vypit k tomu urcitou vec a v navaznosti na to i
 * prodlouzit casovy limit hry (pokud je tato vlastnost aktivni)
 *
 * @author Pavel Jurca, xjurp20@vse.cz
 * @version 2
 */
public class PrikazPij extends Prikaz {

    public PrikazPij(Hra hra, String popis) {
        super(hra, popis);
    }

    @Override
    public boolean proved(String[] parametry) {
        if (null == parametry
                || parametry.length < 1 || parametry[0].equals("")) {
            result = " err: dobre, pij, ale co?";
            return false;
        }

        HerniPlan planek = hra.getHerniPlan();
        switch (parametry.length) {
            case 1:
                String co = parametry[0];
                Vec pij = planek.hlavniPostava().selectVec(co);
                if (pij != null) {
                    if (pij.isPiti()) {
                        planek.hlavniPostava().pij(
                                planek.hlavniPostava().odeberVec(pij.toString()));

                        result = " USPECH: vypili jste " + pij;

                        if (hra.isCasovac()) {
                            hra.prodluzCasovac(pij.getNutriCislo());

                            result += String.format(
                                    "\n%9s%s", "", "cas "
                                    + (pij.getNutriCislo() < 0 ? "zkracen" : "prodlouzen"));
                            result += String.format("\n%9s%s%d min : %02d sec",
                                    "", "zbyva ",
                                    (hra.getZbyvaCas() / 1000) / 60,
                                    (hra.getZbyvaCas() / 1000) % 60);
                        }

                        return true;
                    } else if (pij.isJidlo()) {
                        result = " neuspech: tohle je prece k jidlu";
                    } else {
                        result = " neuspech: fuj je to!";
                    }
                } else {
                    pij = planek.kdeJsem().selectVec(co);
                    if (null == pij) {
                        for (Vec v : planek.kdeJsem().getVeci()) {
                            if (v.hasVec(co)) {
                                pij = v.selectVec(co);
                            }
                        }
                    }
                    if (pij != null) {
                        if (pij.isPiti()) {
                            result = " neuspech: nejdrive musite "
                                    + pij + " sebrat";
                        } else {
                            result = " neuspech: vec " + pij + " neni k piti";
                        }
                    } else {
                        result = " err: nemate zadne piti \"" + co + "\"";
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
        return PrikazyVycet.PIJ.toString();
    }
}
