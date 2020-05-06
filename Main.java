
public class Main {
	static JackAnalyzer compiler;

	public static void main(String[] args){
		String file = args[0];
		System.out.println(file);

		compiler = new JackAnalyzer(file);

		compiler.run();
	}
}
