package compiler.lexico;

import utils.TokenType;

public class Token {
	private TokenType type;
	private String content;
	private int lin;
	private int col;
	
	public Token(TokenType type, String content) {
		super();
		this.type = type;
		this.content = content;

	}
	public TokenType getType() {
		return type;
	}
	public void setType(TokenType type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "Token [type=" + type + ", content=" + content + "]";
	}

	public int getLine() {
		return lin;
	}

	public void setLine(int lin) {
		this.lin = lin;
	}

	public int getColumn() {
		return col;
	}

	public void setColumn(int col) {
		this.col = col;
	}

	
}
