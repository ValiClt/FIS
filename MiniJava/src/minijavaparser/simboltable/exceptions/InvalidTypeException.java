package minijavaparser.simboltable.exceptions;

public class InvalidTypeException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	
	public InvalidTypeException(String expectedType, String receiveType, int line) {
		super("At line " + line + " expression with type \"" + receiveType +  "\" was given, but type \"" + expectedType +"\" was expected!");
	}
	
	public InvalidTypeException(String expectedType, String receiveType, int line, String where) {
		super("Exception issue at line " + line + " " + where+ ". Expression with type \"" + receiveType +  "\" was given, but type \"" + expectedType +"\" was expected!");
	}
}
