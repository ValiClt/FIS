package minijavaparser.visitors;

import minijavaparser.*;

public class DisplayVisitor implements MiniJavaVisitor {
	

	@Override
	public Object visit(SimpleNode node, Object data) {
		node.childrenAccept(this, data);
		return null;
	}

	@Override
	public Object visit(ASTProgram node, Object data) {
		System.out.println(node);
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public Object visit(ASTMainClass node, Object data) {
		System.out.println(node + ": " + ((Token)node.jjtGetValue()).image);
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public Object visit(ASTMainFuncDecl node, Object data) {
		System.out.println(node);
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public Object visit(ASTClassDecl node, Object data) {
		var value = ((String[]) node.jjtGetValue());
		String info = value[0];
		if(value.length>1)	//if we have extends
			info+= " -> " + value[1];
		System.out.println(node + ": " + info);
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public Object visit(ASTVarDecl node, Object data) {
		String[] type = new String[1];
		node.childrenAccept(this, type);
		System.out.println(node + ": " + ((String) node.jjtGetValue()) + ":" + type[0]);
		return data;
	}

	@Override
	public Object visit(ASTMethodDecl node, Object data) {
		System.out.println(node);
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public Object visit(ASTFormalList node, Object data) {
		System.out.println(node);
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public Object visit(ASTFormalRest node, Object data) {
		System.out.println(node);
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public Object visit(ASTType node, Object data) {
		if(data != null)
			((String[]) data)[0] = (String) node.jjtGetValue();
		System.out.println(node + ": " + (String) node.jjtGetValue());
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public Object visit(ASTStatement node, Object data) {
		System.out.println(node);
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public Object visit(ASTOrCond node, Object data) {
		System.out.println(node);
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public Object visit(ASTAndCond node, Object data) {
		System.out.println(node);
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public Object visit(ASTRelExp node, Object data) {
		System.out.println(node);
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public Object visit(ASTAdOp node, Object data) {
		System.out.println(node + ": " + (String) node.jjtGetValue());
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public Object visit(ASTArExp node, Object data) {
		System.out.println(node);
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public Object visit(ASTTerm node, Object data) {
		System.out.println(node);
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public Object visit(ASTMulOp node, Object data) {
		System.out.println(node + ": " + (String) node.jjtGetValue());
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public Object visit(ASTFactor node, Object data) {
		System.out.println(node);
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public Object visit(ASTFactorRest node, Object data) {
		System.out.println(node + ": "  + (String) node.jjtGetValue());
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public Object visit(ASTExpFinal node, Object data) {
		System.out.println(node + ": " + (String) node.jjtGetValue());
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public Object visit(ASTExpList node, Object data) {
		System.out.println(node);
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public Object visit(ASTRelOp node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

}
