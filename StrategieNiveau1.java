public class StrategieNiveau1 extends Strategie {
	
	private static final long serialVersionUID = 1L;

	public void calculTour(Plateau plateau) throws ActionFailureException {
		
		calculTourGenerale(plateau, 3, 3, false);
	}
}
