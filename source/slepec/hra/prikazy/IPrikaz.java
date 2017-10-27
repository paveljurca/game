package slepec.hra.prikazy;

/**
 * Verejne rozhrani kazdeho prikazu
 *
 * @author Pavel Jurca, xjurp20@vse.cz
 * @version 2
 */
public interface IPrikaz {

    boolean proved(String[] parametry);

    String getResult();

    String[] tokenize(String vstup);

    String getPopis();
}
