package compiler.lexico;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import compiler.exceptions.LexicalException;
import utils.TokenType;

public class Scanner {
	
	private char[] contentBuffer;
	private int state;
	private int pos;
	private int lin;
	private int col;
	
	public Scanner(String filename) {
		try {
			String contentTxt = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
			this.contentBuffer = contentTxt.toCharArray();
			this.pos = 0;
			this.lin = 1;
			this.col = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Token nextToken() {
		char currentChar;
		Token tk;
		String content = "";
		if(isEOF()) {
			return null;
		}
		this.state = 0;
		
		while(true) {
			currentChar = nextChar();
			updatePos(currentChar);
			switch (state) {
				case 0:
					if(isLetter(currentChar)) {
						content += currentChar;
						state = 1;
					}
					else if (isNumber(currentChar)) {
						content += currentChar;
						state = 2;
					}
					else if(isDot(currentChar,content)){
						content += "0.";
						state = 2;
					}
					else if(isRelational(currentChar)){
						content += currentChar;
						state = 3;
					}
					else if(isBoolean(currentChar)){
						content += currentChar;
						state = 4;
					}
					else if(isMath(currentChar)){
						content += currentChar;
						tk = new Token(TokenType.MATH_OP, content);
						tk.setLine(lin);
						tk.setColumn(col);
						return tk;
					}
					else if(isAssign(currentChar)){
						content += currentChar;
						tk = new Token(TokenType.ASSIGN_OP, content);
						tk.setLine(lin);
						tk.setColumn(col);
						return tk;
					}
					else if(isString(currentChar)){
						content += currentChar;
						state = 6;
					}
					else if(isComment(currentChar)) {
						state = 5;
					}
					else if(isParentheses(currentChar)){
						content += currentChar;
						if(currentChar == '('){
							tk = new Token(TokenType.O_PARENTHESE, content);
						}
						else{
							tk = new Token(TokenType.C_PARENTHESE, content);
						}
						tk.setLine(lin);
						tk.setColumn(col);
						return tk;
					}
					else if(isSpace(currentChar)){
						if(isEOF()){
							return null;
						}
						continue;
					}
					else if(currentChar == ':'){
						content += currentChar;
						tk = new Token(TokenType.RESERVED, content);
						tk.setLine(lin);
						tk.setColumn(col);
						return tk;
					}
					else{
						throw new LexicalException("Unrecognized Symbol on: row " + this.lin + " | column " + this.col);
					}
					break;
				case 1:
					if(isLetter(currentChar) || isNumber(currentChar)) {
						content += currentChar;
					}
					else if(isInvalid(currentChar)) {
						throw new LexicalException("Unrecognized Identifier on: row " + this.lin + " | column " + this.col);
					}
					else {
						if(currentChar != '\0') {
							back();
						}
						if(isReserved(content)){
							tk = new Token(TokenType.RESERVED, content);
						}
						else{
							tk = new Token(TokenType.IDENTIFIER, content);
						}
						tk.setLine(lin);
						tk.setColumn(col);
						return tk;
					}
					break;
				case 2:
					if(isNumber(currentChar) || isDot(currentChar, content)) {
						content += currentChar;
					}
					else if(isInvalid(currentChar) || isLetter(currentChar)) {
						throw new LexicalException("Malformed Number on: row " + this.lin + " | column " + this.col);
					}
					else {
						if(currentChar != '\0') {
							back();
						}
						if(content.contains(".")){
							tk = new Token(TokenType.FLOAT, content);
						}
						else{
							tk = new Token(TokenType.INTEGER, content);
						}
						tk.setLine(lin);
						tk.setColumn(col);
						return tk;
					}
					break;
				case 3:
					if(isAssign(currentChar)){
						content += currentChar;
					}
					else if(isInvalid(currentChar) || content.equals("!")){
						throw new LexicalException("Unknown operator on: row " + this.lin + " | column " + this.col);
					}
					else{
						if(currentChar != '\0') {
							back();
						}

						tk = new Token(TokenType.RELATIONAL_OP, content);
						tk.setLine(lin);
						tk.setColumn(col);
						return tk;
					}
					break;
				case 4:
					if(isBoolean(currentChar) && content.length() < 2 && content.charAt(0) == currentChar){
						content += currentChar;
					}
					else if(isInvalid(currentChar) || content.length() < 2){
						throw new LexicalException("Unknown operator on: row " + this.lin + " | column " + this.col);
					}
					else{
						if(currentChar != '\0') {
							back();
						}
						tk = new Token(TokenType.BOOLEAN_OP, content);
						tk.setLine(lin);
						tk.setColumn(col);
						return tk;
					}
					break;					
				case 5:
					if(currentChar == '\n' || currentChar == '\r' || currentChar == '\0'){
						if(currentChar != '\0') {
							back();
							state = 0;
						}
						else{
							return null;
						}
					}
					break;
				case 6:
					content += currentChar;	
					if(currentChar == '"'){
						tk = new Token(TokenType.STRING, content);
						tk.setLine(lin);
						tk.setColumn(col);
						return tk;
					}					
			}
		}
	}
	
	private boolean isEOF() {
		if(this.pos >= this.contentBuffer.length) {
			return true;
		}
		return false;
	}
	private boolean isInvalid(char c) {
		return !isLetter(c) && !isNumber(c) && !isSpace(c) && !isAssign(c) && 
			   !isOperator(c) && !isParentheses(c) && !(c == ':') &&!(c == '\0');
	}
	private boolean isLetter(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
	}
	private boolean isReserved(String str) {
		return str.matches("int|float|print|if|else|then|while|:|DECLARACOES|ALGORITMOS");
	}
	private boolean isNumber(char c) {
		return c >= '0' && c <= '9';
	}
	private boolean isDot(char c, String dot) {
		if(c != '.' || isEOF()){
			return false;
		}
		else{
			return dot.indexOf('.') == -1;
		}
	}
	private boolean isAssign(char c) {
		return c == '=';
	}
	private boolean isMath(char c) {
		return c == '+' || c == '-' || c == '*' || c == '/';
	}
	private boolean isRelational(char c) {
		return c == '>' || c == '<' || c == '!';
	}
	private boolean isBoolean(char c) {
		return c == '&' || c == '|';
	}
	private boolean isOperator(char c) {
		return isMath(c) || isRelational(c) || isBoolean(c); 
	}
	private boolean isSpace(char c) {
		return c == ' ' || c == '\n' || c == '\t' || c == '\r';
	}
	private boolean isComment(char c) {
		return c == '#';
	}
	private boolean isParentheses(char c) {
		return c == '(' || c == ')';
	}
	private boolean isString(char c){
		return c == '"';
	}


	private char nextChar() {
		if(isEOF()) {
			return '\0';
		}
		return this.contentBuffer[pos++];
	}
	private void back() {
		this.pos--;
		this.col--;
		
	}
	private void updatePos(char c){
		if(c == '\n'){
			this.lin++;
			this.col = 1;
		}
		else if(c != '\r' && c != '\0'){
			this.col++;
		}
	}
}
