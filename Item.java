import java.io.Serializable;

/**
 * Un item est un objet contenu par une case d'un plateau.
 * @author Lenczner Paul & Mathieu Vu.
 *
 */
public abstract class Item implements Serializable {
	
	private static final long serialVersionUID = 1L;

	protected int vie;
	
	public int getVie(){

		return this.vie;
	}

	public void evolution() {

		vie--;
	}

	public boolean isVivant() {
		
		return !(vie == 0);
	}
	
}
