package slepec;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import slepec.hra.Hra;

/**
 * <h1>HRA SLEPEC</h1>
 *
 * @author Pavel Jurca, xjurp20@vse.cz
 * @version 2
 */
public class Play {

    public static final Logger globalLog = Logger.getLogger("slepec");

    private Play() {
    }

    /**
     * Zapni a spust novou hru
     *
     * @param args parametr 'text' spusti hru v textovem rezimu
     */
    public static void main(String[] args) {
        initLogger();
        try {
            Hra hra;
            if (args != null && args.length > 0
                    && args[0].matches("(?i)text.*")) {
                hra = new Hra(false);
            } else {
                hra = new Hra(true);
            }

            hra.novaHra(System.getProperty("user.name")); //jmeno hrace
        } catch (Exception ex) {
            globalLog.log(Level.SEVERE, "Something went wrong", ex);
            ex.printStackTrace(System.err);
            System.exit(1);
        }
    }

    private static void initLogger() {
        try {
            FileHandler fh = new FileHandler(Hra.PATH_LOGS_FILE);
            fh.setFormatter(new SimpleFormatter());
            fh.setLevel(Level.ALL);
            
            globalLog.setUseParentHandlers(false);
            globalLog.addHandler(fh);
        } catch (IOException | SecurityException ex) {
            //ex.printStackTrace(System.err);
        }
    }
}
