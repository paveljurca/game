package slepec.hra;

import slepec.hra.planek.HerniPlan;

/**
 * Verejne rozhrani pro ovladani hry
 *
 * @author Pavel Jurca, xjurp20@vse.cz
 * @version 2
 */
public interface IHra extends
        slepec.util.Subject,
        slepec.util.Observer {

    void novaHra(String hrac);

    boolean ulozAktualniHra();

    void ukonciHru();

    KonecHry getKonecHry();

    boolean isCasovac();

    void prodluzCasovac(int oKolikSekund);

    String getHrac();

    long getHerniCas();

    long getZbyvaCas();

    String getVypisPrikazu();

    HerniPlan getHerniPlan();
}
