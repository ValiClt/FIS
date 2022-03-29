package minijavaparser.simboltable;

public class MVar implements TypedNode{
	public String declName;
	public MType declType;
	public int declLine = -1;
	
	@Override
	public void setType(MType type) {
		this.declType = type;
	}
	
	@Override
	public boolean equals(Object o) {
		
		if(!(o instanceof MVar))
			return false;
		
		return ((MVar)o).declName.equals(this.declName);
	}
}
