package projeto_final_compiladores;

public class ScopeToken extends Token{
	private int scope;
	
	
	public ScopeToken(int tipo, StringBuilder lexema, int scope) {
		super(tipo);
		this.lexema = lexema;
		this.scope = scope;
	}
	
	public int getScope() {
		return scope;
	}
	
	public void setScope(int scope) {
		this.scope = scope;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof ScopeToken)) {
			return false;
		}
		ScopeToken token = (ScopeToken)o;
		return super.lexema.toString().equals(token.getLexema().toString());
	}
	
	@Override
	public String toString() {
		return "Tipo: " + tipo + "; Lexema: " + this.lexema.toString() + "; Escopo: " + this.scope;
	}
	

}
