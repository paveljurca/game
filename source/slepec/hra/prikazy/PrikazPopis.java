package slepec.hra.prikazy;

import slepec.hra.Hra;
import slepec.hra.planek.HerniPlan;
import slepec.hra.planek.prostor.osoba.Osoba;
import slepec.hra.planek.prostor.vec.Vec;
import slepec.hra.planek.prostor.vec.VecBatoh;

/**
 * Prikaz pro ziskani informaci o dane veci ci osobe
 * 
 * @author Pavel Jurca, xjurp20@vse.cz
 * @version 2
 */
public class PrikazPopis extends Prikaz {

    public PrikazPopis(Hra hra, String popis) {
        super(hra, popis);
    }

    @Override
    public boolean proved(String[] parametry) {
        if (null == parametry
                || parametry.length < 1 || parametry[0].equals("")) {
            result = " err: dobre, popsat, ale co?";
            return false;
        }

        HerniPlan planek = hra.getHerniPlan();
        switch (parametry.length) {
            case 1:
                /*
                 * popisovat lze jen veci a osoby, ktere jsou v aktual. prostoru
                 */
                String co = parametry[0]; //popis ceho/koho (osoby/veci)
                predmety:
                for (Vec predmet : planek.kdeJsem().getVeci()) {
                    if (predmet.toString().equalsIgnoreCase(co)) {
                        result = getPopisVeci(planek.kdeJsem().selectVec(co));

                        return true;
                    }
                    if (predmet.hasVec(co)) {
                        result = getPopisVeci(predmet.selectVec(co));

                        return true;
                    }
                }

                String jmeno = parametry[0];
                osoby:
                for (Osoba osoba : planek.kdeJsem().getOsoby()) {
                    if (osoba.toString().equalsIgnoreCase(jmeno)) {
                        result = getPopisOsoby(
                                planek.kdeJsem().selectOsoba(jmeno));

                        return true;
                    }
                    //pro veci, ktere jsou v majetku osob v aktualnim prostoru
                    if (osoba.hasVec(co)) {
                        result = getPopisVeci(osoba.selectVec(co));

                        return true;
                    }
                }
                //pro hlavni postavu hry
                if (planek.hlavniPostava().hasVec(co)) {
                    result = getPopisVeci(planek.hlavniPostava().selectVec(co));

                    return true;
                }
                //prikaz 'popis me' vrati popis hlavni postavy
                if (jmeno.equalsIgnoreCase("me")) {
                    result = getPopisOsoby(planek.hlavniPostava());

                    return true;
                }

                result = " err: zadna vec/osoba \"" + co
                        + "\" v prostoru neni";

                break;
            default:
                result = " err: spatny pocet parametru"
                        + String.format("\n%6s%s", "", "tip: 'napovez'");
                break;
        }

        return false;
    }

    private String getPopisVeci(Vec v) {
        StringBuilder info = new StringBuilder();

        info.append(String.format("%8s%-5s%s", "vec", " -> ",
                v.toString())).append("\n");
        info.append(String.format("%8s%-5s%s", "popis", " -> ",
                v.getPopis())).append("\n");
        info.append(String.format("%9s%-8s : %s gramu", "", "vaha",
                v.getVaha())).append("\n");
        if (!(v.isJidlo() || v.isPiti())) {
            info.append(String.format("%9s%-8s : %s", "", "obsahuje",
                v.isSchranka() ? v.getSeznamVeci() : "N/A")).append("\n");
        }
        //VecBatoh je specialni Vec se specialni peci
        if (v instanceof VecBatoh) {
            VecBatoh batoh = (VecBatoh) v;
            info.append(String.format("%9s%-8s : %s gramu", "", "max.vaha",
                    batoh.getMaxZatez())).append("\n");
            info.append(String.format("%9s%-8s : %s", "", "max.veci",
                    batoh.getMaxPocVeci())).append("\n");
        } else {
            String temp;
            temp = v.isPiti() ? "ano/" : "ne/";
            temp += v.isJidlo() ? "ano" : "ne";
            temp = (v.isJidlo() || v.isPiti()) ? temp : "nepozivatelne";

            info.append(String.format("%9s%-8s : %s", "", "pij/jez",
                    temp)).append("\n");

            if (v.isJidlo() || v.isPiti()) {
                info.append(String.format("%9s%-8s : %s", "", "vyziva",
                        v.isJidlo() || v.isPiti() ? v.getNutriCislo()
                        + " kcal" : "N/A")).append("\n");
            }
        }

        return info.toString().replaceFirst("(?siu)\\n$", "");
    }

    private String getPopisOsoby(Osoba o) {
        StringBuilder info = new StringBuilder();

        info.append(String.format("%8s%-5s%s", "osoba", " -> ",
                o.toString())).append("\n");
        info.append(String.format("%8s%-5s%s", "popis", " -> ",
                o.getPopis())).append("\n");
        info.append(String.format("%9s%-8s : %s", "", "majetek",
                o.getSeznamVeci())).append("\n");
        //HlavniPostava hry je specialni Osoba se specialni peci ;)
        if (o.equals(hra.getHerniPlan().hlavniPostava())) {
            if (hra.isCasovac()) {
                info.append(String.format("%9s//zemrete za %d min a %02d sek, panebozee!", "",
                        (hra.getZbyvaCas() / 1000) / 60,
                        (hra.getZbyvaCas() / 1000) % 60)).append("\n");
            }
        } else {
            info.append(String.format("%9s%-8s : %s", "", "monology",
                    o.getMonology())).append("\n");
        }

        return info.toString().replaceFirst("(?siu)\\n$", "");
    }

    @Override
    public String toString() {
        return PrikazyVycet.POPIS.toString();
    }
}
