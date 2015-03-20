import java.io.Serializable;

public abstract class Strategie implements Deplacement, Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Si un animal ne fait rien on initialise quand même son action
	 * @param c Case que l'on doit initialiser
	 */
	protected void neFaitRien (Plateau.Cases c) throws ActionFailureException {

		ActionAnimal animal = c.getAnimal().getAction();
		animal.setDeplacement(RIEN_FAIRE);
		animal.setAction();
		
		if (c.isLoup())
                c.setLoup(animal);
		else
                c.setMouton(animal);
	}
	
	/**
	 * Construction d'un déplacement pour un animal et enregistrement de l'action
	 * @param c Case de départ
	 * @param pos déplacement de l'animal
	 */
	protected void initDeplacement(Plateau.Cases c, Plateau.Position pos) 
		throws ActionFailureException {

		/* On initialise le comportement de l'animal */
		ActionAnimal animal = c.getAnimal().getAction();
		animal.setDeplacementByPosition(c.getPosition(), pos);
		animal.setAction();

		/* On enregistre le comportement de déplacement */
		if (c.isLoup())
			c.getCase(pos).setLoup(animal);
		if (c.isMouton())
			c.getCase(pos).setMouton(animal);
	}
	
	/**
	 * Permet d'initialiser la direction du couple (pour qu'ils se regardent dans les yeux)
	 * @param pos1
	 * @param pos2
	 */
	protected void setDirectionReproduction(Plateau.Cases case1, Plateau.Cases case2) 
		throws ActionFailureException {

		ActionAnimal animal1  = case1.getAnimal().getAction();
		ActionAnimal animal2  = case2.getAnimal().getAction();
		Plateau.Position pos1 = case1.getPosition();
		Plateau.Position pos2 = case2.getPosition();
		
		animal1.setDeplacementByPosition(pos1, pos2);
		animal2.setDeplacementByPosition(pos2, pos1);
	}
	
	/**
	 * Initialise les structures d'action de reproduction
	 * @param case1
	 * @param case2
	 * @throws ActionFailureException
	 */
	protected void setReproduction(Plateau.Cases case1, Plateau.Cases case2)
		throws ActionFailureException {

		setDirectionReproduction(case1, case2);
        neFaitRien(case1);
		neFaitRien(case2);
	}

	/**
	 * On evite que le loup mange le mouton
	 * @param c Case que l'on doit initialiser
	 * @throws ActionFailureException
	 */
	protected void loupNeMangePlus(Plateau.Cases c) throws ActionFailureException {
		
		ActionAnimal animal = c.getAnimal().getAction();
		
		/* Si le loup veut manger, on l'en empêche et on sauve le mouton */
		if (c.isLoup()) {
			if (animal.isMange()) {

                animal.unsetMange();
                ActionAnimal mouton = c.getMouton();
                if (mouton == null) throw new ActionFailureException("Loup mange sans mouton");
                mouton.unsetSeFaitManger();
			}

		} else {

			if (animal.isSeFaitManger()) {

                animal.unsetSeFaitManger();
                ActionAnimal loup = c.getLoup();
                if (loup == null) throw new ActionFailureException("Mouton se fait manger sans loup");
                loup.unsetMange();
			}
		}
	}
	
	/**
	 * Permet de déplacer un animal aléatoirement sur la carte
	 * @param c case à déplacer
	 * @return true si on peut se déplacer, false sinon
	 * @throws ActionFailureException
	 */
	protected boolean tryAleaDeplacement(Plateau.Cases c) throws ActionFailureException {

		Plateau.Position pos = c.getAleaPositionLibre();
		if (pos != null) {
					
			/* On recrache le mouton sous les dents du loup */
			loupNeMangePlus(c);

			/* Déplacement aléatoire du loup */
			initDeplacement(c, pos);
			
			return true;
		} 
	
		return false;
	}

	/**
	 * Permet d'appliquer une stratégie sur le plateau donné en paramètre
	 * @param plateau
	 * @throws ActionFailureException
	 */
	public abstract void calculTour(Plateau plateau) throws ActionFailureException;

	
	/**
	 * Permet d'appliquer une stratégie sur le plateau donné en paramètre
	 * @param plateau
	 * @param faimLoup cycle de faim des loups
	 * @param faimMouton cycle de faim des moutons
	 * @param correctLoup si on veut faire une correction du nombre de mouton et de loup
	 * @throws ActionFailureException
	 */
	public void calculTourGenerale(Plateau plateau, int faimLoup, int faimMouton, boolean correctLoup) throws ActionFailureException {
		
		/* On précalcul des valeurs nécessaires qui caractérise un écosysteme stable */
		int nbTotalCases = (plateau.getM() * plateau.getN());
		int nbMaxLoups   = Math.max(nbTotalCases / 15, 4);
		int nbLoups      = plateau.getNombreLoup();
		int nbMoutons    = plateau.getNombreMouton();

		/* On parcourt toutes les cases du tableau */
		Plateau.Cases[][] cases = plateau.getCases();
		for (Plateau.Cases[] clist : cases) {
			for (Plateau.Cases c : clist) {
				
				/* On vérifie si il y a un animal */
				if (!c.isAnimal())
					continue;

				/* Si l'animal a déjà passé son tour on continue */
				if (c.getAnimal().getAction().isActionFait())
					continue;

				/* Si c'est un loup */
				if (c.isLoup()) {
					
					/* Si le loup est immobile il ne peut rien faire */
					if (c.getAnimal().isImmobile()) {

						neFaitRien(c);
						continue;
					}
	
					/* Le loup va bientôt mourir de faim */
					if ((c.getFaim() < faimLoup) && (!correctLoup || (nbLoups  < nbMoutons))) {
						
						/* Un mouton s'est déjà jeté sous les dents du loup */
						if (c.getAnimal().getAction().isMange()) {

							neFaitRien(c);
							continue;
						}

						Plateau.Position pos = null;

						/* On regarde autour de soit si un mouton est disponible */
						pos = c.getNourritureLoup();
						if (pos == null) {
							
							pos = c.getNourriturePeutEtreLoup();
						}
						
						if (pos != null) {
							
							/* On initialise le comportement du loup */
							ActionAnimal loup = c.getAnimal().getAction();
							loup.setMange();
							initDeplacement(c, pos);

							/* On paramètre le comportement du mouton */
							ActionAnimal mouton;
							if (plateau.getCase(pos).unMoutonArrive())
								mouton = plateau.getCase(pos).getMouton();
							else 
								mouton = plateau.getCase(pos).getAnimal().getAction();
							mouton.setSeFaitManger();
							
							continue;
						}
					}
					
					/* Le loup/La louve cherche à se reproduire, 	*
					 * remarque si le loup mange déjà, il est		*
					 * impossible de se reproduire					*/
					if (!c.getAnimal().getAction().isMange() && (!correctLoup || (nbLoups <= nbMaxLoups))) {
						
						Plateau.Position posLoup = c.tryReproduire();
						if (posLoup != null) {

							/* Le loup se reproduit, il ne peut rien faire d'autre */
							setReproduction(c, plateau.getCase(posLoup));
                            continue;
						}
					}
					
					/* Si le loup va bientôt mourir de vieillesse on essaye de le tuer sur de la terre */
					if (c.getAnimal().getVie() == 1 || c.getAnimal().getFaim() == 1) {

						Plateau.Position posLibreTerre = c.getPositionTerreLibre();
						if (posLibreTerre != null) {

							initDeplacement(c, posLibreTerre);
							continue;
						} 
					}

					/* Sinon on se déplace aléatoirement.	*
					 * Finalement on préfère éviter que le	*
					 * mouton meurt pour (presque) rien		*/
					if (tryAleaDeplacement(c))
						continue;
						
					/* Le loup ne peut rien faire */
					neFaitRien(c);
					
				/* Si c'est un mouton */
				} else if (c.isMouton()) {

					/* Si le mouton est immobile il ne peut rien faire */
					if (c.getAnimal().isImmobile()) {

						neFaitRien(c);
						continue;
					}
	
					/* Le mouton va bientôt mourir de faim */
					if (c.getFaim() < faimMouton) {
						
						/* On laisse la priorité au loup si le mouton se fait déjà manger */
						if (c.getAnimal().getAction().isSeFaitManger()) {
							neFaitRien(c);
							continue;
						
						/* Si le mouton se trouve sur de l'herbe, il le mange directement 			*
						 * Ou bien le mouton va bientôt mourir on le laisse tomber sur de la terre 	*/
						} else if (c.isHerbe()) {
							
							c.getAnimal().getAction().setMange();
							neFaitRien(c);
							continue;

						/* Si le mouton meurt bientôt, on ne le déplace pas, il peut encore se reproduire */
						} else if (c.getFaim() != 1) {
						
							/* Sinon le mouton cherche de l'herbe */
							Plateau.Position posLibreHerbe = c.getPositionHerbeLibre();
							if (posLibreHerbe != null) {

								initDeplacement(c, posLibreHerbe);
								continue;
							} 
						}

					}

					/* Le bélier/La brebis cherche à se reproduire */
					/* Remarque, si c'est une femelle qui peut se faire manger on abandonne */
					if (!(c.getAnimal().isFemelle() && c.getAnimal().getAction().isSeFaitManger())) {

						Plateau.Position posMouton = c.tryReproduire();
						if (posMouton != null) {

							/* Le mouton se reproduit, il ne peut rien faire d'autre */
							setReproduction(c, plateau.getCase(posMouton));
                            continue;
						}
					}
					
					/* Si le mouton va bientôt mourir de vieillesse on essaye de le tuer sur de la terre */
					if (c.getAnimal().getVie() == 1) {

						Plateau.Position posLibreTerre = c.getPositionTerreLibre();
						if (posLibreTerre != null) {

							initDeplacement(c, posLibreTerre);
							continue;
						} 
					}

					/* Sinon on se déplace aléatoirement. */
					if (tryAleaDeplacement(c))
						continue;
					
					/* Si le mouton ne peut rien faire */
					neFaitRien(c);
				}
			}
		}
	}
}
