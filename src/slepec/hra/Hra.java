package slepec.hra;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;
import slepec.Play;
import slepec.hra.planek.HerniPlan;
import slepec.hra.planek.prostor.vec.Vec;
import slepec.hra.prikazy.*;
import slepec.hra.rozhrani.*;
import slepec.hra.rozhrani.gui.GUI;
import slepec.util.Observer;

/**
 * <h1>HRA SLEPEC</h1>
 *
 * @author Pavel Jurca, xjurp20@vse.cz
 * @version 2
 */
public final class Hra implements IHra {

    //nazev hry
    public static final String NAZEV = "Slepec";
    //cesta k souboru s hernimi saves
    public static final String PATH_SAVES_FILE =
            System.getProperty("java.io.tmpdir") + File.separator
            + NAZEV.toLowerCase() + ".save";
    //cesta k souboru s hernimi logy
    public static final String PATH_LOGS_FILE =
            System.getProperty("java.io.tmpdir") + File.separator
            + NAZEV.toLowerCase() + ".log";
    private static final Logger logger =
            Play.globalLog.getLogger(Hra.class.getName());
    private static final int TIME_LIMIT = 600; //casovy limit hry v sekundach
    private static boolean isNovaHra; //true, pokud byla rozehrana nova hra
    // Use CopyOnWriteArrayList to avoid ConcurrentModificationExceptions if a
    // listener attempts to remove itself during event notification.
    private final CopyOnWriteArrayList<slepec.util.Observer> listeners;
    private String hrac;
    private boolean replay; //true, pokud se ma hra spustit znovu
    private HerniPlan planek;
    private EvidencePrikazu evidPrikazu;
    private static IRozhrani rozhrani;
    private Timer casovac;
    private long zacHry; //timestamp zacatku casovani hry v milisekundach
    private long ubehloTemp; //pomocna promena casovace    
    private KonecHry konecHry; //dokud je null, hra neskoncila
    //kazdy nove ulozeny prikaz znamena postup ve hre
    private List<String> postup;

    /**
     * Privatni trida pro stopovani casu
     */
    private class TimeLimit implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            //cas vyprsel, hra zacne indikovat GAMEOVER stav
            konecHry = KonecHry.GAMEOVER;
            casovac.stop();
            notifyObservers(UdalostHry.KONECHRY);            
        }
    }

    /**
     * HRA
     *
     * @param grafika true pro graficke rozhrani
     */
    public Hra(boolean grafika) {
        hrac = "";
        planek = new Scenar();
        postup = new ArrayList<>();
        listeners = new CopyOnWriteArrayList<>();
        evidPrikazu = new EvidencePrikazu("herni prikazy");

        zavestHerniPrikazy();
        rozhrani = grafika ? new GUI(this, "$> ") : new CLI("$> ");
        rozhrani.attachObserver(Hra.this);
    }    

    /**
     * Samotne spusteni hry <i>pracuje s rozhranim hry, vypisuje uvod a zaver
     * hry, zpracovava vstup, kontroluje podminky hry apod.</i> <strong>Neni
     * mozne mit spusteno vice novych her soucasne!</strong>
     *
     * @param hrac jmeno hrace nove hry
     */
    @Override
    public synchronized void novaHra(String hrac) {
        if (!isNovaHra) {
            /* ZAVEDENI hry */
            isNovaHra = true; //nyni NENI mozne spustit novou hru znovu
            zavestNovouHru(hrac);

            smycka_hry:
            do {
                rozhrani.setVystup(zpracujVstup(rozhrani.getVstup()));

                if (isVyhra()) {
                    konecHry = KonecHry.VYHRA;
                } else if (isGameOver()) {
                    konecHry = KonecHry.GAMEOVER;
                }
            } while (null == konecHry);

            /* UKONCENI hry */
            casovac.stop();
            rozhrani.setVystup(getOutro());
            isNovaHra = false; //nyni JE mozne spustit novou hru znovu

            notifyObservers(UdalostHry.KONECHRY);
            if (rozhrani instanceof GUI) {
                while (!replay) {
                    try {
                        wait();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace(System.err);
                    }
                }
                replay = false;
                novaHra(hrac);
            }
        } else {
            //hrat muze vzdy jen jeden hrac
            logger.log(Level.INFO,
                    "<< NELZE SPUSTIT VICE NOVYCH HER SOUCASNE >>");
        }
    }

    /**
     * Privatni metoda, ktera pripravi hru pred spustenim a provede nezbytne
     * ukony
     *
     * @param hrac
     */
    private void zavestNovouHru(String hrac) {
        priprav_promenne:
        {
            this.hrac = hrac;
            planek = new Scenar(); //KONKRETNI HERNI PLAN HRY SLEPEC
            replay = false;            
            konecHry = null;
            postup.clear();
            zacHry = System.currentTimeMillis();
            ubehloTemp = 0;
        }
        zapniCasovac(TIME_LIMIT);
        planek.hlavniPostava().prejmenuj(hrac);
        planek.attachObserver(rozhrani);
        planek.hlavniPostava().attachObserver(rozhrani);        
                
        notifyObservers(UdalostHry.NOVAHRA);
        //protoze Scenar byl vytvoren drive nez jsme pripojili Observery!
        planek.notifyObservers(UdalostHry.ZMENIL_PROSTOR);
        planek.hlavniPostava().notifyObservers(UdalostHry.SEBRAL_VEC);
        
        rozhrani.setVystup(getIntro() + "\n");
        String[] prikazy = getMinulaHra();
        if (prikazy.length > 1) {
            if (rozhrani.getVstup(
                    "$ nahrat posledni hru [a/n]> ").matches("(?i)^\\s*a.*")) {
                nactiStavHry(prikazy);
            }
            smazMinulaHra();
        } else {
            postup.add(planek.kdeJsem().toString());
        }

        //vypise informace o mistnosti, ve ktere hra zacina
        rozhrani.setVystup("##\n" + planek.kdeJsem().getInfo());
    }

    /**
     * Zpracovani uzivatelova vstupu
     *
     * @param vstup vstup od uzivatele (hrace) hry
     * @return reakce hry na uzivateluv vstup
     */
    private String zpracujVstup(String vstup) {
        Prikaz p = evidPrikazu.getPrikaz(
                evidPrikazu.hledejPlatnyPrikaz(vstup));

        if (p != null) {
            if (p.proved(p.tokenize(vstup))) {
                if (!p.toString().equals(PrikazyVycet.SAVE.toString())) {
                    //prikaz byl uspesny => aktualizujeme postup hry
                    postup.add(vstup);
                }
            }

            return p.getResult();
        } else {
            return " tip: prikaz '" + PrikazyVycet.NAPOVEZ + "'";
        }
    }

    /**
     * Nacte stav hry (save) na zaklade pole predanych prikazu
     *
     * @param prikazy pole prikazu hry, ktere se maji davkove provest
     */
    protected void nactiStavHry(String[] prikazy) {
        prikazy:
        for (int i = 0; i < prikazy.length; i++) {
            if (i == 0) {
                planek.move(prikazy[i], true);
                postup.add(planek.kdeJsem().toString());
                continue prikazy;
            }
            zpracujVstup(prikazy[i]);
        }
    }

    /**
     * Indikuje vyhru
     *
     * @return true, pokud situace hry splnuje podminky pro vyhru
     */
    protected boolean isVyhra() {
        try {
            List<Vec> snedeno = planek.hlavniPostava().getSnedeno();
            List<Vec> vypito = planek.hlavniPostava().getVypito();

            String snedl = "" + //posledni jidlo
                    snedeno.get(snedeno.size() - 1);
            String vypil = "" + //posledni piti
                    vypito.get(vypito.size() - 1);

            if (snedl.toLowerCase().equals("cepels")
                    && vypil.toLowerCase().equals("voda")) {
                return true;
            }
        } catch (IndexOutOfBoundsException ex) {
        }

        return false;
    }

    /**
     * Indikuje neuspesne dokonceni hry
     *
     * @return true, pokud byly poruseny podminky hry
     */
    protected boolean isGameOver() {
        return !isCasovac();
    }

    /**
     * Uvodni informace ke hre
     *
     * @return prolog
     */
    private String getIntro() {
        return "Tady je hra " + NAZEV + "\n"
                + " najdete vodu a prasek cepels\n"
                + " mate na to 10 minut\n"
                + " tip: RTFM\n"
                + "Ted!";
    }

    /**
     * Finalni komentar na zaver hry
     *
     * @return epilog
     */
    protected String getOutro() {
        String outro = "";
        switch (getKonecHry()) {
            case UZIVATEL:
                outro += " *have a great day!\n";
                break;
            case GAMEOVER:
                outro += "\n *cas vyprsel :'(\n";
                break;
            case VYHRA:
                String msg = " zvladli".toUpperCase()
                        + " jste to primissima hip hip hura!\n";
                String border = " ";
                for (int i = 1; i + 1 < msg.length(); i++) {
                    border += "-";
                }
                border += "\n";

                outro += "\n\n" + border + msg + border + "\n\n";
                break;
        }
        outro += String.format(" %d min : %02d sek",
                (getHerniCas() / 1000) / 60, (getHerniCas() / 1000) % 60)
                + ", " + this;

        return outro;
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
    public final synchronized void update(UdalostHry udalost) {
        switch (udalost) {
            case NOVAHRA:
                replay = true;
                notifyAll();
                break;
        }
    }

    /**
     * Vrati aktualne hrou pouzivany herni plan
     *
     * @return herni plan hry
     */
    @Override
    public HerniPlan getHerniPlan() {
        return planek;
    }

    /**
     * Ulozi zaznamenane (spravne provedene) prikazy hry do souboru <nazev hry>
     * do adresare definovaneho statickou promennou PATH_SAVES_FILE tridy Hra
     *
     * @return boolean vysledek operace
     */
    @Override
    public boolean ulozAktualniHra() {
        if (postup.size() > 0) {
            PrintWriter souborZapis = null;
            try {
                souborZapis = new PrintWriter(
                        new OutputStreamWriter(
                        new FileOutputStream(PATH_SAVES_FILE), "UTF-8"));
                for (String prikaz : postup) {
                    souborZapis.println(prikaz);
                }
            } catch (UnsupportedEncodingException e) {
            } catch (IOException e) {
                System.err.println(e.getMessage());
                logger.log(Level.WARNING, "Nepodarilo se ulozit hru");
            } finally {
                try {
                    if (souborZapis != null) {
                        souborZapis.close();
                        return true;
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }

        return false;
    }

    /**
     * Pokusi se v souborovem systemu vyhledat save posledni instance hry,
     * nacist prikazy do pole a vratit jej
     *
     * @return pole prikazu minule hry
     */
    private String[] getMinulaHra() {
        BufferedReader br = null;
        StringBuilder radky = null;
        try {
            radky = new StringBuilder();
            br = new BufferedReader(new FileReader(PATH_SAVES_FILE));
            String line = br.readLine();

            while (line != null) {
                radky.append(line);
                radky.append("\n");
                line = br.readLine();
            }

        } catch (FileNotFoundException e) {
            //posledni hra nebyla ulozena
        } catch (IOException e) {
            radky.setLength(0);
            logger.log(Level.WARNING, "Nelze precist soubor s hernimi saves");
            System.err.println(e.getMessage());
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }

            return radky.toString().split("\\n");
        }
    }

    /**
     * Smaze pripadny save minule hry
     */
    private void smazMinulaHra() {
        File poslHra = new File(PATH_SAVES_FILE);
        if (poslHra.exists() && poslHra.isFile()) {
            poslHra.delete();
        }
    }

    /**
     * Hra se pri nejblizsi prilezitosti prerussi
     */
    @Override
    public final void ukonciHru() {
        konecHry = KonecHry.UZIVATEL;
    }

    /**
     * Vrati vyctovy typ konce hry, ktery nastal
     *
     * @return duvod ukonceni
     */
    @Override
    public KonecHry getKonecHry() {
        return konecHry;
    }

    /**
     * Vrati uplynuly herni cas od zacatku nove hry
     *
     * @return herni cas v milisekundach
     */
    @Override
    public final long getHerniCas() {
        if (zacHry == 0) {
            return zacHry;
        }

        return (System.currentTimeMillis() - zacHry);
    }

    /**
     * Pokud je nova hra spustena s casovym omezenim, vraci zbyvajici
     * milisekundy do konce hry
     *
     * @return zbyvajici cas do konce hry
     */
    @Override
    public final long getZbyvaCas() {
        if (casovac != null) {
            return (casovac.getInitialDelay() - (getHerniCas() - ubehloTemp));
        } else {
            return 0;
        }
    }

    /**
     * Pokud je nova hra spustena s casovym omezenim, umozni jeho zmenu
     *
     * @param oKolikSekund pocet sekund (i zapornych) o ktery chceme zmenit
     * zbyvajici casovy limit
     */
    @Override
    public final void prodluzCasovac(int oKolikSekund) {
        if (isCasovac()) {
            casovac.stop();
            int delay = (int) getZbyvaCas() + (oKolikSekund * 1000);
            if (delay <= 0) {
                casovac.setInitialDelay(0);
                konecHry = KonecHry.GAMEOVER;
            } else {
                casovac.setInitialDelay(delay);
                casovac.start();
            }
            ubehloTemp = getHerniCas();
        }
    }

    /**
     * Privatni metoda pro zapnuti casoveho limitu hry
     *
     * @param pocSekund velikost casoveho limitu v sekundach
     */
    private void zapniCasovac(int pocSekund) {
        if (null == casovac || !casovac.isRunning()) {
            casovac = new Timer(pocSekund * 1000, new TimeLimit());
            casovac.setInitialDelay(casovac.getDelay());
            casovac.setRepeats(false);
            casovac.start();
        }
    }

    /**
     * Indikuje casove omezenou hru
     *
     * @return true, pokud je spustena hra casove omezena
     */
    @Override
    public final boolean isCasovac() {
        if (casovac != null && casovac.isRunning()) {
            return true;
        }

        return false;
    }

    /**
     * Jmeno aktualne hrajiciho hrace
     *
     * @return jmeno hrace
     */
    @Override
    public String getHrac() {
        return hrac;
    }

    /**
     * Vrati nazev hry
     *
     * @return nazev hry
     */
    @Override
    public String toString() {
        return NAZEV;
    }

    /**
     * Seznam prikazu hry
     *
     * @return zformatovany seznam prikazu hry
     */
    @Override
    public String getVypisPrikazu() {
        return evidPrikazu.getVypisPrikazu();
    }

    /**
     * Privatni metoda, ktera zavede herni prikazy
     */
    private void zavestHerniPrikazy() {
        evidPrikazu.clear();

        evidPrikazu.pridejPrikaz(new PrikazKonec(
                this, "ukonci hru"));
        evidPrikazu.pridejPrikaz(new PrikazNapovez(
                this, "co mam delat?"));
        evidPrikazu.pridejPrikaz(new PrikazJdi(
                this, "jdi <prostor>"));
        evidPrikazu.pridejPrikaz(new PrikazKdejsem(
                this, "(i) kde prave jste"));
        evidPrikazu.pridejPrikaz(new PrikazSeber(
                this, "seber <co> [z ceho/koho]"));
        evidPrikazu.pridejPrikaz(new PrikazPoloz(
                this, "poloz <co> [do ceho/koho]"));
        evidPrikazu.pridejPrikaz(new PrikazMluv(
                this, "mluv <s kym> [o cem]"));
        evidPrikazu.pridejPrikaz(new PrikazPopis(
                this, "popis <co/koho>"));
        evidPrikazu.pridejPrikaz(new PrikazBatoh(
                this, "batoh na vasich zadech"));
        evidPrikazu.pridejPrikaz(new PrikazZamkni(
                this, "zamkni <prostor>"));
        evidPrikazu.pridejPrikaz(new PrikazOdemkni(
                this, "odemkni <prostor>"));
        evidPrikazu.pridejPrikaz(new PrikazPij(
                this, "pij <co>"));
        evidPrikazu.pridejPrikaz(new PrikazJez(
                this, "jez <co>"));
        evidPrikazu.pridejPrikaz(new PrikazSave(
                this, "uloz hru"));
    }
}
