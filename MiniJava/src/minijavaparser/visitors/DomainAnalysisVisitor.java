package minijavaparser.visitors;

import java.util.LinkedList;

import minijavaparser.*;
import minijavaparser.simboltable.*;
import minijavaparser.simboltable.exceptions.*;

public class DomainAnalysisVisitor implements MiniJavaVisitor {

	@Override
	public Object visit(SimpleNode node, Object data) {
		node.childrenAccept(this, data);
		return null;
	}
	
	/*here I add the classes into the class table and into the type table, respectively. Also, I initialise the symbol table(s)*/
	@Override
	public Object visit(ASTProgram node, Object data) {		
		SymbolTable.init(); // we initialise the symbol table
		node.childrenAccept(this, null);
		SymbolTable.checkAllDefined(); // we verify if all used types were defined untill the end of the file
		SymbolTable.secondCheck(); //verify if there are superclasses defined after the subclasses for member duplicates 
		
		//verify then with the VariableCheckVisitor
		VariableCheckVisitor vis = new VariableCheckVisitor();
		node.jjtAccept(vis, null);
		
		return null;
	}

	@Override
	public Object visit(ASTMainClass node, Object data) {
		var newClass =  SymbolTable.addNewClass(((Token) node.jjtGetValue()).image);
		node.childrenAccept(this, newClass);
		return null;
	}

	@Override
	public Object visit(ASTMainFuncDecl node, Object data) {
		MFunc mainFunc = new MFunc();
		mainFunc.declName = "main";
		mainFunc.declRetType = null;
		MVar args = new MVar();
		args.declName = ((Token)node.jjtGetValue()).image;
		args.declLine = ((Token)node.jjtGetValue()).beginLine;
		args.declType = SymbolTable.findType("String[]");
		mainFunc.parameters.add(args);
		mainFunc.parent = (MClass)data;
		mainFunc.declLine = args.declLine;
		node.jjtSetValue(mainFunc);
		return null; // no chieldAccept is needed because the chields are Exp which are analised later
	}

	@Override
	public Object visit(ASTClassDecl node, Object data) {
		Token[] clsData = (Token[]) node.jjtGetValue();
		MClass thisClass;
		
		//verify that is unique
		if(SymbolTable.findClass(clsData[0].image) != null)
			throw new TypeRedefinitionException(clsData[0].image, clsData[0].beginLine);
		
		//add new class
		if(clsData.length == 1)
			thisClass = SymbolTable.addNewClass(clsData[0].image);
		else if(clsData.length == 2) {
			thisClass = SymbolTable.addNewClass(clsData[0].image, clsData[1].image);
			
			//check for inheritance cycle.
			MClass soup = SymbolTable.findClass(clsData[1].image);
			while(soup != null) {
				//if the superclass is not yet defined so be it. It was introduce in the type table and will be checked if it was defined at the end.
				//or if at some point the superclass field is null, meaning no inheritance, then we stop and it's ok
				if(soup.superclass == null) break; //no more superclasses
				else {
					soup = soup.superclass.declType;
					if(soup == thisClass)
						throw new CycleInheritanceException(clsData[0].image, clsData[0].beginLine);
				}
			}
		}
		else
			throw new RuntimeException("unexpected number of tokens for class " + clsData[0].image + ", number of args " + clsData.length + "at line" + clsData[0].beginLine); // sanity check for posible future modifs
		
		node.childrenAccept(this, thisClass);
		return null;
	}

	@Override
	public Object visit(ASTVarDecl node, Object data) {
		MVar newVar = new MVar();
		newVar.declName = ((Token) node.jjtGetValue()).image;
		newVar.declLine = ((Token) node.jjtGetValue()).beginLine;
		node.jjtGetChild(0).jjtAccept(this, newVar); //get the type returned by the type node
		boolean success = ((VariableContainer)data).addVar(newVar); // we get either the class whom member is or the function whom local variable is
		if(success) //everithing was ok
			return null;
		else //variable already exists
			throw new VariableRedefinitionException(newVar.declName, ((Token) node.jjtGetValue()).beginLine);
	}

	@Override
	public Object visit(ASTMethodDecl node, Object data) {
		LinkedList<MVar> params = new LinkedList<MVar>();
		
		//make the param list
		var paramNode =  node.jjtGetChild(1);
		if(paramNode instanceof ASTFormalList) //if we have params
		paramNode.jjtAccept(this, params);
		
		Token fun = (Token) node.jjtGetValue();
		MFunc thisFunc =  SymbolTable.addNewMethod(fun.image, params, (MClass) data);
		if(thisFunc == null) //signature already exists
			throw new VariableRedefinitionException(fun.image, fun.beginLine);
		
		thisFunc.declLine = fun.beginLine;
		
		for(int i=0; i<node.getChildCount(); i++) {
			if(i==1 && (paramNode instanceof ASTFormalList)) continue; //ignore the ParameterList node
			node.jjtGetChild(i).jjtAccept(this, thisFunc);
		}
		
		Object[] newVar = new Object[] {fun/*old val on first place*/,  thisFunc /*pointer to method table*/};
		node.jjtSetValue(newVar);
		
		return null;
	}

	@Override
	public Object visit(ASTFormalList node, Object data) {
		MVar thisParam = new MVar();
		Token funcTok = (Token) node.jjtGetValue();
		thisParam.declName = funcTok.image;
		thisParam.declLine = funcTok.beginLine;
		((LinkedList<MVar>)data).addLast(thisParam);
		
		ASTType fst = (ASTType)node.jjtGetChild(0); //first node should be the type. if not, an cast exception will occure
		fst.jjtAccept(this, thisParam);
		
		for(int i=1; i< node.getChildCount(); i++) { //if there aren't any more params this for will cycle 0 times
			node.jjtGetChild(i).jjtAccept(this, data); //for the Other parameters
		}
		return null;
	}

	@Override
	public Object visit(ASTFormalRest node, Object data) {
		Token thisPrmTk = (Token) node.jjtGetValue();
		for(var pr : (LinkedList<MVar>)data) {
			if(pr.declName.equals(thisPrmTk.image))
				throw new VariableRedefinitionException(thisPrmTk.image, thisPrmTk.beginLine);
		}
		
		MVar thisParam = new MVar();
		thisParam.declName = thisPrmTk.image;
		thisParam.declLine = thisPrmTk.beginLine;
		
		((LinkedList<MVar>)data).addLast(thisParam);
		node.jjtGetChild(0).jjtAccept(this, thisParam); //supose we do not have anything but the type nod as child
		return null;
	}

	@Override
	public Object visit(ASTType node, Object data) {
		((TypedNode)data).setType(
									SymbolTable.findType( ((Token)node.jjtGetValue()).image )
								 ); //set type for TypedNode implementations (MVar and MFunc)
		return null;
	}

	@Override
	public Object visit(ASTStatement node, Object data) {
		//no need to do nothing
		return null;
	}

	@Override
	public Object visit(ASTOrCond node, Object data) {
		//no need to do nothing
		return null;
	}

	@Override
	public Object visit(ASTAndCond node, Object data) {
		//no need to do nothing
		return null;
	}

	@Override
	public Object visit(ASTRelExp node, Object data) {
		//no need to do nothing
		return null;
	}
	
	@Override
	public Object visit(ASTRelOp node, Object data) { //here nothing to do
		//no need to do nothing
		return null;
	}
	
	@Override
	public Object visit(ASTArExp node, Object data) {
		//no need to do nothing
		return null;
	}
	
	@Override
	public Object visit(ASTAdOp node, Object data) { //here nothing to do
		//no need to do nothing
		return null;
	}
	
	@Override
	public Object visit(ASTTerm node, Object data) {
		//no need to do nothing
		return null;
	}

	@Override
	public Object visit(ASTMulOp node, Object data) { //here nothing to do
		//no need to do nothing
		return null;
	}

	@Override
	public Object visit(ASTFactor node, Object data) {
		//no need to do nothing
		return null;
	}

	@Override
	public Object visit(ASTFactorRest node, Object data) { 
		//no need to do nothing
		return null;
	}

	@Override
	public Object visit(ASTExpFinal node, Object data) {		
		//no need to do nothing
		return null;
	}

	@Override
	public Object visit(ASTExpList node, Object data) {
		//no need to do nothing
		return null;
	}

	

}
