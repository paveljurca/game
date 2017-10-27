package slepec.hra.rozhrani;

/**
 * Verejne metody pro ovladani rozhrani programu
 *
 * @author Pavel Jurca, xjurp20@vse.cz
 * @version 2
 */
public interface IRozhrani extends 
        slepec.util.Subject,
        slepec.util.Observer {

    void setVystup(String vystup);

    String getVstup(); //vrati textovy retezec ze vstupu
    
    String getVstup(String vyzva);
}
