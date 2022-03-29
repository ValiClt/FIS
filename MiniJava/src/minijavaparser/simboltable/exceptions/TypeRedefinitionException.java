package minijavaparser.simboltable.exceptions;

public class TypeRedefinitionException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public TypeRedefinitionException(String varname, int line) {
		super("Class/type \"" + varname + "\" was defined previousely at line " + line);
	}	
}
