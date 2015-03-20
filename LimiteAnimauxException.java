/**
 * Exception levée lorsqu'il y a plus d'animaux que de cases dans le plateau.
 * @author Lenczner Paul & Mathieu Vu.
 *
 */
public class LimiteAnimauxException extends Exception {

	private static final long serialVersionUID = 1L;

	public LimiteAnimauxException(String msg) {
		super (msg);
	}
}
