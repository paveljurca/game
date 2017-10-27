## RUN
	> dvojklik na soubor 'Slepec.jar'
		
		> NEBO

	> 'java -jar Slepec.jar' z prikazove radky
		> musite byt v adresari se souborem 'Slepec.jar'
		> * 'java -jar Slepec.jar text' spusti hru v textovem rezimu


## SYNOPSIS
	> Slepec je demo hra s GUI a CLI rozhranim

	* prikazy akceptuji 0, 1 nebo 2 parametry
	* viceslovne parametry musi byt uzavreny do uvozovek ("") nebo apostrofu ('')
	* veci mohou byt zanorene do libovolne urovne
		> prikaz 'seber' a 'poloz' dosahne jen na veci do 2. urovne zanoreni
	* nahodna vychozi pozice
	* mereni herniho casu
	* limit na vyhrani hry je 10 minut
	* pomoci jidla a piti lze zvysit/snizit casovy limit
	* moznost nacteni posledni hry, pokud jste hru ulozili a neskoncila vyhrou
	tip: po nacteni posledni hry zacinate opet s plnym casovym limitem

	* veskere chyby programu zaznamenavany do souboru "slepec.log"
		> Windows => C:\Temp\slepec.log
		> GNU/Linux => /tmp/slepec.log


## MANUAL

      batoh  /batoh na vasich zadech
        jdi  /jdi <prostor>
        jez  /jez <co>
    kdejsem  /vase oci
      konec  /ukonci hru
       mluv  /mluv <s kym> [o cem]
    napovez  /co mam delat?
    odemkni  /odemkni <prostor>
        pij  /pij <co>
      poloz  /poloz <co> [do ceho/koho]
      popis  /popis <co/koho>
       save  /uloz hru
      seber  /seber <co> [z ceho/koho]
     zamkni  /zamkni <prostor>


## TODO
	> vyresit: kazda instance hry Slepec (objekt tridy Hra) uklada do stejneho souboru
		> tzn. zustanou jen saves posledni instance
		> a jeste muze dojit ke kolizi se souborovym systemem
	*
        > final trida Hra dela prilis mnoho!
	> vyhnout se praci s vlakny ('wait' a 'notifyAll')
		> puvodni design struktury hry byl urcen ClI rozhrani
		> pro GUI rozhrani uz neni moc vhodny
			> par veci se musi delat pres ruku
				> napr. spusteni nove hry za behu (v CLI nebylo mozne)
		> navrh: ovladat hru z abstraktni tridy Rozhrani (nyni interface)
			> final tridu Hra zjednodusit, jeji ne nezbytne ulohy vydelit _
			> nechat ji pracovat jen ciste s logikou hry (model)
                        > single purpose
			> neco na zpusob MVC?
	*
        > refaktorovat do 'cz.jurcapavel.*' balicku


## CHANGELOG
	verze 2
		> GUI rozhrani
                    > obrazkovy panel batohu
                    > vykreslovani pozice na herni mape
                > logovani vsech vystupu a varovani hry
		> viceslovne parametry prikazu
		> prikazy reseny formou trid
		> pridany prikazy: odemkni, zamkni, jez, pij, batoh, popis, save
		> prepsan scenar hry
		> update uzivatelske prirucky
		> obrazky hry v samostatnem archivu 'img.jar'
	verze 1
		> pouze CLI rozhrani
		> prikazy reseny formou SortedMap a switch/case


## CODE
	* hra je napsana v jazyku Java
		> NetBeans IDE 7.2 (Build 201207171143)
		> 1.7.0_07; Java HotSpot(TM) 64-Bit Server VM 23.3-b01
		> odladeno na
			> openSUSE 12.1 Asparagus (x86_64)
			> Linux version 3.1.10-1.16-desktop running on amd64; UTF-8; en_US (nb)

	> final trida Hra ma "HasA relationship" trid _
		> Scenar, CLI/GUI (IRozhrani), HerniPlan a EvidencePrikazu
	
        > kazde uspesne zavolani metody 'novaHra' vytvori novou instanci tridy Scenar
                > pozor na zastarale reference!

	> pro ukonceni hry nestaci pouhe zavolani metody 'ukonciHru'
		> hra totiz bezi jako smycka while a ceka na vstup, tzn. _
		> na vstupu jeste musi dojit ke stisknuti klavesy ENTER

	> zadne prostory, osoby, predmety se NESMI jmenovat stejne
		> objekt se stejnym nazvem se proste neprida
		> tyto entity hry jsou vzdy ulozeny v mnozine HashSet
	> z prostoru lze odebrat jen predmety, ktere jsou v 1. urovni zanoreni
		> tzn. lednici ANO, ale jogurt v lednici uz NE
			> proto trida Prostor nema metodu 'hasVec'
	> ALE muzete z prostoru pomoci metody 'selectVec' ziskat referenci na predmet lednice
		> a diky metode 'odeber' zavolane nad lednici odebrat vec jogurt
		> tedy odebrat objekt tridy Vec z jineho objektu Vec lze z libovolne urovne!

	> read the code ;)


## COMMENTS
	> soubor 'Slepec.jar' je samotna hra, soubor 'img.jar' jsou obrazky pouzite ve hre
		> pro spravne fungovani potrebujete oba (minimalne ale 'Slepec.jar')
		> oba soubory musi byt umisteny ve stejnem adresari
                > archiv 'img.jar' musi mit adresar 'images' se soubory JPEG _
                > pojmenovanych po vecech vyskytujicich se ve hre
                > nazvy souboru vzdy malymi pismeny!

	> ke spusteni nove hry potrebujete vzdy i novou (cistou) instanci herniho planu - a to je problem
		> puvodne jsem chtel pouzit copy-constructor tridy HerniPlan, coz by ale znamenalo, ze _
		> by copy-constructorem musely disponovat vsechny objekty(Osoba, Vec, Prostor) tridy HerniPlan
		
		> PROTO nakonec musela vzniknout trida Scenar, ktera z tridy HerniPlan dedi
			> 'new Scenar()' vytvori novy herni plan hry Slepec
                        > + pridava vycet vsech Prostoru i Veci hry

## ADDENDUM

        > adventura mela puvodne vychazet ze skeletonu Zork 1
          http://www.bluej.org/resources/exercises.html#zork1

          upraveneho do kurzu 4IT101
          http://java.vse.cz/wiki/uploads/4it101/adventuraZaklad2013.zip

## COPYRIGHT

půdorys domu © 2002 GSERVIS s.r.o., © 2010 Pavel Jurča

images courtesy of FreeDigitalPhotos.net
