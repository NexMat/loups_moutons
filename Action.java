import java.io.Serializable;

public class Action implements Serializable {

	private static final long serialVersionUID = 1L;

	/* Permet de déterminer la nature de l'action */
	boolean meurt = false, mange = false, herbe = false, loup = false, bouge = false, mineraux = false;
	boolean animalMineraux = false, extra = false;
	Plateau.Position debut = null, fin = null; // Positions où l'animal effectue son action
	Integer direction = null; // Direction dans laquelle l'animal effectue son action
	
	/** Permet d'ajouter des arbres / pierres etc...
	 * posExtra
	 */
	public Action(Plateau.Position posExtra) {
		
		this.extra = true;
		this.debut = new Plateau.Position(posExtra);
	}

	/**
	 * Permet de faire pousser ou détruire de l'herbe, ou bien de poser des minéraux
	 * @param posHerbe
	 * @param vie true l'herbe pousse, false l'herbe a été mangé
	 * @param herbe true c'est de l'herbe, false des minéraux
	 */
	public Action(Plateau.Position posHerbe, boolean vie, boolean herbe) {

		if (herbe) this.herbe = true;
		else this.mineraux    = true;
		this.meurt            = vie;
		this.debut            = new Plateau.Position(posHerbe);
	}
	
	/**
	 * Permet d'effectuer une action statique (qui se passe sur une seule case
	 * @param debut Case où on veut effectuer une action
	 * @param meurt Si l'animal meurt
	 * @param mange Si l'animal mange
	 * @param animal Si il s'agit d'un loup (true) ou d'un mouton (false)
	 */
	public Action(Plateau.Position debut, boolean meurt, boolean mange, boolean animal, Integer direction) {
		
		this.bouge     = false;
		this.loup      = animal;
		this.mange     = mange;
		this.meurt     = meurt;
		this.direction = new Integer (direction);
		this.debut     = new Plateau.Position(debut);
		
		/* Pour determiner si l'animal meurt en engendrant des mineraux */
		if (mange && meurt) {
			this.animalMineraux = true;
			this.mange = false;
		}
	}

	/**
	 * Permet d'effectuer un déplacement avec un animal
	 * @param debut Case de départ
	 * @param fin Case de fin
	 * @param direction type de déplacement
	 * @param meurt Pour un loup : il mange. Pour un mouton : il meurt.
	 * @param animal Loup si vrai, mouton sinon
	 */
	public Action(Plateau.Position debut, Plateau.Position fin, Integer direction, 
				  boolean meurt, boolean animal) {

		this.loup = animal;

		/* Un loup peut manger un mouton lors de son déplacement */
		if (this.loup) this.mange = meurt;
		/* Un mouton meurt en se déplacent sur un loup */
		else this.meurt = meurt;

		this.bouge     = true;
		this.direction = new Integer (direction);
		this.debut     = new Plateau.Position(debut);
		this.fin       = new Plateau.Position(fin);
	}
}
