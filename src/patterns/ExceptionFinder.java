package patterns;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.JavaModelException;
import handlers.SampleHandler;
import visitors.TryCatchInfo;
import visitors.TryStatementVisitor;
import visitors.CatchClauseVisitor;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.dom.*;

public class ExceptionFinder {

	HashMap<MethodDeclaration, String> suspectMethods = new HashMap<>();
	public static int numOverCatch;

	public static CatchClauseVisitor exceptionVisitor;
	public static PrintWriter writer = null;
	public static String destinationFile = "/Users/navibrar/Downloads/output.txt";
	

	public HashMap<MethodDeclaration, String> getSuspectMethods() {
		return suspectMethods;
	}

	public void findExceptions(IProject project) throws JavaModelException, ClassNotFoundException {

		try{
			File file = new File(destinationFile);
			if(file.exists())
				file.delete();

			writer = new PrintWriter(destinationFile, "UTF-8");

			writer.write(" SOEN 691 Assignment: Discovering Anti Patterns \n");
			writer.flush(); 
			writer.write("\n_________________________________________________________________________________\n\n");
			writer.flush(); 
		}catch(Exception e){
			System.out.println("PRINT-WRITER : " + e);
		}
		IPackageFragment[] packages = JavaCore.create(project).getPackageFragments();

		for(IPackageFragment mypackage : packages){

			findTargetCatchClauses(mypackage);

		}
		SampleHandler.printMessage("\n_________________________________________________________________________________\n");
		SampleHandler.printMessage("\n\n*********Overall Statistics*********");
		SampleHandler.printMessage("\n_________________________________________________________________________________\n");
		SampleHandler.printMessage("\n\n NUMBER OF RETURN NULL ANTIPATTERN : " + exceptionVisitor.getNumReturnNull()+ "\n\n");
		SampleHandler.printMessage(" NUMBER OF DESTRUCTIVE WRAPPING ANTIPATTERN : " + exceptionVisitor.getNumdestwrap()+ "\n\n");
		SampleHandler.printMessage(" NUMBER OF OVER CATCH ANTIPATTERN : " + numOverCatch+ "\n\n");
		SampleHandler.printMessage("NUMBER OF CATCHES ANALYSED:"+exceptionVisitor.getCatchCount());
		writer.write("\n\n OVERALL STATISTICS \n\n");
		writer.flush(); 
		writer.write( " NUMBER OF CATCHES ANALYSED : " + exceptionVisitor.getCatchCount()+ "\n\n");
		writer.flush();
		writer.write( " NUMBER OF RETURN NULL ANTIPATTERN : " + exceptionVisitor.getNumReturnNull()+ "\n\n");
		writer.flush(); 
		writer.write(" NUMBER OF DESTRUCTIVE WRAPPING ANTIPATTERN : " + exceptionVisitor.getNumdestwrap()+ "\n\n");
		writer.flush(); 
		writer.write(" NUMBER OF OVER CATCH ANTIPATTERN : " + numOverCatch+ "\n\n");
		writer.flush(); 
		writer.write("\n_________________________________________________________________________________\n");
		writer.flush();
		writer.close();
	}

	private void findTargetCatchClauses(IPackageFragment packageFragment) throws JavaModelException, ClassNotFoundException {
		for (ICompilationUnit unit : packageFragment.getCompilationUnits()) {
			CompilationUnit parsedCompilationUnit = parse(unit);

			SampleHandler.printMessage(unit.getCorrespondingResource().getLocation().toFile().toString());
			//do method visit here and check stuff
			exceptionVisitor = new CatchClauseVisitor();
			exceptionVisitor.setWriter(writer);
			exceptionVisitor.setFile(unit.getCorrespondingResource().getLocation().toFile());

			parsedCompilationUnit.accept(exceptionVisitor);


			// printExceptions(exceptionVisitor);
			getMethodsWithTargetCatchClauses(exceptionVisitor);

			TryStatementVisitor tryStatmentVisitor = new TryStatementVisitor();
			tryStatmentVisitor.setFile(unit.getCorrespondingResource().getLocation().toFile());
			parsedCompilationUnit.accept(tryStatmentVisitor);
			findMissingExceptionOverCatch(tryStatmentVisitor);
		}
	}

	private void getMethodsWithTargetCatchClauses(CatchClauseVisitor catchClauseVisitor) {

		for(CatchClause nullCatch: catchClauseVisitor.getReturnNullCatches()) {
			suspectMethods.put(findMethodForCatch(nullCatch), "Return Null");

		}

		for(CatchClause desCatch: catchClauseVisitor.getDestructiveCatches()) {
			suspectMethods.put(findMethodForCatch(desCatch), "Destructive Wrapping");
		}

		/* you can uncomment this to print only the patterns
		if(!suspectMethods.isEmpty()) {
			printExceptions(suspectMethods);
		}*/
	}

	private ASTNode findParentMethodDeclaration(ASTNode node) {
		if(node.getParent().getNodeType() == ASTNode.METHOD_DECLARATION) {
			return node.getParent();
		} else {
			return findParentMethodDeclaration(node.getParent());
		}
	}

	private MethodDeclaration findMethodForCatch(CatchClause catchClause) {
		return (MethodDeclaration) findParentMethodDeclaration(catchClause);
	}

	public static CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS10);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		parser.setStatementsRecovery(true);
		return (CompilationUnit) parser.createAST(null); // parse
	}


	/**
	 * to find and print missing exception in catch clause
	 * 
	 * @param tryStatmentVisitor
	 * @param exceptionThrownByTryStatement
	 * @param catchBlockException
	 * @throws ClassNotFoundException 
	 */
	public void findMissingExceptionOverCatch(TryStatementVisitor tryStatmentVisitor) throws ClassNotFoundException {
		//String result = "";

		
		
		for (TryCatchInfo info : tryStatmentVisitor.getTryCatchInfo()) {
			List<String> missingException = new LinkedList<>();
			ArrayList<String> arrayList = new ArrayList<String>();
		
			
			if(info.getExceptionThrownByMethod().size()<1 && (info.getCatchBlockException().contains("Exception") || info.getCatchBlockException().contains("Throwable")))
			{
				numOverCatch++;
				suspectMethods.put(info.getCatchBody(), "Over Catch");

				writer.write("\n **************ANTI-PATTERN : Over Catch**************");
				writer.flush();
				writer.write("\n FILE NAME : " + info.getFileName());
				writer.flush();
				writer.write("\n CATCH CLAUSE : " + info.getBody());
				writer.flush();
				writer.write("\n Method Name : " + info.getFuncName());
				writer.flush();
				writer.write("\n_________________________________________________________________________________\n");
				writer.flush(); 
			}
			
				for (String exception : info.getExceptionThrownByMethod()) {
					if(!exception.contains(exceptional))
					{
						System.out.print(exception + "  Exception   ");
						Class<?> C = Class.forName(exception);
						while (C != null) {
							C = C.getSuperclass();
							if(C!=null)
								arrayList.add(C.getSimpleName());
						}

						ArrayList<String> catches = new ArrayList<String>();
						for(int i=0;i<info.getCatchBlockException().size();i++)
						{
							catches.add(info.getCatchBlockException().get(i));
						}
						for(String a:catches)
						{
							if(arrayList.contains(a)) {
								//if (!info.getCatchBlockException().contains(exception)) {
								missingException.add(exception);

							}
						}
				} 

			}



			if (missingException.size() > 0) {
				numOverCatch++;
				suspectMethods.put(info.getCatchBody(), "Over Catch");

				writer.write("\n **************ANTI-PATTERN : Over Catch**************");
				writer.flush();
				writer.write("\n FILE NAME : " + info.getFileName());
				writer.flush();
				writer.write("\n CATCH CLAUSE : " + info.getBody());
				writer.flush();
				writer.write("\n Method Name : " + info.getFuncName());
				writer.flush();
				writer.write("\n Missing exceptions are : " + missingException);
				writer.flush();
				writer.write("\n_________________________________________________________________________________\n");
				writer.flush(); 

			}
		}

	}
	public static String exceptional= "org.mockito";

}
