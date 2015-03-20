import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

/**
 * La classe Fenetre hérite de JFrame. Elle gère l'interface graphique utilisateur.
 * @author Lenczner Paul & Mathieu Vu
 *
 */
public class Fenetre extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Référence vers les différentes images nécessaires à l'animation.
	 * @see Animation
	 */
	private Animation animation;
	/**
	 * Référence vers l'univers à afficher.
	 * @see Univers
	 */
	private Univers univers;
	/**
	 * La barre de menu associée à la fenêtre.
	 */
	private BarreDeMenu barreDeMenu;
	
	/**
	 * Constructeur de la classe Fenetre: crée une JFrame et met en place les éléments de l'UI.
	 */
	public Fenetre(){
		super("Loups vs. Moutons v0.9");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		
		/* Initialisation de la barre de menu */
		this.barreDeMenu = new BarreDeMenu(this);
		this.setJMenuBar(barreDeMenu);

		/* Chargement des images */
		try {
			this.animation = new Animation();
			animation.chargeAllImage();

		} catch (IOException e){
			e.printStackTrace();
			this.createErrBox("Erreur lors du chargement des images", "IOException");
		}
		
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	/**
	 * @param plateau référence vers le plateau de jeu dont dépend l'univers
	 */
	public Fenetre(Plateau plateau) {
		this();

		univers = new Univers(plateau, this);
		this.add(univers);
		this.barreDeMenu.enableButton();
		this.barreDeMenu.enableMenu();
	}
	
	/**
	 * Classe interne permettant d'initialiser les composants de la barre de menu.
	 * @author Lenczner Paul & Mathieu Vu
	 *
	 */
	public class BarreDeMenu extends JMenuBar {
		
		/**
		 * Le champ pour l'enregistrement des données dans un fichier.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Référence vers la fenêtre qui contiendra la BarreDeMenu.
		 */
		private Fenetre fenetre;
		
		/**
		 * Variable permettant de savoir si la partie est en pause ou pas. (En pause, onPause = true)
		 */
		public boolean onPause = false;

		/**
		 * Les boutons permettant de gérer l'affichage et l'animation de l'univers.
		 * pauseBut: met en pause ou en lecture.
		 * slowBut, fastBut: respectivement ralentit et accélére les animations de l'univers.
		 */
		private JButton pauseBut, slowBut, fastBut;
		/**
		 * Le bouton de sauvegarde.
		 */
		private JMenuItem menuItemSave;
		/**
		 * Le sous-menu "Interaction" de la barre de menu.
		 */
		protected JMenu menuInteraction;
		
		/**
		 * Les images de boutons pauseBut, slowBut et fastBut;
		 */
		private ImageIcon icoPause, icoFaster, icoSlower, icoPlay;

		/**
		 * Met en place et affiche les composants de la barre de menu.
		 * @param fen Référence vers la fenêtre contenant la barre de menu.
		 */
		public BarreDeMenu(Fenetre fen) {

			super();
			
			this.fenetre = fen;
			
			/* Menu "Fichier" */
			JMenu menu = new JMenu("Fichier");
			menu.setMnemonic(KeyEvent.VK_F);
			this.add(menu);

			JMenuItem menuItem;
			
			/* MenuItem Nouveau */
			menuItem = new JMenuItem("Nouveau", KeyEvent.VK_N);
			menuItem.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e){
					new JNewBox(fenetre);
				}
				
			});
			menu.add(menuItem);

			/* MenuItem Ouvrir */
			menuItem = new JMenuItem("Ouvrir...", KeyEvent.VK_O);
			menuItem.addActionListener(new ActionListener() {
				
				/* (non-Javadoc)
				 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
				 */
				public void actionPerformed(ActionEvent e){
					
					File file;
					Plateau plateau;
					ObjectInputStream in;

					/* Ouverture de la boîte de dialogue "Ouvrir" */
					JFileChooser fc = new JFileChooser();
					/* Ne prend en compte que les fichiers d'extension ".lvm" */
					FileFilter filtre = new FileFilter(){

						public boolean accept(File f) {
							if (f.isDirectory()) return true;
							else if (f.getName().endsWith(".lvm")) return true;
							else return false;
						}

						public String getDescription() {
							return "Sauvegarde Loups vs Moutons (.lvm)";
						}
						
					};
					fc.setFileFilter(filtre);

					int ret = fc.showOpenDialog(fenetre);

					if (ret == JFileChooser.APPROVE_OPTION){
						file = fc.getSelectedFile();
					} else {
						createErrBox("Le fichier choisi n'a pas été correctement ouvert", "File selection error");
						return;
					}

					try {
						in = new ObjectInputStream(new FileInputStream(file));
						plateau = (Plateau) in.readObject();

						/* Suppression de l'univers précédent */
						if (univers != null){
							univers.getTimer().stop();
							fenetre.remove(univers);
						}
						
						/* Création d'un nouvel univers à partir de la sauvegarde ouverte */
						univers = new Univers(plateau, fenetre);
						fenetre.add(univers);
						enableButton();
						enableMenu();
						
						if (onPause == true) {
							if (univers != null)
								univers.setPause(true);
						}

						fenetre.pack();
						univers.setVisible(true);

					} catch (FileNotFoundException e1) {
						createErrBox("Le fichier choisi n'a pas été correctement ouvert", "File selection error");

					} catch (IOException e1) {
						createErrBox("Echec lors de la lecture du fichier", "File selection error");

					} catch (ClassNotFoundException e1) {
						createErrBox("Le fichier choisi ne contient pas de sauvegarde", "File selection error");
					}
					
				}
				
			});
			menu.add(menuItem);
			
			/* MenuItem Sauvegarder */
			menuItemSave = new JMenuItem("Sauvegarder...", KeyEvent.VK_S);
			menuItemSave.addActionListener(new ActionListener() {
				
				/* (non-Javadoc)
				 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
				 */
				public void actionPerformed(ActionEvent e){

					File file = null;
					ObjectOutputStream out;
					Plateau plateau = univers.getPlateau();
					
					/* Ouverture de la boîte de dialogue "Sauvegarder" */
					JFileChooser fc = new JFileChooser();
					/* Ne prend en compte que les fichiers d'extension ".lvm" */
					FileFilter filtre = new FileFilter(){

						public boolean accept(File f) {
							if (f.isDirectory()) return true;
							else if (f.getName().endsWith(".lvm")) return true;
							else return false;
						}

						public String getDescription() {
							return "Sauvegarde Loups vs Moutons (.lvm)";
						}
						
					};
					fc.setFileFilter(filtre);

					int ret = fc.showSaveDialog(fenetre);
					File file2;
					if (ret == JFileChooser.APPROVE_OPTION){
						file = fc.getSelectedFile();

						try {
							out = new ObjectOutputStream(new FileOutputStream(file));
							out.writeObject(plateau);
							out.close();
							
							/* Ajout de l'extension du fichier si ce n'est pas déjà fait */
							if (!file.getName().endsWith(".lvm")) {
								file2 = new File(file.toString() + ".lvm");
								file.renameTo(file2);
							}

						} catch (IOException e1) {
							createErrBox("Le fichier choisi n'a pas été correctement ouvert", "File selection error");

						}
					}
				}
			});
			menuItemSave.setEnabled(false);
			menu.add(menuItemSave);

			/* MenuItem Quitter */
			menuItem = new JMenuItem("Quitter", KeyEvent.VK_Q);
			menuItem.addActionListener(new ActionListener (){
				
				/* (non-Javadoc)
				 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
				 */
				public void actionPerformed(ActionEvent e){
					System.exit(0);
				}
				
			});
			menu.add(menuItem);

			
			/* Menu Interaction -------------------- */
			menuInteraction = new JMenu("Interaction");
			menuInteraction.setMnemonic(KeyEvent.VK_I);
			menuInteraction.setEnabled(false);
			this.add(menuInteraction);
			
			/* Sous-menuItem Parametres -----------------*/
			menuItem = new JMenuItem("Paramètres", KeyEvent.VK_P);
			menuItem.addActionListener(new ActionListener() {
				
				/* (non-Javadoc)
				 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
				 */
				public void actionPerformed(ActionEvent e){
					new JParamBox();
				}
				
			});
			menuInteraction.add(menuItem);

			/* Bouton Accelerer */
            icoFaster = new ImageIcon(getClass().getResource("/img/faster.png"));
			this.fastBut = new JButton(icoFaster);
			this.fastBut.setPreferredSize(new Dimension(20, 20));
			this.fastBut.setEnabled(false);
			this.fastBut.addActionListener(new ActionListener() {
				
				/* (non-Javadoc)
				 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
				 */
				public void actionPerformed(ActionEvent e){
					int delay      = univers.getDelay();
					int minDelay   = univers.getMinDelay();
					int refresh    = univers.getRefresh();
					int maxRefresh = univers.getMaxRefresh();
					int tourParAff = univers.getTourParAffichage();
					int maxTourPar = univers.getMaxTourParAffichage();

					/* Incrémentation du nombre de tour par affichage */
					if (refresh == maxRefresh) {

						if (tourParAff + 1 == maxTourPar) {
							fastBut.setEnabled(false);
						}
						
						univers.setTourParAffichage(tourParAff + 1);
						
					/* Accélération des animations */
					} else if (delay == minDelay) {
						
						if (refresh + 1 > maxRefresh / 2) 
							univers.setRefresh(maxRefresh);
						else
							univers.setRefresh(refresh + 1);
						
					/* On peut accélerer les images par secondes */
					} else if (delay >= minDelay) {
						univers.setDelay(delay - 10);
						univers.setChangeDelay(true);
					}
				}
				
			});
			fastBut.setMnemonic(KeyEvent.VK_PLUS);
			
			
			/* Bouton Ralentir */
            icoSlower = new ImageIcon(getClass().getResource("/img/slower.png"));
			this.slowBut = new JButton("", icoSlower);
			this.slowBut.setPreferredSize(new Dimension(20, 20));
			this.slowBut.setEnabled(false);
			this.slowBut.addActionListener(new ActionListener() {

				/* (non-Javadoc)
				 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
				 */
				public void actionPerformed(ActionEvent e) {

					int delay      = univers.getDelay();
					int refresh    = univers.getRefresh();
					int maxRefresh = univers.getMaxRefresh();
					int tourParAff = univers.getTourParAffichage();
	
					/* Décrémentation du nombre de tours par affichage */
					if (tourParAff != 1) {
						
						univers.setTourParAffichage(tourParAff - 1);

					/* Ralentissement des animations */
					} else if (refresh != 1) {
						
						if (refresh > maxRefresh / 2)
							univers.setRefresh(maxRefresh / 2);
						else 
							univers.setRefresh(refresh - 1);

					/* Ralentissement du nombre d'image par seconde */
					} else {

						univers.setDelay(delay + 10);
						univers.setChangeDelay(true);
					}

					fastBut.setEnabled(true);
				}
				
			});
			this.slowBut.setMnemonic(KeyEvent.VK_MINUS);
			this.add(slowBut);

			/* Bouton Play/Pause */
            icoPause  = new ImageIcon(getClass().getResource("/img/pause.png"));
            icoPlay   = new ImageIcon(getClass().getResource("/img/play.png"));
			this.pauseBut = new JButton("", icoPause);
			this.pauseBut.setPreferredSize(new Dimension(20, 20));
			this.pauseBut.setEnabled(false);
			this.pauseBut.addActionListener(new ActionListener(){
				
				/* (non-Javadoc)
				 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
				 */
				public void actionPerformed(ActionEvent e){
					if (!onPause){
						menuInteraction.setEnabled(true);
						pauseBut.setIcon(icoPlay);
						onPause = true;
						if (univers != null)
							univers.setPause(true);

					} else {
						menuInteraction.setEnabled(false);
						pauseBut.setIcon(icoPause);
						onPause = false;
						if (univers != null)
							univers.setPause(false);
					}
				}
				
			});
			this.pauseBut.setMnemonic(KeyEvent.VK_ENTER);
			this.add(this.pauseBut);
			
			/* Bouton avance rapide */
			this.add(fastBut);
			
			/* Menu Aide -------------------- */
			JMenu menuAide = new JMenu("Aide");
			menuAide.setMnemonic(KeyEvent.VK_D);
			this.add(menuAide);
			
			/* MenuItem Aide */
			menuItem = new JMenuItem("Aide");
			menuItem.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					try {

						File pdfFile = new File(getClass().getResource("/compterendu.pdf").getFile());

						if (Desktop.isDesktopSupported())
							Desktop.getDesktop().open(pdfFile);

						else
							System.out.println("Cf. compterendu.pdf");

					} catch (IOException exc) {
						System.out.println("Fichier \"compterendu.pdf\" introuvable");

					}
				}

			});
			menuAide.add(menuItem);

			/* MenuItem A Propos */
			menuItem = new JMenuItem("À propos");
			menuItem.addActionListener(new ActionListener(){
				
				/* (non-Javadoc)
				 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
				 */
				public void actionPerformed(ActionEvent e){
					/* Ouverture d'une boîte de dialogue */
					JOptionPane.showMessageDialog(fenetre,
							"Loups vs. Moutons v0.9\n"
							+ "Paul Lenczner & Mathieu Vu\n"
							+ "POO-IG 2014/2015",
							"À propos",
							JOptionPane.INFORMATION_MESSAGE);
				}

			});
			menuAide.add(menuItem);
			
		}
		
		/**
		 * Active le menu de sauvegarde lorsqu'un univers est en cours.
		 */
		private void enableMenu(){
			this.menuItemSave.setEnabled(true);
		}
		
		/**
		 * Permet d'activer les boutons de gestion de vitesse du jeu
		 */
		private void enableButton() {
			
			pauseBut.setEnabled(true);
			slowBut.setEnabled(true);
			fastBut.setEnabled(true);
		}
	}
	
	/**
	 * @return l'univers couramment affiché.
	 */
	public Univers getUnivers(){
		return this.univers;
	}
	
	
	/**
	 * Affiche une boîte de dialogue d'erreur.
	 * @param message Message d'erreur affiché par la boîte de dialogue.
	 * @param erreur Titre de la boîte de dialogue.
	 */
	public void createErrBox(String message, String erreur){
		JOptionPane.showMessageDialog(this, message, erreur, JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Classe interne initialisant les composants et leurs affichages dans les paramètres.
	 * @author Paul Lenczner & Mathieu Vu
	 *
	 */
	public class JParamBox extends JDialog implements ChangeListener {

		/**
		 * Variable pour l'enregistrement dans un fichier.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Le JSlider correspondant au cycle de reproduction d'un loup.
		 */
		private JSlider sliderCycleLoup;
		/**
		 * Le JLabel correspondant au cycle de reproduction d'un loup.
		 */
		private JLabel cycleLoupValue;
		/**
		 * Le JSlider correspondant au cycle de reproduction d'un mouton.
		 */
		private JSlider sliderCycleMouton;
		/**
		 * Le JLabel correspondant au cycle de reproduction d'un mouton.
		 */
		private JLabel cycleMoutonValue;

		/**
		 * Le numéro de la stratégie employée.
		 */
		private int strat;
		
		/**
		 * Constructeur de la boîte de dialogue des paramètres.
		 */
		public JParamBox(){
			super();
			
			Strategie strategie = univers.getPlateau().getStrategie();
			int nbStrat = 2;
			if (strategie instanceof StrategieNiveau1)
				nbStrat = 1;
			else if (strategie instanceof StrategieNiveau2)
				nbStrat = 2;
			else if (strategie instanceof StrategieNiveau3)
				nbStrat = 3;

			int cycleLoup       = univers.getPlateau().getCycleLoup();
			int cycleMouton	    = univers.getPlateau().getCycleMouton();

			this.setTitle("Paramètres");
			this.setPreferredSize(new Dimension(500, 500));
			this.setSize(new Dimension(500, 500));
			this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			/* On choisit un GridLayout */
			this.setLayout(new GridLayout(4, 3));
			GridBagConstraints gbc = new GridBagConstraints();

			/* Regroupe les stratégies */
			ButtonGroup radioStrats = new ButtonGroup();

			/* JRadio Stratégie 1 */
			JRadioButton radioStrat = new JRadioButton();
			radioStrat.setText("Stratégie 1");
			radioStrat.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e){
					strat = 1;
				}
				
			});

			radioStrats.add(radioStrat);
			if (nbStrat == 1) {
				radioStrat.setSelected(true);
				strat = 1;
			}
			this.add(radioStrat, gbc);

			/* JRadio Stratégie 2 */
			radioStrat = new JRadioButton();
			radioStrat.setText("Stratégie 2");
			radioStrat.addActionListener(new ActionListener(){
				
				public void actionPerformed(ActionEvent e){
					strat = 2;
				}
				
			});
			radioStrats.add(radioStrat);
			if (nbStrat == 2){
				radioStrat.setSelected(true);
				strat = 2;
			}
			this.add(radioStrat, gbc);

			/* JRadio Stratégie 3 */
			radioStrat = new JRadioButton();
			radioStrat.setText("Stratégie 3");
			radioStrat.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e){
					
				}
				
			});
			radioStrats.add(radioStrat);
			if (nbStrat == 3){
				radioStrat.setSelected(true);
				strat = 3;
			}
			this.add(radioStrat, gbc);

			/* Cycle de reproduction des brebis */
			JLabel nomSlider = new JLabel("Cycle des brebis");
			nomSlider.setToolTipText("Cycle des brebis");
			nomSlider.setHorizontalAlignment(JLabel.CENTER);
			this.add(nomSlider, gbc);
			
			sliderCycleMouton = new JSlider();
			sliderCycleMouton.setMaximum(6);
			sliderCycleMouton.setMinimum(1);
			sliderCycleMouton.setValue(cycleMouton);
			sliderCycleMouton.addChangeListener(this);
			this.add(sliderCycleMouton, gbc);

			cycleMoutonValue = new JLabel(Integer.toString(sliderCycleMouton.getValue()));
			cycleMoutonValue.setHorizontalAlignment(JLabel.CENTER);
			this.add(cycleMoutonValue, gbc);

			/* Cycle de reproduction des louves */
			nomSlider = new JLabel("Cycle des louves");
			nomSlider.setToolTipText("Cycle des louves");
			nomSlider.setHorizontalAlignment(JLabel.CENTER);
			this.add(nomSlider, gbc);

			sliderCycleLoup = new JSlider();
			sliderCycleLoup.setMaximum(10);
			sliderCycleLoup.setMinimum(1);
			sliderCycleLoup.setValue(cycleLoup);
			sliderCycleLoup.addChangeListener(this);
			this.add(sliderCycleLoup, gbc);

			cycleLoupValue = new JLabel(Integer.toString(sliderCycleLoup.getValue()));
			cycleLoupValue.setHorizontalAlignment(JLabel.CENTER);
			this.add(cycleLoupValue, gbc);

			/* Bouton Réinitialiser */
			JButton but1 = new JButton("Réinitialiser");
			but1.addActionListener(new ActionListener(){
			
				/* (non-Javadoc)
				 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
				 */
				public void actionPerformed(ActionEvent e){

					sliderCycleMouton.setValue(50);
					sliderCycleLoup.setValue(50);
					
				}
				
			});
			this.add(but1, gbc);

			/* Bouton Annuler */
			JButton but2 = new JButton("Annuler");
			but2.addActionListener(new ActionListener(){
				
				/* (non-Javadoc)
				 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
				 */
				public void actionPerformed(ActionEvent e){
					dispose();
				}
				
			});
			this.add(but2, gbc);

			/* Bouton Terminer */
			JButton but3 = new JButton("Terminer");
			but3.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e) {
					
					if (univers == null) return;

					if (strat == 1) {
						univers.getPlateau().setStrategie(new StrategieNiveau1());

					} else if (strat == 2) {
						univers.getPlateau().setStrategie(new StrategieNiveau2());

					} else if (strat == 3) {

						univers.getPlateau().setStrategie(new StrategieNiveau3());
					} else {
						createErrBox("Aucune stratégie sélectionné", "Erreur Stratégie");
						return;
					}
					
					/* Cycle de reproduction */
					univers.getPlateau().setCycleLoup(sliderCycleLoup.getValue());
					univers.getPlateau().setCycleMouton(sliderCycleMouton.getValue());
					
					dispose();
				}
				
			});
			this.add(but3, gbc);
			

			this.setLocationRelativeTo(null);
			this.setVisible(true);
			
		}

		/**
		 * @param e Change les valeurs de JLabel en fonction de la valeur du JSlider.
		 */
		@Override
		public void stateChanged(ChangeEvent e) {
			if (e.getSource() == sliderCycleMouton) {
				cycleMoutonValue.setText(Integer.toString(sliderCycleMouton.getValue()));

			} else if (e.getSource() == sliderCycleLoup) {
				cycleLoupValue.setText(Integer.toString(sliderCycleLoup.getValue()));
				
			}
		}
	}

	/**
	 * Initialise la boîte de dialogue pour la création d'un nouvel univers.
	 * @author Lenczner Paul & Mathieu Vu
	 *
	 */
	public class JNewBox extends JDialog implements ChangeListener {

		/**
		 * Variable pour l'enregistrement dans un fichier.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Modifie la longueur de la carte.
		 */
		private JSlider sliderLongueurMap;
		private JLabel longueurMapValue;
		/**
		 * Modifie la hauteur de la carte.
		 */
		private JSlider sliderHauteurMap;
		private JLabel hauteurMapValue;

		/**
		 * Modifie le pourcentage de loups.
		 */
		private JSlider sliderNbLoup;
		private JLabel nbLoupValue;
		/**
		 * Modifie le pourcentage de moutons.
		 */
		private JSlider sliderNbMouton;
		private JLabel nbMoutonValue;

		/**
		 * Modifie la durée d'un cycle de reproduction des loups.
		 */
		private JSlider sliderCycleLoup;
		private JLabel cycleLoupValue;
		/**
		 * Modifie la durée d'un cycle de reproduction des moutons.
		 */
		private JSlider sliderCycleMouton;
		private JLabel cycleMoutonValue;
		/**
		 * La numéro de la stratégie employée.
		 */
		private int strat = 2;

		/**
		 * Référence vers la fenêtre contenant cette boîte de dialogue.
		 */
		private Fenetre fenetre;
		
		/**
		 * Constructeur de la boîte de dialogue "Nouveau".
		 * @param fen Référence vers la fenêtre contenant cette boîte de dialogue.
		 */
		public JNewBox(Fenetre fen){
			super();
			
			this.fenetre = fen;

			this.setTitle("Nouveau");
			this.setPreferredSize(new Dimension(500, 300));
			this.setSize(new Dimension(500, 300));
			this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			/* On choisit un GridLayout */
			this.setLayout(new GridLayout(8,3));
			GridBagConstraints gbc = new GridBagConstraints();

			/* Regroupe les JRadio stratégies */
			ButtonGroup radioStrats = new ButtonGroup();

			/* Stratégie 1 */
			JRadioButton radioStrat = new JRadioButton();
			radioStrat.setText("Stratégie 1");
			radioStrat.addActionListener(new ActionListener(){
				
				public void actionPerformed(ActionEvent e){
					strat = 1;
				}
				
			});
			radioStrats.add(radioStrat);
			this.add(radioStrat, gbc);

			/* Stratégie 2 */
			radioStrat = new JRadioButton();
			radioStrat.setText("Stratégie 2");
			radioStrat.addActionListener(new ActionListener(){
				
				public void actionPerformed(ActionEvent e){
					strat = 2;
				}
				
			});
			radioStrat.setSelected(true);
			radioStrats.add(radioStrat);
			this.add(radioStrat, gbc);

			/* Stratégie 3 */
			radioStrat = new JRadioButton();
			radioStrat.setText("Stratégie 3");
			radioStrat.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e){
					strat = 3;
				}
				
			});
			radioStrats.add(radioStrat);
			this.add(radioStrat, gbc);
			
			/* Largeur de la carte */
			JLabel nomSlider = new JLabel("Largeur de la carte");
			nomSlider.setToolTipText("Largeur de la carte");
			nomSlider.setHorizontalAlignment(JLabel.CENTER);
			this.add(nomSlider, gbc);
			
			sliderLongueurMap = new JSlider();
			sliderLongueurMap.setMaximum(350);
			sliderLongueurMap.setMinimum(1);
			sliderLongueurMap.setValue(100);
			sliderLongueurMap.addChangeListener(this);
			this.add(sliderLongueurMap, gbc);
			
			longueurMapValue = new JLabel(Integer.toString(sliderLongueurMap.getValue()) + " cases");
			longueurMapValue.setHorizontalAlignment(JLabel.CENTER);
			this.add(longueurMapValue, gbc);

			/* Hauteur de la carte */
			nomSlider = new JLabel("Hauteur de la carte");
			nomSlider.setToolTipText("Hauteur de la carte");
			nomSlider.setHorizontalAlignment(JLabel.CENTER);
			this.add(nomSlider, gbc);
			
			sliderHauteurMap = new JSlider();
			sliderHauteurMap.setMaximum(350);
			sliderHauteurMap.setMinimum(1);
			sliderHauteurMap.setValue(100);
			sliderHauteurMap.addChangeListener(this);
			this.add(sliderHauteurMap, gbc);
			
			hauteurMapValue = new JLabel(Integer.toString(sliderHauteurMap.getValue()) + " cases");
			hauteurMapValue.setHorizontalAlignment(JLabel.CENTER);
			this.add(hauteurMapValue, gbc);

			/* Pourcentage de loups au départ sur la carte */
			nomSlider = new JLabel("Nombre loups");
			nomSlider.setToolTipText("Nombre loups");
			nomSlider.setHorizontalAlignment(JLabel.CENTER);
			this.add(nomSlider, gbc);
			
			sliderNbLoup = new JSlider();
			sliderNbLoup.setMaximum(100);
			sliderNbLoup.setMinimum(0);
			sliderNbLoup.setValue(5);
			sliderNbLoup.addChangeListener(this);
			this.add(sliderNbLoup, gbc);
			
			nbLoupValue = new JLabel(Integer.toString(sliderNbLoup.getValue()) + "%");
			nbLoupValue.setHorizontalAlignment(JLabel.CENTER);
			this.add(nbLoupValue, gbc);

			/* Pourcentage de moutons au départ sur la carte */
			nomSlider = new JLabel("Nombre moutons");
			nomSlider.setToolTipText("Nombre moutons");
			nomSlider.setHorizontalAlignment(JLabel.CENTER);
			this.add(nomSlider, gbc);
			
			sliderNbMouton = new JSlider();
			sliderNbMouton.setMaximum(100);
			sliderNbMouton.setMinimum(0);
			sliderNbMouton.setValue(5);
			sliderNbMouton.addChangeListener(this);
			this.add(sliderNbMouton, gbc);
			
			nbMoutonValue = new JLabel(Integer.toString(sliderNbMouton.getValue()) + "%");
			nbMoutonValue.setHorizontalAlignment(JLabel.CENTER);
			this.add(nbMoutonValue, gbc);

			/* Cycle de reproduction des brebis */
			nomSlider = new JLabel("Cycle des brebis");
			nomSlider.setToolTipText("Cycle des brebis");
			nomSlider.setHorizontalAlignment(JLabel.CENTER);
			this.add(nomSlider, gbc);
			
			sliderCycleMouton = new JSlider();
			sliderCycleMouton.setMaximum(6);
			sliderCycleMouton.setMinimum(1);
			sliderCycleMouton.setValue(1);
			sliderCycleMouton.addChangeListener(this);
			this.add(sliderCycleMouton, gbc);

			cycleMoutonValue = new JLabel(Integer.toString(sliderCycleMouton.getValue()));
			cycleMoutonValue.setHorizontalAlignment(JLabel.CENTER);
			this.add(cycleMoutonValue, gbc);

			/* Cycle de reproduction des louves */
			nomSlider = new JLabel("Cycle des louves");
			nomSlider.setToolTipText("Cycle des louves");
			nomSlider.setHorizontalAlignment(JLabel.CENTER);
			this.add(nomSlider, gbc);

			sliderCycleLoup = new JSlider();
			sliderCycleLoup.setMaximum(10);
			sliderCycleLoup.setMinimum(1);
			sliderCycleLoup.setValue(1);
			sliderCycleLoup.addChangeListener(this);
			this.add(sliderCycleLoup, gbc);

			cycleLoupValue = new JLabel(Integer.toString(sliderCycleLoup.getValue()));
			cycleLoupValue.setHorizontalAlignment(JLabel.CENTER);
			this.add(cycleLoupValue, gbc);

			/* Bouton Réinitialiser */
			JButton but1 = new JButton("Réinitialiser");
			but1.addActionListener(new ActionListener(){

				/* (non-Javadoc)
				 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
				 */
				public void actionPerformed(ActionEvent e){

					/* Remise aux valeurs initiales des JSliders */
					sliderHauteurMap.setValue(100);
					sliderLongueurMap.setValue(100);
					sliderNbLoup.setValue(5);
					sliderNbMouton.setValue(5);
					sliderCycleMouton.setValue(1);
					sliderCycleLoup.setValue(1);
				}
			});
			this.add(but1, gbc);

			/* Bouton Annuler */
			JButton but2 = new JButton("Annuler");
			but2.addActionListener(new ActionListener(){

				/* (non-Javadoc)
				 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
				 */
				public void actionPerformed(ActionEvent e){
					dispose();
				}

			});
			this.add(but2, gbc);

			/* Bouton Terminer */
			JButton but3 = new JButton("Terminer");
			but3.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent event){

					try {
						
						/* Taille de la map */
						int n = sliderHauteurMap.getValue();
						int m = sliderLongueurMap.getValue();

						/* Cycle de reprouduction */
						int cycleLoup = sliderCycleLoup.getValue();
						int cycleMouton = sliderCycleMouton.getValue();

						/* Nombre de loups et de moutons */
						double tmpLoup   = ((double) n) * ((double)m) * (((double)sliderNbLoup.getValue()) / 100);
						double tmpMouton = ((double) n) * ((double)m) * (((double)sliderNbMouton.getValue()) / 100);
						int nbLoups   = (int) Math.floor(tmpLoup);
						int nbMoutons = (int) Math.floor(tmpMouton);

						if (nbLoups + nbMoutons > n * m) {
							createErrBox("Trop de loups et de moutons", "Erreur répartition");
							return;
						}

						/* Choix de la stratégie */
						Strategie strategie = null;
						if (strat == 1) {
							strategie = new StrategieNiveau1();

						} else if (strat == 2) {
							strategie = new StrategieNiveau2();

						} else if (strat == 3) {
							strategie = new StrategieNiveau3();

						} else {
							createErrBox("Aucune stratégie sélectionné", "Erreur Stratégie");
							return;
						}
						
						/* Création d'un nouveau plateau avec les données fournies */
						Plateau plateau = new Plateau(n, m, nbMoutons, nbLoups, cycleMouton, cycleLoup, strategie);
						
						/* Arrêt de l'ancien univers */
						if (univers != null){
							univers.getTimer().stop();
							fenetre.remove(univers);
							//univers.getTimer().removeActionListener(univers);
						}

						/* Création d'un nouvel univers avec les données fournies */
						univers = new Univers(plateau, fenetre);
						fenetre.add(univers);
						fenetre.barreDeMenu.enableButton();
						fenetre.barreDeMenu.enableMenu();

						if (barreDeMenu.onPause == true) {
							if (univers != null)
								univers.setPause(true);
						}

						fenetre.pack();
						univers.setVisible(true); 

					} catch (LimiteAnimauxException e){
						System.out.println("LimiteAnimauxException: Trop d'animaux !");
						createErrBox("Le plateau ne peut pas contenir autant d'animaux", "LimiteAnimauxException");

					} catch (ActionFailureException e){
						System.out.println(e.getMessage());
					}
					
					dispose();

				}
			});
			this.add(but3, gbc);

			this.setLocationRelativeTo(null);
			this.setVisible(true);
		}

		/* (non-Javadoc)
		 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
		 */
		@Override
		public void stateChanged(ChangeEvent e) {
			if (e.getSource() == sliderLongueurMap){
				if (sliderLongueurMap.getValue() == 1)
					longueurMapValue.setText(Integer.toString(sliderLongueurMap.getValue()) + " case");
				else
					longueurMapValue.setText(Integer.toString(sliderLongueurMap.getValue()) + " cases");

			} else if (e.getSource() == sliderHauteurMap) {
				if (sliderHauteurMap.getValue() == 1)
					hauteurMapValue.setText(Integer.toString(sliderHauteurMap.getValue()) + " case");
				else
					hauteurMapValue.setText(Integer.toString(sliderHauteurMap.getValue()) + " cases");

			} else if (e.getSource() == sliderNbLoup) {
				nbLoupValue.setText(Integer.toString(sliderNbLoup.getValue()) + "%");

			} else if (e.getSource() == sliderNbMouton) {
				nbMoutonValue.setText(Integer.toString(sliderNbMouton.getValue()) + "%");

			} else if (e.getSource() == sliderCycleMouton) {
				cycleMoutonValue.setText(Integer.toString(sliderCycleMouton.getValue()));

			} else if (e.getSource() == sliderCycleLoup) {
				cycleLoupValue.setText(Integer.toString(sliderCycleLoup.getValue()));

			}
		}
	}
}
