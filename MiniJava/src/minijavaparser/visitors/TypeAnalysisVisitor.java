package minijavaparser.visitors;

import java.util.LinkedList;

import minijavaparser.*;
import minijavaparser.simboltable.*;
import minijavaparser.simboltable.exceptions.*;

public class TypeAnalysisVisitor implements MiniJavaVisitor {

	@Override
	public Object visit(SimpleNode node, Object data) {
		node.childrenAccept(this, data);
		return null;
	}

	@Override
	public Object visit(ASTProgram node, Object data) {
		node.childrenAccept(this, data);
		return null;
	}

	@Override
	public Object visit(ASTMainClass node, Object data) {
		node.childrenAccept(this, data);
		return null;
	}

	@Override
	public Object visit(ASTMainFuncDecl node, Object data) {

		MFunc thisFunc = (MFunc) node.jjtGetValue();

		node.childrenAccept(this, thisFunc);
		return null;
	}

	@Override
	public Object visit(ASTClassDecl node, Object data) {
		node.childrenAccept(this, data);
		return null;
	}

	@Override
	public Object visit(ASTVarDecl node, Object data) {
		// no use in this
		return null;
	}

	@Override
	public Object visit(ASTMethodDecl node, Object data) {

		MFunc thisFunc = (MFunc) ((Object[]) node.jjtGetValue())[1];

		node.childrenAccept(this, thisFunc);
		return null;
	}

	@Override
	public Object visit(ASTFormalList node, Object data) {
		// no use in this
		return null;
	}

	@Override
	public Object visit(ASTFormalRest node, Object data) {
		// no use in this
		return null;
	}

	@Override
	public Object visit(ASTType node, Object data) {
		// no use in this
		return null;
	}

	@Override
	public Object visit(ASTStatement node, Object data) {
		
		Object value = node.jjtGetValue();
		
		if (value instanceof Token) { // if while and println
			Token t = (Token) value;
			if (t.kind == MiniJavaConstants.PRINTLN) {
				node.childrenAccept(this, data);
			} else { // if or while: chekc fist child to return boolean the rest don't care
				MType tp = (MType) node.jjtGetChild(0).jjtAccept(this, data);
				if (!tp.equals(SymbolTable.findType("boolean")))
					throw new InvalidTypeException("boolean", tp.typeName, t.beginLine);
				for (int i = 1; i < node.getChildCount(); i++)
					node.jjtGetChild(i).jjtAccept(this, data);
			}
		} else if (value instanceof Object[]) {
			Token t = (Token) ((Object[]) value)[0];

			if ((boolean) ((Object[]) value)[1]) { // id[]=
				MType idType 	= ((MFunc)data).getVarRef(t.image).declType;
				MType indexType = (MType) node.jjtGetChild(0).jjtAccept(this, data);
				MType exprType 	= (MType) node.jjtGetChild(1).jjtAccept(this, data);
				
				if(!idType.typeName.equals(SymbolTable.findType("int[]").typeName))
					throw new InvalidTypeException("int[]", idType.typeName, t.beginLine, "before \"[]\"");
				
				if(!indexType.typeName.equals(SymbolTable.findType("int").typeName))
					throw new InvalidTypeException("int", indexType.typeName, t.beginLine, "inside \"[]\"");
				
				if(!exprType.typeName.equals(SymbolTable.findType("int").typeName))
					throw new InvalidTypeException("int", exprType.typeName, t.beginLine, "after \"=\"");
				
			} else { // id=
				MType returnedType = (MType) node.jjtGetChild(0).jjtAccept(this, data);
				
				MType idType = ((MFunc)data).getVarRef(t.image).declType;
				
				if(!idType.isSuperTypeOf(returnedType)) {
					throw new InvalidTypeException(idType.typeName, returnedType.typeName, t.beginLine);
				}
			}
		} else //function call without an id= before or something like that
			node.childrenAccept(this, data);
		return null;
	}

	@Override
	public Object visit(ASTOrCond node, Object data) {
		if (node.getChildCount() == 1) { // here is the case that it can be an arithmetic expr or other type of boolean
											// expressions (and, comparation)
			// either way, we return the type of the expression bellow
			return node.jjtGetChild(0).jjtAccept(this, data);
		} else {
			// other way we have something like AndCond Or AndCond (Or AndCond)*, so we have
			// multiple children, each must be of type boolean because
			// we have Or between each 2 (Or must take 2 boolean sub-expressions

			MType subExprType;
			MType boolType = SymbolTable.findType("boolean");

			for (int i = 0; i < node.getChildCount(); i++) {
				subExprType = (MType) node.jjtGetChild(i).jjtAccept(this, data);
				if (!subExprType.equals(boolType)) { // if it is not bool as expected, than throw an error
					throw new InvalidTypeException("boolean", subExprType.typeName,
							((Token) node.jjtGetValue()).beginLine);
				}
			}
			return boolType;
		}
	}

	@Override
	public Object visit(ASTAndCond node, Object data) { // same as OrCond
		if (node.getChildCount() == 1) { // here is the case that it can be an arithmetic expr or other type of boolean
											// expressions (and, comparation)
			// either way, we return the type of the expression bellow
			return node.jjtGetChild(0).jjtAccept(this, data);
		} else {
			// other way we have something like AndCond Or AndCond (Or AndCond)*, so we have
			// multiple children, each must be of type boolean because
			// we have Or between each 2 (Or must take 2 boolean sub-expressions

			MType subExprType;
			MType boolType = SymbolTable.findType("boolean");

			for (int i = 0; i < node.getChildCount(); i++) {
				subExprType = (MType) node.jjtGetChild(i).jjtAccept(this, data);
				if (!subExprType.equals(boolType)) { // if it is not bool as expected, than throw an error
					throw new InvalidTypeException("boolean", subExprType.typeName,
							((Token) node.jjtGetValue()).beginLine);
				}
			}
			return boolType;
		}
	}

	@Override
	public Object visit(ASTRelExp node, Object data) {
		if (node.getChildCount() == 1) { // here is the case that it can be an arithmetic expr or other type of boolean
											// expressions (and, comparation)
			// either way, we return the type of the expression bellow
			return node.jjtGetChild(0).jjtAccept(this, data);
		} else { // in this case we have 3 children : ArExp1 RelOp ArExp2
			MType subExprType;
			MType intType = SymbolTable.findType("int");
			Token t = (Token) node.jjtGetChild(1).jjtAccept(this, null);

			subExprType = (MType) node.jjtGetChild(0).jjtAccept(this, data); // get type of ArExp1, which should be int!
			if (!subExprType.equals(intType)) { // if it is int as expected then everithin is fine!
				throw new InvalidTypeException("int", subExprType.typeName, t.beginLine, "before " + t.image);
			}

			subExprType = (MType) node.jjtGetChild(2).jjtAccept(this, data); // get type of ArExp2, which should be int!
			if (!subExprType.equals(intType)) { // if it is int as expected then everithin is fine!
				throw new InvalidTypeException("int", subExprType.typeName, t.beginLine, "after " + t.image);
			}
			// if no throw was issued then it's ok, so we return the type boolean, which is
			// the type of expression "ArExp1 RelOp ArExp2"
			return SymbolTable.findType("boolean"); // then everithing is fine!
		}
	}

	@Override
	public Object visit(ASTRelOp node, Object data) {
		// no use in this
		return node.jjtGetValue(); // if called return the token for the line
	}

	@Override
	public Object visit(ASTArExp node, Object data) {
		// here also we can expect 2 cases:
		// 1 Term
		if (node.getChildCount() == 1) {
			return node.jjtGetChild(0).jjtAccept(this, data); // same as before for this basic case
		}
		// 2 Term ([+|-] Term) +
		else {
			MType subExprType;
			MType intType = SymbolTable.findType("int");

			Token t = (Token) node.jjtGetChild(1).jjtAccept(this, data); // first + or -

			for (int i = 0; i < node.getChildCount(); i += 2) {
				subExprType = (MType) node.jjtGetChild(i).jjtAccept(this, data); // get type of ArExp1, which should be
																					// int!
				if (!subExprType.equals(intType)) { // if it is int as expected then everithin is fine!
					throw new InvalidTypeException("int", subExprType.typeName, t.beginLine);
				}
				// if no throw was issued then it's ok, so we return the type boolean, which is
				// the type of expression "ArExp1 RelOp ArExp2"
			}

			return SymbolTable.findType("int"); // then everithing is fine!
		}
	}

	@Override
	public Object visit(ASTAdOp node, Object data) {
		// no use in this
		return node.jjtGetValue(); // we give the token for line and image
	}

	@Override
	public Object visit(ASTTerm node, Object data) {
		// here also we can expect 2 cases:
		// 1 Term
		if (node.getChildCount() == 1) {
			return node.jjtGetChild(0).jjtAccept(this, data); // same as before for this basic case
		}
		// 2 Factor ([*|/] Factor) +
		else {
			MType subExprType;
			MType intType = SymbolTable.findType("int");

			Token t = (Token) node.jjtGetChild(1).jjtAccept(this, data); // first * or /

			for (int i = 0; i < node.getChildCount(); i += 2) {
				subExprType = (MType) node.jjtGetChild(i).jjtAccept(this, data); // get type of ArExp1, which should be
																					// int!
				if (!subExprType.equals(intType)) { // if it is int as expected then everithin is fine!
					throw new InvalidTypeException("int", subExprType.typeName, t.beginLine);
				}
				// if no throw was issued then it's ok, so we return the type boolean, which is
				// the type of expression "ArExp1 RelOp ArExp2"
			}

			return SymbolTable.findType("int"); // then everithing is fine!
		}
	}

	@Override
	public Object visit(ASTMulOp node, Object data) {
		// no use in this
		return node.jjtGetValue(); // we give the token for line and image
	}

	@Override
	public Object visit(ASTFactor node, Object data) {
		// here also we can expect 2 cases:
		// 1 terminal : int-literal; true; false
		if (node.jjtGetValue() != null) {
			Token t = (Token) node.jjtGetValue();
			if (t.kind == MiniJavaConstants.INTEGER_LITERAL)
				return SymbolTable.findType("int");
			else // if(t.kind == MiniJavaConstants.TRUE || t.kind == MiniJavaConstants.FALSE)
				return SymbolTable.findType("boolean");
		}
		// 2 non-terminal
		else {
			// here 2 cases: we either have or don't have FactorRest node(s)
			MType retType = (MType) node.jjtGetChild(0).jjtAccept(this, data); // we analise the first node which is
																				// surely an ExpFinal node
			for (int i = 1; i < node.getChildCount(); i++) { // if we have FactorRest nodes, we iterate throw all of
																// them
				// data is a MFunc - the member function in which we are in, retType is the type
				// of the token before
				retType = (MType) node.jjtGetChild(i).jjtAccept(this, new Object[] { data, retType });
			}
			return retType;
		}
	}

	@Override
	public Object visit(ASTFactorRest node, Object data) {
		Object[] cData = (Object[]) data;
		MType previouseObjectType = (MType) cData[1];
		Object[] nodeInfo = (Object[]) node.jjtGetValue();
		String whatOp = (String) nodeInfo[0];
		Token info = (Token) nodeInfo[1];

		switch (whatOp) {
		case "CallOp":
			LinkedList<MType> givenParams = null;
			if (node.getChildCount() > 0)
				givenParams = (LinkedList<MType>) node.jjtGetChild(0).jjtAccept(this, cData[0]);

			if (previouseObjectType.declType != null) {
				var funcRef = previouseObjectType.declType.getMethodWithSignature(info.image, givenParams);
				if (funcRef != null)
					return funcRef.declRetType; // that's what's returned by a call;
				throw new InvalidFunctionSignature(info.image, givenParams, info.beginLine);
			}
			throw new InvalidTypeException("reference type", previouseObjectType.typeName, info.beginLine,
					"before ., call of " + info.image);

		case "length":
			if (previouseObjectType.equals(SymbolTable.findType("int[]"))) {
				return SymbolTable.findType("int");
			}
			throw new InvalidTypeException("int[]", previouseObjectType.typeName, info.beginLine, "before.");

		case "indexed":
			if (previouseObjectType.equals(SymbolTable.findType("int[]"))) {

				previouseObjectType = (MType) node.jjtGetChild(0).jjtAccept(this, cData[0]); // cData[0] is the crt
																								// function ref

				if (previouseObjectType.equals(SymbolTable.findType("int"))) {
					return SymbolTable.findType("int");
				}

				throw new InvalidTypeException("int", previouseObjectType.typeName, info.beginLine, "before \"]\"");
			}
			throw new InvalidTypeException("int[]", previouseObjectType.typeName, info.beginLine, "before \".\"");

		default:
			throw new RuntimeException("Something went very wrong");
		}
	}

	@Override
	public Object visit(ASTExpFinal node, Object data) {
		Object[] value = (Object[]) node.jjtGetValue();
		String op = (String) value[0];
		Token info = (Token) value[1];
		MFunc crtFunc = ((MFunc) data);

		switch (op) {
		case "This":
			return crtFunc.parent.getAsType(); // we return the type of this as the type of the class that contains the
												// method we are in
		case "Object":
			return crtFunc.getVarRef(info.image).declType; // we get the type of the variable
		case "New Op":
			if (info.kind == MiniJavaConstants.INT) {
				return SymbolTable.findType("int[]");
			} else {
				return SymbolTable.findType(info.image);
			}
		case "Subexpression":
			return node.jjtGetChild(0).jjtAccept(this, data);
		case "Not Op":
			MType retType = (MType) node.jjtGetChild(0).jjtAccept(this, data);
			if (retType == SymbolTable.findType("boolean")) // here because we have !child, we expect the type of chield
															// to be boolean
				return retType;
			else
				throw new InvalidTypeException("boolean", retType.typeName, info.beginColumn, "after !");
		default:
			throw new RuntimeException("Something went wrong, seems to be something not implemented");
		}
	}

	@Override
	public Object visit(ASTExpList node, Object data) {
		LinkedList<MType> paramList = new LinkedList<MType>();

		for (int i = 0; i < node.getChildCount(); i++)
			paramList.addLast((MType) node.jjtGetChild(i).jjtAccept(this, data)); // add into the list the type of the
																					// expressions that compose the
																					// param list
																					// we also send the ref to the crt
																					// func

		return paramList;
	}

}
