package slepec.util;

import slepec.hra.UdalostHry;

/**
 * Observer a.k.a. pozorovatel
 * 
 * @author Pavel Jurca, xjurp20@vse.cz
 * @version 2
 */
public interface Observer {
   /**
    * The observer pulls the changed state from the subject.<br />
    * <i>..na zaklade typu vyvolane udalosti</i>
    */  
   void update(UdalostHry udalost); 
}
