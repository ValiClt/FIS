package minijavaparser.simboltable.exceptions;

public class VariableRedefinitionException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public VariableRedefinitionException(String varname, int location) {
		super("Member/Identifier \"" + varname + "\", " + location + " is already defined");
	}

}
