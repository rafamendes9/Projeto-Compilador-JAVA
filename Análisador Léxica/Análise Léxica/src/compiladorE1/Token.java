package compiladorE1;

public class Token {

	public static int TIPO_INTEIRO = 0;
	public static int TIPO_REAL = 1;
	public static int TIPO_CHAR = 2;
	public static int TIPO_IDENTIFICADOR = 3;
	public static int TIPO_OPERADOR_RELACIONAL = 4;
	public static int TIPO_OPERADOR_ARITIMETICO = 5;
	public static int TIPO_OPERADOR_ATRIBUICAO = 6;
	public static int TIPO_CARACTERE_ESPECIAL = 7;
	public static int TIPO_PALAVRA_RESERVADA = 8;
	public static int TIPO_OPERADOR_EXPONENCIAL = 9;
	public static int TIPO_COMENTARIO = 10;
	public static int TIPO_FINAL_CODIGO = 99;

	private int tipo; // tipo do token
	private String lexema; // conteudo do token

	public Token(String lexema, int tipo) {
		this.lexema = lexema;
		this.tipo = tipo;
	}

	public int getTipo() {
		return tipo;
	}

	public String getLexema() {
		return lexema;
	}

	public String toString() {
		switch (this.tipo) {
		case 0:
			return this.lexema + " - INTEIRO";
		case 1:
			return this.lexema + " - REAL";
		case 2:
			return this.lexema + " - CHAR";
		case 3:
			return this.lexema + " - IDENTIFICADOR";
		case 4:
			return this.lexema + " - OPERADOR RELACIONAL";
		case 5:
			return this.lexema + " - OPERADOR ARITIMETICO";
		case 6:
			return this.lexema + " - OPERADOR DE ATRIBUIÇÃO";
		case 7:
			return this.lexema + " - CARACTERE ESPECIAL";
		case 8:
			return this.lexema + " - PALAVRA RESERVADA";
		case 9:
			return this.lexema + " - OPERADOR EXPONENCIAL";
		case 10:
			return this.lexema + " -  COMENTARIO";
		case 99:
			return this.lexema + " - FIM DO CODIGO";
		}
		return "";

	}
}
