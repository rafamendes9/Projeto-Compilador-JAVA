package projeto_final_compiladores;

public class Stop extends Exception{
	public Stop(int row, int colum, Token token, String erro) {
		super("Erro na linha" + row + " coluna" + colum + " ultimo token lido: " + token + "|" + erro);
	}

}
