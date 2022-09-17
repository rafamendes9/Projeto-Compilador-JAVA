package projeto_final_compiladores;

public class Token {
	protected int tipo;
	protected StringBuilder lexema;
	
	public Token(int tipo) {
		this.tipo = tipo;
	}
	
	public Token(int tipo, StringBuilder lexema) {
		this.tipo = tipo;
		this.lexema = lexema;
	}
	
	public int getTipo() {
		return tipo;
	}
	
	public void setTipo(int tipo) {
		this.tipo = tipo;
	}
	
	public StringBuilder getLexema() {
		return lexema;
	}
	
	public void setLexema(StringBuilder lexema){
		this.lexema = lexema;
	}
	
	@Override
	public String toString() {
		if(tipo == -2) {
			return "EOF";
		}else if(tipo == -1) {
			return "Nenhum";
		}else {
			if(lexema == null) {
				return "Tipo: " + tipo;
			}else {
				return "Tipo: " + tipo + "; Lexema: " + lexema.toString();
			}
		}
	}
}
