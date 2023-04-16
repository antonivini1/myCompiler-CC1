//RESOLVER PROBLEMA NOS COMANDOS RELACIONADO AO NEXTTOKEN();
package compiler.syntax;

import compiler.exceptions.SyntaxException;
import compiler.lexico.Scanner;
import compiler.lexico.Token;
import utils.TokenType;

public class Parser {
	Scanner scanner;
	Token token;
	
	public Parser(Scanner scanner) {
		this.scanner = scanner;
	}
	//--------------------------------------------------
	public void programa(){
        //':' 'DECLARACOES' listaDeclaracoes ':' 'ALGORITMO' listaComandos;
        token = scanner.nextToken();
        do{
            if(token != null && token.getContent().equals(":")){
                token = scanner.nextToken();
                if(token == null){
                    throw new SyntaxException("Failed to read the scope, nothing was found.");
                }

                if(token.getContent().equals("DECLARACOES")){
                    System.out.println(":DECLARACOES");
                    this.listaDeclaracoes();
                }
                else if(token.getContent().equals("ALGORITMO")){
                    System.out.println("\n\n:ALGORITMO");
                    this.listaComandos();
                }
                else{
                    throw new SyntaxException("Failed to read the scope.");
                }
            }
        }while(token != null);
    }
    //--------------------------------------------------
    public void listaDeclaracoes(){
        //tipoVar declaracao listaDeclaracoes | tipoVar declaracao | VAZIO
        token = scanner.nextToken();
        do{
            this.tipoVar(token);
            token = scanner.nextToken();
            if(token == null || token.getContent().equals(":")){
                System.out.println();
                throw new SyntaxException("Variable expected, but nothing was found.");
            }
            else if(token.getType() != TokenType.IDENTIFIER){
                System.out.println();
                throw new SyntaxException("Identifier was expected, found a  " + token.getContent() + "(" + token.getType() + 
                                          ") instead at line " + token.getLine() + " and column " + token.getColumn());
            } 
            else{
                this.declaracoes();
                if(token == null)
                    break;
            }
        }while(!(token.getContent().equals(":")));
    }
    public void tipoVar(Token tk){
        //INTEIRO | REAL
        if(tk == null){
            throw new SyntaxException("Type expected, nothing was found");
        }
        else if(tk.getType() != TokenType.RESERVED){
            throw new SyntaxException("No variable type was found, found a " + tk.getContent() + "(" + tk.getType() + 
                                      ") instead at line " + tk.getLine() + " and column " + tk.getColumn());
        }
        else if(token.getContent().equals("int")){
            System.out.print(tk.getContent() + " ");
        }
        else if(token.getContent().equals("float")){
            System.out.print(tk.getContent() + " ");
        }
        else{
            throw new SyntaxException("Type was expected, found a " + tk.getContent() + "(" + tk.getType() + 
                                      ") instead at line " + tk.getLine() + " and column " + tk.getColumn());
        }
    }
    public void declaracoes(){
        //VARIAVEL;
        do{
            if(token.getContent().equals(":")){
                break;
            }
            else if(token.getType() != TokenType.IDENTIFIER){
                throw new SyntaxException("Identifier was expected, found a " + token.getContent() + "(" + token.getType() + 
                                        ") instead at line " + token.getLine() + " and column " + token.getColumn());
            }
            else{
                System.out.print(token.getContent() + " ");
            }
            token = scanner.nextToken();
        }while(token != null && token.getType() == TokenType.IDENTIFIER);
        System.out.println("\b\n");
        
    }
    //--------------------------------------------------
    public void listaComandos(){
        //comando listaComandos | comando;
        token = scanner.nextToken();
        do{
            this.comando(token);
        }while(token != null);
    }
    public void comando(Token tk){
        //comandoAtribuicao | comandoSaida | comandoCondicao | comandoRepeticao;
        if(tk == null){
            throw new SyntaxException("Command expected, but nothing was found.");
        }
        else if(tk.getType() != TokenType.IDENTIFIER && tk.getType() != TokenType.RESERVED){
            throw new SyntaxException("Command was expected, found a " + tk.getContent() + "(" + tk.getType() + 
                                      ") at line " + tk.getLine() + " and column " + tk.getColumn());
        }
        else if(tk.getType() == TokenType.IDENTIFIER){
            this.comandoAtribuicao(tk);
        }
        else{
            switch (tk.getContent()){
                case "print":
                    this.comandoSaida();
                    break;
                case "if":
                    this.comandoCondicao();
                    break;
                case "while":
                    this.comandoRepeticao();
                    break;
                default:
                    throw new SyntaxException("Malformed command(" + tk.getContent() + ") at line " + 
                                              tk.getLine() + " and column " + tk.getColumn());
            
            }
        }

    }

    public void comandoAtribuicao(Token tk){
        //VARIAVEL '=' expressaoAritmetica;
        token = scanner.nextToken();
        
        if(token == null){
            throw new SyntaxException(tk.getContent() + " cannot be resolve");
        }
        else if(token.getType() != TokenType.ASSIGN_OP){
            throw new SyntaxException("Assign operator was expected, found a " + tk.getContent() + "(" + tk.getType() + 
                                      ") at line " + tk.getLine() + " and column " + tk.getColumn());
        }
        else{
            System.out.print(tk.getContent() + " = ");

            token = scanner.nextToken();
            this.expressaoAritmetica();
            System.out.println("\b");
        }
    }  
    public void comandoSaida(){
        //'PRINT'  (VARIAVEL | CADEIA);
        token = scanner.nextToken();
        
        if(token == null){
            throw new SyntaxException("Nothing to print was found.");
        }
        else if(token.getType() != TokenType.IDENTIFIER && token.getType() != TokenType.STRING){
            throw new SyntaxException("String was expected, found a " + token.getContent() + "(" + 
                                      token.getType() + ") at line " + token.getLine() + " and column " 
                                      + token.getColumn());
        }
        else{
            System.out.println("PRINT " + token.getContent());
            token = scanner.nextToken();
        }
    }
    public void comandoCondicao(){
        //'IF' expressaoRelacional 'THEN' comando | 'IF' expressaoRelacional 'THEN' comando 'ELSE' comando;
        System.out.print("IF ");
        token = scanner.nextToken();
        this.expressaoRelacional();

        if(token == null){
            System.out.println();
            throw new SyntaxException("THEN expected, but nothing was found.");
        }
        else if(token.getContent().equals("then")){
            System.out.println(" " + "THEN");
            token = scanner.nextToken();
            this.comando(token);

            if(token.getContent().equals("else")){
                System.out.println("ELSE");
                token = scanner.nextToken();
                this.comando(token);
            }
        }
        else{
            System.out.println();
            throw new SyntaxException("THEN expected, but was found a " + token.getContent() + "(" + 
                                      token.getType() + ") instead at line " + token.getLine() + 
                                      " and column " + token.getColumn());
        }
    }
    public void comandoRepeticao(){
        //'WHILE' expressaoRelacional comando;
        System.out.print("WHILE ");
        token = scanner.nextToken();
        if(token == null){
            System.out.println();
            throw new SyntaxException("Relational term expected was expected, but nothing was found.");
        }
        else{
            this.expressaoRelacional();
            System.out.print(" ");
            this.comando(token);
        }
    }
    //--------------------------------------------------
    public void expressaoAritmetica(){
        //termoAritmetico expressaoAritmetica2
        this.termoAritmetico();
        this.expressaoAritmetica2();
    }
    public void expressaoAritmetica2(){
        //expressaoAritmetica3 expressaoAritmetica2 | VAZIO
        if(token != null){
            if(token.getContent().equals("+") || token.getContent().equals("-")){
                    this.expressaoAritmetica3();
                    this.expressaoAritmetica2();
            } 
        }    
    }
    public void expressaoAritmetica3(){
        //'+' termoAritmetico | '-' termoAritmetico 
        System.out.print(" " + token.getContent() + " ");

        token = scanner.nextToken();
        this.termoAritmetico();
    }
    public void termoAritmetico(){
        //fatorAritmetico termoAritmetico2;
        this.fatorAritmetico();
        this.termoAritmetico2();
    }
    public void termoAritmetico2(){
        //termoAritmetico3 termoaritmetico2 | VAZIO
        if(token != null){
            if(token.getContent().equals("*") || token.getContent().equals("/")){
                    this.termoAritmetico3();
                    this.termoAritmetico2();
            }
        }
    }
    public void termoAritmetico3(){
        //'*' fatorAritmetico | '/' fatorAritmetico
        System.out.print(" " + token.getContent() + " ");

        token = scanner.nextToken();
        this.fatorAritmetico();
    }
    public void fatorAritmetico(){
        //NUMINT | NUMREAL | VARIAVEL |	'(' expressaoAritmetica ')'
        if(token == null){
            System.out.println();
            throw new SyntaxException("Arithmetic factor was expected but nothing was found.");
        }
        else if(token.getType() != TokenType.INTEGER && token.getType() != TokenType.FLOAT &&
                token.getType() != TokenType.O_PARENTHESE && token.getType() != TokenType.IDENTIFIER){
            System.out.println();
            throw new SyntaxException("Arithmetic factor was expected, but was found a " + token.getContent() + 
                                      "( " + token.getType() + ") instead at line " + token.getLine() +
                                      " and column " + token.getColumn());
        }
        else if(token.getType() == TokenType.O_PARENTHESE){
            System.out.print("(");

            token = scanner.nextToken();
            this.expressaoAritmetica();

            if((token == null || token.getType() != TokenType.C_PARENTHESE)){
                System.out.println();
                throw new SyntaxException("')' is missing at line " + token.getLine() +
                                          " and column " + token.getColumn());
            }
            else{
                System.out.print(") ");
            }
        }
        else{
            System.out.print(token.getContent());
        }
        token = scanner.nextToken();
    }
    //--------------------------------------------------
    public void expressaoRelacional(){
        //termoRelacional expressaoRelacional2
        this.termoRelacional();
        this.expressaoRelacional2();
    }
    public void termoRelacional(){
        //expressaoAritmetica OP_REL expressaoAritmetica |	'(' expressaoRelacional ')';
        if(token == null){
            throw new SyntaxException("Relational term expected was expected, but nothing was found.");
        }
        else if(token.getType() != TokenType.INTEGER && token.getType() != TokenType.FLOAT &&
                token.getType() != TokenType.O_PARENTHESE && token.getType() != TokenType.IDENTIFIER){
            System.out.println();
            throw new SyntaxException("Relational term was expected, but was found a " + token.getContent() + 
                                "( " + token.getType() + ") instead at line " + token.getLine() +
                                " and column " + token.getColumn());
        }
        else if(token.getType() == TokenType.O_PARENTHESE){
            System.out.print("(");

            token = scanner.nextToken();
            this.expressaoRelacional();

            if((token == null || token.getType() != TokenType.C_PARENTHESE)){
                System.out.println();
                throw new SyntaxException("')' is missing at line " + token.getLine() +
                                          " and column " + token.getColumn());
            }
            else{
                System.out.print(")");
                token = scanner.nextToken();
            }
        }
        else{
            this.expressaoAritmetica();

            if(token == null){
                throw new SyntaxException("Relational Operator was expected, but nothing was found.");
            }
            else if(token.getType() != TokenType.RELATIONAL_OP){
                throw new SyntaxException("Relational Operator was expected, but was found " + token.getContent() + 
                                          "( " + token.getType() + ") instead at line " + token.getLine() +
                                          " and column " + token.getColumn());
            }
            else{
                System.out.print(" " + token.getContent() + " ");
                token = scanner.nextToken();
                this.expressaoAritmetica();
            }
        }        
    }
    public void expressaoRelacional2(){
        //operadorBooleano termoRelacional expressaoRelacional2 | VAZIO
        if(token != null){
            if(token.getType() == TokenType.BOOLEAN_OP){
                this.operadorBooleano();
                this.termoRelacional();
                this.expressaoRelacional2();
            }
        }
    }
    public void operadorBooleano(){
        // E | OU
        System.out.print(" " + token.getContent() + " ");
        token = scanner.nextToken();
    }
}