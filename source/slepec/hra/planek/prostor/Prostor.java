package slepec.hra.planek.prostor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import slepec.hra.planek.prostor.osoba.Osoba;
import slepec.hra.planek.prostor.vec.Vec;

/**
 * Hlavni stavebni kamen hry, protoze vsechno (veci a osoby)
 * musi byt nekde v prostoru
 *
 * @author Pavel Jurca, xjurp20@vse.cz
 * @version 2
 */
public class Prostor implements IProstor {

    private String nazev;
    private String popis;
    private boolean zamknuto;
    private boolean lzeZamknout; //true, pokud je mozne prostor zamknout
    private java.util.Set<Prostor> vychody;
    private java.util.Set<Vec> predmety;
    private java.util.Set<Osoba> osoby;

    public Prostor(String nazev, String popis) {
        this.nazev = nazev;
        this.popis = popis;
        lzeZamknout = true;
        zamknuto = false;
        vychody = new HashSet<>();
        predmety = new HashSet<>();
        osoby = new HashSet<>();
    }

    public Prostor(String nazev, String popis, boolean lzeZamknout) {
        this(nazev, popis);
        this.lzeZamknout = lzeZamknout;
    }

    @Override
    public void setVychody(Prostor... vychody) {
        if (null == vychody) {
            return;
        }
        //this.vychody.addAll(Arrays.asList(vychody));
        for (Prostor vychod : vychody) {
            if (vychod != null) {
                this.vychody.add(vychod);
            }
        }
    }

    @Override
    public boolean pridejVeci(Vec... veci) {
        if (null == veci) {
            return false;
        }
        //this.predmety.addAll(Arrays.asList(veci));
        for (Vec predmet : veci) {
            if (predmet != null) {
                this.predmety.add(predmet);
            }
        }

        return true;
    }

    @Override
    public boolean pridejOsoby(Osoba... osoby) {
        if (null == osoby) {
            return false;
        }
        //this.osoby.addAll(Arrays.asList(osoby));
        for (Osoba o : osoby) {
            if (o != null) {
                this.osoby.add(o);
            }
        }

        return true;
    }

    @Override
    public Vec odeberVec(String nazevVeci) {
        Vec odebrat;
        Iterator<Vec> iter = predmety.iterator();
        while (iter.hasNext()) {
            odebrat = iter.next();
            if (odebrat.toString().equalsIgnoreCase(nazevVeci)) {
                iter.remove();
                return odebrat;
            }
        }

        return null;
    }

    @Override
    public Osoba odeberOsoba(String jmenoOsoby) {
        Osoba odebrat;
        Iterator<Osoba> iter = osoby.iterator();
        while (iter.hasNext()) {
            odebrat = iter.next();
            if (odebrat.toString().equalsIgnoreCase(jmenoOsoby)) {
                iter.remove();
                return odebrat;
            }
        }

        return null;
    }

    @Override
    public Vec selectVec(String nazevVeci) {
        for (Vec predmet : predmety) {
            if (predmet.toString().equalsIgnoreCase(nazevVeci)) {
                return predmet;
            }
        }

        return null;
    }

    @Override
    public Osoba selectOsoba(String jmenoOsoby) {
        for (Osoba osoba : osoby) {
            if (osoba.toString().equalsIgnoreCase(jmenoOsoby)) {
                return osoba;
            }
        }

        return null;
    }

    @Override
    public Set<Vec> getVeci() {
        return Collections.unmodifiableSet(predmety);
    }

    @Override
    public Set<Osoba> getOsoby() {
        return Collections.unmodifiableSet(osoby);
    }

    @Override
    public boolean isPruchoziS(Prostor vychod) {
        return vychody.contains(vychod);
    }

    /**
     * Popis prostoru, seznam vychodu z prostoru, seznam veci a osob v prostoru
     *
     * @return
     */
    @Override
    public String getInfo() {
        StringBuilder info = new StringBuilder();
        //info.setLength(0);

        info.append(String.format("%8s%-5s%s", "lokace", " => ",
                nazev.toUpperCase())).append("\n");
        info.append(String.format("%8s%-5s%s", "popis", " => ",
                popis)).append("\n");
        info.append(String.format("%9s%-8s : %s", "", "osoby",
                getSeznamOsob())).append("\n");
        info.append(String.format("%9s%-8s : %s", "", "predmety",
                getSeznamVeci())).append("\n");
        info.append(String.format("%9s%-8s : %s", "", "vychody",
                getSeznamVychodu())).append("\n");

        return info.toString().replaceFirst("(?siu)\\n$", "");
    }

    private String getSeznamOsob() {
        String seznam = "";
        for (Osoba osoba : osoby) {
            seznam += osoba.toString();
            if (osoba.getBatoh()!=null) {
                seznam += "(" + osoba.getSeznamVeci();
                seznam += "), ";
            } else {
                seznam += ", ";
            }
        }

        return seznam.replaceFirst(",\\s$", "");
    }

    private String getSeznamVeci() {
        String seznam = "";
        for (Vec predmet : predmety) {
            if (predmet.getVnitrek().size() > 0) {
                seznam += predmet + "(" + predmet.getSeznamVeci() + "), ";
            } else {
                seznam += predmet + ", ";
            }

        }

        return seznam.replaceFirst(",\\s$", "");
    }

    private String getSeznamVychodu() {
        String seznam = "";
        for (Prostor vychod : vychody) {
            seznam += vychod + ", ";
        }

        return seznam.replaceFirst(",\\s$", "");
    }

    @Override
    public boolean zamknout() {
        if (lzeZamknout) {
            zamknuto = true;
            return true;
        }

        return false;
    }

    @Override
    public boolean odemknout() {
        if (lzeZamknout) {
            zamknuto = false;
            return true;
        }

        return false;
    }

    @Override
    public boolean isZamknuto() {
        return zamknuto;
    }

    @Override
    public boolean lzeZamknout() {
        return lzeZamknout;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Prostor) {
            Prostor vychod = (Prostor) o;
            return nazev.equalsIgnoreCase(vychod.toString());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return nazev.hashCode();
    }

    @Override
    public String toString() {
        return nazev;
    }
}
