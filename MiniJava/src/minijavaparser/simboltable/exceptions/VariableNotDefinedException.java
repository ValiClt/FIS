package minijavaparser.simboltable.exceptions;

public class VariableNotDefinedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public VariableNotDefinedException(String varname) {
		super("Indentifier \"" + varname + "\" not defined but used");
	}
	
	public VariableNotDefinedException(String varname, int line) {
		super("Indentifier \"" + varname + "\" not defined but used at line " + line);
	}
}
