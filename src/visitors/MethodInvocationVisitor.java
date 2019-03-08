package visitors;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

public class MethodInvocationVisitor extends ASTVisitor{
	Set<MethodDeclaration> suspectDeclarations = new HashSet<MethodDeclaration>();
	HashSet<MethodInvocation> suspectInvocations = new HashSet<MethodInvocation>();
	List<String> exceptionNames = new LinkedList<>();
	List<Class>	classNames = new LinkedList<>();
	List<String> functionName = new LinkedList<>();
	
	public MethodInvocationVisitor(Set<MethodDeclaration> suspectDeclarations) {
		this.suspectDeclarations = suspectDeclarations;
	}
	
	@Override
	public boolean visit(MethodInvocation node) {
		IMethodBinding linkedDeclaration = node.resolveMethodBinding().getMethodDeclaration();
		
		// Added for over catch
				ITypeBinding[] exceptionTypes = linkedDeclaration.getExceptionTypes();
				for (int i = 0; i < exceptionTypes.length; i++) {
					exceptionNames.add(exceptionTypes[i].getBinaryName());
					classNames.add(exceptionTypes[i].getClass());
				}
				functionName.add(node.getName().toString());
				// end
		
		for(MethodDeclaration suspectDeclaration: suspectDeclarations) {
			
			if(suspectDeclaration.resolveBinding().getMethodDeclaration().isEqualTo(linkedDeclaration)) {
				suspectInvocations.add(node);
			}
			
		}
		return super.visit(node);
	}
	
	public HashSet<MethodInvocation> getSuspectInvocations() {
		return suspectInvocations;
	}
	
	public List<String> getExceptionNames() {
		return exceptionNames;
	}

	public List<String> getFunctionName() {
		return functionName;
	}
	
	public List<Class> getClassNames() {
		return classNames;
	}
	
	
}
