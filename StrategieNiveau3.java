public class StrategieNiveau3 extends Strategie {
	
	private static final long serialVersionUID = 1L;

	public void calculTour(Plateau plateau) throws ActionFailureException {
		
		calculTourGenerale(plateau, 8, 2, false);
	}
}
