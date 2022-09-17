package projeto_final_compiladores;

import java.io.*;

public class CompiladorHellFire {

	public static void main(String[] args) {
		Scanner scanner;
		FileInputStream codigoFonte;
		Parser parser;
		try {
			if (args.length > 0) {
				codigoFonte = new FileInputStream(args[0]);
			} else {
				codigoFonte = new FileInputStream("src\\projeto_final_compiladores\\arquivo.txt");
			}
			scanner = new Scanner(codigoFonte);
			parser = new Parser(scanner);
			parser.programa();
		} catch (FileNotFoundException ex) {
			System.out.println("FileNotFoundException");
		} catch (Stop ex) {
			System.out.println(ex.getMessage());
		} catch (IOException ex) {
			System.out.println("IOException");
		}
	}

}
