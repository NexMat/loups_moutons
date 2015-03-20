import java.io.Serializable;
import java.util.LinkedList;

public class Plateau implements Deplacement, Serializable {

	private static final long serialVersionUID = 1L;

	private Strategie strategie; 			// Stratégie utilisé pour déterminer le déplacement des animaux
	private int univers, nbLoup, nbMouton;	// Compteurs d'animaux
	private Cases[][] cases; 				// Cases du plateau
	private int n, m;						// Ligne, colonne du plateau
	private int cycleLoup, cycleMouton;		// Cycle de reproduction des animaux
	protected int tours = 1;				// Compteur des tours
	private LinkedList<Action> nouveauBebe = new LinkedList<Action>(); // Liste des bébé créer durant un tour
	// Cette liste permet de faire une deuxième étude après les calculs des animaux

	/**
	 * @author Paul Lenczner et Mathieu Vu
	 * Classe indispensable permettant de simplifier les accès au plateau
	 */
	public static class Position implements Serializable {

		private static final long serialVersionUID = 1L;
		
		private int x;
		private int y;
		
		public Position(Position pos) {
			
			this.x = pos.getX();
			this.y = pos.getY();
		}

		public Position (int x, int y) {

			this.x = x;
			this.y = y;
		}
		
		public void addX(int x) {

			this.x += x;
		}

		public void addY(int y) {

			this.y += y;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}
		
		public String toString() {
			
			return "[x:" + x + ", y:" + y + "]";
		}
	}
	
	/**
	 * @author Mathieu Vu et Paul Lenczner
	 * Sous-class permettant de caractériser une case du plateau
	 * 
	 */
	public class Cases implements Serializable {

		private static final long serialVersionUID = 1L;
		
		private Position pos        = null;  // Position de la case dans le tableau
		private Animal animal       = null;  // Animal associé à la case
		private Vegetal vegetal     = null;  // Vegetaux associés à la case
		private ActionAnimal mouton = null;  // L'action du futur mouton associé à la case
		private ActionAnimal loup   = null;  // L'action du futur loup associé à la case
		private boolean maj         = false; // Si la case est déjà mis à jour
		private boolean extra		= false; // Si la case est une position extra (arbre, rocher...)
		private Action actionV      = null;  // Action du sol
		private Action actionA      = null;  // Action des animaux
		private Action actionB      = null;  // Action des animaux bébés
		private Action actionE      = null;  // Action extra

		/**
		 * Constructeur d'une case du plateau
		 * @param animal animal à associer à la case
		 * @param herbe true si il y a de l'herbe, false sinon
		 * @param selsMineraux true si il y a des sels minéraux, false sinon
		 * @param pos position exacte de la case dans le plateau
		 */
		public Cases(Animal animal, boolean herbe, boolean selsMineraux, Position pos) {
			
			this.pos = pos;

			this.animal = animal;

			if (herbe)
				newHerbe();

			else if (selsMineraux)
				newMineraux();
		}
		
		/**
		 * Permet d'obtenir l'action 'extra' de la case
		 * @return
		 */
		public Action getActionExtra () {

			return this.actionE;
		}

		/**
		 * Permet d'obtenir l'action animal de la case
		 * @return
		 */
		public Action getActionAnimal () {

			return this.actionA;
		}

		/**
		 * Permet d'obtenir l'action vegetal de la case
		 * @return
		 */
		public Action getActionVegetal () {

			return this.actionV;
		}

		/**
		 * Permet d'obtenir l'action bébé animal de la case
		 * @return
		 */
		public Action getActionAnimalBebe () {

			return this.actionB;
		}


		/**
		 * Permet d'initialiser l'action extra de la case
		 * @param a
		 */
		public void setActionExtra (Action a) {

			this.actionE = a;
		}
		
		/**
		 * Permet d'initialiser l'action vegetal de la case
		 * @param a
		 */
		public void setActionVegetal (Action a) {

			this.actionV = a;
		}

		/**
		 * 
		 * Permet d'initialiser l'action animal de la case
		 * @param a
		 */
		public void setActionAnimal (Action a) {

			this.actionA = a;
		}

		/**
		 * Permet d'initialiser l'action bébé animal de la case
		 * @param a
		 */
		public void setActionAnimalBebe (Action a) {

			this.actionB= a;
		}


		/**
		 * Permet d'obtenir une case à partir de la position
		 * @param pos
		 * @return
		 */
		public Cases getCase(Position pos) {
			
			return Plateau.this.getCase(pos);
		}

		/**
		 * Permet d'obtenir une liste chaînée des positions des cases adjacentes de la case courante
		 * @return une liste chaînée de type LinkedList de Position
		 */
		public LinkedList<Position> getAdjacentes() {
			
			LinkedList<Position> adja = new LinkedList<Position>();

			if (pos.getX() - 1 >= 0) adja.add(new Position (pos.getX() - 1, pos.getY()));
			if (pos.getY() - 1 >= 0) adja.add(new Position (pos.getX()    , pos.getY() - 1));
			if ((pos.getX() - 1 >= 0) && (pos.getY() - 1 >= 0))
									 adja.add(new Position (pos.getX() - 1, pos.getY() - 1));
			if (pos.getX() + 1 < m)  adja.add(new Position (pos.getX() + 1, pos.getY()));
			if (pos.getY() + 1 < n)  adja.add(new Position (pos.getX()    , pos.getY() + 1));
			if ((pos.getX() + 1 < m) && (pos.getY() + 1 < n))
									 adja.add(new Position (pos.getX() + 1, pos.getY() + 1));
			if ((pos.getX() - 1 >= 0) && (pos.getY() + 1 < n))
									 adja.add(new Position (pos.getX() - 1, pos.getY() + 1));
			if ((pos.getX() + 1 < m) && (pos.getY() - 1 >= 0))
									 adja.add(new Position (pos.getX() + 1, pos.getY() - 1));

			return adja;
		}

		/**
		 * Permet d'obtenir la position courante d'une case courante
		 * @return
		 */
		public Position getPosition() {
			
			return pos;
		}

		/**
		 * Vérifie si la case est accessible par l'animal présent dans la case src
		 * @param src case de l'animal voulant accéder à la case courante
		 * @return true si c'est possible, false sinon
		 */
		public boolean isAccessible(Plateau.Position src) {

			Plateau.Cases c = getCase(src);

			/* On vérifie la cohérence du mouvement */
			if (Math.abs(src.getX() - pos.getX()) > 1 || 
				Math.abs(src.getY() - pos.getY()) > 1 ||
				src.getX()  <  0 || src.getY()  <  0  ||
				src.getX()  >= m || src.getY()  >= n) 
					return false;
			
			/* Si on peut se déplacer sur la case */
			
			/* Si il y a un extra, ce n'est pas possible */
			if (isExtra()) {
				
				return false;

			/* Si on est un mouton */
			} else if (isMouton()) {

				if (c.isMouton()) return false;
				if (c.unMoutonArrive()) return false;

			/* Si on est un Loup */
			} else if (isLoup()) {

				if (c.isLoup()) return false;
				if (c.unLoupArrive()) return false;

			}

			return true;
		}

		/**
		 * Renvoie la première case où de la nourriture est 'peut-être' disponible pour le loup
		 * @return la position de la case, null sinon
		 */
		public Position getNourriturePeutEtreLoup() {

			LinkedList<Position> adja = getAdjacentes();
			
			/* Parcourt des cases adjacentes */
			for (Position p : adja) {
				if (nourritureDispoLoup(p, true))
					return p;
			}
			
			return null;
		}

		/**
		 * Renvoie la première case où de la nourriture est disponible pour le loup
		 * @return la position de la case, null sinon
		 */
		public Position getNourritureLoup() {

			LinkedList<Position> adja = getAdjacentes();
			
			/* Parcourt des cases adjacentes */
			for (Position p : adja) {
				if (nourritureDispoLoup(p, false))
					return p;
			}
			
			return null;
		}

		/**
		 * Permet de remonter l'action du mouton de la case courante
		 * @return
		 */
		public ActionAnimal getMouton() {
			
			return this.mouton;
		}

		/**
		 * Permet de remonter l'action du loup de la case courante
		 * @return
		 */
		public ActionAnimal getLoup() {
			
			return this.loup;
		}

		/**
		 * Permet d'initialiser l'action du mouton de la case courante
		 * @param mouton
		 */
		public void setMouton (ActionAnimal mouton) {

			this.mouton = mouton;
		}
	
		/**
		 * Permet d'initialiser l'action du loup de la case courante
		 * @param loup
		 */
		public void setLoup (ActionAnimal loup) {

			this.loup = loup;
		}

		/**
		 * Permet de vérifier si la case src possède un mouton à manger pour le loup
		 * @param src 
		 * @param liberte si true, permet un peu plus de possibilité de recherche pour le loup
		 * @return true si la case possède un mouton mangeable, false sinon
		 */
		public boolean nourritureDispoLoup(Position src, boolean liberte) {

			Cases c = Plateau.this.getCase(src);

			/* Si la case n'est pas accessible et qu'il s'agit d'un loup */
			if (!isAccessible(src)) 
				return false;

			/* Si un mouton arrive et qu'il n'est pas prévu qu'un loup le mange */
			if (c.unMoutonArrive() && !c.getMouton().isSeFaitManger()) 
				return true;
			/* Si un mouton est dispo pour le casse-croute */
			else if (c.isAnimal()) {

				Animal animal = c.getAnimal();
				ActionAnimal actionAnimal = animal.getAction();
				
				/* Si le mouton stagne mais n'est pas déjà prévu au diner d'un autre loup */
                if (actionAnimal.isStagne() && !actionAnimal.isSeFaitManger()) 
                	return true;
                /* Si le mouton est en train de s'accoupler et qu'un autre loup n'est pas sur le coup */
                else if (animal.isImmobile() && !actionAnimal.isSeFaitManger()) 
                	return true;

                else {
                	/* Permet d'être un peu plus libre dans le choix de la case.		*
                	 * Ici, on ne sait pas forcement quel est le déplacement du mouton. */
                	if (liberte && !animal.getAction().isDeplace() && !actionAnimal.isSeFaitManger()) 
                		return true;
                	return false;
                }

			} else return false;
		}

		/**
		 * Suppression de l'herbe de la case courante 
		 */
		public void delHerbe() {
			
			if (this.vegetal instanceof Herbe) {
				setActionVegetal(new Action(pos, false, true));
                this.vegetal = null;
			}
		}
		
		/**
		 * Est-ce qu'il y de l'herbe dans la case courante ?
		 * @return true si oui, false sinon
		 */
		public boolean isHerbe() {
			
			if (this.vegetal instanceof Herbe)
				return !(this.vegetal == null);
			return false;
		}

		/**
		 * Création de l'herbe dans la case courante
		 */
		public void newHerbe() {
			
			if (this.vegetal == null) {
				setActionVegetal(new Action(pos, true, true));
                this.vegetal = new Herbe ();
			}
		}
		
		/**
		 * Permet de mettre à jour les végétaux dans la case courante 
		 */
		public void majVegetaux() {
			
			/* Si il y a des mineraux on regarde leur évolution */
			if (isMineraux()) {

                vegetal.evolution();
                
                if (!vegetal.isVivant()) {
                        
                	delMineraux();
                    newHerbe();
                }

            /* Si il y a de l'herbe on rajoute une action pour l'indiquer */
			} else if (isHerbe()) {

				setActionVegetal(new Action(pos, true, true));
				
			/* Si il n'y a rien, on rajoute une action pour l'indiquer */
			} else {
				setActionVegetal(new Action(pos, false, true));

			}
		}
		
		/**
		 * Est-ce qu'il y a des mineraux dans la case courante ?
		 * @return oui si true, false sinon
		 */
		public boolean isMineraux() {
			
			if (this.vegetal instanceof SelsMineraux)
                return !(this.vegetal == null);
			return false;
		}

		/**
		 * Destruction des minéraux dans la case courante
		 */
		public void delMineraux() {
			
			if (this.vegetal instanceof SelsMineraux)
                this.vegetal = null;
		}

		/**
		 * Création des minéraux dans la case courante
		 */
		public void newMineraux() {
			
			if (this.vegetal == null) {
				/* On pose des minéraux */
				setActionVegetal(new Action(pos, false, false));
                this.vegetal = new SelsMineraux();
			}
		}
		
		/**
		 * Est-ce qu'il y a un élément 'extra' dans la case courante ?
		 * @return
		 */
		public boolean isExtra() {

			return this.extra;
		}

		/**
		 * Création d'un élément extra dans la case courante
		 */
		public void newExtra() {

			this.extra = true;
		}

		/**
		 * Création d'un loup dans la case courante
		 * @throws ActionFailureException
		 */
		public void newLoup() throws ActionFailureException {
			
			setAnimal(new Loup(Plateau.this.tours), true);
		}

		/**
		 * Création d'un mouton dans la case courante
		 * @throws ActionFailureException
		 */
		public void newMouton() throws ActionFailureException {
			
			setAnimal(new Mouton(Plateau.this.tours), false);
		}

		/**
		 * Est-ce qu'il y a un mouton dans la case courante ?
		 * @return true si oui, false sinon
		 */
		public boolean isMouton() {
			
			if (isAnimal())
				return (this.animal instanceof Mouton);
			return false;
		}

		/**
		 * Est-ce qu'il y a un loup dans la case courante ?
		 * @return true si oui, false sinon
		 */
		public boolean isLoup() {
			
			if (isAnimal())
				return (this.animal instanceof Loup);
			return false;
		}

		/**
		 * Est-ce qu'il y a un animal dans la case courante ?
		 * @return true si oui, false sinon
		 */
		public boolean isAnimal() {
			
			return !(this.animal == null);
		}

		/**
		 * Permet d'alimenter un animal
		 */
		public void eatAnimal() {
			
			this.animal.setFaim();
		}
		
		/**
		 * Est-ce qu'un loup arrive au tour suivant ?
		 * @return true si oui, false sinon
		 */
		public boolean unLoupArrive() {

			return (this.loup != null);
		}

		/**
		 * Est-ce qu'un mouton arrive au tour suivant ?
		 * @return true si oui, false sinon
		 */
		public boolean unMoutonArrive() {
			
			return (this.mouton != null);
		}

		/**
		 * Permet d'obtenir le niveau de faim de l'animal courant
		 * @return
		 */
		public int getFaim() {

			return this.animal.getFaim();
		}

		/**
		 * Permet de supprimer l'animal courant
		 * @throws ActionFailureException
		 */
		public void delAnimal() throws ActionFailureException {
		
			/* On oublie l'animal */
			setAnimal(null, isLoup());
		}

		/**
		 * Permet d'obtenir l'animal courant
		 * @return
		 */
		public Animal getAnimal () {

			return this.animal;
		}

		/**
		 * Essaye de se reproduire avec un autre animal de la même espèce et le sexe opposé
		 * @return la position de l'animal avec qui l'animal courant se reproduit, null sinon
		 */
		public Position tryReproduire() {
			
			int cycle;
			LinkedList<Position> adja = getAdjacentes();

            /* Permet de récupérer si il y a un animal de sexe opposé disposé à se reproduire */
            for (Position pos : adja) {

            	Animal animalCible = Plateau.this.getCase(pos).getAnimal();
            	
            	/* Si on a trouvé un(e) compagnon qui n'est pas déjà occupé(e) ! */
                if (this.animal.isReproductibleAvec(animalCible) && !animalCible.getAction().isActionFait()) {
                	
                	/* On récupère le cycle de reproduction approprié */
                	if (this.isLoup())
                		cycle = Plateau.this.cycleLoup;
                	else
                		cycle = Plateau.this.cycleMouton;

                	/* On vérifie si la femelle peut bien se reproduire */
                	if (animalCible.isFemelle()) {
                		if (!animalCible.peutSeReproduire(Plateau.this.tours, cycle))
                			continue;
                	} else {
                		if (!this.animal.peutSeReproduire(Plateau.this.tours, cycle))
                			continue;
                	}

                	/* Ce n'est pas utile d'essayer de se reproduire si la femelle se fait dévorer */
                	if (animalCible.isFemelle() && animalCible.getAction().isSeFaitManger())
                		continue;

                	/* On immobilise les deux animaux */
                	this.animal.setImmobile();
                	animalCible.setImmobile();
                	return pos;
                }
			} 
            
            return null;
		}

		/**
		 * Récupère les cases libres parmis les cases adjacentes 
		 * @return la liste des positions libres
		 */
		public LinkedList<Position> getPositionsLibres() { 

			/* Récupère les cases adjacentes */
			LinkedList<Position> adja = getAdjacentes();
			LinkedList<Position> posLibre = new LinkedList<Position>();
				
			/* Récupère les cases libres parmis les cases adjacentes */
			for (Position pos : adja) {

				Cases c = Plateau.this.getCase(pos);
				if ((!c.isAnimal() || c.getAnimal().getAction().isDeplace())  &&
					!c.unLoupArrive() && !c.unMoutonArrive() && !c.isExtra())
					posLibre.add(pos);
			}
			
			return posLibre;
		}
		
		/**
		 * Récupère une position adjacente où l'herbe est libre
		 * @return position de l'herbe libre
		 */
		public Position getPositionTerreLibre() { 

			/* Récupère les cases adjacentes libres */
			LinkedList<Position> posLibre = getPositionsLibres();
				
			for (Position pos : posLibre) {

				Cases c = Plateau.this.getCase(pos);
				if (!c.isHerbe())
					return pos;
			}
			
			return null;
		}
	
		/**
		 * Récupère une position adjacente où l'herbe est libre
		 * @return position de l'herbe libre
		 */
		public Position getPositionHerbeLibre() { 
			/* Récupère les cases adjacentes libres */
			LinkedList<Position> posLibre = getPositionsLibres();
				
			for (Position pos : posLibre) {

				Cases c = Plateau.this.getCase(pos);
				if (c.isHerbe())
					return pos;
			}
			
			return null;
		}
	

		/**
		 * On récupère une position libre aléatoirement 
		 * @return une position libre ou null s'il n'y en a pas
		 */
		public Position getAleaPositionLibre() {

				/* Récupère les cases libres parmis les cases adjacentes */
				LinkedList<Position> posLibre = getPositionsLibres();

				/* Si aucune position n'est disponible */
				if (posLibre.size() == 0)
					return null;

				/* Récupère la place aléatoire parmis les places libres */
				int index = (int) (Math.random() * (double) posLibre.size());
				
				return posLibre.get(index);
		}

		/**
		 * Si l'animal de la case courante est enceinte et si il y a une case libre dans une 
		 * case adjacente, on peut alors déposer un nouveau bébé
		 * @throws ActionFailureException
		 */
		public void naissanceAnimal() throws ActionFailureException {
			
			/* On va déposer un bébé de manière aléatoire */
			if (this.animal.isEnceinte()) {

				/* On récupère une position libre aléatoirement */
				Position posBebe = getAleaPositionLibre();
				if (posBebe == null) {

					return;
				}

				Cases c = Plateau.this.getCase(posBebe);
				/* Dépose le nouveau-né */
				if (this.animal instanceof Mouton)
					c.newMouton();
				else
					c.newLoup();

				/* On met à jour la case / Initialise et mémorise l'action */
				c.setMaj();
				c.getAnimal().getAction().setAction();
				Action bebeAction = new Action(pos, posBebe, 
						ActionAnimal.getDeplacementByPosition(pos, posBebe), false, isLoup());
				setActionAnimalBebe(bebeAction);
				
				/* On enregistre la position du nouveau bebe mouton pour vérifier si	  *
				 * par hasard, à la fin du tour, il ne se fait pas dévorer à la naissance */
				if (this.animal instanceof Mouton)
					nouveauBebe.add(bebeAction);
			}
		}

		/**
		 * Permet de signaler au tableau qu'une case a bien été mise à jour
		 */
		public void setMaj() {
			this.maj = true;
		}

		/**
		 * Est-ce qu'une case a bien été mise à jour
		 * @return
		 */
		public boolean isMaj() {
			return this.maj;
		}

		/**
		 * Permet de nettoyer la case courante
		 */
		public void purgeAction() {
				
			this.maj     = false;
			this.mouton  = null;
			this.loup    = null;
			this.actionA = null;
			this.actionV = null;
			this.actionE = null;
			this.actionB = null;
			if (this.animal != null)
				this.animal.getAction().clean();
		}

		/**
		 * Permet de mettre à jour les éléments extras dans la case courante
		 */
		public void majExtra() {

			setActionExtra(new Action(this.pos));
		}

		/**
		 * Permet de mettre à jour l'animal de la case courante
		 * @throws ActionFailureException
		 */
		public void majAnimal() throws ActionFailureException {
			
			this.animal.evolution ();
			this.animal.decFaim ();
			this.animal.decImmobile();
			naissanceAnimal();
			
			if (!this.animal.isVivant()) {
				
				/* Lorsque qu'un animal meurt de fin ou de vieillesse il dépose des minéraux */
				boolean mineraux = false;
				if (this.vegetal == null) mineraux = true;
				newMineraux();

				/* On ajoute l'action de la mort de l'animal */
				setActionAnimal(new Action(this.pos, true, mineraux, isLoup(), 
							   getAnimal().getAction().getRegard()));
				
				delAnimal();

			/* Si l'animal est bloqué, il ne peut rien faire avec personne ! */
			} else if (this.animal.isImmobile()) {
				
				ActionAnimal animalAction = this.animal.getAction();
				setMaj();
				animalAction.setAction();
				setActionAnimal(new Action(this.pos, false, false, isLoup(), animalAction.getRegard()));
			}
		}

		/**
		 * Permet de créer un animal dans la case courante
		 * @param animal animal a associer à la case courante
		 * @param loup true si c'est un loup, false sinon
		 * @throws ActionFailureException
		 */
		public void setAnimal(Animal animal, boolean loup) throws ActionFailureException {
			
			/* On met à jour les compteurs de l'univers, celà revient à tuer l'animal de la case */
			if (animal == null) {
				Plateau.this.univers--;
				if (loup)
					Plateau.this.nbLoup--;
				else
					Plateau.this.nbMouton--;

			/* Création d'un animal dans la case */
			} else {

				if (this.animal != null) {

					/* Mise à jour des compteurs pour l'ancien animal de la case */
					Plateau.this.univers--;
					if (isLoup()) throw new ActionFailureException("Loup remplacé !");
					Plateau.this.nbMouton--;

					/* Permet de corriger une incohérence d'affichage */
					if (getActionAnimal() != null) {
						if (!getActionAnimal().meurt && !getActionAnimal().bouge)
							getActionAnimal().meurt = true;
					}
				}

				/* Mise à jour des compteurs pour la création du nouveau animal */
				Plateau.this.univers++;
				if (loup)
					Plateau.this.nbLoup++;
				else
					Plateau.this.nbMouton++;
			}

			this.animal = animal;
		}
	}
	
	/**
	 * Initialisation du plateau
	 * @param n hauteur du plateau
	 * @param m largeur du plateau
	 * @param nbMoutons nombre de mouton sur le plateau
	 * @param nbLoups nombre de loup sur le plateau
	 * @param cycleMouton cycle de fécondation de la brebi
	 * @param cycleLoup cycle de fécondation de la louve
	 * @param strategie stratégie à utiliser par défaut
	 * @throws LimiteAnimauxException lève une exception si il y a trop d'animaux pour pas assez de cases
	 * @throws ActionFailureException lève une exception si il y a une incohérence d'action
	 */
	public Plateau (int n, int m, int nbMoutons, int nbLoups, int cycleMouton, int cycleLoup, Strategie strategie) 
			throws LimiteAnimauxException, ActionFailureException {

		this.strategie = strategie;
		this.univers = 0;
		this.nbLoup = 0;
		this.nbMouton = 0;
		this.n = n;
		this.m = m;
		this.cases = new Cases[n][m];
		this.cycleMouton = cycleMouton;
		this.cycleLoup   = cycleLoup;
		int nbAnimals = nbMoutons + nbLoups;
		int nbExtra;

		/* On vérifie que la limite de cases n'est pas atteinte */
		if (nbAnimals > n * m) {
			throw new LimiteAnimauxException("Plateau");
		}
		
		nbExtra = Math.min ((n * m) / 20, (n * m) - nbAnimals); 

		/* On remplit le tableau d'herbe */
		for (int i = 0 ; i < n ; i++) {
			
			for (int j = 0 ; j < m ; j++) {
				
				cases[i][j] = new Cases(null, true, false, new Position (j, i));
			}
		}

		/* On engendre une suite de combinaisons de position distinctes */
		LinkedList<Position> posAlea = 
				Aleatoire.genPositionAleatoireDistinctes(n, m, nbAnimals + nbExtra);
		
		/* On positionne aléatoirement les moutons et les loups */
		for (int i = 0 ; i < nbMoutons ; i++) {
			
			Position pos = posAlea.get(i);
			getCase(pos).newMouton();
			getCase(pos).setActionAnimal(new Action(pos, false, false, false, BAS));
		}

		for (int i = 0 ; i < nbLoups ; i++) {

			Position pos = posAlea.get(i + nbMoutons);
			getCase(pos).newLoup();
			getCase(pos).setActionAnimal(new Action(pos, false, false, true, BAS));
		}
		
		/* On positionne aléatoirement les extras (arbres / pierre / etc...) */
		for (int i = 0 ; i < nbExtra ; i++) {
			Position pos = posAlea.get(i + nbMoutons + nbLoups);
			getCase(pos).newExtra();
			getCase(pos).setActionExtra(new Action(pos));
		}
	}

	/**
	 * Permet d'obtenir une case à partir d'une position
	 * @param pos
	 * @return
	 */
	public Cases getCase(Position pos) {

		return cases[pos.getY()][pos.getX()];
	}

	/**
	 * Permet de de déterminer si l'univers est mort
	 * @return
	 */
	public boolean isUniversMort () {
		
		return (univers == 0);
	}
	
	/**
	 * Permet d'obtenir le nombre de ligne du plateau
	 * @return
	 */
	public int getN() {
		return this.n;
	}

	/**
	 * Permet d'obtenir le nombre de colonne du plateau
	 * @return
	 */
	public int getM() {
		return this.m;
	}
	
	/**
	 * Remet à zéro le système d'action du jeu
	 */
	private void purgeActionPlateau() {

		for (Cases[] clist : this.cases) {
			for (Cases c : clist) 
				c.purgeAction();
		}
	}

	/**
	 * 1) Mise à jour des sels minéraux sur la carte.
	 * 2) Mise à jour des décès de faim ou de vieillesse des loups et des moutons.
	 * @throws ActionFailureException 
	 */
	private void majFauneEtFlore() throws ActionFailureException {
		
		/* On vide l'ancienne liste des nouveaux bébés */
		nouveauBebe.clear();

		for (Cases[] clist : this.cases) {
			for (Cases c : clist) {
				c.majVegetaux();
				if (c.isExtra()) {
					c.majExtra();
				} else if (c.isAnimal())
					c.majAnimal();
			}
		}
	}

	/**
	 * Permet d'obtenir toutes les cases du plateau
	 * @return
	 */
	public Cases[][] getCases() {

		return cases;
	}

	/**
	 * Mise à jour d'une case du plateau
	 * @param c Case à mettre à jour
	 * @throws ActionFailureException
	 */
	private void majCase(Cases c) throws ActionFailureException {
		
		Animal animal       = c.getAnimal();
		ActionAnimal action = animal.getAction();
		Position pos        = c.getPosition();
		Integer direction	= new Integer (action.getRegard());
		
		/* Si l'animal se fait manger (le mouton) */
		if (action.isSeFaitManger() && action.isStagne()) {

			c.setActionAnimal(new Action(pos, true, false, false, direction));
			c.delAnimal();

		/* Si le mouton mange de l'herbe */
		} else if (action.isMange() && c.isMouton()) {

			c.delHerbe();
			animal.setFaim();
			c.setActionAnimal(new Action(pos, false, true, false, direction));

		/* Si le loup stagne mais un mouton se jette sous ses dents */
		} else if (action.isStagne() && c.isLoup() && action.isMange()) {
			
			c.setActionAnimal(new Action(pos, false, true, true, direction));
		
		/* Si un animal se déplace */
		} else if (action.isDeplace()) {
			
			Position cible = ActionAnimal.getPositionApresDeplacement(direction, pos);
			/* Vérification de la cohérence du déplacement */
			if (cible.getX() >= this.m || cible.getX() < 0 || cible.getY() >= this.n || cible.getY() < 0)
				throw new ActionFailureException("Plateau[" + m + "," + n + "] Cible" + cible +
						" Source" + pos + " Direction" + direction);

			
			/* On mémorise notre animal */
			Animal animalTmp = c.getAnimal();
			
			/* On mémorise le déplacement dans la liste d'action */
			if (c.isLoup()) 
				c.setActionAnimal(new Action(pos, cible, direction, action.isMange(), true)); 
			else 
				c.setActionAnimal(new Action(pos, cible, direction, action.isSeFaitManger(), false));

			/* On oublie l'animal pour ne pas perturber l'animal qui se trouve dans la case cible */
			c.delAnimal();
			
			Cases caseCible = getCase(cible);
			/* Si un animal se trouvait dans la case cible */
			if (caseCible.isAnimal()) {
					
				/* On va résoudre les actions de l'animal reccursivement */
				if (!caseCible.isMaj())
					majCase(caseCible);
			}

			/* On met l'animal dans la case cible */
			if(!action.isSeFaitManger()) {
				caseCible.setAnimal(animalTmp, animalTmp instanceof Loup);
				caseCible.setMaj();
			}

		/* Si les animaux stagnes */
		} else if (action.isStagne()) {

			c.setActionAnimal(new Action(pos, false, false, c.isLoup(), action.getRegard()));
		}
		
		/* On nourrit l'animal si il faut */
		if (action.isMange())
			animal.setFaim();

		/* On met à jour la case */
		c.setMaj();
	}

	/**
	 * Permet de répercuter les changements du tour actuel sur la plateau
	 * -> Déplacement, -> Manger, -> Stagner principalement
	 * @throws ActionFailureException
	 */
	private void majTour() throws ActionFailureException {

		/* Parcourt des cases du plateau */
		for (Cases[] clist : this.cases) {
			for (Cases c : clist) {
				
				if (!c.isMaj() && c.isAnimal()) {
					majCase(c);
				}
			}
		}
		
		/* On vérifie si les nouveaux bébés moutons se font manger à la naissance */
		for (int i = 0 ; i < nouveauBebe.size(); i++) {
			
			Action actionDuBebe = nouveauBebe.get(i);
			if (getCase(actionDuBebe.fin).unLoupArrive() && actionDuBebe.meurt == false) 
				actionDuBebe.meurt = true;
		}
	}

	/**
	 * Permet de récupérer le numéro du tour
	 * @return
	 */
	public int getNumTour() {

		return this.tours;
	}

	/**
	 * Permet de calculer un nouveau tour pour le plateau
	 * @throws ActionFailureException
	 */
	public void nouveauTour() throws ActionFailureException {

		this.tours++;

		/* On remet à zéro le système d'action */
		purgeActionPlateau();

		/* Met à jour la faune et la flore du plateau (faim/vieilesse/sels minéraux) */
		majFauneEtFlore();

		/* Si il n'y a plus d'animaux sur la carte */
		if (isUniversMort())
			return;

		/* On met en action la faune du jeu */
		strategie.calculTour(this);

		/* On met à jour le plateau et les actions */
		majTour();

		return;
	}
	
	/**
	 * Permet d'obtenir le nombre de loup courant
	 * @return
	 */
	public int getNombreLoup() {
		return this.nbLoup;
	}

	/**
	 * Permet d'obtenir le nombre de mouton courant
	 * @return
	 */
	public int getNombreMouton() {
		return this.nbMouton;
	}
	
	/**
	 * Permet d'obtenir le cycle de reproduction des moutons
	 * @return
	 */
	public int getCycleMouton() {
		
		return this.cycleMouton;
	}

	/**
	 * Permet d'obtenir le cycle de reproduction des loups
	 * @return
	 */
	public int getCycleLoup() {
		
		return this.cycleLoup;
	}
	
	/**
	 * Permet de paramétrer le cycle de reproduction des loups
	 * @param c
	 */
	public void setCycleLoup(int c) {
		
		this.cycleLoup = c;
	}

	/**
	 * Permet de paramétrer le cycle de reproduction des moutons
	 * @param c
	 */
	public void setCycleMouton(int c) {
		
		this.cycleMouton = c;
	}
	
	/**
	 * Permet de paramétrer la stratégie utilisée
	 * @param strat
	 */
	public void setStrategie(Strategie strat){

		this.strategie = strat;
	}

	/**
	 * Permet d'accéder à la stratégie utilisée
	 * @return La stratégie employée.
	 */
	public Strategie getStrategie(){

		return this.strategie;
	}
}
