package slepec.hra.planek.prostor.vec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * S vyjimkou osoby je vse v prostoru nejaka vec! Kazda vec ma prehled o vsech
 * vecech v ni obsazenych
 *
 * @author Pavel Jurca, xjurp20@vse.cz
 * @version 2
 */
public class Vec implements IVec {

    private String nazev;
    private String popis;
    private int vaha;
    private boolean schranka;
    private int vyziva; //nutricni hodnota veci (potravy)
    private boolean jez;
    private boolean pij;
    private java.util.Set<Vec> veci;

    /**
     *
     * @param nazev oznaceni veci
     * @param popis vysvetlivka, k cemu tato vec slouzi
     * @param vaha v gramech
     * @param schranka true, pokud muze dana vec obsahovat dalsi veci
     */
    public Vec(String nazev, String popis, int vaha) {
        this.nazev = nazev;
        this.popis = popis;
        this.vaha = vaha;
        schranka = false; //defaultne vec nemuze obsahovat dalsi vec
        jez = false;
        pij = false;
        vyziva = 0;
        if (this.vaha < 0) {
            this.vaha = 0;
        }
        veci = new HashSet<>();
    }

    /**
     * Konstruktor veci jako potravy
     *
     * @param nazev
     * @param popis
     * @param vaha
     * @param jez true, pokud to lze jist
     * @param pij true, pokud to lze pit
     * @param vyziva nutricni hodnota potravy (muze byt i zaporna)
     */
    public Vec(String nazev, String popis, int vaha, boolean jez,
            boolean pij, int vyziva) {
        this(nazev, popis, vaha);
        this.vyziva = vyziva;
        this.jez = jez;
        this.pij = pij;
    }

    public Vec(String nazev, String popis, int vaha, boolean schranka) {
        this(nazev, popis, vaha);
        this.schranka = schranka;
    }

    @Override
    public boolean pridej(Vec... veci) {
        if (null == veci || schranka == false) {
            return false;
        }

        int vel = this.veci.size();
        for (Vec v : veci) {
            //nelze pridat vec se stejnym nazvem
            if (v != null && !v.equals(this) && !v.hasVec(nazev)) {
                vaha += v.getVaha(); //vaha pri pridavani musi narustat
                this.veci.add(v);
            }
        }

        if (vel == this.veci.size()) {
            return false;
        }

        return true;
    }

    @Override
    public Vec odeber(String nazevVeci) {
        Iterator<Vec> iter;
        ArrayList<Vec> uroven = new ArrayList<>();
        Set<Vec> checked = new HashSet<>(); //uz prohledane uzly
        uroven.add(this);

        urovne:
        while (!uroven.isEmpty()) {//prochazeni do hloubky
            //prave zkoumana vec
            iter = uroven.get(uroven.size() - 1).veci.iterator();
            while (iter.hasNext()) {
                Vec vec = iter.next();
                if (vec.toString().equalsIgnoreCase(nazevVeci)) {
                    iter.remove();
                    //vaha veci (schranky) pri odebirani musi klesat
                    for (int i = 1; i <= uroven.size(); i++) {
                        uroven.get(uroven.size() - i).vaha -= vec.getVaha();
                    }

                    return vec;
                }
                if (!checked.contains(vec) && vec.getVnitrek().size() > 0) {
                    checked.add(vec);
                    uroven.add(vec);
                    continue urovne;
                }
                checked.add(vec); //mnozina nepovoli duplicity, takze ok!
            }
            uroven.remove(uroven.size() - 1);
        }

        return null;
    }

    @Override
    public boolean hasVec(String nazevVeci) {
        if (selectVec(nazevVeci) != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Vec selectVec(String nazevVeci) {
        Iterator<Vec> iter;
        ArrayList<Set<Vec>> urovne = new ArrayList<>();
        urovne.add(new HashSet<>(this.getVnitrek()));

        urovne: //prochazeni stromu do hloubky
        while (!urovne.isEmpty()) {
            iter = urovne.get(urovne.size() - 1).iterator();
            while (iter.hasNext()) {
                Vec vec = iter.next();
                if (vec.toString().equalsIgnoreCase(nazevVeci)) {
                    return vec;
                }
                if (vec.getVnitrek().size() > 0) {
                    iter.remove();
                    urovne.add(new HashSet<>(vec.getVnitrek()));
                    continue urovne;
                }
                iter.remove();
            }
            urovne.remove(urovne.size() - 1);
        }

        return null;
    }

    /**
     *
     * @param nazevVeci
     * @return unmodifiable Set veci, ktere obsahuje tato vec
     */
    @Override
    public Set<Vec> getVnitrek() {
        return Collections.unmodifiableSet(veci);
    }

    /**
     * <strong>Zformatovany seznam veci</strong>
     *
     * @return zformatovany seznam veci
     */
    @Override
    public String getSeznamVeci() {
        String seznam = "";
        Iterator<Vec> iter;
        ArrayList<Set<Vec>> urovne = new ArrayList<>();
        urovne.add(new HashSet<>(this.getVnitrek()));

        urovne: //prochazeni stromu do hloubky
        while (!urovne.isEmpty()) {
            iter = urovne.get(urovne.size() - 1).iterator();
            while (iter.hasNext()) {
                Vec vec = iter.next();
                if (vec.getVnitrek().size() > 0) {
                    iter.remove();
                    urovne.add(new HashSet<>(vec.getVnitrek()));
                    seznam += ", " + vec + "(";
                    continue urovne;
                }
                iter.remove();
                seznam += ", " + vec;
            }
            if (urovne.size() > 1) {
                seznam += ")";
            }
            urovne.remove(urovne.size() - 1);
        }
        seznam = seznam.replaceFirst("^,\\s", "");

        return seznam.replaceAll("\\(,\\s", "(");
    }

    @Override
    public String getPopis() {
        return popis;
    }

    @Override
    public int getVaha() {
        return vaha;
    }

    @Override
    public boolean isSchranka() {
        return schranka;
    }

    @Override
    public boolean isJidlo() {
        return jez;
    }

    @Override
    public boolean isPiti() {
        return pij;
    }

    /**
     * Vyzivova (nutricni) hodnota veci
     *
     * @return
     */
    @Override
    public int getNutriCislo() {
        return vyziva;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Vec) {
            Vec vec = (Vec) o;
            return nazev.equalsIgnoreCase(vec.toString());
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
