package slepec.hra.prikazy;

import slepec.hra.Hra;
import slepec.hra.planek.prostor.vec.*;

/**
 * PrikazBatoh zprostredkuje zakladni informace o batohu
 *
 * @author Pavel Jurca, xjurp20@vse.cz
 * @version 2
 */
public class PrikazBatoh extends Prikaz {

    public PrikazBatoh(Hra hra, String popis) {
        super(hra, popis);
    }

    /**
     * Informace o batohu hlavni postavy hry
     */
    @Override
    public boolean proved(String[] parametry) {
        VecBatoh batoh = hra.getHerniPlan().hlavniPostava().getBatoh();
        if (batoh != null) {
            StringBuilder info = new StringBuilder();

            info.append(String.format("%12s%-5s%s", "nazev", " -> ",
                    batoh.toString())).append("\n");
            info.append(String.format("%12s%-5s%s gramu", "max.zatez", " -> ",
                    batoh.getMaxZatez())).append("\n");
            info.append(String.format("%12s%-5s%s", "max.veci", " -> ",
                    batoh.getMaxPocVeci())).append("\n");
            info.append(String.format("%9s%-8s : %s", "", "obsahuje",
                    batoh.getSeznamVeci())).append("\n");
            info.append(String.format("%9s%-8s : %s gramu", "", "vaha",
                    batoh.getVaha())).append("\n");                        
            
            result = info.toString().replaceFirst("(?siu)\\n$", "");
            
            return true;
        } else {
            result = " err: nemate zadny batoh";
        }

        return false;
    }

    @Override
    public String toString() {
        return PrikazyVycet.BATOH.toString();
    }
}
