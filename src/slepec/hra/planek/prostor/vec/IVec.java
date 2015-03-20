package slepec.hra.planek.prostor.vec;

import java.util.Set;

/**
 * Verejne rozhrani pro ovladani veci (predmetu ve hre)
 *
 * @author Pavel Jurca, xjurp20@vse.cz
 * @version 2
 */
public interface IVec {

    boolean pridej(Vec... veci); //jedna vec muze obsahovat dalsi veci

    Vec odeber(String nazevVeci);

    boolean hasVec(String nazevVeci);

    Vec selectVec(String nazevVeci);

    Set<Vec> getVnitrek();

    String getSeznamVeci();

    String getPopis();

    int getVaha(); //vraci vahu veci v gramech

    boolean isSchranka(); //true, pokud dana vec muze obsahovat dalsi veci

    boolean isJidlo(); //true, pokud danou vec lze snist

    boolean isPiti(); //true, pokud danou vec lze vypit

    int getNutriCislo(); //nutricni hodnota
}
