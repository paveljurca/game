package slepec.hra.rozhrani;

import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;
import slepec.Play;
import slepec.hra.Hra;
import slepec.util.Observer;
import slepec.hra.UdalostHry;

/**
 * Rozhrani programu formou terminalu (prikazove radky)
 *
 * @author Pavel Jurca, xjurp20@vse.cz
 * @version 2
 */
public final class CLI implements IRozhrani {

    private static final Logger logger =
            Play.globalLog.getLogger(CLI.class.getName());
    private Scanner sc;
    private final String prompt;
    private final CopyOnWriteArrayList<slepec.util.Observer> listeners;

    public CLI(String prompt) {
        this.prompt = prompt;
        listeners = new CopyOnWriteArrayList<>();
    }

    @Override
    public void setVystup(String vystup) {
        logger.info(String.format("{VYSTUP PROGRAMU}%n%s", vystup));
        System.out.println(vystup);
    }

    @Override
    public String getVstup() {
        return getVstup(prompt);
    }

    @Override
    public String getVstup(String vyzva) {
        System.out.print(vyzva);
        sc = new Scanner(System.in);
        String vstup = sc.nextLine().trim();
        logger.info(String.format("{VSTUP UZIVATELE}%n%s", vstup));

        return vstup;
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
    public void update(UdalostHry udalost) {
    }
}
