package minijavaparser.visitors;

import minijavaparser.*;
import minijavaparser.simboltable.*;
import minijavaparser.simboltable.exceptions.*;

public class VariableCheckVisitor implements MiniJavaVisitor {

	@Override
	public Object visit(SimpleNode node, Object data) {
		node.childrenAccept(this, data);
		return null;
	}

	@Override
	public Object visit(ASTProgram node, Object data) {
		node.childrenAccept(this, null);
		return null;
	}

	@Override
	public Object visit(ASTMainClass node, Object data) {
		node.childrenAccept(this, null); // no more types to check
		return null;
	}

	@Override
	public Object visit(ASTMainFuncDecl node, Object data) {
		node.childrenAccept(this, node.jjtGetValue()); //check the expresion
		return null;
	}

	@Override
	public Object visit(ASTClassDecl node, Object data) {
		node.childrenAccept(this, null);
		return null;
	}

	@Override
	public Object visit(ASTVarDecl node, Object data) {
		//no need to do nothing
		return null;
	}

	@Override
	public Object visit(ASTMethodDecl node, Object data) {
		Object[] info = (Object[]) node.jjtGetValue(); //from SymbolTablePopulateVisitor : token and then ref to method table for this node
		
		node.childrenAccept(this, (MFunc) info[1]); //check if info[1] is truly a fun ref
		return null;
	}

	@Override
	public Object visit(ASTFormalList node, Object data) {
		//no need to do nothing
		return null;
	}

	@Override //I need this and the other in order to identify the function!
	public Object visit(ASTFormalRest node, Object data) {
		//no need to do nothing
		return null;
	}

	@Override
	public Object visit(ASTType node, Object data) {
		//no need to do nothing
		return null;
	}

	@Override
	public Object visit(ASTStatement node, Object data) {
		Object value = node.jjtGetValue();
		
		if(value instanceof Object[]) { //for id ... = we check the "id" to be defined
			Token id = (Token) (((Object[])value)[0]);
			if(!SymbolTable.varDefined(id.image, (MFunc)data))
				throw new VariableNotDefinedException(id.image, id.beginLine);
		}
		
		//if we have either of them, we check the next statement/expression		
		node.childrenAccept(this, data);
		return null;
	}

	@Override
	public Object visit(ASTOrCond node, Object data) {
		node.childrenAccept(this, data);
		return null;
	}

	@Override
	public Object visit(ASTAndCond node, Object data) {
		node.childrenAccept(this, data);
		return null;
	}

	@Override
	public Object visit(ASTRelExp node, Object data) {
		node.childrenAccept(this, data);
		return null;
	}

	@Override
	public Object visit(ASTRelOp node, Object data) {
		// nothing to do here
		return null;
	}

	@Override
	public Object visit(ASTArExp node, Object data) {
		node.childrenAccept(this, data);
		return null;
	}

	@Override
	public Object visit(ASTAdOp node, Object data) {
		// nothing to do here
		return null;
	}

	@Override
	public Object visit(ASTTerm node, Object data) {
		node.childrenAccept(this, data);
		return null;
	}

	@Override
	public Object visit(ASTMulOp node, Object data) {
		// nothing to do here
		return null;
	}

	@Override
	public Object visit(ASTFactor node, Object data) {
		node.childrenAccept(this, data);
		return null;
	}

	@Override
	public Object visit(ASTFactorRest node, Object data) {
		node.childrenAccept(this, data); //verify  [Exp()]
		// nothing to do for [], .id() or .length not verified here; for id should know the type the var on which is defined! Only check subexpresions
		return null;
	}

	@Override
	public Object visit(ASTExpFinal node, Object data) {		
		//fst I check if the node has children. If he has, than is non-terminal and I have nothing to do anymore here
		if(node.getChildCount() > 0) {
			if(node.getChildCount() == 1){ //sanity check, normaly this kind of nodes have no more than 1 Expresion/Factor non-terminals, so shouldn't have more than one node
				return node.jjtGetChild(0).jjtAccept(this, data);
			}
			throw new RuntimeException("Node tree unexpected");
		}
		//else if it's one of the others variants we test which
		Object[] whatOp = (Object[]) node.jjtGetValue();
		if(((String)whatOp[0]).startsWith("Object")) { // id
			if(!SymbolTable.varDefined(((Token)whatOp[1]).image, (MFunc)data))
				throw new VariableNotDefinedException(((Token)whatOp[1]).image,((Token)whatOp[1]).beginLine);
		}
		else if(((String)whatOp[0]).startsWith("New Op")) { //new id()
			if(SymbolTable.findClass(((Token)whatOp[1]).image) == null) {
				throw new TypeNotDefinedButUsedException(((Token)whatOp[1]).image,((Token)whatOp[1]).beginLine);
			}
		}
		return null;
	}

	@Override
	public Object visit(ASTExpList node, Object data) {
		node.childrenAccept(this, data);
		return null;
	}

}
