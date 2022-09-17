package projeto_final_compiladores;

import java.io.IOException;
import java.util.Stack;

public class Parser {
	
	private Scanner scan;
	private Token token;
	private int scope,label,temp;
	private Stack<ScopeToken> symbols;
	
	public Parser(Scanner scan) {
		this.scan = scan;
		this.token = new Token(-1);
		this.scope = 0;
		this.label = 0;
		this.temp = 0;
		this.symbols = new Stack<>();	
	}
	
		//-1 Ã© o token inicial e EOF
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
	private void next() throws Stop,IOException{
		token = scan.getNextToken();
	}
	
	private int tipo() {
		return token.getTipo();
	}
	
	private void setScopeToken(int tipo) throws Stop {
		if (tipo == 7) tipo = 50;
		else if (tipo == 8) tipo = 51;
		else tipo = 40;
		ScopeToken scopeToken = new ScopeToken(tipo, token.getLexema(), scope);
		int pos = symbols.search(scopeToken);
		if (pos == -1 || symbols.get(-1).getScope() < scope) symbols.push(scopeToken);
		else throw new Stop(scan.getRow(),scan.getColum(),token,"Identificador \"" + scopeToken.getLexema().toString() + "\"Já¡ foi usado neste escopo");
	}
	
	private void endScope() {
		while(!symbols.empty() && (symbols.peek().getScope() == scope)) {
			symbols.pop();
		}
	}
	
	public void programa() throws Stop,IOException{
		head();
		bloco();
		if (tipo() != -2) throw new Stop(scan.getRow(),scan.getColum(),token,"Simbolo encontrado apos finalização do bloco");
	}
	
	private  void head() throws Stop,IOException{
		next();
		if (tipo() != 7) throw new Stop(scan.getRow(),scan.getColum(),token, "Cabeçalho com má formação");
		next();
		if (tipo() != 1) throw new Stop(scan.getRow(),scan.getColum(),token, "Cabeçalho com má formação");
		next();
		if (tipo() != 30) throw new Stop(scan.getRow(),scan.getColum(),token, "Cabeçalho com má  formação");
		next();
		if (tipo() != 31) throw new Stop(scan.getRow(),scan.getColum(),token, "Cabeçalho com má formação");
		next();
	}
	
	private void bloco() throws Stop, IOException{
		this.scope++;
		if (tipo() != 32) throw new Stop(scan.getRow(),scan.getColum(),token,"Bloco não iniciado");
		next();
		//declaraÃ§Ã£o de variavel
		while(tipo() == 7 || tipo() == 8 || tipo() == 9) declaracao(); //int, float e char
		while(tipo() == 0 || tipo() == 2 || tipo() == 4 || tipo() == 5 || tipo() == 32) comando(); //ID, if, while, do e {
		if (tipo() == 7 || tipo() == 8 || tipo() == 9) throw new Stop(scan.getRow(),scan.getColum(),token,"Ordem de bloco desrespeitada");
		if (tipo() != 33) throw new Stop(scan.getRow(),scan.getColum(),token,"Bloco não finalizado, ultimo token não lido corretamente");
		endScope();
		this.scope--;
		next();
	}
	
	private void declaracao() throws Stop, IOException{
		int tipo = tipo();
		while (tipo() != 35) {
			next();
			if (tipo() != 0) throw new Stop(scan.getRow(),scan.getColum(),token,"Problema de declaração: falta de intentificador");
			setScopeToken(tipo);
			next();
			if (tipo() != 35 && tipo() != 34) throw new Stop(scan.getRow(),scan.getColum(),token,"Problema de declarações: falta de , ou ;");		
		}
		next();
	}
	
	private void comando() throws Stop, IOException {
		if (tipo() == 0 || tipo() == 32) comandoBasico();
		else if (tipo() == 4 || tipo() == 5) iteracao();
		else if (tipo() == 2) condicional();
		else throw new Stop(scan.getRow(),scan.getColum(),token,"Comando não foi identificado");
	}
	
	private void comandoBasico() throws Stop,IOException {
		if (tipo() == 32) {
			bloco();
		}else atribuicao();
	}
	
	private void atribuicao() throws Stop, IOException {
		//verifica se variavel ja foi declarada e salva o tipo para comparaÃ§Ã£o
		int pos = symbols.search(new ScopeToken(0, token.getLexema(),0));
		if (pos == -1) throw new Stop(scan.getRow(),scan.getColum(),token,"Variavel \""+token.getLexema().toString()+"\"ainda não declarada no escopo");
		Token alvo = symbols.get(symbols.size() - pos);
		int tipo = alvo.getTipo();
		next();
		if (tipo() != 24) throw new Stop(scan.getRow(),scan.getColum(),token,"Sem atribuições definida");
		next();
		//Guarda o retorno de expArit comparado com o tipo de variavel inserido
		Token result = expArit();
		int tipo2 = result.getTipo();
		if (tipo != tipo2) {
			String msg = null;
			if (tipo == 40 && tipo2 == 50) msg = "Tipo char não recebe tipo int";
			else if (tipo == 40 && tipo2 == 51) msg ="Tipo char não recebe tipo float";
			else if (tipo == 50 && tipo2 == 40) msg = "Tipo int não recebe tipo char";
			else if (tipo == 50 && tipo2 == 51) msg = "Tipo int não recebe tipo float";
			else if (tipo == 51 && tipo2 == 40) msg = "Tipo float não recebe tipo char";
			if (msg != null) throw new Stop(scan.getRow(),scan.getColum(),token,msg);
			if(tipo == 51) {
				System.out.println("_t"+temp+" = float "+result.getLexema().toString());
				result.setLexema(new StringBuilder("_t"+temp));
				temp++;
			}
		}
		System.out.println(alvo.getLexema().toString()+" = "+result.getLexema().toString()+";");
		if(tipo() != 35) throw new Stop(scan.getRow(),scan.getColum(),token,"Atribuições não pode ser concluida");
		next();
	}
	
	private void iteracao() throws Stop, IOException {
		if (tipo() == 4) { //while
			next();
			if (tipo() != 30) throw new Stop(scan.getRow(),scan.getColum(),token,"Falta argumentos para iniciar a condição");
			next();
			String labelName = "_L"+label;
			label++;
			System.out.println(labelName+":");
			String compara = expRel();
			if (tipo() != 31) throw new Stop(scan.getRow(),scan.getColum(),token,"Falta argumentos para iniciar a condição");
			String labelName2 = "_L"+label;
			label++;
			System.out.println("if "+compara+" == 0 GoTo "+labelName2+";");
			next();
			comando();
			System.out.println("GoTo "+labelName+";");
			System.out.println(labelName2+":");
		}else {
			next(); //do-while
			String labelName = "_L"+label;
			label++;
			System.out.println(labelName+":");
			comando();
			if (tipo() != 4) throw new Stop(scan.getRow(),scan.getColum(),token,"Loop não iniciado");
			next();
			if (tipo() != 30) throw new Stop(scan.getRow(),scan.getColum(),token,"Falta argumentos para iniciar a condição");
			next();
			String compara = expRel();
			if (tipo() != 31) throw new Stop(scan.getRow(),scan.getColum(),token,"Falta argumentos para iniciar a condição");
			next();
			if (tipo() != 35) throw new Stop(scan.getRow(),scan.getColum(),token,"Faltando ; para finalizar o Loop");
			System.out.println("if "+compara+" != 0 GoTo"+ labelName+";");
			next();		
		}
	}
	
	private void condicional() throws Stop, IOException { //if
		next();
		if (tipo() != 30) throw new Stop(scan.getRow(),scan.getColum(),token,"Falta argumentos para iniciar a condição");
		next();
		String compara = expRel();
		String labelName = "_L"+label;
		label++;
		System.out.println("if "+compara+ " == 0 GoTo "+labelName+";");
		if (tipo() != 31) throw new Stop(scan.getRow(),scan.getColum(),token,"Falta argumentos para iniciar a condição");
		next();
		comando();
		if (tipo() == 2) {
			String labelName2 = "_L"+label;
			label++;
			System.out.println("GoTo"+ labelName2+";");
			System.out.println(labelName+":");
			next();
			comando();
			System.out.println(labelName2+":");
		}else System.out.println(labelName+":");		
	}
	
	private String expRel() throws Stop, IOException { //expresÃ£o relacinal
		Token tipo1,tipo2;
		tipo1 = expArit();
		if (tipo() < 10 || tipo() > 15) throw new Stop(scan.getRow(),scan.getColum(),token,"Operador relacional mal colocado ou não existe");
		int operador = tipo();
		next();
		tipo2 = expArit();
		if(tipo1.getTipo() == 40 && tipo2.getTipo() != 40 || tipo2.getTipo() == 40 && tipo1.getTipo() != 40) {
			throw new Stop(scan.getRow(),scan.getColum(),token,"Conflito de operadores");
		}
		if(tipo1.getTipo() != tipo2.getTipo()) {
			if(tipo1.getTipo() == 50) {
				System.out.println("_t"+temp+" = float"+ tipo1.getLexema().toString()+";");
				tipo1.setLexema(new StringBuilder("_t"+temp));
				temp++;
			}else if (tipo2.getTipo() == 50) {
				System.out.println("_t"+temp+" = float"+ tipo2.getLexema().toString()+";");
				tipo2.setLexema(new StringBuilder("_t"+temp));
				temp++;
			}
		}
		String var = "_t"+temp;
		temp++;
		StringBuilder build = new StringBuilder(var+" = "+tipo1.getLexema().toString()+" ");
		switch(operador) {
			case 10:
				build.append("> ");
				break;
			case 11:
				build.append(">= ");
			case 12:
				build.append("< ");
				break;
			case 13:
				build.append("<= ");
				break;
			case 14:
				build.append("!= ");
				break;
			case 15:
				build.append("==");
				break;
		}
		build.append(tipo2.getLexema().toString()+";");
		System.out.println(build.toString());
		return var;
	}
	
	private Token expArit() throws Stop, IOException { //expresÃ£o aritimetica
		Token aux1,aux2,tipo;
		char operador;
		aux1 = termo();
		while(tipo() == 20 || tipo() == 21) { // + ou -
			if(tipo() == 20) operador = '+';
			else operador = '-';
			next();
			aux2 = termo();
			tipo = teste(aux1,aux2);
			if (aux1.getTipo() != aux2.getTipo()) {
				if (aux1.getTipo() == 50) {
					System.out.println("_t" + temp +" = float " +aux1.getLexema().toString()+ ";");
					aux1.setLexema(new StringBuilder("_t" + temp));
					temp++;
				}else {
					System.out.println("_t" + temp +" = float " +aux2.getLexema().toString()+ ";");
					aux2.setLexema(new StringBuilder("_t" + temp));
					temp++;
				}
				
			}
			System.out.println("_t" +temp +" = " +aux1.getLexema().toString()+ " "+ operador+ " " +aux2.getLexema()+ ";");
			aux1.setLexema(new StringBuilder("_t" +temp));
			temp++;
			aux1.setTipo(tipo.getTipo());
		}
		return aux1;
	}
	
	private Token termo() throws Stop, IOException {
		Token aux1, aux2, tipo;
		boolean div = false;
		aux1 = fator();
		while(tipo() == 22 || tipo() == 23) {
			if(tipo() == 23) div = true;
			else div = false;
			next();
			aux2 = fator();
			tipo = teste(aux1,aux2);
			convertFloat(aux1,aux2,tipo);
			tipo.setLexema(new StringBuilder("_t"+temp));
			temp++;
			if (div) System.out.println(tipo.getLexema().toString()+ " = "+ aux1.getLexema().toString()+ " / "+ aux2.getLexema().toString()+ ";");
			else System.out.println(tipo.getLexema().toString()+ " = "+ aux1.getLexema().toString()+ " * "+ aux2.getLexema().toString()+ ";");
			if (aux1.getTipo() == 50 && aux2.getTipo() == 50 && div) {
				aux1.setTipo(51);
				aux1.setLexema(tipo.getLexema());
			}else aux1 = tipo;
		}
		return aux1;
	}
	

	private Token fator() throws Stop, IOException {
		if (tipo() != 0 && tipo() != 30 && tipo() != 40 && tipo() != 50 && tipo() != 51) {
			throw new Stop(scan.getRow(),scan.getColum(),token, "Fator aritimetico mal formado");
		}
		StringBuilder build = new StringBuilder();
		if(token.getLexema() != null) {
			build.append(token.getLexema().toString());
		}
		Token tipo = new Token(token.getTipo(),build);
		if (tipo.getTipo() == 0) {
			int pos = symbols.search(new ScopeToken(0,token.getLexema(), 0));
			if (pos == -1) throw new Stop(scan.getRow(),scan.getColum(),token,"Variavel \"" + token.getLexema().toString()+"\" nÃ£o foi definida");
			tipo.setTipo(symbols.get(symbols.size() - pos).getTipo());
			build = new StringBuilder(token.getLexema().toString());
			tipo.setLexema(build);
		}else if (tipo() == 30) {
			next();
			tipo = expArit(); //expressÃ£o aritimetica retorna sem o lexema 
			if (tipo() != 31) throw new Stop(scan.getRow(),scan.getColum(),token,"ExpressÃ£o invalida. Feche os parentesses");
		}
		next();
		return tipo;
	}
	
	
	private Token teste(Token aux1, Token aux2) throws Stop,IOException {
		Token tipo = new Token(-1,null);
		//printando o aux1 e aux 2
		if (aux1.getTipo() == -1) aux1.setTipo(aux2.getTipo());
		else if(aux2.getTipo() == -1) aux2.setTipo(aux1.getTipo());
		if ((aux1.getTipo() == 40 && aux2.getTipo() != 40) || (aux2.getTipo() == 40 && aux1.getTipo() != 40)) {
			throw new Stop(scan.getRow(),scan.getColum(),token,"Imcompatibilidade no tipo aritimeticos");
		}
		if (aux1.getTipo() == 40 || aux2.getTipo() == 40) {
			tipo.setTipo(40);
		}else if (aux1.getTipo() == 51 || aux2.getTipo() == 51) {
			tipo.setTipo(51);
		}else {
			tipo.setTipo(50);
		}
		return tipo;
	}
	
	private void convertFloat(Token aux1, Token aux2, Token tipo) {
		if (aux1.getTipo() != tipo.getTipo()) {
			System.out.println("_t" + temp + " = flaot" + aux1.getLexema().toString() + ";");
			aux1.setLexema(new StringBuilder("_t" + temp));
			temp++;
		}else if (aux2.getTipo() != tipo.getTipo()) {
			System.out.println("_t" + temp + " = float" + aux2.getLexema().toString() + ";");
			aux2.setLexema(new StringBuilder("_t" + temp));
			temp++;
		}
	}
}
