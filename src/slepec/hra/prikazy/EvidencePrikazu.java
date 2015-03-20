package slepec.hra.prikazy;

import java.util.Collection;
import java.util.TreeSet;

/**
 * Evidence pro udrzeni prehledu o dostupnych prikazech
 *
 * @author Pavel Jurca, xjurp20@vse.cz
 * @version 2
 */
public class EvidencePrikazu {

    private String popis;
    private Collection<Prikaz> prikazy;

    public EvidencePrikazu(String popis) {
        this.popis = popis;
        prikazy = new TreeSet<>();
    }

    public void pridejPrikaz(Prikaz prikaz) {
        prikazy.add(prikaz);
    }

    public Prikaz getPrikaz(String prikazNazev) {
        for (Prikaz p : prikazy) {
            if (p.toString().equalsIgnoreCase(prikazNazev)) {
                return p;
            }
        }
        return null;
    }

    public Prikaz[] getPrikazy() {
        return prikazy.toArray(new Prikaz[prikazy.size()]);
    }

    public String getVypisPrikazu() {
        String vystup = "";
        for (Prikaz p : prikazy) {
            vystup += String.format(
                    "%11s  /%s%n", p.toString(), p.getPopis());
        }

        return vystup.replaceFirst("(?siu)\\n$", "");
    }

    /**
     * Zanalyzuje dany vstup na prikaz ze seznamu platnych prikazu
     *
     * @param vstup radek textu, ktery chceme zparsovat
     * @return nazev platneho prikazu (true) nebo prazdny retezec (false)
     */
    public String hledejPlatnyPrikaz(String vstup) {
        vstup = vstup.trim();
        String prikaz = "";

        prikazy:
        for (Prikaz p : prikazy) {
            if (vstup.concat(" ").matches("(?siu)^" + p.toString() + "\\s+.*")) {
                prikaz = p.toString();
                break prikazy;
            }
        }
        return prikaz;
    }

    public void clear() {
        prikazy.clear();
    }

    @Override
    public String toString() {
        return popis;
    }
    /*
     private static SortedMap<Prikaz, String> prikazy =
     new TreeMap<> (new Comparator<Prikaz>() {
     @Override public int compare(Prikaz prik1, Prikaz prik2) {
     return prik1.toString().compareTo(prik2.toString());
     }
     });
     */
}
