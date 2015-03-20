import java.io.Serializable;

/**
 * Classe représentant un animal dans le plateau.
 * @author Lenczner Paul & Mathieu Vu
 *
 */
public abstract class Animal extends Item {

	private static final long serialVersionUID = 1L;

	protected int tourImmobile = 0; // Permet de savoir combien de temps l'animal est immobile
	private Sexe sexe;				// Sexe de l'animal
	protected int faim;				// Niveau de faim de l'animal
	private ActionAnimal action = new ActionAnimal(); // Action que l'animal doit réaliser au prochain tour
	private final int dateDeNaissance; // Date de naissance de l'animal, utile pour les cycles de fécondation

	/**
	 * Création d'un animal
	 * @param date Date de naissance de l'animal (numéro du tour où on a créé l'animal)
	 */
	public Animal(int date) {

		setFaim();
		
		/* Initialisaiton de la date de naissance */
		this.dateDeNaissance = date;

		/* Description sexuelle de l'animal */
		int probaSexe = (int) (Math.random() * 2);
		if (probaSexe == 0)
			sexe = new Male();
		else
			sexe = new Femelle();
	}
	
	/**
	 * Est-ce que l'animal est une femelle ?
	 * @return true si oui, false sinon
	 */
	public boolean isFemelle() {

		if (sexe instanceof Femelle)
			return true;
		return false;
	}

	/**
	 * Est-ce que l'animal est immobile et ne peut donc rien faire ?
	 * @return true si oui, false sinon
	 */
	public boolean isImmobile() {
		return this.tourImmobile != 0;
	}

	/**
	 * Réduit le nombre de tour immobile de un point
	 */
	public void decImmobile() {
		if (this.tourImmobile > 0){
				this.tourImmobile--;
		}
	}
	
	/**
	 * Immobilise un animal. La durée d'immobilité va dépendre du sexe
	 */
	public void setImmobile() {

		sexe.setImmobile();
	}

	/**
	 * @author Mathieu Vu et Paul Lenczner
	 * Classe permettant de désigner le sexe d'un animal
	 *
	 */
	protected abstract class Sexe implements Serializable{

		private static final long serialVersionUID = 1L;
		
		public boolean equals(Object o) {
			if (this instanceof Sexe && o instanceof Sexe) {
				return (this instanceof Male && o instanceof Male) 
						|| (this instanceof Femelle && o instanceof Femelle);
			} else {
				return super.equals(o);
			}
		}
		
		/**
		 * Immobilise un animal. La durée d'immobilité va dépendre du sexe
		 */
		public abstract void setImmobile();
		
		/**
		 * Détermine si l'animal courant est enceinte
		 * @return true si oui, false sinon
		 */
		public abstract boolean isEnceinte();

	}
	
	protected class Male extends Sexe {
		
		private static final long serialVersionUID = 1L;

		public void setImmobile() {

				tourImmobile = 1;
		}

		public boolean isEnceinte() {
			
			return false;
		}
	}

	protected class Femelle extends Sexe {

		private static final long serialVersionUID = 1L;

		private boolean enceinte;

		public Femelle() {
			
			this.enceinte = false;
		}
		
		public void setImmobile() {

				tourImmobile = 2;
				this.enceinte = true;
		}

		public boolean isEnceinte(){

			boolean enceinte = this.enceinte;
			this.enceinte = false;

			return enceinte;
		}
		
		public void setEnceinte(boolean b){
			this.enceinte = b;
		}
		
	}

	/**
	 * Permet de récupérer l'action de l'animal courant
	 * @return
	 */
	public ActionAnimal getAction() {
		
		return action;
	}

	/**
	 * Permet de savoir si l'animal courant est reproductible avec l'animal a
	 * @param a animal à comparer avec l'animal courant
	 * @return true si oui, false sinon
	 */
	public boolean isReproductibleAvec(Item a) {

		if (!(a instanceof Animal))
			return false;

		else if (this instanceof Loup && a instanceof Loup) {
			return (!this.sexe.equals(((Animal)a).sexe)) && 
					!(isImmobile() || ((Animal)a).isImmobile());
		}

		else if (this instanceof Mouton && a instanceof Mouton) {
			return (!this.sexe.equals(((Animal)a).sexe)) && 
					!(isImmobile() || ((Animal)a).isImmobile());
		}

		else
			return false;
	}
	
	/**
	 * Permet d'enlever un niveau de faim à l'animal courant
	 */
	public void decFaim() {
		
		this.faim--;

		if (this.faim == 0) {

			this.vie = 0;
		}
	}

	/**
	 * Permet d'obtenir le niveau de faim de l'animal courant
	 * @return
	 */
	public int getFaim() {

		return this.faim;
	}
	
	/**
	 * Est-ce que l'animal courant est enceinte ?
	 * @return true si oui, false sinon
	 */
	public boolean isEnceinte() {
		
		return this.sexe.isEnceinte();
	}

	/**
	 * Initialise le niveau de faim (au niveau maximum) de l'animal courant
	 */
	public abstract void setFaim();

	/**
	 * Permet de savoir si une femelle a ses chaleurs
	 * @param tours tour actuelle
	 * @param cycle duré de son cycle menstruel
	 * @return true si oui, false sinon
	 */
	public boolean peutSeReproduire(int tours, int cycle) {
		
		int vie = tours - this.dateDeNaissance;
		
		/* Si le bébé vient de naître on ne va quand même pas essayer de la féconder */
		if (vie == 0) return false;
		
		if ((vie % cycle) == 0) 
			return true;

		return false;
	}
}
