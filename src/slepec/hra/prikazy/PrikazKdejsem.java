package slepec.hra.prikazy;

import slepec.hra.Hra;

/**
 * Slouzi jen jako informace o aktualnim prostoru
 *
 * @author Pavel Jurca, xjurp20@vse.cz
 * @version 2
 */
public class PrikazKdejsem extends Prikaz {

    public PrikazKdejsem(Hra hra, String popis) {
        super(hra, popis);
    }

    @Override
    public boolean proved(String[] parametry) {
        result = hra.getHerniPlan().kdeJsem().getInfo();
        
        return true;
    }

    @Override
    public String toString() {
        return PrikazyVycet.KDEJSEM.toString();
    }
}
