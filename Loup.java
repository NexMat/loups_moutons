/**
 * La classe loup permet de modéliser un loup.
 * Hérite d'Animal.
 * @author Lenczner Paul & Mathieu Vu
 * 
 * @see Animal
 *
 */
public class Loup extends Animal {

	/**
	 * Variable pour l'enregistrement dans un fichier.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * @param date
	 */
	public Loup(int date){
		super(date);

		this.vie = 60;
	}

	/* (non-Javadoc)
	 * @see jeudelavie.Animal#setFaim()
	 */
	public void setFaim () {
		this.faim = 10;
	}
}
