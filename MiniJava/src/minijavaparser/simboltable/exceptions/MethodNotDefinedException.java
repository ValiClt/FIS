package minijavaparser.simboltable.exceptions;

public class MethodNotDefinedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public MethodNotDefinedException(String funcname) {
		super("Method \"" + funcname + "\" not defined but caled");
	}
}
