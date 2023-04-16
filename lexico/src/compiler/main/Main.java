package compiler.main;

import compiler.exceptions.LexicalException;
import compiler.exceptions.SyntaxException;

//import compiler.lexico.Token;
import compiler.lexico.Scanner;
import compiler.syntax.Parser;

public class Main {
	public static void main(String[] args) {
		try{
			Scanner scanner = new Scanner("source_code.mc");
			Parser pa = new Parser(scanner);
			
			pa.programa();
		}
		catch (LexicalException ex) {
			System.out.println("Lexical Error: "+ex.getMessage());
		} 
		catch (SyntaxException ex) {
			System.out.println("Syntax Error: " + ex.getMessage());
		}
		catch (Exception ex) {
			System.out.println("Generic Error!!");
			System.out.println(ex.getClass().getName());
		}
	}
}
