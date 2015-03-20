package slepec.hra.planek.prostor.osoba;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import slepec.hra.planek.prostor.vec.Vec;
import slepec.hra.planek.prostor.vec.VecBatoh;
import slepec.util.Observer;
import slepec.hra.UdalostHry;

/**
 * Prvek hry ktery se muze presouvat, mluvit, zkratka interagovat!
 *
 * @author Pavel Jurca, xjurp20@vse.cz
 * @version 2
 */
public class Osoba implements IOsoba {

    private String nazev;
    private String popis;
    private Map<String, String> monology;
    private VecBatoh batoh;
    private List<Vec> snedeno;
    private List<Vec> vypito;
    private final CopyOnWriteArrayList<slepec.util.Observer> listeners;

    public Osoba(String nazev, String popis) {
        this.nazev = nazev;
        this.popis = popis;
        this.monology = new HashMap<>();
        snedeno = new ArrayList<>();
        vypito = new ArrayList<>();
        listeners = new CopyOnWriteArrayList<>();
    }

    public Osoba(String nazev, String popis, Map<String, String> monology) {
        this(nazev, popis);
        this.monology = monology;
    }

    @Override
    public boolean pridejVec(Vec vec) {
        //osoba muze mit vzdy jen jeden batoh
        if (null == batoh) {
            if (vec instanceof VecBatoh) {
                batoh = (VecBatoh) vec;
                notifyObservers(UdalostHry.SEBRAL_VEC);

                return true;
            }
        } else if (batoh.pridej(vec)) {
            notifyObservers(UdalostHry.SEBRAL_VEC);

            return true;
        }

        return false;
    }

    @Override
    public Vec odeberVec(String nazevVeci) {
        if (nazevVeci.equalsIgnoreCase(batoh.toString())) {
            VecBatoh b = batoh;
            batoh = null;
            notifyObservers(UdalostHry.POLOZIL_VEC);

            return b;
        } else {
            Vec odeber = batoh.odeber(nazevVeci);
            if (odeber != null) {
                notifyObservers(UdalostHry.POLOZIL_VEC);

                return odeber;
            }
        }

        return null;
    }

    @Override
    public boolean hasVec(String nazevVeci) {
        if (batoh != null) {
            if (batoh.toString().equalsIgnoreCase(nazevVeci)) {
                return true;
            } else {
                return batoh.hasVec(nazevVeci);
            }
        }

        return false;
    }

    @Override
    public Vec selectVec(String nazevVeci) {
        if (batoh != null) {
            if (nazevVeci.equalsIgnoreCase(batoh.toString())) {
                return batoh;
            } else {
                return batoh.selectVec(nazevVeci);
            }
        }

        return null;
    }

    @Override
    public VecBatoh getBatoh() {
        return batoh;
    }

    @Override
    public void jez(Vec jidlo) {
        if (jidlo != null) {
            snedeno.add(jidlo);
        }
    }

    @Override
    public void pij(Vec piti) {
        if (piti != null) {
            vypito.add(piti);
        }
    }

    @Override
    public List<Vec> getSnedeno() {
        return Collections.unmodifiableList(snedeno);
    }

    @Override
    public List<Vec> getVypito() {
        return Collections.unmodifiableList(vypito);
    }

    /**
     * Vrati nic nerikajici monolog
     *
     * @return monolg
     */
    @Override
    public String promluv() {
        return "Nechci zadny problemy.";
    }

    /**
     * Kazda osoba umi (<i>mela by</i>) vice monologu
     *
     * @return monolg
     */
    @Override
    public String promluv(String oCem) {
        for (String k : monology.keySet()) {
            if (k.equalsIgnoreCase(oCem)) {
                return monology.get(k);
            }
        }

        return promluv();
    }

    @Override
    public String getPopis() {
        return popis;
    }

    @Override
    public String getMonology() {
        String seznam = "";
        for (String monolog : monology.keySet()) {
            seznam += monolog + ", ";
        }

        return seznam.replaceFirst(",\\s$", "");
    }

    /**
     * <strong>Zformatovany seznam veci</strong>
     *
     * @return zformatovany seznam veci
     */
    @Override
    public String getSeznamVeci() {
        String seznam = "";

        if (null == batoh) {
            return "";
        }
        if (batoh.getVnitrek().size() > 0) {
            seznam += batoh + "(" + batoh.getSeznamVeci() + ")";
        } else {
            seznam += batoh;
        }

        return seznam;
    }

    @Override
    public void prejmenuj(String noveJmeno) {
        nazev = noveJmeno;
    }

    @Override
    public final void attachObserver(Observer observer) {
        if (observer != null) {
            listeners.add(observer);
        }
    }

    @Override
    public final void detachObserver(Observer observer) {
        if (observer != null) {
            listeners.remove(observer);
        }
    }

    @Override
    public final void notifyObservers(UdalostHry udalost) {
        for (Observer o : listeners) {
            o.update(udalost);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Osoba) {
            Osoba osoba = (Osoba) o;
            return nazev.equalsIgnoreCase(osoba.toString());
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
