/**
 * @author Paul Lenczner & Mathieu Vu
 *
 */
public class Mouton extends Animal {

	/**
	 * Variable pour l'enregistrement dans un fichier.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * @param date La date de naissance
	 */
	public Mouton(int date) {

		super(date);
		this.vie = 50;
	}
	
	/* (non-Javadoc)
	 * @see jeudelavie.Animal#setFaim()
	 */
	public void setFaim () {
		this.faim = 6;
	}

}
