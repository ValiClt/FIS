package minijavaparser.simboltable;

import java.util.LinkedList;

public class MFunc implements VariableContainer, TypedNode{
	public String declName;
	public MType declRetType;
	
	public int declLine = -1;
	
	public LinkedList<MVar> parameters = new LinkedList<MVar>();
	public LinkedList<MVar> localVars = new LinkedList<MVar>();
	public MClass parent;
	
	@Override
	public boolean addVar(MVar newVar) {
		for(var fld : localVars) {
			if(fld.declName.equals(newVar.declName))
				return false;
		}
		
		for(var fld : parameters) {
			if(fld.declName.equals(newVar.declName))
				return false;
		}
		localVars.addLast(newVar);
		return true;
	}

	@Override
	public void setType(MType type) {
		declRetType = type;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof MFunc) {
			MFunc of = (MFunc)o;
			
			if(of.declName != declName)
				return false;
			
			if(of.parameters.size() != this.parameters.size())
				return false;
			
			for(int i=0; i<this.parameters.size(); i++)
				if(!of.parameters.get(i).equals(this.parameters.get(i)))
					return false;
			return true;
		}
		return false;
	}
	
	public boolean equals(String name, LinkedList<MVar> params) {
		if (!declName.equals(name))
			return false;
		
		if(params == null)
			if(parameters.size() == 0)
				return true;
			else
				return false;
		
		if (parameters.size() != params.size())
			return false;

		for (int i = 0; i < params.size(); i++)
			if (!parameters.get(i).equals(params.get(i)))
				return false;
		return true;
	}
	
	public boolean hasSignature(String name, LinkedList<MType> params) {
		if (!declName.equals(name))
			return false;
		
		if(params == null)
			if(parameters.size() == 0)
				return true;
			else
				return false;

		if (parameters.size() != params.size())
			return false;

		for (int i = 0; i < params.size(); i++)
			if (!parameters.get(i).declType.equals(params.get(i)))
				return false;
		return true;
	}
	
	public MVar getVarRef(MVar otherVar) {
		for(var field : parameters) {
			if(field.equals(otherVar))
				return field;
		}
		
		for(var field : localVars) {
			if(field.equals(otherVar))
				return field;
		}
		
		return parent.getField(otherVar);
	}
	
	public MVar getVarRef(String otherVarName) {
		for(var field : parameters) {
			if(field.declName.equals(otherVarName))
				return field;
		}
		
		for(var field : localVars) {
			if(field.declName.equals(otherVarName))
				return field;
		}
		
		return parent.getField(otherVarName);
	}
}
