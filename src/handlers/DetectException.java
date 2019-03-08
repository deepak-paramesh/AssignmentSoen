package handlers;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.JavaModelException;
import patterns.ExceptionFinder;
import patterns.PatternCallers;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;


public class DetectException extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		
		IProject[] projects = root.getProjects();
		
		try {
			detectInProjects(projects);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		SampleHandler.printMessage("DONE DETECTING");
		
		return null;
	}
	
	private void detectInProjects(IProject[] projects) throws ClassNotFoundException {
		for(IProject project : projects) {
			SampleHandler.printMessage("DETECTING IN: " + project.getName());
			ExceptionFinder exceptionFinder = new ExceptionFinder();
			
			try {
				// find the exceptions and their methods
				exceptionFinder.findExceptions(project);
				
				// find all the methods that call them
				PatternCallers patternCallers = new PatternCallers(exceptionFinder.getSuspectMethods());
				patternCallers.findPatternCallers(project);
				
			} catch (JavaModelException e) {
				e.printStackTrace();
			}	
	}
	}
}
