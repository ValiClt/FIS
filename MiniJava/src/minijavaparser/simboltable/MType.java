package minijavaparser.simboltable;

public class MType {
	public String typeName;
	public MClass declType;
	public boolean notDefined=false;
	
	public boolean equals(Object o) {
		if(o instanceof MType) {
			return ((MType)o).typeName.equals(typeName);
		}
		return false;
	}
	
	public boolean isSubtypeTypeOf(MType otherType) {
		if(typeName.equals(otherType.typeName))
			return true;
		
		if(this.declType == null) //is primitive
			return false;
		
		if(this.declType.superclass == null) //has no superclass
			return false;
		
		return this.declType.superclass.isSubtypeTypeOf(otherType);
	}
	
	public boolean isSuperTypeOf(MType otherType) {
		return otherType.isSubtypeTypeOf(this);
	}
}
