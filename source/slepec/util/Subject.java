package slepec.util;

import slepec.hra.UdalostHry;

/**
 * Observable a.k.a predmet vaseho zajmu
 *
 * @author Pavel Jurca, xjurp20@vse.cz
 * @version 2
 */
public interface Subject {
    void attachObserver(Observer observer);
    void detachObserver(Observer observer);
    void notifyObservers(UdalostHry udalost);
}
