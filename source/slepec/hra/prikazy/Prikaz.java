package slepec.hra.prikazy;

import slepec.hra.Hra;

/**
 * Abstraktni Prikaz, kteremu schazi uz jen implementace metody 'proved' a
 * 'toString,' ktere zaviseji vzdy na konretnim vyuziti prikazu
 *
 * @author Pavel Jurca, xjurp20@vse.cz
 * @version 2
 */
public abstract class Prikaz implements IPrikaz, Comparable<Prikaz> {

    private String popis;
    protected Hra hra;
    protected String result;

    public Prikaz(Hra hra, String popis) {
        this.hra = hra;
        this.popis = popis;
        result = "";
    }

    @Override
    public String getResult() {
        return result;
    }

    @Override
    public String getPopis() {
        return popis;
    }

    @Override
    public abstract String toString();

    /**
     * Rozdeli textovy <em>retezec na jednotlive tokeny</em> (casti textu) dle
     * mezer nebo uvozovek/apostrofu
     *
     * @param vstup radek textu k 'rozkouskovani'
     * @return pole Stringu z vytvorenych tokenu o minimalni delce 1
     */
    @Override
    public String[] tokenize(String vstup) {
        vstup = vstup.replaceAll("(?s)['\"]\\s+['\"]", "\"\"");
        vstup = vstup.replaceAll("['\"]([^'\"]+)['\"]", "\"#$1\"");

        StringBuilder tokeny = new StringBuilder();

        tokeny:
        for (String token : vstup.split("['\"]+")) {
            token = token.trim();
            if (token.charAt(0) == '#') {
                tokeny.append(token.replaceFirst("^#\\s*", ""));
                tokeny.append("\n");
                continue tokeny;
            }
            for (String t : token.split("\\s+")) {
                tokeny.append(t.concat("\n"));
            }
        }
        //1. parametr je vzdy nazev prikazu
        tokeny.delete(0, tokeny.indexOf("\n") + 1);

        return tokeny.toString().split("\n");
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Prikaz) {
            Prikaz p = (Prikaz) o;
            return this.toString().equalsIgnoreCase(p.toString());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public int compareTo(Prikaz druhyPrikaz) {
        return this.toString().compareToIgnoreCase(druhyPrikaz.toString());
    }
}
