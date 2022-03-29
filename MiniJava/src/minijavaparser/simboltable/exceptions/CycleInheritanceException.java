package minijavaparser.simboltable.exceptions;

public class CycleInheritanceException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CycleInheritanceException(String varname, int line) {
		super("Class \"" + varname + "\" defined at line " + line + " has form an cycle inheritance graph");
	}
}
