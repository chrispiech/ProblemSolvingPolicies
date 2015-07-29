package models.ast;


import java.util.*;

import minions.CodeBlockCreater;
import util.FileSystem;

/**
 * Class: Program
 * Program is a class type which represents a students homework solution.
 * Most importantly, it has a list of all the underlying codeBlocks.
 */
public class Program {

	public static final int PASSING_SCORE = 20; // though 100 is max
	public static final int PERFECT_SCORE = 100;
	
	// The id of the program
	private String id;

	// The code of the program (in xml)
	private String code;
	
	// The AST of the program
	private Tree ast;
	
	// The number of students who submitted this program
	private int count;
	
	// The result from running the unit tests
	private int unitTestResult;
	
	// The juiciest part of a program are the codeBlocks that it contains
	private List<CodeBlock> codeBlocks = null;

	public Program(String id, String code, Tree ast, int count, int unitTestResult) {
		this.id = id;
		this.code = code;
		this.ast = ast;
		this.count = count;
		this.unitTestResult = unitTestResult;
	}
	
	public String getCode() {
		return code;
	}
	
	public Tree getAst() {
		return ast;
	}
	
	public String getId() {
		return id;
	}
	
	public int getCount() {
		return count;
	}
	
	public int getUnitTestResult() {
		return unitTestResult;
	}
	
	public boolean passedUnitTests() {
		return unitTestResult >= PASSING_SCORE;
	}
	
	public boolean perfectOnUnitTests() {
		return unitTestResult >= PERFECT_SCORE;
	}
	
	public List<CodeBlock> getCodeBlocks() {
		if (codeBlocks == null) {
			codeBlocks = CodeBlockCreater.createCodeBlocks(this);
		}
		return codeBlocks;
	}
	
	@Override
	public boolean equals(Object o) {
		Program other = (Program) o;
		return ast.equals(other.ast);
	}
	
	@Override 
	public int hashCode() {
		//return id.hashCode();
		return ast.hashCode();
	}
	
	@Override
	public String toString() {
		String str = "";
		str += "id: " + id + "\n";
		str += "ast: " + ast.toString();
		str += "students: " + count + "\n";
		str += "score: " + unitTestResult + "\n";
		return str;
		
	}
}
