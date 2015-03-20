/**
 * Exception levée lors d'une erreur d'action d'un animal.
 * @author Lenczner Paul & Mathieu Vu
 *
 */
public class ActionFailureException extends Exception {

	private static final long serialVersionUID = 1L;

	public ActionFailureException(String msg) {

		super(msg);
	}
}
