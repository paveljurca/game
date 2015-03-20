package slepec.hra.prikazy;

/**
 * Seznam prikazu podporovanych hrou
 * 
 * @author Pavel Jurca, xjurp20@vse.cz
 * @version 2
 */
public enum PrikazyVycet {
    JDI(0),
    ODEMKNI(1),
    ZAMKNI(2),
    MLUV(3),
    SEBER(4),
    POLOZ(5),
    JEZ(6),
    PIJ(7),
    BATOH(8),
    NAPOVEZ(9),
    POPIS(10),
    KDEJSEM(11),
    SAVE(12),
    KONEC(13);
    
    int value;
    private PrikazyVycet(int value) {this.value=value;}
    
    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
