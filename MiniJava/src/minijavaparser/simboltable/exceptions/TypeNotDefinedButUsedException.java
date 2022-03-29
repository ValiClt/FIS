package minijavaparser.simboltable.exceptions;

public class TypeNotDefinedButUsedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public TypeNotDefinedButUsedException(String varname, int line) {
		super("Class/type \"" + varname + "\" was used but never defined at line " + line);
	}
	
}
