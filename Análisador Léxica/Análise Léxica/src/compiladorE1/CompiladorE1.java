package compiladorE1;

public class CompiladorE1 {

	public static void main(String[] args) {
		Lexema lexema = new Lexema("src\\compiladorE1\\codigo.txt");
		Token t = null;
		while ((t = lexema.nextToken()) != null) {
			System.out.println(t.toString());
		}
	}

}
