package slepec.hra.planek.prostor.vec;

import slepec.hra.planek.prostor.Prostor;

/**
 * Trida VecKlic jen jako konkretnejsi instance tridy Vec<br />
 * VecKlic lze pouzit k odemknuti a zamknuti vzdy jen jednoho prostoru
 *
 * @author Pavel Jurca, xjurp20@vse.cz
 * @version 2
 */
public final class VecKlic extends Vec {

    private Prostor prostor; //prostor, pro ktery je tento klic vhodny

    public VecKlic(Prostor prostor) {
        super("klic" + prostor.toString().toUpperCase(),
                "klic od prostoru " + prostor, 25);
        this.prostor = prostor;
    }

    public String odProstoru() {
        return prostor.toString();
    }
}
