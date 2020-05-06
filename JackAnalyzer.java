import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

public class JackAnalyzer {
	private ComplilationEngine engine;
	private FileInputStream inFile;
	private FileOutputStream outFile;
 
	JackAnalyzer(String source){
		String outName = source.substring(0,source.indexOf('.')) + ".xml";
		System.out.println("source:"+source);
		System.out.println("outfile:"+outName);
		try {
			inFile = new FileInputStream(source);
			outFile = new FileOutputStream(outName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		engine = new ComplilationEngine(source,outName);
	}

	public  void Close(){
		try {
			inFile.close();
			outFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run(){
		engine.compileClass();
	}

}
