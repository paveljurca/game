package slepec.hra.rozhrani.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import slepec.Play;
import slepec.hra.Hra;
import slepec.hra.Scenar;
import slepec.hra.UdalostHry;
import slepec.hra.planek.prostor.vec.VecBatoh;
import slepec.hra.rozhrani.IRozhrani;
import slepec.util.Observer;

/**
 * Graficke rozhrani programu<br />
 *
 * @author Pavel Jurca, xjurp20@vse.cz
 * @version 2
 */
public class GUI implements IRozhrani {

    private static final Logger logger =
            Play.globalLog.getLogger(GUI.class.getName());
    private final CopyOnWriteArrayList<slepec.util.Observer> listeners;
    private Hra hra;
    private final String prompt;
    private java.util.Map<String, ImageIcon> imagesVec;
    private Timer timer;
    private JFrame frame;
    private DialogOkno oknoNapovedy;
    private DialogOkno oknoLogu;
    private JMenuBar menuBar;
    private JMenu mSoubor;
    private JMenu mNapoveda;
    private JPanel container;
    private JPanel levyContainer;
    private JPanel panelBatohu;
    private JPanel panelVystupu;
    private PanelPlanku panelPlanku;
    private JTextField txtFieldVstup;
    private JTextArea txtAreaVystup;
    private JLabel lblZadnyBatoh;
    private final URL planekURL;
    private final URL napovedaURL;
    private URL logURL;

    /**
     * Inicializace nove hry
     */
    private class NovaHra implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            hra.ukonciHru();
            //Hra vzdy ceka na vstup - proto ho musime uvolnit! 
            wakeUp();
            notifyObservers(UdalostHry.NOVAHRA);
        }
    }

    /**
     * Zobrazi informacni dialog "O programu"
     */
    private class OProgramu implements ActionListener {

        private JLabel lbl;

        public OProgramu() {
            lbl = new JLabel(
                    "<html>"
                    + "<center><br /><h3>Copyleft 2013 Pavel Jurča</h3>"
                    + "<a href='http://jurcapavel.cz'>"
                    + "http://jurcapavel.cz</a><br />"
                    + "<br /><br />.<br />"
                    + "<i>půdorys domu © 2002 GSERVIS s.r.o.</i><br />"
                    + "<i>překreslil © 2010 Pavel Jurča</i><br />"
                    + ".<br />"
                    + "<i>images courtesy of FreeDigitalPhotos.net</i><br />"
                    + ".<br /></center>"
                    + "</html>", JLabel.CENTER);
            lbl.setBorder(
                    BorderFactory.createTitledBorder(
                    BorderFactory.createEmptyBorder(24, 24, 24, 24),
                    "Slepec",
                    TitledBorder.CENTER,
                    TitledBorder.TOP,
                    new Font(Font.MONOSPACED, Font.BOLD, 57),
                    new Color(69, 178, 224)));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(frame, lbl, "O programu",
                    JOptionPane.PLAIN_MESSAGE);
        }
    }

    /**
     * Zobrazovani zbyvajiciho casu hry v titulku okna
     */
    private class ZbyvaCasTimer implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            frame.setTitle(hra.toString() + " | "
                    + String.format(" %d min : %02d sek",
                    (hra.getZbyvaCas() / 1000) / 60, (hra.getZbyvaCas() / 1000) % 60));
        }
    }

    public GUI(Hra hra, String prompt) {
        this.hra = hra;
        this.prompt = prompt;
        listeners = new CopyOnWriteArrayList<>();
        imagesVec = new HashMap<>();
        timer = new Timer(1000, new ZbyvaCasTimer());
        planekURL = GUI.class.getResource("/slepec/zdroje/planek-hry.png");
        napovedaURL = GUI.class.getResource("/slepec/zdroje/napoveda.html");

        initFrame();
        hra.attachObserver(GUI.this);
    }

    private void initFrame() {
        loadImages();

        frame = new JFrame(hra.toString());

        container = new JPanel(new GridLayout(1, 2, 6, 6));
        container.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        //prava strana formulare
        panelPlanku = new PanelPlanku(planekURL);

        //levyContainer => panel batohu + vystup + vstup        
        levyContainer = new JPanel();
        levyContainer.setLayout(new BoxLayout(levyContainer, BoxLayout.Y_AXIS));
        levyContainer.setPreferredSize(new Dimension(550, 550));

        lblZadnyBatoh = new JLabel("nemate zadny batoh", JLabel.CENTER);
        panelBatohu = new JPanel(new GridLayout(1, 1, 10, 10));
        panelBatohu.setPreferredSize(new Dimension(550, 75));
        panelBatohu.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        panelBatohu.setAlignmentY(Component.TOP_ALIGNMENT);
        panelBatohu.add(lblZadnyBatoh);

        txtAreaVystup = new JTextArea(40, 20);
        JScrollPane scrollAreaVystup = new JScrollPane(txtAreaVystup);
        scrollAreaVystup.setPreferredSize(new Dimension(550, 400));
        scrollAreaVystup.setAlignmentY(Component.CENTER_ALIGNMENT);
        txtAreaVystup.setFocusable(false);
        txtAreaVystup.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15));
        txtAreaVystup.setHighlighter(null);
        txtAreaVystup.setEditable(false);

        txtFieldVstup = new JTextField(20);
        txtFieldVstup.setEnabled(false);
        txtFieldVstup.setBorder(BorderFactory.createCompoundBorder(
                txtFieldVstup.getBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        txtFieldVstup.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        txtFieldVstup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                wakeUp();
            }
        });

        panelVystupu = new JPanel();
        panelVystupu.setLayout(new BorderLayout());
        panelVystupu.setPreferredSize(new Dimension(550, 40));
        panelVystupu.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        panelVystupu.add(txtFieldVstup, BorderLayout.NORTH);

        levyContainer.add(panelBatohu);
        levyContainer.add(scrollAreaVystup);
        levyContainer.add(Box.createRigidArea(new Dimension(0, 20)));
        levyContainer.add(txtFieldVstup);
        levyContainer.add(Box.createRigidArea(new Dimension(0, 10)));

        container.add(levyContainer);
        container.add(panelPlanku);

        frame.add(container);
        frame.pack();
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        try {
            logURL = new URL("file", "", Hra.PATH_LOGS_FILE);
        } catch (MalformedURLException ex) {
        }

        if (null == planekURL) {
            logger.log(Level.WARNING, "Planek hry nebyl nalezen!");
        } else if (null == napovedaURL) {
            logger.log(Level.WARNING, "Soubor s napovedou nebyl nalezen!");
        }

        oknoNapovedy = new DialogOkno(frame, "Napoveda | " + hra.toString(),
                napovedaURL);
        oknoLogu = new DialogOkno(frame, "Logovani | " + hra.toString(),
                logURL);

        initMenuBar();
        frame.setJMenuBar(menuBar);
        frame.setVisible(true);
    }

    private void initMenuBar() {
        menuBar = new JMenuBar();
        menuBar.setFocusable(false);
        menuBar.setBackground(new Color(244, 244, 244));

        mSoubor = new JMenu("Soubor");
        mNapoveda = new JMenu("Napoveda");

        mSoubor.setEnabled(false);
        mSoubor.setMnemonic(KeyEvent.VK_S);
        mSoubor.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        mNapoveda.setMnemonic(KeyEvent.VK_N);
        mNapoveda.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        JMenuItem novaHraIt = new JMenuItem(" >  NOVA HRA");
        JMenuItem exitIt = new JMenuItem(" >  Exit");
        JMenuItem pomocIt = new JMenuItem(" >  Pomoc");
        JMenuItem logovaniIt = new JMenuItem(" >  Logovani");
        JMenuItem oProgramuIt = new JMenuItem(" >  O Programu");

        novaHraIt.setBorder(mSoubor.getBorder());
        exitIt.setBorder(mSoubor.getBorder());
        pomocIt.setBorder(mNapoveda.getBorder());
        logovaniIt.setBorder(mNapoveda.getBorder());
        oProgramuIt.setBorder(mNapoveda.getBorder());

        novaHraIt.addActionListener(new NovaHra());
        exitIt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        pomocIt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                oknoNapovedy.zobraz();
            }
        });
        logovaniIt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                oknoLogu.zobraz();
            }
        });
        oProgramuIt.addActionListener(new OProgramu());

        mSoubor.add(novaHraIt);
        mSoubor.addSeparator();
        mSoubor.add(exitIt);
        mNapoveda.add(pomocIt);
        mNapoveda.addSeparator();
        mNapoveda.add(logovaniIt);
        mNapoveda.addSeparator();
        mNapoveda.add(oProgramuIt);
        menuBar.add(mSoubor);
        menuBar.add(mNapoveda);
    }

    private void loadImages() {
        for (Scenar.VecVycet vec : Scenar.VecVycet.values()) {
            //vsechny obrazky veci musi byt JPEG _
            //s koncovkou 'jpg' psanou malymi pismeny
            URL imgURL = GUI.class.getResource("/images/"
                    + vec.toString() + ".jpg");

            if (imgURL != null) {
                String name =
                        vec.toString().replaceFirst("\\.[^\\.]+$", "");
                ImageIcon img = new ImageIcon(imgURL);
                ImageIcon newImg = new ImageIcon(
                        img.getImage().getScaledInstance(75, 75,
                        Image.SCALE_SMOOTH), name);

                imagesVec.put(name.toLowerCase(), newImg);
            }
        }
        if (imagesVec.isEmpty()) {
            logger.warning(String.format(
                    "%s%n%s%s",
                    "Nenacetly se zadne obrazky!",
                    "Chybejici nebo vadny archiv img.jar - ",
                    "prectete si soubor README.txt"));
        }
    }

    @Override
    public void setVystup(String vystup) {
        logger.info(String.format("{VYSTUP PROGRAMU}%n%s", vystup));
        txtAreaVystup.append(vystup);
        txtAreaVystup.append("\n");
        txtAreaVystup.setCaretPosition(txtAreaVystup.getDocument().getLength());
    }

    @Override
    public String getVstup() {
        return getVstup(prompt);
    }

    @Override
    public synchronized String getVstup(String vyzva) {
        txtAreaVystup.append("#\n");
        if (!vyzva.equals(prompt)) {
            logger.info(String.format("{VYSTUP PROGRAMU}%n%s", vyzva));
            txtAreaVystup.append(vyzva + "\n");
        }
        try {
            /**
             * Protoze hra samotna bezi ve smycce while, NEMOHOHU vratit vstup -
             * dokud ho opravdu neziskam
             */
            wait();

            String vstup = txtFieldVstup.getText();
            logger.info(String.format("{VSTUP UZIVATELE}%n%s", vstup));
            txtFieldVstup.setText("");

            return vstup;
        } catch (InterruptedException ex) {
            ex.printStackTrace(System.err);
        }

        return "";
    }

    @Override
    public final void attachObserver(Observer observer) {
        if (observer != null) {
            listeners.add(observer);
        }
    }

    @Override
    public final void detachObserver(Observer observer) {
        if (observer != null) {
            listeners.remove(observer);
        }
    }

    @Override
    public final void notifyObservers(UdalostHry udalost) {
        for (Observer o : listeners) {
            o.update(udalost);
        }
    }

    @Override
    public void update(UdalostHry udalost) {
        switch (udalost) {
            case ZMENIL_PROSTOR:
                String kdeJsem = hra.getHerniPlan().kdeJsem().toString();
                for (Scenar.ProstorVycet prostor : Scenar.ProstorVycet.values()) {
                    if (kdeJsem.equalsIgnoreCase(prostor.toString())) {
                        panelPlanku.prekresliPozici(prostor);
                        break;
                    }
                }
                break;
            case SEBRAL_VEC: //fall through
            case POLOZIL_VEC:
                updatePanelBatohu();
                break;
            case NOVAHRA:
                frame.setTitle(hra.toString());
                txtFieldVstup.setText("");
                txtAreaVystup.setText("");
                txtFieldVstup.setEnabled(true);
                mSoubor.setEnabled(true);
                updatePanelBatohu();
                if (!timer.isRunning()) {
                    timer.setInitialDelay(25);
                    timer.start();
                }
                break;
            case KONECHRY:
                timer.stop();
                wakeUp();
                txtFieldVstup.setEnabled(false);
                break;
        }
    }

    private void updatePanelBatohu() {
        panelBatohu.removeAll();

        VecBatoh batoh = hra.getHerniPlan().hlavniPostava().getBatoh();
        if (batoh != null) {
            for (String vec : batoh.getSeznVeci()) {
                vec = vec.toLowerCase();
                if (imagesVec.containsKey(vec)) {
                    panelBatohu.add(new JLabel(imagesVec.get(vec)));
                }
            }
        } else {
            panelBatohu.add(lblZadnyBatoh);
        }

        panelBatohu.revalidate();
        panelBatohu.repaint();
    }

    /**
     * Probudi vlakno tohoto objektu
     */
    private synchronized void wakeUp() {
        notifyAll();
    }
}
