package slepec.hra.planek;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import slepec.hra.planek.prostor.Prostor;
import slepec.hra.planek.prostor.osoba.Osoba;
import slepec.util.Observer;
import slepec.hra.UdalostHry;

/**
 * Herni prostor hry
 *
 * @author Pavel Jurca, xjurp20@vse.cz
 * @version 2
 */
public class HerniPlan implements IHerniPlan {

    private String nazevHry;
    private Set<Prostor> prostory;
    private Prostor kdeJsem;
    private Osoba hlavniPostava;
    private final CopyOnWriteArrayList<slepec.util.Observer> listeners;

    /**
     *
     * @param nazevHry nazev hry, pro kterou je tento herni plan platny
     * @param hlavniPostava hlavni postava hry, ktera prochazi prostory
     * @param vychoziProstor pocatecni prostor, ve kterem hrac zacina
     */
    public HerniPlan(
            String nazevHry,
            Osoba hlavniPostava,
            Prostor vychoziProstor) {

        this.nazevHry = nazevHry;
        this.hlavniPostava = hlavniPostava;
        kdeJsem = vychoziProstor;
        listeners = new CopyOnWriteArrayList<>();
        prostory = new HashSet<>();
        prostory.add(vychoziProstor);
    }

    /*
     * A copy constructor
     *
     * @param herniPlan
     *
     public HerniPlan(HerniPlan herniPlan) {
     //DEPRECATED!! NEPOUZIVAT!!
     //Prostor, Osoba, Vec apod. budou zase JEN REFERENCE!!
     }
     */
    /**
     * Rozsireni herniho planu o prostory
     *
     * @param prostory
     */
    @Override
    public void pridejProstory(Prostor... prostory) {
        if (prostory != null) {
            for (Prostor p : prostory) {
                if (p != null) {
                    this.prostory.add(p);
                }
            }
        }
    }

    /**
     * Vrati referenci vami vybraneho prostoru
     *
     * @param nazevProstoru nazev hledaneho prostoru
     * @return pokud prostor neexistuje, vraci null
     */
    @Override
    public Prostor selectProstor(String nazevProstoru) {
        for (Prostor p : prostory) {
            if (p.toString().equalsIgnoreCase(nazevProstoru)) {
                return p;
            }
        }

        return null;
    }

    /**
     * Vybere ze vsech nezamcenych prostoru hry jeden nahodny <strong>v pripade,
     * ze budou vsechny prostory zamknute, vybere ten defaultni z
     * konstruktoru</strong>
     *
     * @return nahodne vybrany prostor z dostupnych prostoru herniho planu
     */
    @Override
    public Prostor selectNahodProstor() {
        Prostor nahodProstor = null;
        Random rand = new Random();
        //HashSet prostoru, ktere nejsou zamceny
        List<Prostor> nezamProst = new ArrayList<>(prostory);

        Iterator<Prostor> iter = nezamProst.iterator();
        while (iter.hasNext()) {
            if (iter.next().isZamknuto()) {
                iter.remove();
            }
        }

        //ciselne vyjadreni nahodneho prostoru
        int nahoda = rand.nextInt(nezamProst.size());
        try {
            nahodProstor = nezamProst.get(nahoda);
        } catch (IndexOutOfBoundsException ex) {
        }

        return nahodProstor != null ? nahodProstor : kdeJsem;
    }

    /**
     * Seznam hernich prostoru
     *
     * @return Unmodifiable mnozina hernich prostoru
     */
    @Override
    public Set<Prostor> getProstory() {
        return Collections.unmodifiableSet(prostory);
    }

    /**
     * Odkaz na aktualni umisteni (prostor) v hernim planu
     *
     * @return aktualni prostor, ve kterem se hrac nachazi
     */
    @Override
    public Prostor kdeJsem() {
        return kdeJsem;
    }

    /**
     * Odkaz na hlavni postavu hry
     *
     * @return hlavni postava hry
     */
    @Override
    public Osoba hlavniPostava() {
        return hlavniPostava;
    }

    /**
     * Zmena pozice v hernim planu
     *
     * @param nazevProstoru
     * @return true, pokud byla zmenena aktualni pozice
     */
    @Override
    public boolean move(String nazevProstoru) {
        prostory:
        for (Prostor prostor : prostory) {
            if (prostor.toString().equalsIgnoreCase(nazevProstoru)) {
                if (kdeJsem.isPruchoziS(prostor) && !prostor.isZamknuto()) {
                    kdeJsem = prostor;
                    notifyObservers(UdalostHry.ZMENIL_PROSTOR);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Zmena pozice v hernim planu pri potlaceni vsech implicitnich kontrol
     *
     * @param nazevProstoru nazev prostoru
     * @param ignoreChecks true, potlaci kontroly pruchodu a zamykatelnosti
     * @return
     */
    @Override
    public boolean move(String nazevProstoru, boolean ignoreChecks) {
        if (ignoreChecks) {
            prostory:
            for (Prostor prostor : prostory) {
                if (prostor.toString().equalsIgnoreCase(nazevProstoru)) {
                    kdeJsem = prostor;
                    notifyObservers(UdalostHry.ZMENIL_PROSTOR);
                    return true;
                }
            }
        }

        return move(nazevProstoru);
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

    /**
     * nazev herniho planu
     *
     * @return
     */
    @Override
    public String toString() {
        return nazevHry;
    }
}
