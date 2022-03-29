package minijavaparser.simboltable;

import java.util.LinkedList;

public class MClass implements VariableContainer{
	public String declName;
	public MType classAsType;
	public MType superclass = null;
	
	public LinkedList<MFunc> metods = new LinkedList<MFunc>();
	public LinkedList<MVar> fields = new LinkedList<MVar>();
	
	public MType getAsType() {
		return classAsType;
	}
	
	@Override
	public boolean addVar(MVar newVar) {
		
		if(getField(newVar) == null) {
			fields.addLast(newVar);
			return true;
		}
		else
			return false;
	}
	
	public MVar getField(MVar otherVar) {
		for(var field : fields) {
			if(field.equals(otherVar))
				return field;
		}
		
		if(superclass != null && superclass.declType != null)
			return superclass.declType.getField(otherVar);
		
		return null;
	}
	
	public MVar getField(String otherVarName) {
		for(var field : fields) {
			if(field.declName.equals(otherVarName))
				return field;
		}
		
		if(superclass != null && superclass.declType != null)
			return superclass.declType.getField(otherVarName);
		
		return null;
	}
	
	public MFunc getMethod(MFunc otherFuncSign) {
		for(var field : metods) {
			if(field.equals(otherFuncSign))
				return field;
		}
		
		if(superclass != null && superclass.declType != null)
			return superclass.declType.getMethod(otherFuncSign);
		
		return null;
	}
	
	public MFunc getMethod(String name, LinkedList<MVar> params) {
		for(var func : metods) { //check in the current class
			if(func.equals(name, params))
				return func;
		}
		
		if(superclass != null && superclass.declType != null)
			return superclass.declType.getMethod(name, params);
		
		return null;
	}
	
	public MFunc getMethodWithSignature(String name, LinkedList<MType> paramsType) {
		for(var func : metods) { //check in the current class
			if(func.hasSignature(name, paramsType))
				return func;
		}
		
		if(superclass != null && superclass.declType != null)
			return superclass.declType.getMethodWithSignature(name, paramsType);
		
		return null;
	}
}
