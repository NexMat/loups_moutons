import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.MouseInputListener;

/**
 * L'univers est la classe interfaçant la représentation du modéle (le plateau) et la représentation graphique.
 * Elle gère donc l'affichage de l'univers de l'évolution au sein d'une Fenetre.
 * @author Paul Lenczner & Mathieu Vu
 *
 * @see Plateau
 */
public class Univers extends JPanel implements Deplacement, MouseInputListener, ActionListener {

	/**
	 * Variable pour l'enregistrement dans un fichier.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * La largeur en pixels d'une case du plateau sur l'écran.
	 */
	private static final int LARGEUR_CASE = 100;
	/**
	 * La hauteur en pixels d'une case du plateau sur l'écran.
	 */
	private static final int HAUTEUR_CASE = 100;
	/**
	 * Permet de savoir si la vitesse d'affichage des animations a été mise à jour ou non.
	 */
	private boolean changeDelay = false;
	/**
	 * Permet de savoir si l'univers est en pause ou non.
	 */
	private boolean modePause = false;
	/**
	 * Le délai entre chaque image de l'animation en ms.
	 */
	private int delay = 50;
	/**
	 * Le délai minimum entre chaque image de l'animation en ms.
	 */
	private int minDelay = 40;
	/**
	 * Référence vers la fenêtre contenant cet univers.
	 */
	private Fenetre fenetre;
	/**
	 * Référence vers le plateau que l'univers courant doit afficher.
	 */
	private Plateau plateau; 
	/**
	 * Le timer permettant la synchronisation et l'animation pour l'interface graphique.
	 */
	private Timer timer;
	/**
	 * Le nombre de tour affiché à chaque fois. Peut être augmenté pour accélérer l'univers.
	 */
	private int tourParAffichage = 1;
	/**
	 * Le nombre de tour maximum affiché à chaque fois. Peut être augmenté pour accélérer l'univers.
	 */
	private int maxTourParAffichage = 90;
	/**
	 * La variable permettant de savoir où en est l'affichage.
	 */
	private int refresh = 0;
	/**
	 * 
	 */
	private int absoluteRefresh = 1;
	/**
	 * 
	 */
	private int absoluteMaxRefresh = 30;
	
	/**
	 * La coordonnée en abscisse de la case du plateau actuellement affichée en haut à gauche de l'écran.
	 */
	private int origX = 0;
	/**
	 * La coordonnée en ordonnée de la case du plateau actuellement affichée en haut à gauche de l'écran.
	 */
	private int origY = 0;
	/**
	 * La hauteur de la mini-map en pixels.
	 */
	private	int miniMapHeight = 0;
	/**
	 * La largeur de la mini-map en pixels.
	 */
	private int miniMapWidth = 0;
	/**
	 * La coordonnée en pixels du coin supérieur gauche de la mini-map.
	 */
	private int csgMiniMap;
	/**
	 * La coordonnée en pixels du csh de la mini-map.
	 */
	private int cshMiniMap; 
	/**
	 * La taille en pixel de la longueur d'une case sur la minimap: 2 pixels.
	 */
	private final int echelleX = 2;
	/**
	 * La taille en pixel de la hauteur d'une case sur la minimap: 2 pixels.
	 */
	private final int echelleY = 2;
	/**
	 * La largeur en pixel de la minimap.
	 */
	private int sizeX = 0;
	/**
	 * La hauteur en pixel de la minimap.
	 */
	private int sizeY = 0;
	/**
	 * Le nombre de cases du plateau affiché à l'écran en largeur.
	 */
	private int fenetreX = 13;
	/**
	 * Le nombre de cases du plateau affiché à l'écran en hauteur.
	 */
	private int fenetreY = 7;
	/**
	 * Le buffer sur lequel l'image est temporairement construite avant d'être affichée.
	 */
	private BufferedImage bufferTmp 	= null;
	/**
	 * Le buffer sur lequel la mini-map est temporairement construite avant d'être affichée.
	 */
	private BufferedImage bufMiniMap    = null;

	/**
	 * @param p Référence vers le plateau à afficher.
	 * @param fenetre Référence vers la fenêtre contenant l'univers.
	 */
	public Univers(Plateau p, Fenetre fenetre) {

		super();

		this.setLayout(null);
		setDoubleBuffered(true);
		
		this.origX = 0;
		this.origY = 0;
		
		/* On initialise nos écouteurs de mouvements */
		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		/* On paramètre la fenêtre on on initialise le jeu */
		this.fenetre = fenetre;
		this.plateau = p;
		this.sizeX = Math.min(fenetreX, plateau.getM());
		this.sizeY = Math.min(fenetreY, plateau.getN());
		this.setPreferredSize(new Dimension (100 * this.sizeX, 100 * this.sizeY));

		/* Initialisation de la minimap à afficher */
		this.paintMiniMap();

		/* On initialise notre timer */
		timer = new Timer (this.delay, this);
		timer.start();
	}

	/**
	 * @param g Le graphics sur lequel on dessine.
	 * @param action L'action passée en argument permet de connaître les coordonnées sur le plateau sur lequel l'affichage se fait.
	 */
	public void updateExtra (Graphics g, Action action){

		int pX = (action.debut.getX() - origX) * LARGEUR_CASE;
		int pY = (action.debut.getY() - origY) * HAUTEUR_CASE;
		int indexExtra = (action.debut.getX() + action.debut.getY()) % Animation.extra.length;
		Image imageExtra = Animation.extra[indexExtra];
		g.drawImage(imageExtra, pX - 39, pY - 165, imageExtra.getWidth(null), imageExtra.getHeight(null), null);
	}

	/**
	 * Dessine le contenu de la case et de ses informations contenues dans action.
	 * @param g Le graphics sur lequel on dessine les objets.
	 * @param action L'action permet de connaître la position, quel animal (ou quel végétal) et son état à afficher.
	 */
	public void updateCase(Graphics g, Action action){
		
		int dx = 0, dy = 0, dX = 0, dY = 0;
		int frame = 0;

		int refresh = this.refresh;

		/* Grâce au paramétre action, on peut connaître la direction du mouvement, si mouvement il y a */
		if (action.bouge){

			     if (action.direction == HAUT)        { dY = -1; } 
			else if (action.direction == HAUT_DROITE) { dY = -1; dX = 1; }
			else if (action.direction == DROITE)      { dX = 1; }
			else if (action.direction == BAS_DROITE)  { dY = 1; dX = 1; }
			else if (action.direction == BAS)         { dY = 1; }
			else if (action.direction == BAS_GAUCHE)  { dY = 1; dX = -1; }
			else if (action.direction == GAUCHE)      { dX = -1; }
			else if (action.direction == HAUT_GAUCHE) { dY = -1; dX = -1; }

			dy = dY * refresh;
			dx = dX * refresh;
		}
		
		/* Réglage des décalages entre les images d'attaques ou déplacement...*/
		int decX = 0;
		int decY = 0;
		if (action.loup && action.bouge && action.mange){

			if      (action.direction == DROITE)      { decX = -12; decY = -10; }
			else if (action.direction == GAUCHE)      { decX = -15; decY = -8;  }
			else if (action.direction == HAUT_DROITE) { decX = -10; decY = -10; }
			else if (action.direction == HAUT_GAUCHE) { decX = -20; decY = -10; }
			else if (action.direction == HAUT)        { decX = -20; decY = -10; }
			else if (action.direction == BAS_DROITE)  { decX = -14; decY = -8;  }
			else if (action.direction == BAS_GAUCHE)  { decX = -20; decY = -7;  }
			else if (action.direction == BAS)         { decX = -20; decY = -7;  }

		} else if (action.loup && action.bouge) {

			if      (action.direction == DROITE)      { decX = -1; decY = 7;  }
			else if (action.direction == GAUCHE)      { decX = 0;  decY = 5;  }
			else if (action.direction == HAUT_DROITE) { decX = -3; decY = 8;  }
			else if (action.direction == HAUT_GAUCHE) { decX = 3;  decY = 12; }
			else if (action.direction == HAUT)        { decX = 0;  decY = 0;  }
			else if (action.direction == BAS_DROITE)  { decX = -2; decY = 2;  }
			else if (action.direction == BAS_GAUCHE)  { decX = 0;  decY = 0;  }
			else if (action.direction == BAS)         { decX = 0;  decY = 4;  }

		} else if (action.loup && action.mange) {
			decX = -19;
			decY = -17;

		} else if (!action.loup && action.bouge && action.meurt) {

			if      (action.direction == DROITE)      { decX = 0; decY = 0; }
			else if (action.direction == GAUCHE)      { decX = 0; decY = 0; }
			else if (action.direction == HAUT_DROITE) { decX = 0; decY = 0; }
			else if (action.direction == HAUT_GAUCHE) { decX = 0; decY = 0; }
			else if (action.direction == HAUT)        { decX = 0; decY = 0; }
			else if (action.direction == BAS_DROITE)  { decX = 0; decY = 0; }
			else if (action.direction == BAS_GAUCHE)  { decX = 0; decY = 0; }
			else if (action.direction == BAS)         { decX = 0; decY = 0; }

		} else if (!action.loup && action.bouge) {
			if      (action.direction == DROITE)      { decX = -4; decY =  0; }
			else if (action.direction == GAUCHE)      { decX =  2; decY =  1; }
			else if (action.direction == HAUT_DROITE) { decX =  0; decY =  0; }
			else if (action.direction == HAUT_GAUCHE) { decX =  0; decY =  4; }
			else if (action.direction == HAUT)        { decX = -4; decY =  4; }
			else if (action.direction == BAS_DROITE)  { decX = -2; decY = -2; }
			else if (action.direction == BAS_GAUCHE)  { decX =  0; decY = -3; }
			else if (action.direction == BAS)         { decX = -4; decY = -4; }
		}

		int px = (action.debut.getX() - origX) * LARGEUR_CASE + dx * 5;
		int py = (action.debut.getY() - origY) * HAUTEUR_CASE + dy * 5;
		int pX = (action.debut.getX() - origX) * LARGEUR_CASE;
		int pY = (action.debut.getY() - origY) * HAUTEUR_CASE;

		/* Affichage de l'herbe */
		if (action.herbe && action.meurt) {

			double X = action.debut.getX();
			double Y = action.debut.getY();
			int imageHerbe = (int) (Math.cos(X*X + Y*X + X*Y) * 20.0 * Math.PI) % Animation.herbe.length;
			if (imageHerbe < 0) imageHerbe *= -1;
			g.drawImage(Animation.herbe[imageHerbe], pX, pY, LARGEUR_CASE, HAUTEUR_CASE, null);

		/* Affichage de la terre */
		} else if (action.herbe && !action.meurt){

				int imageTerre = (action.debut.getX() + action.debut.getY()) % Animation.terre.length;
				g.drawImage(Animation.terre[imageTerre], pX, pY, LARGEUR_CASE, HAUTEUR_CASE, null);
		
		/* On superpose les minéraux sur la terre */
		} else if (action.mineraux) {

				int imageTerre = (action.debut.getX() + action.debut.getY()) % Animation.terre.length;
				g.drawImage(Animation.terre[imageTerre], pX, pY, LARGEUR_CASE, HAUTEUR_CASE, null);

				if (action.mineraux && refresh > 20)
					g.drawImage(Animation.sels, pX, pY, LARGEUR_CASE, HAUTEUR_CASE, null);

		/* -------------------------- LOUP -------------------------- */
		} else if (action.loup) {

			/* Si le loup doit bouger et manger en même temps */
			if (action.bouge && action.mange && !modePause) {
				
				int length = Animation.loupMange[action.direction].length;

				/* Le loup marche d'abord un peu */
				if (refresh < 10) {
					length = Animation.loupMarche[action.direction].length;
					g.drawImage(Animation.loupMarche[action.direction][refresh % length], px, py, null);
					
				/* Ensuite il mange le mouton */
				} else if (refresh < 10 + length) {

					int pXlocal = pX + dX * 5 * 10 + decX;
					int pYlocal = pY + dY * 5 * 10 + decY;
					int localRefresh = refresh - 10;
					g.drawImage(Animation.loupMange[action.direction][localRefresh], pXlocal, pYlocal, null);

				/* Le loup continue sa route */
				} else {

					int localRefresh = refresh - 10 - length;
					length = Animation.loupMarche[action.direction].length;
					int pXlocal = pX + dX * 5 * (10 + localRefresh);
					int pYlocal = pY + dY * 5 * (10 + localRefresh);
					g.drawImage(Animation.loupMarche[action.direction][localRefresh % length], pXlocal, pYlocal, null);
				}

			/* Le loup bouge puis attend */
			} else if (action.bouge && !modePause) {

				if (refresh < 20) {
					int length = Animation.loupMarche[action.direction].length;
					g.drawImage(Animation.loupMarche[action.direction][refresh % length], px, py, null);

				} else {
					int length = Animation.loupRien[action.direction].length;
					int pXlocal = pX + dX * 20 * 5 + decX;
					int pYlocal = pY + dY * 20 * 5 + decY;
					g.drawImage(Animation.loupRien[action.direction][refresh % length], 
							pXlocal, pYlocal, null);
				}

			/* Loup mange en attendant que le mouton s'approche */
			} else if (action.mange && !modePause) {

				int length = Animation.loupMange[action.direction].length;

				if (refresh >= 15 && refresh < 15 + length) {

					int localRefresh = refresh - 15;
					g.drawImage(Animation.loupMange[action.direction][localRefresh % length], 
							pX + decX, pY + decY, null);
				} else {

					length = Animation.loupRien[action.direction].length;
					g.drawImage(Animation.loupRien[action.direction][refresh % length], pX, pY, null);
				}

			/* Quand le loup meurt */
			} else if (action.meurt && !modePause) {

				int frameMax = Animation.loupMeurt[action.direction].length - 1;
				if (refresh >= frameMax) frame = frameMax;
				else frame = refresh;

				if (!action.animalMineraux || refresh <= 20)
					g.drawImage(Animation.loupMeurt[action.direction][frame], px, py, null);

			/* Quand le loup ne fait rien */
			} else {

				g.drawImage(Animation.loupRien[action.direction][refresh % Animation.loupRien[action.direction].length],
						pX, pY, null);
			}

		/* ------------------------ MOUTON ------------------------ */
		} else if (!action.loup) {

			/* Le mouton se déplace mais meurt à l'arrivé */
			if (action.bouge && action.meurt && !modePause) {

				int length = Animation.moutonMeurt[action.direction].length;
				/* Le mouton marche d'abord un peu */
				if (refresh < 15) {

					length = Animation.moutonMarche[action.direction].length;
					g.drawImage(Animation.moutonMarche[action.direction][refresh % length], px, py, null);
					
				/* Ensuite le mouton se fait tuer par le loup */
				} else if (refresh < 15 + length) {

					int pXlocal = pX + dX * 5 * 15 + decX;
					int pYlocal = pY + dY * 5 * 15 + decY;
					int localRefresh = refresh - 15;
					g.drawImage(Animation.moutonMeurt[action.direction][localRefresh], pXlocal, pYlocal, null);

				/* On reste sur la dernière frame du mouton mort */
				} else {

					int pXlocal = pX + dX * 5 * 15 + decX;
					int pYlocal = pY + dY * 5 * 15 + decY;
					int frameMax = Animation.moutonMeurt[action.direction].length - 1;
					g.drawImage(Animation.moutonMeurt[action.direction][frameMax], pXlocal, pYlocal, null);
				}

			/* Le mouton se déplace et attend */
			} else if (action.bouge && !modePause) {

				if (refresh < 20) {
					int length = Animation.moutonMarche[action.direction].length;
					g.drawImage(Animation.moutonMarche[action.direction][refresh % length], px, py, null);

				} else {
					int length = Animation.moutonRien[action.direction].length;
					int pXlocal = pX + dX * 20 * 5 + decX;
					int pYlocal = pY + dY * 20 * 5 + decY;
					g.drawImage(Animation.moutonRien[action.direction][refresh % length], 
							pXlocal, pYlocal, null);
				}

			/* Le mouton meurt */
			} else if (action.meurt && !modePause) {

				/* Le mouton attend de se faire attaquer avant de mourir */
				if (!action.animalMineraux) {
					if (refresh < 12) {
						int length = Animation.moutonRien[action.direction].length;
						g.drawImage(Animation.moutonRien[action.direction][refresh % length], px, py, null);
					} else  {
						int localRefresh = refresh - 12;
						int frameMax = Animation.moutonMeurt[action.direction].length - 1;
						if (localRefresh >= frameMax) frame = frameMax;
						else frame = localRefresh;
						g.drawImage(Animation.moutonMeurt[action.direction][frame], px, py, null);
					}

				/* Le mouton meurt puis n'affiche plus son cadavre pour observer les sels mineraux */
				} else {
				
					if (refresh <= 20) {
						int frameMax = Animation.moutonMeurt[action.direction].length - 1;
						if (refresh >= frameMax) frame = frameMax;
						else frame = refresh;
						g.drawImage(Animation.moutonMeurt[action.direction][frame], px, py, null);
					}
				}

			/* Le mouton ne fait rien */
			} else {
				g.drawImage(Animation.moutonRien[action.direction][refresh % Animation.moutonRien[action.direction].length],
						pX, pY, null);
			} 
		}
	}

	/**
	 * @param g Le graphics sur lequel on dessine.
	 * @param gBuffer Le graphics temporaire sur lequel l'image est calculée avant d'être affichée.
	 */
	public void paintGame(Graphics g, Graphics gBuffer) {

		/* Calcul des coordonnées de fin de l'univers affiché à l'écran */
		int endOrigX = Math.min(origX + sizeX, plateau.getM() - 1);
		int endOrigY = Math.min(origY + sizeY, plateau.getN() - 1);

		/* On colle le sol */
		for (int j = origX ; j <= endOrigX ; j++) {
			for (int i = origY ; i <= endOrigY ; i++) {

				Plateau.Position pos = new Plateau.Position(j, i);
				Action action = this.plateau.getCase(pos).getActionVegetal();
				if (action != null)
					this.updateCase(gBuffer, action);
			}
		}

		/* On colle les animaux */
		for (int j = origX ; j <= endOrigX ; j++) {
			for (int i = origY ; i <= endOrigY ; i++) {

				Plateau.Position pos = new Plateau.Position(j, i);
				Action action = this.plateau.getCase(pos).getActionAnimal();
				if (action != null)
					this.updateCase(gBuffer, action);

				/* On colle en même temps les bébés */
				action = this.plateau.getCase(pos).getActionAnimalBebe();
				if (action != null)
					this.updateCase(gBuffer, action);
			}
		}

		/* On colle les extras */
		for (int j = origX ; j <= endOrigX ; j++) {
			for (int i = origY ; i <= endOrigY ; i++) {

				Plateau.Position pos = new Plateau.Position(j, i);
				Action action = this.plateau.getCase(pos).getActionExtra();
				if (action != null)
					this.updateExtra(gBuffer, action);
			}
		}
	}

	/**
	 * Permet d'afficher, en haut à gauche, une petite fenêtre donnant des informations relatives au jeu
	 * @param g
	 */
	public void paintInformations (Graphics g) {

        Color couleur = new Color(0, 0, 0, 100);
        g.setColor(couleur);
        g.fillRect(10, 10, 155, 48);

        String sLoup = "loups", sMouton = "moutons", sSprite = "sprite";
        if (this.plateau.getNombreLoup() <= 1) sLoup = "loup";
        if (this.plateau.getNombreMouton() <= 1) sMouton = "mouton";
        if (absoluteRefresh > 1) sSprite = "sprites";

        g.setFont(new Font("TimesRoman", Font.PLAIN, 10));
        g.setColor(Color.white);
        g.drawString("fraps : " + 1000 / this.delay + ", " + sSprite + " : " + this.absoluteRefresh +
        		", inc : " + this.tourParAffichage, 12, 20);
        g.drawString("hauteur : " + this.plateau.getN() + ", largeur : " + this.plateau.getM(), 12, 32);
        g.drawString(sLoup + " : " + this.plateau.getNombreLoup() + 
        		", " + sMouton + " : " + this.plateau.getNombreMouton(), 12, 44);
        g.drawString("tour : " + this.plateau.getNumTour(), 12, 56);

	}
	
	/**
	 * Permet d'afficher en haut à droite une représentation plus condensée de la carte en entier.
	 */
	public void paintMiniMap() {
		
		Color couleur;

		/* Calcul des dimensions et du positionnement de la minimap */
		this.miniMapHeight = this.plateau.getN() * echelleY;
		this.miniMapWidth  = this.plateau.getM() * echelleX;
		this.csgMiniMap = 100 * (this.sizeX) - (plateau.getM() * this.echelleX);
		this.cshMiniMap = 0;

		/* Création d'un BufferedImage où la minimap est dessinée */
		if (this.bufMiniMap == null) 
			this.bufMiniMap = (BufferedImage) new BufferedImage(miniMapWidth, miniMapHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics g = this.bufMiniMap.createGraphics();
		
		/* On dessine le contenu de la minimap */
		for (Plateau.Cases[] clist : this.plateau.getCases()) {
			for (Plateau.Cases c : clist) {

				if (c.isLoup()){
					// Couleur noire pour le loup
					couleur = new Color(0, 0, 0, 255);

				} else if(c.isMouton()){
					// Couleur blanche pour le mouton
					couleur = new Color(255, 255, 255, 255);

				} else if (c.isExtra()){

					// Couleur vert foncé pour les extras
					couleur = new Color(0, 50, 0, 255);

				} else if (c.isHerbe()) {

					// Couleur verte pour de l'herbe ou de la terre */
					couleur = new Color(0, 100, 0, 255);

				} else {

					// Couleur marron pour la terre
					couleur = new Color(110, 50, 0, 255);
				}

				g.setColor(couleur);
				g.fillRect(c.getPosition().getX() * echelleX, c.getPosition().getY() * echelleY, echelleX, echelleY);
			}
		}
	}

	/**
	 * @param g Le graphics sur lequel on dessine le rectangle qui permet de situer la position courante de l'univers sur la minimap.
	 */
	public void paintFocusMinimap(Graphics g) {
		
		/* Carré visible à l'écran */
		Color couleur = new Color(255, 0, 0, 255);
		g.setColor(couleur);
		g.drawLine(origX * echelleX + csgMiniMap, origY * echelleY + this.cshMiniMap, (origX + sizeX) * echelleX + csgMiniMap, origY * echelleY + this.cshMiniMap);
		g.drawLine(origX * echelleX + csgMiniMap, origY * echelleY + this.cshMiniMap, origX * echelleX + csgMiniMap, (origY + sizeY) * echelleY + this.cshMiniMap);
		g.drawLine((origX + sizeX) * echelleX + csgMiniMap, origY * echelleY + this.cshMiniMap, (origX + sizeX) * echelleX + csgMiniMap, (origY + sizeY) * echelleY + this.cshMiniMap);
		g.drawLine(origX * echelleX + csgMiniMap, (origY + sizeY) * echelleY + this.cshMiniMap, (origX + sizeX) * echelleX + csgMiniMap, (origY + sizeY) * echelleY + this.cshMiniMap);
		
		/* On fait un petit encadrement */
		couleur = new Color(0, 0, 0, 128);
		g.setColor(couleur);
		g.fillRect(this.csgMiniMap - 5, 0, 5, (plateau.getN()) * echelleY + 1);
		g.fillRect(this.csgMiniMap - 5, (plateau.getN()) * echelleY, plateau.getM() * echelleX + 5, 5);
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g){
		super.paint(g);

		/* Initialisation du système de double buffering */
		BufferedImage buffer = (BufferedImage) new BufferedImage (this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics gBuffer = buffer.createGraphics();

		/* On dessine les éléments du jeu sur notre buffer temporaire */
		paintGame(g, gBuffer);

		/* On mémorise notre buffer pour la première fois */
		if (bufferTmp == null)
			this.bufferTmp = buffer;
		/* On recolle sur notre buffer temporaire, ce que l'on a réussit à copier */
		else {
			Graphics gBufferTmp = bufferTmp.createGraphics();

			/* On colle la carte du jeu */
			gBufferTmp.drawImage(buffer, 0, 0, this);

			/* On efface les petits artefacts de déplacement */
			gBufferTmp.setColor (getBackground());
			gBufferTmp.clearRect(100 * (this.sizeX), 0, 100, 100 * (this.sizeY + 1));
			gBufferTmp.fillRect (100 * (this.sizeX), 0, 100, 100 * (this.sizeY + 1));
			gBufferTmp.clearRect(0, 100 * (this.sizeY), 100 * (this.sizeX + 1), 100);
			gBufferTmp.fillRect (0, 100 * (this.sizeY), 100 * (this.sizeX + 1), 100);

			/* On colle une petite fenêtre d'informations sur la carte */
			paintInformations(gBufferTmp);

			/* On dessine la minimap */
			gBufferTmp.drawImage(this.bufMiniMap, this.csgMiniMap, this.cshMiniMap, this);
			paintFocusMinimap(gBufferTmp);
		}

		/* On colle notre tampon */
		g.drawImage(bufferTmp, 0, 0, this);

		Toolkit.getDefaultToolkit().sync();
		g.dispose();

	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		/* Si le tour doit changer on remet les compteurs à zéro */
		if (this.refresh >= this.absoluteMaxRefresh && !this.plateau.isUniversMort()) {


			/* On charge (une ou plusieurs fois) le nouveau tour */
			try {

				for (int i = 0 ; i < this.tourParAffichage ; i++) {
					this.plateau.nouveauTour();
				}

			} catch (ActionFailureException ex){
				this.fenetre.createErrBox("Erreur lors du nouveau tour.", "ActionFailureException");
				ex.printStackTrace();
				System.exit(0);
			}

			/* Ré-initialisation de la minimap */
			this.paintMiniMap();

			/* On redemarre le compteur de frame */
			this.refresh = 0;
		}

		/* On configure le delai du timer */
		if (delay <= 0)
			timer.setDelay(0);
		else
			timer.setDelay(delay);

		/* On redessine la fenêtre de jeu */
	    repaint();

		/* On fait en sorte qu'au prochain appel de paint on incrémente le compteur d'animation */
	    if (!modePause)
	    	this.refresh = Math.min (this.absoluteMaxRefresh, this.refresh + absoluteRefresh);
	    else
			refresh = (refresh + 1) % 6;
	}

	/**
	 * Permet de connaître le délai entre chaque image de l'animation.
	 * @return 
	 */
	public boolean getChangeDelay(){
		return this.changeDelay;
	}

	/**
	 * Permet de changer le délai entre chaque image de l'animation.
	 * @param changeDelay
	 */
	public void setChangeDelay(boolean changeDelay){
		this.changeDelay = changeDelay;
	}

	/**
	 * Permet de changer la valeur qui situe où en est l'affichage.
	 * @param refresh
	 */
	public void setRefresh(int refresh) {

		this.absoluteRefresh = refresh;
	}

	/**
	 * Permet de récupérer la valeur actuelle de l'étape d'affichage.
	 * @return Le numéro de l'étape de l'affichage.
	 */
	public int getRefresh() {
		return this.absoluteRefresh;
	}
	
	/**
	 * @return La valeur maximum de refresh
	 */
	public int getMaxRefresh() {
		return this.absoluteMaxRefresh;
	}

	/**
	 * @return La valeur minimale du temps de délai minimum.
	 */
	public int getMinDelay() {
		return this.minDelay;
	}

	/**
	 * @return La valeur actuelle du délai d'affichage.
	 */
	public int getDelay(){
		return this.delay;
	}
	
	/**
	 * @return Le nombre de tour à afficher.
	 */
	public int getTourParAffichage() {
		return this.tourParAffichage;
	}
	
	/**
	 * @return Le nombre maximum de tour à afficher.
	 */
	public int getMaxTourParAffichage() {
		return this.maxTourParAffichage;
	}
	
	/**
	 * @param n Le nombre de tour à afficher.
	 */
	public void setTourParAffichage(int n) {
		this.tourParAffichage = n;
	}

	/**
	 * @param delay Le délai à actualiser.
	 */
	public void setDelay(int delay){
		if (delay <= minDelay)
			this.delay = minDelay;
		else
			this.delay = delay;
	}
	
	/**
	 * @param pause Met en pause(true) ou en lecture (false).
	 */
	public void setPause (boolean pause) {
	
		this.modePause = pause;
	}
	
	/**
	 * @return Le plateau couramment affiché par l'univers.
	 */
	public Plateau getPlateau(){
		return this.plateau;
	}
	
	/**
	 * @return Le timer qui permet la synchronisation et l'affichage de l'univers.
	 */
	public Timer getTimer(){
		return this.timer;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		
		if (x - (fenetreX / 2) >= this.csgMiniMap && x - (fenetreX / 2) <= this.csgMiniMap + miniMapWidth
				&& y - (fenetreY / 2) >= this.cshMiniMap && y - (fenetreY / 2) <= this.cshMiniMap + miniMapHeight) {
			this.origX = Math.min((x - ((fenetreX / 2) * echelleX) - this.csgMiniMap) / echelleX, this.plateau.getM() - this.sizeX); 
			this.origX = Math.max(this.origX, 0);
			this.origY = Math.min((y - ((fenetreY / 2) * echelleY)- this.cshMiniMap) / echelleY, this.plateau.getN() - this.sizeY);
			this.origY = Math.max(this.origY, 0);
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		
		if (x - (fenetreX / 2) >= this.csgMiniMap && x - (fenetreX / 2) <= this.csgMiniMap + miniMapWidth
				&& y - (fenetreY / 2) >= this.cshMiniMap && y - (fenetreY / 2) <= this.cshMiniMap + miniMapHeight) {
			this.origX = Math.min((x - ((fenetreX / 2) * echelleX) - this.csgMiniMap) / echelleX, this.plateau.getM() - this.sizeX); 
			this.origX = Math.max(this.origX, 0);
			this.origY = Math.min((y - ((fenetreY / 2) * echelleY)- this.cshMiniMap) / echelleY, this.plateau.getN() - this.sizeY);
			this.origY = Math.max(this.origY, 0);
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
	}
}
