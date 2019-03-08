package visitors;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;


public class MethodDeclarationVisitor extends ASTVisitor{
	HashSet<MethodDeclaration> callSuspectMethods = new HashSet<>();
	HashSet<MethodDeclaration> suspectDeclarations = new HashSet<MethodDeclaration>();

	public MethodDeclarationVisitor(Set<MethodDeclaration> suspectDeclarations) {
		this.suspectDeclarations.addAll(suspectDeclarations);
	}
	
	@Override
	public boolean visit(MethodDeclaration node) {
		
		MethodInvocationVisitor methodInvocationVisitor = new MethodInvocationVisitor(suspectDeclarations);
		node.accept(methodInvocationVisitor);
		
		if(!methodInvocationVisitor.getSuspectInvocations().isEmpty()) {
			callSuspectMethods.add(node);
			suspectDeclarations.add(node);
		}
		
		return super.visit(node);
	}
	
	public HashSet<MethodDeclaration> getCallSuspectMethods() {
		return callSuspectMethods;
	}
	
}
