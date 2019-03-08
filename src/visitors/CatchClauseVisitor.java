package visitors;

import java.util.HashSet;

import org.eclipse.jdt.core.dom.*;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;
import handlers.SampleHandler;


public class CatchClauseVisitor extends ASTVisitor{

	HashSet<CatchClause> nullCatches = new HashSet<>();
	HashSet<CatchClause> destructiveCatches = new HashSet<>();


	public static int returnnull = 0;
	public static int catchCount=0;
	public static int destwrap = 0;
	public static File f;
	public static PrintWriter writer;


	public void setWriter(PrintWriter pw){
		writer = pw;
	}

	public void setFile(File f){
		this.f = f;
	}

	public int getNumReturnNull(){
		return returnnull;
	}

	public int getNumdestwrap(){
		return destwrap;
	}
	
	public int getCatchCount() {
		return catchCount;
	}

	@Override
	public boolean visit(CatchClause node) {
		catchCount++;

		String body = node.getBody().toString();
		boolean returnNullOrNot = false;

		if((body.trim().contains("return null;")) || (body.trim().contains("return (null);"))) {
			returnNullOrNot = true;
			//System.out.println("****** CONTAINS RETURN NULL ******");
		}

		if(returnNullOrNot) {
			nullCatches.add(node);

			writer.write("\n ***************ANTI-PATTERN : Return null***************");
			writer.flush();
			writer.write("\n FILE NAME : " + f.getAbsolutePath());
			writer.flush();
			writer.write("\n CATCH CLAUSE : " + findMethodForCatch(node));
			writer.flush();
			writer.write("\n_________________________________________________________________________________\n");
			writer.flush(); 
			returnnull++;

			return super.visit(node);
		}

		List<Statement> statements = node.getBody().statements();
		int statementListSize = statements.size();
		for(int i=0;i<statementListSize;i++){
			Statement s = (Statement)statements.get(i);
			String content = s.toString();
			if( content.contains("throw") && content.contains("new")&& content.contains("getMessage") ){

				SingleVariableDeclaration var = node.getException();
				String incoming = var.getType().toString();
				content =content.replaceAll("[\\W+]", " ");
				ArrayList<String> temp = new ArrayList<String>(Arrays.asList(content.split(" ")));
				boolean flag= false;
				for ( String k: temp)
				{
					if(k.contentEquals(incoming))
					{
						flag=true;
						break;
					}
				}if(flag==false)
				{
					if(!temp.contains(incoming))
					{
						destructiveCatches.add(node);
						writer.write("\n **************ANTI-PATTERN : Destructive Wrapping**************");
						writer.flush();
						writer.write(" \n FILE NAME : " + f.getAbsolutePath());
						writer.flush(); 
						writer.write("\n CATCH CLAUSE : " + findMethodForCatch(node));
						writer.flush();
						writer.write("\n_________________________________________________________________________________\n");
						writer.flush(); 
						destwrap++;
					}
				}
			}
		}

		return super.visit(node);

	}



	public HashSet<CatchClause> getReturnNullCatches() {
		return nullCatches;
	}


	public HashSet<CatchClause> getDestructiveCatches() {
		return destructiveCatches;
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


}
