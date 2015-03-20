import java.io.Serializable;

public class ActionAnimal implements Deplacement, Serializable {
	
	private static final long serialVersionUID = 1L;

	/* Direction de l'action de l'animal et direction de son regard */
	private Integer direction  = RIEN_FAIRE, directionRegard = BAS;
	private boolean estMange   = false; // Si l'animal se fait manger
	private boolean mange      = false; // Si l'animal mange
	private boolean actionFait = false; // Si l'action de l'animal a déjà calculé
	
	/**
	 * Permet d'obtenir la direction du regard de l'animal
	 * @return
	 */
	public Integer getRegard() {

		return directionRegard;
	}

	/**
	 * Permet de savoir si l'action de l'animal a déjà été calculé
	 * @return true si oui, false sinon
	 */
	public boolean isActionFait() {
		
		return this.actionFait;
	}


    /**
     * Signal que l'action de l'animal a été calculé
     * @throws ActionFailureException
     */
    public void setAction() throws ActionFailureException {

		if (this.actionFait == true)
			throw new ActionFailureException("setAction alors que actionFait est déjà true");
		this.actionFait = true;
	}

	/**
	 * Permet de donner une direction a déplacement de l'animal
	 * @param direction
	 */
	public void setDeplacement(Integer direction) {
		
		this.direction = direction;
	}

	/**
	 * Permet de calculer une position après un déplacement à partir d'une direction et d'une case de départ
	 * @param deplacement direction du déplacement
	 * @param pos position de départ
	 * @return position d'arrivé
	 * @throws ActionFailureException
	 */
	public static Plateau.Position getPositionApresDeplacement(Integer deplacement, Plateau.Position pos)
		throws ActionFailureException {
		
		Plateau.Position cible = new Plateau.Position(pos);
		
		switch(deplacement) {
			case HAUT:        cible.addY(-1);                 break;
			case BAS:         cible.addY(1);                  break;
			case DROITE:      cible.addX(1);                  break;
			case GAUCHE:      cible.addX(-1);                 break;
			case BAS_DROITE:  cible.addX(1); cible.addY(1);   break;
			case BAS_GAUCHE:  cible.addX(-1); cible.addY(1);  break;
			case HAUT_DROITE: cible.addX(1); cible.addY(-1);  break;
			case HAUT_GAUCHE: cible.addX(-1); cible.addY(-1); break;
			default: throw new ActionFailureException("Deplacement impossible : " + deplacement + " !");
		}

		return cible;
	}

	/**
	 * Permet d'obtenir la direction à partir d'une case de départ et d'une case d'arrivée
	 * @param src case de départ
	 * @param dest case d'arrivée
	 * @return direction de l'animal
	 * @throws ActionFailureException
	 */
	public static Integer getDeplacementByPosition(Plateau.Position src, Plateau.Position dest)
		throws ActionFailureException {

		if (src.getX() - dest.getX() == -1 && src.getY() - dest.getY() == 0)
			return DROITE;
		else if (src.getX() - dest.getX() == 1 && src.getY() - dest.getY() == 0)
			return GAUCHE;
		else if (src.getX() - dest.getX() == 0 && src.getY() - dest.getY() == -1)
			return BAS;
		else if (src.getX() - dest.getX() == 0 && src.getY() - dest.getY() == 1)
			return HAUT;
		else if (src.getX() - dest.getX() == -1 && src.getY() - dest.getY() == -1)
			return BAS_DROITE;
		else if (src.getX() - dest.getX() == 1 && src.getY() - dest.getY() == -1)
			return BAS_GAUCHE;
		else if (src.getX() - dest.getX() == -1 && src.getY() - dest.getY() == 1)
			return HAUT_DROITE;
		else if (src.getX() - dest.getX() == 1 && src.getY() - dest.getY() == 1)
			return HAUT_GAUCHE;
		else if (src.getX() - dest.getX() == 0 && src.getY() - dest.getY() == 0)
			return RIEN_FAIRE;
		else
			throw new ActionFailureException("Déplacement impossible : " + src + " -> " + dest);
	}

	/**
	 * Permet d'enregistrer un déplacement à partir d'une position de départ et d'arrivée
	 * @param src position de départ
	 * @param dest position d'arrivée
	 * @throws ActionFailureException
	 */
	public void setDeplacementByPosition(Plateau.Position src, Plateau.Position dest) 
		throws ActionFailureException {

		if (src.getX() - dest.getX() == -1 && src.getY() - dest.getY() == 0)
			setDeplacement(DROITE);
		else if (src.getX() - dest.getX() == 1 && src.getY() - dest.getY() == 0)
			setDeplacement(GAUCHE);
		else if (src.getX() - dest.getX() == 0 && src.getY() - dest.getY() == -1)
			setDeplacement(BAS);
		else if (src.getX() - dest.getX() == 0 && src.getY() - dest.getY() == 1)
			setDeplacement(HAUT);
		else if (src.getX() - dest.getX() == -1 && src.getY() - dest.getY() == -1)
			setDeplacement(BAS_DROITE);
		else if (src.getX() - dest.getX() == 1 && src.getY() - dest.getY() == -1)
			setDeplacement(BAS_GAUCHE);
		else if (src.getX() - dest.getX() == -1 && src.getY() - dest.getY() == 1)
			setDeplacement(HAUT_DROITE);
		else if (src.getX() - dest.getX() == 1 && src.getY() - dest.getY() == 1)
			setDeplacement(HAUT_GAUCHE);
		else
			throw new ActionFailureException("Déplacement impossible : " + src + " -> " + dest);
		
		
		directionRegard = new Integer(direction);
	}
	
	/**
	 * Est-ce que l'animal se déplace ?
	 * @return true si oui, false sinon
	 */
	public boolean isDeplace() {

		return (isActionFait() && !(direction.equals(RIEN_FAIRE)));
	}

	/**
	 * Est-ce que l'animal stagne ?
	 * @return true si oui, false sinon
	 */
	public boolean isStagne() {

		return (isActionFait() && direction.equals(RIEN_FAIRE));
	}

	/**
	 * Permet d'obtenir la direction du déplacement
	 * @return
	 */
	public Integer getDeplacement() {
		
		return this.direction;
	}

	/**
	 * Abandonne l'action de manger
	 * @throws ActionFailureException
	 */
	public void unsetMange() throws ActionFailureException {
		
		if (this.mange == false)
			throw new ActionFailureException("unsetMange alors que mange est déjà false");
		this.mange = false;
	}

	/**
	 * Signal que l'animal mange
	 * @throws ActionFailureException
	 */
	public void setMange() throws ActionFailureException {

		if (this.mange == true)
			throw new ActionFailureException("setMange alors que mange est déjà true");
		this.mange = true;
	}
	
	/**
	 * Est-ce que l'animal courant mange ?
	 * @return true si oui, false sinon
	 */
	public boolean isMange() {

		return this.mange;
	}

	/**
	 * Signal que l'animal courant ne se fait plus manger
	 * @throws ActionFailureException
	 */
	public void unsetSeFaitManger() throws ActionFailureException {

		if (this.estMange == false)
			throw new ActionFailureException("unsetSeFaitManger alors que estMange est déjà false");	
		this.estMange = false;
	}

	/**
	 * Signal que l'animal courant se fait manger
	 * @throws ActionFailureException
	 */
	public void setSeFaitManger() throws ActionFailureException {

		if (this.estMange == true)
			throw new ActionFailureException("setSeFaitManger alors que estMange est déjà true");	
		this.estMange = true;
	}
	
	/**
	 * Est-ce que l'animal courant se fait manger ?
	 * @return true si oui, false sinon
	 */
	public boolean isSeFaitManger() {

		return this.estMange;
	}
	
	/**
	 * Nettoie l'animal courant
	 */
	public void clean() {

		this.direction = RIEN_FAIRE;
		this.estMange = false;
		this.mange = false;
		this.actionFait = false;
	}
}
