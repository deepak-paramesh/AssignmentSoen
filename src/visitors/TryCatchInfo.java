package visitors;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.dom.*;

public class TryCatchInfo {
	List<String> funcName = new LinkedList<>();
	List<String> catchBlockException = new LinkedList<>();
	List<String> exceptionThrownByMethod = new LinkedList<>();
	List<Class> binaryNameforThrowExceptions = new LinkedList<>();


	public List<Class> getBinaryNameforThrowExceptions() {
		return binaryNameforThrowExceptions;
	}

	public void setBinaryNameforThrowExceptions(List<Class> binaryNameforThrowExceptions) {
		this.binaryNameforThrowExceptions = binaryNameforThrowExceptions;
	}

	String FileName;
	MethodDeclaration CatchBody;
	String body;

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public MethodDeclaration getCatchBody() {
		return CatchBody;
	}

	public void setCatchBody(MethodDeclaration catchBody) {
		CatchBody = catchBody;
	}

	public String getFileName() {
		return FileName;
	}

	public void setFileName(String fileName) {
		FileName = fileName;
	}

	public List<String> getFuncName() {
		return funcName;
	}

	public void addFuncName(String funcName) {
		this.funcName.add(funcName);
	}

	public void setFuncName(List<String> funcNames) {
		this.funcName = funcNames;
	}

	public List<String> getCatchBlockException() {
		return catchBlockException;
	}

	public void addCatchBlockException(String catchBlockException) {
		this.catchBlockException.add(catchBlockException);
	}

	public List<String> getExceptionThrownByMethod() {
		return exceptionThrownByMethod;
	}

	public void addExceptionThrownByMethod(String exceptionThrownByMethod) {
		this.exceptionThrownByMethod.add(exceptionThrownByMethod);
	}

	public void setExceptionThrownByMethod(List<String> exceptionThrownByMethod) {
		this.exceptionThrownByMethod = exceptionThrownByMethod;
	}

}
