package compiladorE1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Lexema {

	private char[] content;
	private int contentIndex;

	public Lexema(String sourceCodePath) {

		try {
			String strContent;
			strContent = new String(Files.readAllBytes(Paths.get(sourceCodePath)));
			this.content = strContent.toCharArray();
			this.contentIndex = 0;
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// check the next char
	private char nextChar() {
		return this.content[this.contentIndex++];
	}

	// check the length of the source code
	private boolean hasNextChar() {
		return contentIndex < this.content.length;
	}

	// go back to the current char in a unity
	private void back() {
		this.contentIndex--;
	}

	// check if the letters are down
	private boolean isLetter(char c) {
		return (c >= 'a') && (c <= 'z');
	}

	// check if there is a digit
	private boolean isDigit(char c) {
		return (c >= '0') && (c <= '9');
	}

	// check if the next token is valid otherwise will run an error message

	public Token nextToken() {
		Token token = null;
		char c;
		int state = 0;

		StringBuffer lexema = new StringBuffer();
		while (this.hasNextChar()) {
			c = this.nextChar();
			/*
			 * Call according to the state machine
			 */
			switch (state) {
			case 0:
				if (c == ' ' || c == '\n' || c == '\t' || c == '\r') { // white spaces chars
					state = 0;
				}
				// identifier type
				else if (this.isLetter(c) || c == '_') {
					lexema.append(c);
					state = 1;
				}
				// number type
				else if (this.isDigit(c)) {
					lexema.append(c);
					state = 2;
				}
				// special character
				else if (c == ')' || c == '(' || c == '[' || c == ']' || c == '@' || c == '{' || c == '}' || c == ','
						|| c == ';' || c == '[' || c == ']') {
					lexema.append(c);
					state = 9;
				}
				// char
				else if (this.isLetter(c) || this.isDigit(c)) {
					lexema.append(c);
					state = 5;
				}
				// relacional operetor
				else if (c == '<' || c == '>' || c == '<' + '=' || c == '>' + '=' || c == '=' + '=' || c == '!' + '=') {
					lexema.append(c);
					state = 6;
				}
				// arithmetic type
				else if (c == '+' || c == '-' || c == '%' || c == '*' || c == '/') {
					lexema.append(c);
					state = 7;
				}
				// assignment character
				else if (c == '=') {
					lexema.append(c);
					state = 8;
				}
				// exponential type
				else if (c == '&') {
					lexema.append(c);
					state = 11;
				// Commentary type
				} else if (c == '#') {
					lexema.append(c);
					state = 12;
				}	
				// end of the code
				else if (c == '$') {
					lexema.append(c);
					state = 99;
					this.back();
				} else {
					lexema.append(c);
					throw new RuntimeException("ERRO : Token Invalido \"" + lexema.toString() + "\"");
				}
				break;
			/*
			 * call according to the token type
			 */
			case 1:
				if (this.isLetter(c) || this.isDigit(c) || c == '_') {
					lexema.append(c);
					state = 1;
				} else {
					this.back();
					return new Token(lexema.toString(), Token.TIPO_IDENTIFICADOR);
				}
				break;
			case 2:
				if (this.isDigit(c)) {
					lexema.append(c);
					state = 2;
				} else if (c == '.') {
					lexema.append(c);
					state = 3;
				} else {
					this.back();
					return new Token(lexema.toString(), Token.TIPO_INTEIRO);
				}
				break;
			case 3:
				if (this.isDigit(c)) {
					lexema.append(c);
					state = 4;
				} else {
					throw new RuntimeException("ERRO : Tipo não coresponde a Float \"" + lexema.toString() + "\"");
				}
				break;
			case 4:
				if (this.isDigit(c)) {
					lexema.append(c);
					state = 4;
				} else {
					this.back();
					return new Token(lexema.toString(), Token.TIPO_REAL);
				}
				break;
			case 5:
				if (this.isLetter(c) || this.isLetter(c)) {
					lexema.append(c);
					state = 5;
				} else {
					this.back();
					return new Token(lexema.toString(), Token.TIPO_CHAR);
				}
				break;
			case 6:
				this.back();
				return new Token(lexema.toString(), Token.TIPO_OPERADOR_RELACIONAL);
			case 7:
				this.back();
				return new Token(lexema.toString(), Token.TIPO_OPERADOR_ARITIMETICO);
			case 8:
				this.back();
				return new Token(lexema.toString(), Token.TIPO_OPERADOR_ATRIBUICAO);
			case 9:
				this.back();
				return new Token(lexema.toString(), Token.TIPO_CARACTERE_ESPECIAL);
			case 10:
				this.back();
				return new Token(lexema.toString(), Token.TIPO_PALAVRA_RESERVADA);
			case 11:
				this.back();
				return new Token(lexema.toString(), Token.TIPO_OPERADOR_EXPONENCIAL);
			case 12:
				if (c == '#' && this.isLetter(c) || this.isDigit(c)) {
					lexema.append(c);
					state = 12;
				} else {
					this.back();
					return new Token(lexema.toString(), Token.TIPO_COMENTARIO);
				}
			
			case 99:
				return new Token(lexema.toString(), Token.TIPO_FINAL_CODIGO);
			}
		}
		return token;
	}

}
