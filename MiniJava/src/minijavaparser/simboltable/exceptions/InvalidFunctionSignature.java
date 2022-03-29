package minijavaparser.simboltable.exceptions;

import java.util.LinkedList;

import minijavaparser.simboltable.MType;

public class InvalidFunctionSignature extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	
	public InvalidFunctionSignature(String invalidName, LinkedList<MType> params, int line) {
		super("Exception at line " + line + ". No function with signature \"(" + compose(params) +  ")\" was found");
	}
	
	private static String compose(LinkedList<MType> params) {
		if(params == null)
			return "";
		String paramsS = "";
		for(var tp:params)
			paramsS += ", " + tp.typeName;
		return paramsS.substring(1);
	}
}
