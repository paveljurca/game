package slepec.hra.prikazy;

import slepec.hra.Hra;
import slepec.hra.planek.HerniPlan;
import slepec.hra.planek.prostor.vec.Vec;

/**
 * Pomoci PrikazJez lze zkonzumovat k tomu urcitou vec a v navaznosti na to i
 * prodlouzit casovy limit hry (pokud je tato vlastnost aktivni)
 * 
 * @author Pavel Jurca, xjurp20@vse.cz
 * @version 2
 */
public class PrikazJez extends Prikaz {

    public PrikazJez(Hra hra, String popis) {
        super(hra, popis);
    }

    @Override
    public boolean proved(String[] parametry) {
        if (null == parametry
                || parametry.length < 1 || parametry[0].equals("")) {
            result = " err: dobre, jez, ale co?";
            return false;
        }

        HerniPlan planek = hra.getHerniPlan();
        switch (parametry.length) {
            case 1:
                String co = parametry[0];
                Vec jez = planek.hlavniPostava().selectVec(co);
                if (jez != null) {
                    if (jez.isJidlo()) {
                        planek.hlavniPostava().jez(
                                planek.hlavniPostava().odeberVec(jez.toString()));

                        result = " USPECH: snedli jste " + jez;

                        if (hra.isCasovac()) {
                            hra.prodluzCasovac(jez.getNutriCislo());

                            result += String.format(
                                    "\n%9s%s", "", "cas "
                                    + (jez.getNutriCislo() < 0 ? "zkracen" : "prodlouzen"));
                            result += String.format(
                                    "\n%9s%s%d min : %02d sec",
                                    "", "zbyva ",
                                    (hra.getZbyvaCas() / 1000) / 60,
                                    (hra.getZbyvaCas() / 1000) % 60);
                        }

                        return true;
                    } else if (jez.isPiti()) {
                        result = " neuspech: tohle je prece k piti";
                    } else {
                        result = " neuspech: fuj je to!";
                    }
                } else {
                    jez = planek.kdeJsem().selectVec(co);
                    if (null == jez) {
                        for (Vec v : planek.kdeJsem().getVeci()) {
                            if (v.hasVec(co)) {
                                jez = v.selectVec(co);
                            }
                        }
                    }
                    if (jez != null) {
                        if (jez.isJidlo()) {
                            result = " neuspech: nejdrive musite "
                                    + jez + " sebrat";
                        } else {
                            result = " neuspech: vec " + jez + " neni k jidlu";
                        }
                    } else {
                        result = " err: nemate zadne jidlo \"" + co + "\"";
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
        return PrikazyVycet.JEZ.toString();
    }
}
