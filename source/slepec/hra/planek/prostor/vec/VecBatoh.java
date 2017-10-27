package slepec.hra.planek.prostor.vec;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Trida VecBatoh pro prenaseni veci jako rozsirena funkcionalita tridy Vec
 *
 * @author Pavel Jurca, xjurp20@vse.cz
 * @version 2
 */
public final class VecBatoh extends Vec {

    private final int MAX_VECI;
    private final int MAX_VAHA;
    private ArrayList<String> seznamVeci;

    /**
     *
     * @param nazev
     * @param popis
     * @param maxZatez nosnost batohu v gramech
     */
    public VecBatoh(String nazev, String popis, int maxZatez, int maxVeci) {
        super(nazev, popis, 0, true);
        MAX_VAHA = maxZatez < 0 ? 0 : maxZatez;
        MAX_VECI = maxVeci < 0 ? 0 : maxVeci;
        seznamVeci = new ArrayList<>();
    }

    @Override
    public boolean pridej(Vec... veci) {
        if (null == veci) {
            return false;
        }

        int pocetNovVeci = 0;
        int vahaNovVeci = 0;
        for (Vec v : veci) {
            if (v instanceof VecBatoh) {
                //batoh v batohu je ZAKAZAN!
                return false;
            }
            if (v != null) {
                pocetNovVeci++;
                vahaNovVeci += v.getVaha();
            }
        }

        if ((getPocVeci() + pocetNovVeci) > MAX_VECI) {
            return false;
        }
        if ((this.getVaha() + vahaNovVeci) > MAX_VAHA) {
            return false;
        }

        return super.pridej(veci);
    }

    public int getMaxZatez() {
        return MAX_VAHA;
    }

    public int getMaxPocVeci() {
        return MAX_VECI;
    }

    public int getPocVeci() {
        int pocet = 0;
        seznamVeci.clear();
        Iterator<Vec> iter;
        ArrayList<java.util.Set<Vec>> urovne = new ArrayList<>();
        urovne.add(new HashSet<>(this.getVnitrek()));

        urovne: //prochazeni stromu do hloubky
        while (!urovne.isEmpty()) {
            iter = urovne.get(urovne.size() - 1).iterator();
            while (iter.hasNext()) {
                Vec vec = iter.next();
                pocet++;
                seznamVeci.add(vec.toString());
                if (vec.getVnitrek().size() > 0) {
                    iter.remove();
                    urovne.add(new HashSet<>(vec.getVnitrek()));
                    continue urovne;
                }
                iter.remove();
            }
            urovne.remove(urovne.size() - 1);
        }

        return pocet;
    }

    /**
     * Pole nazvu vsech veci obsazenych v tomto batohu<br /> <i>kazda vec jako
     * element pole -> neni videt zanoreni!</i>
     *
     * @return pole vsech veci ve Stringu
     */
    public String[] getSeznVeci() {
        return seznamVeci.toArray(new String[getPocVeci()]);
    }
}
