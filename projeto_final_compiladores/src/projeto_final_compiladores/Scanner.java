package projeto_final_compiladores;

import java.io.FileInputStream;
import java.io.IOException;

public class Scanner {
	
	private char c, prev;
	private Token token, backup;
	private FileInputStream codigoFonte;
	private int content;
	private int row;
	private int colum;
	private String[] reservadas = {"main", "if", "else", "while", "do", "for", "int", "float", "char"};
	
	//-1 é o token inicial e EOF
	//0 = ID inicial
	/*
	 * palavras reservadas 1-9
	 * 1 - main
	 * 2 - if
	 * 3 - else
	 * 4 - while
	 * 5 - do
	 * 6 - for
	 * 7 - int
	 * 8 - float
	 * 9 - char
	 * 
	 * operador relacional 10 - 15
	 * 10 - >
	 * 11 - >=
	 * 12 - <
	 * 13 - <= 
	 * 14 - !=
	 * 15 - ==
	 * 
	 * operador aritimetico 20 - 24
	 * 20 - +
	 * 21 - -
	 * 22 - *
 	 * 23 - /
 	 * 24 - = 
 	 * 
 	 * caracter especial 30 - 35
 	 * 30 - (
 	 * 31 - )
 	 * 32 - {
 	 * 33 - }
 	 * 34 - ,
 	 * 35 - ;
 	 * 
 	 * 40 - char
 	 * 
 	 * valores numericos 50 - 51
 	 * 50 - int
 	 * 51 - float
	 */

	public Scanner(FileInputStream codigoFonte) {
		this.codigoFonte = codigoFonte;
		this.c = ' ';
		this.prev = ' ';
		this.token = new Token(-1);
		this.backup = null;
		this.content = 0;
		this.row = 1;
		this.colum = 0;
	}
	
	public boolean endOfLine() {
		return content == -1 && backup ==null;
	}
	
	private void nextChar() throws IOException{
		if (content != 1) {
			content = codigoFonte.read();
			prev = c;
			c = (char) content;
			if (c == '\n') {
				
				colum = 0;
				row++;
			} else if (c == '\t') {
				
				colum+=4;
			} else {
				colum++;
			}
		}
	}
	
	public Token getNextToken() throws Stop,IOException{
		if (backup != null) {
			token = backup;
			backup = null;
			return token;
		}
		StringBuilder build = new StringBuilder();
		while(content != -1) {
			while(Character.isWhitespace(c)) {
				nextChar();
			}
			//ID = 0
			if(Character.isAlphabetic(c) || c == '_') {
				boolean reser = false;
				if(token.getTipo() == -1 || (token.getTipo() >= 20 && token.getTipo() <= 24)
						|| (token.getTipo() >= 30 && token.getTipo() <= 35) || Character.isWhitespace(prev)) {
					reser = true;
				}
				while (Character.isAlphabetic(c)) {
					build.append(c);
					nextChar();
				}
				if(Character.isDigit(c) || c == '_') {
					reser = false;
				}
				while(Character.isAlphabetic(c) || Character.isDigit(c) || c == '_') {
					build.append(c);
					nextChar();
				}
				if(reser) {
					int tipo = 0;
					for (int i = 0; i < reservadas.length; i++) {
						if(build.toString().equals(reservadas[i])) {
							tipo = i + 1;
							break;
						}else if (i == reservadas.length - 1) {
							reser = false;
						}
					}
					// diferenciando o == do = para inseri-lo no backup
					if(reser &&(Character.isWhitespace(c) || c == ')' || c == '(' || c == '{' || c == '}' || c ==','
							|| c == ';' || c == '+' || c == '-' || c == '*' || c == '/' || c == '=')){
						if (c == '=') {
							nextChar();
							if(c == '=') {
								backup = new Token(15);
								nextChar();
							} else {
								backup = new Token(20);
								setToken(tipo,null);
							}
						} else {
							setToken(tipo,null);
						}
					} else {
						setToken(0, build);
					}
				} else {
					setToken(0, build);
				}
				return token;
			}
			// OP < : 10
			else if (c == '>') {
				nextChar();
				if (c != '=') {
					setToken(10,null);
					return token;
					//OP >= : 11
				} else {
					nextChar();
					setToken(11,null);
					return token;
				}				
			}
			//OP < : 12
			else if (c == '<') {
				nextChar();
				if (c != '=') {
					setToken(12,null);
					return token;
					//OP <= :13 
				}else {
					nextChar();
					setToken(13,null);
					return token;
				}
			}
			//OP != : 14
			else if(c == '!') {
				nextChar();
				if (c == '=') {
					nextChar();
					setToken(14,null);
					return token;
				}else {
					throw new Stop(row, colum, token, "Exclamação não seguida de igual correto seria: !=");
				}
			}
			// OP = : 24
			else if (c == '=') {
				nextChar();
				//OP == : 15
				if (c == '=') {
					nextChar();
					setToken(15,null);
					return token;
				}else {
					setToken(24,null);
					return token;
				}
			}
			// OP + : 20  
			else if (c == '+') {
				nextChar();
				setToken(20,null);
				return token;
			}
			//OP - : 21
			else if (c == '-') {
				nextChar();
				setToken(21,null);
			}
			//OP * : 22
			else if (c == '*') {
				nextChar();
				setToken(22,null);
			}
			//OP / : 23
			else if (c == '/') {
				nextChar();
				// comentario linha //
				if (c == '/') {
					do {
						nextChar();
					}while (c != '\n' && content != -1);
					nextChar();
				}
				// comentario em bloco //
				if(c == '*') {
					nextChar();
					do {
						while (c != '*' && content != -1) {
						nextChar();
						}
						if (content == -1) {
							throw new Stop(row,colum,token, "EOF em comentario multilinha");
						}
						nextChar();
					}while (c != '/');
					nextChar();
					//OP 23 : /
				}else {
					setToken(23,null);
					return token;
				}
								
			}
			// caracter especial ( : 30
			else if (c == '(') {
				nextChar();
				setToken(30,null);
				return token;
			}
			// caracter especial ) : 31
			else if (c == ')') {
				nextChar();
				setToken(31,null);
				return token;
			}
			// caracter especial { : 32
			else if (c == '{') {
				nextChar();
				setToken(32,null);
				return token;
			}
			// caracter especial } : 33
			else if (c == '}') {
				nextChar();
				setToken(33,null);
				return token;
			}
			// caracter especial , : 34
			else if (c == ',') {
				nextChar();
				setToken(34,null);
				return token;
			}
			// caracter especial ; : 35
			else if (c == ';') {
				nextChar();
				setToken(35,null);
				return token;
			}
			// Char digito ou letra : 40
			else if (c == 39) {
				build.append(c);
				nextChar();
				if(Character.isAlphabetic(c) || Character.isDigit(c)) {
					build.append(c);
					nextChar();
					if (c == 39) {
						build.append(c);
						nextChar();
						setToken(40,build);
						return token;
					} else {
						throw new Stop(row,colum,token, "Valor de char com má formação");
					}
				} else {
					throw new Stop(row,colum,token, "valor de char com má formação");
				}
			}
			// Digito 123...
			// Inteiro : 50
			else if (Character.isDigit(c)) {
				do {
					build.append(c);
					nextChar();
				} while(Character.isDigit(c));
				// Float : 51
				if (c == '.') {
					return isFloat(build);
				} else {
					setToken(50,build);
					return token;
				}
			}
			// Float : 51 
			else if (c == '.') {
				return isFloat(build);
			}
			// EOF apos espaços 
			else if (endOfLine()) {
				break;
			}
			// caracter invalido 
			else {
				throw new Stop(row,colum,token, "Caracter invalido.");
			}
			 
		}
		setToken(-2,null);
		return token;
	}
	
	
	public Token isFloat(StringBuilder build) throws Stop, IOException {
		build.append(c);
		nextChar();
		if (Character.isDigit(c)) {
			do {
				build.append(c);
				nextChar();
			} while (Character.isDigit(c));
			setToken(51,build);
			return token;
		} else {
			throw new Stop(row,colum,token, "Valor Float com má formação");
		}
	}
	
	private void setToken(int tipo, StringBuilder build) {
		token.setTipo(tipo);
		token.setLexema(build);
	}
	
	
	public int getRow() {
		return this.row;
	}

	public int getColum() {
		return this.colum;
	}
}
