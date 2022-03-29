package minijavaparser.simboltable;

import java.util.LinkedList;

import minijavaparser.simboltable.exceptions.*;

public class SymbolTable {
	private static LinkedList<MClass> classeTable;
	private static LinkedList<MType> TypeTable;
	
	public static void init() {
		classeTable = new LinkedList<MClass>();
		TypeTable = new LinkedList<MType>();
		
		//populate type table with primite types, later with the predefined classes.
		MType initType;

		// int
		initType = new MType();
		initType.typeName = "int";
		TypeTable.add(initType);
		
		// int[] 
		initType = new MType();
		initType.typeName = "int[]"; 
		TypeTable.add(initType);


		// boolean
		initType = new MType();
		initType.typeName = "boolean";
		TypeTable.add(initType);
		
		// populate class table with the predefined classes
		MClass initTemp;
		
		//add Object class
		initTemp = new MClass();
		initTemp.declName = "Object";
		classeTable.add(initTemp);
		
		//add Object class into type table
		initType = new MType();
		initType.typeName = initTemp.declName;
		initType.declType = initTemp;
		TypeTable.add(initType);
		
//		//add String class type
//		initTemp = new MClass();
//		initTemp.declName = "String";
//		initTemp.superclass = initType;
//		classeTable.add(initTemp);
//		
		
		 //add String[] type into type table // 
		initType = new MType();
		initType.typeName = "String[]";
		initType.declType = null;
		TypeTable.add(initType);
	}
	
	public static void checkAllDefined() {
		for(var type : TypeTable) {
			if(type.notDefined)
				throw new TypeNotDefinedButUsedException(type.typeName, -1);
		}
	}
	
	public static void secondCheck() {
		for(MClass mClass: classeTable) {
			if( mClass.superclass == null)
				continue;
			MClass superClass = mClass.superclass.declType;
			for(MVar field : mClass.fields) {
				if(superClass.getField(field) != null) //check if fields remain unqiue after parsing superclasses defined after subclasses
					throw new VariableRedefinitionException(field.declName, field.declLine);
			}
			
			for(MFunc method : mClass.metods) {
				if(superClass.getMethod(method) != null) //check if methods remain unqiue after parsing superclasses defined after subclasses
					throw new VariableRedefinitionException(method.declName, method.declLine);
			}
		}
	}
	
	//adding new stuff into the table
	
	//for classes
	public static MClass addNewClass(String className) { // we suppose that the class was verified not to exist
		return addNewClass(className, "Object");
	}
	
	public static MClass addNewClass(String className, String superclass) {
		MClass newClass = new MClass();
		newClass.declName = className;
		classeTable.addLast(newClass);
		MType type = findType(className);
		type.declType = newClass;
		type.notDefined = false;
		newClass.superclass = findType(superclass);
		newClass.classAsType = type;
		return newClass;
	}
	
	public static MFunc addNewMethod(String methodName, LinkedList<MVar> params, MClass classRef) {
		
		MFunc method = classRef.getMethod(methodName, params); // check for the asked signature
		if(method != null && method.parent.declName.equals(classRef.declName)) //check if the function exists and if it's in this class, not in a superclass
			return null; // redefinition!
		
		//if we arive here => method name is valid;
		MFunc fun = new MFunc();
		fun.declName = methodName;
		fun.parent = classRef;
		fun.parameters = params;
		classRef.metods.addLast(fun);
		
		return fun;
	}
	
	//getters and checkers
	
	//for types in general
	public static MType findType(String name) {
		for(var cls : TypeTable) {
			if(cls.typeName.equals(name))
				return cls;
		}
		//for forward use; not yet defined
		MType notDefined = new MType();
		notDefined.typeName = name;
		notDefined.notDefined = true;
		TypeTable.addLast(notDefined);
		return notDefined;
	}
	
	//for classes
	public static MClass findClass(String name) {
		for(var cls : classeTable) {
			if(cls.declName.equals(name))
				return cls;
		}
		return null;
	}
	
	public static boolean varDefined(String varName, MFunc functionRef) {
		if (functionRef.getVarRef(varName) != null) // mostly a sanity check
			return true;
		return false;
	}
}
