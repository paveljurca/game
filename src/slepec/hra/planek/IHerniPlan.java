package slepec.hra.planek;

import java.util.Set;
import slepec.hra.planek.prostor.Prostor;
import slepec.hra.planek.prostor.osoba.Osoba;

/**
 * Verejne rozhrani pro praci s hernim planem
 *
 * @author Pavel Jurca, xjurp20@vse.cz
 * @version 2
 */
public interface IHerniPlan extends slepec.util.Subject {

    void pridejProstory(Prostor... prostory);

    Prostor selectProstor(String nazevProstoru);

    Set<Prostor> getProstory();

    Prostor selectNahodProstor();

    Prostor kdeJsem();

    Osoba hlavniPostava();

    boolean move(String nazevProstoru); //zmeni aktualni pozici
    //potlaci kontrolu pruchodnosti a kontrolu zamcene mistnosti

    boolean move(String nazevProstoru, boolean ignoreChecks);
}
