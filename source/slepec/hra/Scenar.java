package slepec.hra;

import java.util.HashMap;
import java.util.Map;
import slepec.hra.planek.HerniPlan;
import slepec.hra.planek.prostor.Prostor;
import slepec.hra.planek.prostor.osoba.*;
import slepec.hra.planek.prostor.vec.*;

/**
 * "DECORATOR" (WRAPPER) tridy HerniPlan <b>Scenar hry - <i>konkretni
 * implementace herniho planu</i></b>
 *
 * @author Pavel Jurca, xjurp20@vse.cz
 * @version 2
 */
public final class Scenar extends HerniPlan {

    public enum ProstorVycet {
        PREDSIN(0.40d, 0.72d),
        ZAHRADA(0.89d, 0.14d),
        PRACOVNA(0.18d, 0.81d),
        TOALETA(0.57d, 0.70d),
        GARAZ(0.79d, 0.68d),
        KUCHYN(0.16d, 0.46d),
        OBYVAK(0.52d, 0.31d),
        CHODBA(0.38d, 0.53d),
        KOMORA(0.53d, 0.56d);
        
        //x-osovy-pomer pozice prostoru na planku hry v procentech
        private final double xPomer;
        //y-osovy-pomer pozice prostoru na planku hry v procentech
        private final double yPomer;
        ProstorVycet(double xPomer, double yPomer) {
            this.xPomer = xPomer;
            this.yPomer = yPomer;
        }

        /**
         * Pro oznaceni pozice prostoru na planku hry
         *
         * @return pomer na X ose k rozmerum planku hry v PROCENTECH
         */
        public double getXpomer() {
            return xPomer;
        }

        /**
         * Pro oznaceni pozice prostoru na planku
         *
         * @return pomer na Y ose k rozmerum planku hry v PROCENTECH
         */
        public double getYpomer() {
            return yPomer;
        }

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    /**
     * <b>Vycet vsech veci hry</b>
     */
    public enum VecVycet {
        BATUZEK,
        AUTO,
        REZERVA,
        HRABE,
        KOMPOT,
        LEDNICE,
        MIKROVLNKA,
        KURE,
        JOGURT,
        PASTIKA,
        TELEVIZE,
        KRB,
        POHOVKA,
        CEPELS,
        NABOJ,
        STOLEK,
        SUPLIK,
        KOMODA,
        TEPLAKY,
        JOHNNIE,
        BOTA,
        SUCHARY,
        VODA,
        PIVO,
        SEKT,
        CHILLI,
        SKRINKA,
        KARTACEK,
        MEJDLO,
        BATOH,
        MYDLO,
        KLICKOMORA,
        KOMORAKLIC;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    /**
     * <strong>Vytvori herni svet</strong><br />=> prostory, osoby a veci, <i>
     * nastavi vychody, zamkne/odemkne mistnosti apod.</i>
     */
    public Scenar() {
        super("Slepec",
                new Osoba("ja", "pejsek, ktery hleda vodu a prasek cepels"),
                new Prostor(ProstorVycet.PREDSIN.toString(),
                "vstupni prostor domu"));

        //predsin je vychozim prostorem (dle konstruktoru tridy HerniPlan)
        Prostor predsin = super.kdeJsem();
        //zahradu nelze zamknout
        Prostor zahrada = new Prostor(ProstorVycet.ZAHRADA.toString(),
                "zelen, vzduch a svoboda", false);
        Prostor pracovna = new Prostor(ProstorVycet.PRACOVNA.toString(),
                "bez prace nejsou kolace");
        Prostor toaleta = new Prostor(ProstorVycet.TOALETA.toString(),
                "kazdy sem jednou musi");
        Prostor garaz = new Prostor(ProstorVycet.GARAZ.toString(),
                "docela fajnova garaz");
        Prostor kuchyn = new Prostor(ProstorVycet.KUCHYN.toString(),
                "tady se vari a pote hoduje");
        Prostor obyvak = new Prostor(ProstorVycet.OBYVAK.toString(),
                "zona pro odpocinek");
        Prostor chodba = new Prostor(ProstorVycet.CHODBA.toString(),
                "centralni chodba prizemi");
        Prostor komora = new Prostor(ProstorVycet.KOMORA.toString(),
                "spizirna s dobrotami a alkoholem");

        zahrada.setVychody(predsin, garaz, obyvak);
        pracovna.setVychody(predsin);
        predsin.setVychody(pracovna, chodba, zahrada, toaleta);
        toaleta.setVychody(predsin);
        garaz.setVychody(zahrada);
        kuchyn.setVychody(chodba);
        obyvak.setVychody(zahrada, chodba);
        chodba.setVychody(predsin, kuchyn, obyvak, komora);
        komora.setVychody(chodba);

        //pridani vsech prostoru do herniho planu
        super.pridejProstory(zahrada, pracovna, toaleta,
                garaz, kuchyn, obyvak, chodba, komora);

        komora.zamknout();

        //chodba
        //max.zatez 7kg, max.veci 10
        VecBatoh batoh = new VecBatoh(VecVycet.BATOH.toString(),
                "proste vetsi batoh pro psy", 7000, 10);

        chodba.pridejVeci(batoh);
        //garaz
        Vec auto = new Vec(VecVycet.AUTO.toString(),
                "Mini Cooper S 1967", 650000);
        Vec rezerva = new Vec(VecVycet.REZERVA.toString(),
                "11\"", 4000);
        Vec hrabe = new Vec(VecVycet.HRABE.toString(),
                "na podzimni listi", 2000);
        Vec kompot = new Vec(VecVycet.KOMPOT.toString(),
                "jahodovy", 250, true, false, 300);

        auto.pridej(rezerva);

        garaz.pridejVeci(auto, hrabe, kompot);
        //kuchyn
        Vec lednice = new Vec(VecVycet.LEDNICE.toString(),
                "velka bila znacky Liebherr", 21200, true);
        Vec mikrovlnka = new Vec(VecVycet.MIKROVLNKA.toString(),
                "stara neumyta znacky Zanussi", 5000, true);
        Vec kure = new Vec(VecVycet.KURE.toString(),
                "vodnanske", 1500, true, false, 450);
        Vec jogurt = new Vec(VecVycet.JOGURT.toString(),
                "florian jahodovy", 120, true, false, 180);
        Vec pastika = new Vec(VecVycet.PASTIKA.toString(),
                "jatrova", 200, true, false, 130);

        lednice.pridej(jogurt, pastika);
        mikrovlnka.pridej(kure);

        kuchyn.pridejVeci(lednice, mikrovlnka);
        //obyvak
        Vec televize = new Vec(VecVycet.TELEVIZE.toString(),
                "bednicka z trubadury", 13800);
        Vec krb = new Vec(VecVycet.KRB.toString(),
                "handmade", 101010, true);
        Vec pohovka = new Vec(VecVycet.POHOVKA.toString(),
                "z IKEA", 12000);

        obyvak.pridejVeci(televize, krb, pohovka);
        //pracovna
        Vec cepels = new Vec(VecVycet.CEPELS.toString(),
                "prasek pro silne diabetiky", 50,
                true, false, 0);
        Vec naboj = new Vec(VecVycet.NABOJ.toString(),
                "ammo do AK-47", 40);
        Vec stolek = new Vec(VecVycet.STOLEK.toString(),
                "nocni stolek vasi panicky",
                9000, true);
        Vec suplik = new Vec(VecVycet.SUPLIK.toString(),
                "soucast stolku",
                batoh.getMaxZatez() - cepels.getVaha() - naboj.getVaha(), true);
        Vec komoda = new Vec(VecVycet.KOMODA.toString(),
                "z duboveho dreva", 15000, true);
        Vec teplaky = new Vec(VecVycet.TEPLAKY.toString(),
                "teplakova souprava - kule dej si doprava!", 300);
        Vec johnnie = new Vec(VecVycet.JOHNNIE.toString(), "whisky Johnnie Walker",
                700, false, true, -360);

        suplik.pridej(cepels, naboj);
        stolek.pridej(suplik);
        komoda.pridej(teplaky, johnnie);

        pracovna.pridejVeci(stolek, komoda);
        //predsin
        Vec bota = new Vec(VecVycet.BOTA.toString(), "platena botka vel. 42", 450);

        predsin.pridejVeci(bota);
        //komora     
        Vec voda = new Vec(VecVycet.VODA.toString(),
                "500ml Bonaqua PET", 500, false, true, 20);
        Vec suchary = new Vec(VecVycet.SUCHARY.toString(),
                "psi suchary - mnamka", 350, true,
                false, 120);
        Vec pivo = new Vec(VecVycet.PIVO.toString(),
                "Velkopopovicky kozel", 500, false,
                true, 240);
        Vec sekt = new Vec(VecVycet.SEKT.toString(),
                "brut znacky Bohemia", 700, false,
                true, -200);
        Vec chilli = new Vec(VecVycet.CHILLI.toString(),
                "extra palive, pozor!",
                25, true, false, -360);

        komora.pridejVeci(voda, suchary, pivo, sekt, chilli);
        //toaleta
        Vec skrinka = new Vec(VecVycet.SKRINKA.toString(),
                "skrinka pod umyvadlem", 12300, true);
        Vec kartacek = new Vec(VecVycet.KARTACEK.toString(),
                "na udrzbu zubu", 25);
        Vec mejdlo = new Vec(VecVycet.MEJDLO.toString(),
                "jelen na prani", 120);
        Vec mydlo = new Vec(VecVycet.MYDLO.toString(),
                "snehove bile od Duff", 80);

        skrinka.pridej(kartacek, mejdlo, mydlo);

        toaleta.pridejVeci(skrinka);
        //zahrada
        VecBatoh batuzek = new VecBatoh(VecVycet.BATUZEK.toString(),
                "myvaluv batuzek", 5000, 10);
        VecKlic klicKOMORA = new VecKlic(komora);
        //pozor, je to padelek!
        Vec komoraKLIC = new Vec(
                VecVycet.KOMORAKLIC.toString(), "klic od komory", 25);
        /*myval*/
        Map<String, String> m = new HashMap<>();
        m.put("barter", "Vymenim cokoliv za mydlo. Ale ty prvni!");
        m.put("hledam informace",
                "Cepels je v pracovne a voda v komore. A ted vypadni.");
        OsobaMyval myval = new OsobaMyval("myval", "chlupate to zviratko", m);

        batuzek.pridej(klicKOMORA, komoraKLIC);
        myval.pridejVec(batuzek);

        zahrada.pridejOsoby(myval);
        /* --- */


        //NASTAVENI NAHODNEHO VYCHOZIHO PROSTORU
        //pravdepodobne odlisnou od te v konstruktoru tridy HerniPlan
        //parametr true potlaci implicitni kontroly pri zmene aktualniho prostoru
        super.move(super.selectNahodProstor().toString(), true);
    }
}
