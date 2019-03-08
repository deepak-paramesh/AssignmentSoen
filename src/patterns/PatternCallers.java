package patterns;

import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import handlers.SampleHandler;
import visitors.MethodDeclarationVisitor;


public class PatternCallers {
	HashMap<MethodDeclaration, String> suspectMethods = new HashMap<>();
	HashSet<MethodDeclaration> badCallers = new HashSet<>();

	
	public PatternCallers(HashMap<MethodDeclaration, String> suspectMethods) {
		this.suspectMethods = suspectMethods;
	}
	public void findPatternCallers(IProject project) throws JavaModelException {
		IPackageFragment[] packages = JavaCore.create(project).getPackageFragments();

		for(IPackageFragment mypackage : packages){
			findTargetCallers(mypackage);
		}
		printInvocations();
	}
	
	private void findTargetCallers(IPackageFragment packageFragment) throws JavaModelException {
		for (ICompilationUnit unit : packageFragment.getCompilationUnits()) {
			CompilationUnit parsedCompilationUnit = ExceptionFinder.parse(unit);
			
			MethodDeclarationVisitor methodDeclarationVisitor = new MethodDeclarationVisitor(suspectMethods.keySet());
			parsedCompilationUnit.accept(methodDeclarationVisitor);
			badCallers.addAll(methodDeclarationVisitor.getCallSuspectMethods());
			
			//to build a complete tree of bad callers and bad methods you can do this
			for(MethodDeclaration md: methodDeclarationVisitor.getCallSuspectMethods()) {
				suspectMethods.put(md, "BAD_CALLER");
			}
		}
	}
	
	private void printInvocations() {
		for(MethodDeclaration declaration : suspectMethods.keySet()) {
			String type = suspectMethods.get(declaration);
			SampleHandler.printMessage(String.format("The following method suffers from the %s pattern", type));
			SampleHandler.printMessage(declaration.toString());
			SampleHandler.printMessage("\n_________________________________________________________________________________\n");

		}
	}
	
	public HashMap<MethodDeclaration, String> getSuspectMethods() {
		return suspectMethods;
	}
}
