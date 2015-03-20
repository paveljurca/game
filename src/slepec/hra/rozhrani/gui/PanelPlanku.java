package slepec.hra.rozhrani.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import slepec.hra.Scenar;

/**
 * Panel s plankem hry pro zobrazovani aktualniho prostoru
 *
 * @author Pavel Jurca, xjurp20@vse.cz
 * @version 2
 */
public class PanelPlanku extends JPanel {

    private LblPlanek lblPlanek;
    private ImageIcon iconPlanek;
    private Scenar.ProstorVycet prostor;
    private boolean scaled; //adjust-size-for-the-first-time flag

    private class LblPlanek extends JLabel {

        public LblPlanek() {
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (prostor != null) {
                g.setColor(Color.YELLOW);
                g.fillRoundRect(
                        (int) Math.round(prostor.getXpomer()
                        * getSize().width),
                        (int) Math.round(prostor.getYpomer()
                        * getSize().height),
                        getSize().width / 20, getSize().height / 20,
                        10, 10);
            }
        }
    }

    public PanelPlanku(URL planekURL) {
        super(new GridLayout(1, 1, 0, 0));

        lblPlanek = new LblPlanek();
        add(lblPlanek);

        if (planekURL != null) {
            iconPlanek = new ImageIcon(planekURL);
        }
    }

    public void prekresliPozici(Scenar.ProstorVycet prostor) {
        this.prostor = prostor;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        //super.paintComponent(g);
        if (iconPlanek != null) {
            if (!scaled) {
                iconPlanek = new ImageIcon(
                        iconPlanek.getImage().getScaledInstance(
                        getSize().width, getSize().height,
                        Image.SCALE_SMOOTH), "planek hry");
                lblPlanek.setIcon(iconPlanek);
                scaled = true;
            }

            lblPlanek.repaint();
        }
    }
}
