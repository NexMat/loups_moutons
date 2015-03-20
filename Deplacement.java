/**
 * Interface permettant de simplifier l'utilisation des directions.
 * @author Lenczner Paul & Mathieu Vu
 *
 */
public interface Deplacement {

	/* Déplacements des loups et des moutons */
	static final int HAUT        = 0;
	static final int BAS         = 1;
	static final int GAUCHE      = 2;
	static final int DROITE      = 3;
	static final int HAUT_GAUCHE = 4;
	static final int HAUT_DROITE = 5;
	static final int BAS_GAUCHE  = 6;
	static final int BAS_DROITE  = 7;
	static final int RIEN_FAIRE  = 8;
}
