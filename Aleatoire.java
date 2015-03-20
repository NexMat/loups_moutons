import java.util.LinkedList;

/**
 * @author Paul Lenczer et Mathieu Vu
 *
 */
public class Aleatoire {

	/**
	 * Permet de générer une liste de positions distinctes de manière aléatoire
	 * C'est l'algorithme dit 'le mélange de Knuth'
	 * @param n ligne du plateau
	 * @param m colonne du plateau
	 * @param nombre nombre de position à obtenir
	 * @return liste de position aléatoire dans le plateau
	 */
	public static LinkedList<Plateau.Position> genPositionAleatoireDistinctes(int n, int m, int nombre) {
		
		int taille = n * m;
		Integer[] tab = new Integer[taille]; 
		
		/* Remplissage du plateau */
		for (int i = 0 ; i < taille ; i++) 
			tab[i] = i;
		
		/* Permutation aléatoire des index du plateau */
		for (int i = 0 ; i < taille ; i++) {

			int alea = (int) (Math.random() * taille);
			int tmp = tab[alea];
			tab[alea] = tab[i];
			tab[i] = tmp;
		}
		
		/* On récupère les positions aléatoires dans notre tableau mélangé */
		LinkedList<Plateau.Position> posAlea = new LinkedList<Plateau.Position>();
		
		for (int i = 0 ; i < nombre ; i++) {
				int x = tab[i] % n;
				int y = (int) (tab[i] / n);
                posAlea.add(new Plateau.Position (y, x));
		}
		
		return posAlea;
	}
}
