package visitors;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TryStatement;



public class TryStatementVisitor extends ASTVisitor {

	List<TryCatchInfo> tryCatchInfo = new LinkedList<>();
	public static File f;
	
	public void setFile(File f){
		this.f = f;
	}

	@Override
	public boolean visit(TryStatement node) {
		TryCatchInfo tryCatchInfoObject = new TryCatchInfo();
		tryCatchInfoObject = getExceptionNamesFromCatchBlock(node);
		MethodInvocationVisitor visitor = getMethodInvocation(node);
		tryCatchInfoObject.setExceptionThrownByMethod(visitor.getExceptionNames());
		tryCatchInfoObject.setBinaryNameforThrowExceptions(visitor.getClassNames());
		tryCatchInfoObject.setFuncName(visitor.getFunctionName());
		tryCatchInfo.add(tryCatchInfoObject);
		return super.visit(node);
	}

	/**
	 * get all exception names
	 * 
	 * @param node
	 * @return
	 */
	private static MethodInvocationVisitor getMethodInvocation(TryStatement node) {
		BlockVisitor blockVisitor = new BlockVisitor();
		node.getBody().accept(blockVisitor);

		// MethodInvocationVisitor invocationVisitor =
		// callMethodInvocationForTryBlock(blockVisitor);
		return callMethodInvocationForTryBlock(blockVisitor);
	}

	/**
	 * Method invocation for call to method in each line in block
	 * 
	 * @param blockVisitor
	 * @return
	 */
	private static MethodInvocationVisitor callMethodInvocationForTryBlock(BlockVisitor blockVisitor) {
		HashSet<MethodDeclaration> suspectException = new HashSet<MethodDeclaration>();
		MethodInvocationVisitor methodInvocation = new MethodInvocationVisitor(suspectException);

		for (Iterator iterator = blockVisitor.getStatements().iterator(); iterator.hasNext();) {
			Statement statement = (Statement) iterator.next();
			if (statement instanceof ExpressionStatement) {
				ExpressionStatement expressionStatement = (ExpressionStatement) statement;
				expressionStatement.getExpression().accept(methodInvocation);
			}

		}
		return methodInvocation;
	}

	/**
	 * 
	 * Get exception from catch block
	 * 
	 * @param node
	 * @return
	 */
	private static TryCatchInfo getExceptionNamesFromCatchBlock(TryStatement node) {
		TryCatchInfo info = new TryCatchInfo();
		for (Iterator<CatchClause> iterator = node.catchClauses().iterator(); iterator.hasNext();) {
			CatchClause catchClause = iterator.next();
			info.addCatchBlockException(catchClause.getException().getType().toString());
			info.setBody(node.toString());
			info.setFileName(f.getAbsolutePath());
		}
		return info;
	}

	public List<TryCatchInfo> getTryCatchInfo() {
		return tryCatchInfo;
	}

	public void setTryCatchInfo(List<TryCatchInfo> tryCatchInfo) {
		this.tryCatchInfo = tryCatchInfo;
	}

	
	private static  ASTNode findParentMethodDeclaration(ASTNode node) {
		
		
		
		if(node.getParent().getNodeType() == ASTNode.METHOD_DECLARATION) {
			return node.getParent();
		} else {
			return findParentMethodDeclaration(node.getParent());
		}
	}
	
	private static  MethodDeclaration findMethodForCatch(CatchClause catchClause) {
		return (MethodDeclaration) findParentMethodDeclaration(catchClause);
	}
	
}
