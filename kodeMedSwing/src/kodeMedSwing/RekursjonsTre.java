package kodeMedSwing;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.*;
import java.util.Random;

/**
 * Dette er mitt forslag til et rekursivt tre/fraktal tre . Det er her tatt i bruk Swing, da det er dette jeg er best kjent med fra før av.
 * Applikasjonen genererer et fraktal tre basert på verdier bruker bestemmer. Det tas i bruk metoder fra java.awt/java.awt.geom for å generere  
 * streker, som etterhvert former dette treet. Treet kommer "ferdig" generert, da det er lagt inn noen standard verdier. Bruker kan bestemme lengde på greine, vinkel
 * og i hvor stor grad det skal forekomme tilfeldige anomaliteter ut fra instillingene brukeren selv velger. Det er ikke lagt til brukerbestemt tykkelse på treet
 * eller når treet skal avslutte, dette er faste verdier (litt som en beskyttelse for brukeren, da disse verdiene fort kan endres til å skape en stack-overflow).
 * 
 * Inspirasjon: https://www.youtube.com/watch?v=0jjeOYMjmDU&t=7s&ab_channel=TheCodingTrain
 * 
 * @author <strong>Eirik Sundbø, 258030; Zakharii Korol, 246978 </strong>
 *                 
 */

public class RekursjonsTre extends JPanel {

    private static final long serialVersionUID = 1L;
    
    /**
     *  Verdi for vinkel på "neste" gren, her med standard verdi 45 grader 
     */
    double vinkel = Math.PI / 4; 
    /**
     *  Start-lengde på stammen, denne reduseres for hver gren som tegnes
     */
    double length = 100; 
    /**
     *  Verdi for hvor mye tilfeldighet skal rå over genereringen
     */
    double randomValg = 0.0;
    /**
     *  java.util.random metode for å generere tilfeldige tall. 
     */
    Random random = new Random();

    public static void main(String[] args) {  
        RekursjonsTre rt = new RekursjonsTre();
        rt.startUI();
                
    }
    
  
    /**
     * Lager UI til applikasjon. Inneholder også lytte-metoder for å tegne treet ut fra brukerens valgte verdier. Inneholder ellers ingen andre funksjoner.
     */
    public void startUI() {
    	JFrame frame = new JFrame("RekursjonsTre");
        
        // Slider for å justere vinkelen
        JSlider vinkelSlider = new JSlider(0, 100, 25);
        vinkelSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                double sliderValue = vinkelSlider.getValue() / 100.0;
                vinkel = sliderValue * (Math.PI / 4);
                repaint();
            }
        });

        // Slider for å justere lengden
        JSlider lengdeSlider = new JSlider(10, 200, 100);
        lengdeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                length = lengdeSlider.getValue();
                repaint();
            }
        });

        // Slider for å justere graden av randomisering
        JSlider randomSlider = new JSlider(0, 100, 0);
        randomSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                randomValg = randomSlider.getValue() / 100.0;
                repaint();
            }
        });
        JLabel vinkelLabel = new JLabel("Vinkel");
        JLabel lengdeLabel = new JLabel("Lengde");
        JLabel randomLabel = new JLabel("Tilfeldighet");

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(6, 1));
        controlPanel.add(vinkelLabel);
        controlPanel.add(vinkelSlider);
        controlPanel.add(lengdeLabel);
        controlPanel.add(lengdeSlider);
        controlPanel.add(randomLabel);
        controlPanel.add(randomSlider);

        frame.setLayout(new BorderLayout());
        frame.add(this, BorderLayout.CENTER); 
        frame.add(controlPanel, BorderLayout.NORTH);
        frame.setSize(450, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
    /**
     * Kaller opp genereringen av treet og definerer båte tykkelse og plassering av strekene. Bruker av java.awt.Graphics og java.awt.Graphics2D for å
     * illustrere strekene.  	
     */
     protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // Bestemmer tykkelsen på streken, her satt til 2px
        g2.setStroke(new BasicStroke(2));
        // Sørger for at treet tegnes i midten av Jframe
        g2.translate(getWidth() / 2, getHeight());

        tegne(g2, length);
    }

    /**
     * Genererer et tre ved hjelp av rekursjon. Ved hjelp av AffineTransform, kan vi produsere to grener ut av en gren. I praksis tegner vi en gren til en side,
     * hopper tilbake til hvor vi "lagret" og tegner en gren til andre siden. Det er lagt inn en begrensing på treets grener, rekursjonen stopper når greinene
     * ikke er lengre enn "2" piksler (eller ihvertfall 2 i verdi, som skal representere px). 
     * 
     * 
     * @param g2, Graphics biblioteket for å lage streker.
     * @param length, definering av stammens lengde (og resten av treets lengde)
     */
    public void tegne(Graphics2D g2, double length) {
    	
    	/**
    	 *  Lagt en sperre på maks 30% variasjon for at treet ikke skal risikere å ta slutt etter første generering. Selvom verdien {@code randomValg}
    	 *  innehar maksimal tilfeldighet (1.0), så vil det fortsatt maks variere 30% fra forrige grein. Jeg startet lavt og syntes resultatene så best ut
    	 *  med 30%.
    	 */
    	double randomLength = length * (1 - randomValg * random.nextDouble() * 0.3);

    	Line2D.Double linje = new Line2D.Double(0, 0, 0, -randomLength);
    	g2.draw(linje);
    	g2.translate(0, -randomLength);
    	
    	// Generering foregår inntil de siste grenene er 2px i lengde
    	if (length > 2) {
    		
    		/**
    		 *  For at linja mi ikke bare utgreiener seg fra ei grein, bruker jeg AffineTransform til å huske hvor jeg var før jeg tegner ny linje. På denne måten
    		 *  kan jeg tegne linja til høyre, gå tilbake til midtpunktet og tegne linja til venstre. 
    		 */ 
    		AffineTransform originalTransform = g2.getTransform();
    		/**
    		 * Bestemmer hvordan formen av neste gren skal se ut. {@code randomValg} er standard 0. Dette er brukerstyrt, så det ganges med {@code random.nextDouble()}
    		 * for å bestemme hva slags tilfeldig variasjon neste generering får. Siden standard er 0 vil generering skje uten tilfeldigheter så lenge bruker ikke har 
    		 * valgt å endre på denne verdien.
    		 */
    		double randomVinkel = vinkel + randomValg * (random.nextDouble());  		
    		// Tegner første gren fra stammen
    		g2.rotate(randomVinkel);
    		tegne(g2, randomLength * 0.67); // ganger lengden med 0.67, dette er hva som gjør neste grein kortere for hver gang (33% kortere).
 
    		g2.setTransform(originalTransform);
    		// Kaller igjen, men denne gangen med -{@code vinkel} for å generere speilvendt.
    		randomVinkel = -vinkel + randomValg * (random.nextDouble());
    		// Tegner andre gren fra stammen
    		g2.rotate(randomVinkel);
    		tegne(g2, randomLength * 0.67);
    		
    		
    		g2.setTransform(originalTransform);
    	}
    }
}
