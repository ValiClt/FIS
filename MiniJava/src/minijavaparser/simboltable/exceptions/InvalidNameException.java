package minijavaparser.simboltable.exceptions;

public class InvalidNameException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	
	public InvalidNameException(String invalidName) {
		super("Invalid identifier \"" + invalidName +  "\"");
	}
	
}
