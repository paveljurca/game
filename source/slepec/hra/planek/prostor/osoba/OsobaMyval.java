package slepec.hra.planek.prostor.osoba;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import slepec.hra.planek.prostor.vec.Vec;
import slepec.hra.planek.prostor.vec.VecBatoh;

/**
 * Specificka implementace Osoby
 *
 * @author Pavel Jurca, xjurp20@vse.cz
 * @version 2
 */
public class OsobaMyval extends Osoba {

    private int mydla; //pocet mydel, ktera myval vlastni

    public OsobaMyval(String nazev, String popis, Map<String, String> monology) {
        super(nazev, popis, monology);
        mydla = 0;
    }

    @Override
    public boolean pridejVec(Vec vec) {
        if (super.pridejVec(vec)) {
            Pattern p = Pattern.compile("(?i)(mydlo|mejdlo)");
            Matcher mNazev = p.matcher(vec.toString());
            Matcher mPopis = p.matcher(vec.getPopis());
            if (mNazev.find() || mPopis.find()) {
                mydla++;
            }

            return true;
        }

        return false;
    }

    /**
     * Myval je ochotny <b>smenit cokoliv krome mydla a batohu za mydlo</b>, ale
     * protoze je to neduverive zviratko, <strong>musite ho nejdrive obdarovat
     * vy</strong>
     *
     * @param nazevVeci
     * @return Vec odebrana vec, v pripade neuspechu null
     */
    @Override
    public Vec odeberVec(String nazevVeci) {
        Vec odebrat = null;
        if (mydla > 0) {
            Pattern p = Pattern.compile("(?i)(mydlo|mejdlo)");
            Matcher mNazev = p.matcher(nazevVeci);

            if (!mNazev.find()) {
                odebrat = super.odeberVec(nazevVeci);
                if (odebrat != null) {
                    if (odebrat instanceof VecBatoh) {
                        this.pridejVec(odebrat);
                        odebrat = null;
                    } else {
                        mydla--;
                    }
                }
            }
        }

        return odebrat;
    }
}
