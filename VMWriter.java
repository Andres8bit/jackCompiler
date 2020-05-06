import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class VMWriter {
	private FileInputStream in;
	private FileOutputStream out;
	private FileWriter vm;

	public VMWriter(String inFile,String outFile){
		try {
			in = new FileInputStream (inFile);
			out = new FileOutputStream (outFile);
			vm= new FileWriter(outFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void close(){
		try {
			in.close();
			out.close();
			vm.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void writePush(String segment,int index){
		try {
				vm.write("push " + segment + "\n");
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writePop(String segment,int index){
		try {
			if(segment.isEmpty())
				vm.write("pop " + "constant " + index + "\n");
			else
				vm.write("pop " + segment + "\n");
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeArithmentic(String op){
		try {
			switch(op){
			case"+":
				vm.write("add\n");
				break;
			case"-":
				vm.write("sub\n");
				break;
			case "*":
				vm.write("mult\n");
				break;
			case"/":
				vm.write("div\n");
				break;
			case"&":
				vm.write("and\n");
				break;
			case"|":
				vm.write("or");
				break;
			case"~":
				vm.write("not");
				break;
			case"<":
				vm.write("lt");
				break;
			case">":
				vm.write("gt");
				break;
			case"unary":
				vm.write("neg");
				break;
				default:
					System.out.println("error an unknown operator was called");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeLabel(String label){
		try {
			vm.write("label " + label +  "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void writeGoTo(String label){
		try {
			vm.write("goto " + label + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeIf(String label){
		try {
			vm.write("if-goto " + label + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeCall(String label,int nArgs){
		try {
			vm.write("call " + label + " " + nArgs + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeFunction(String label,int nLocals){
		try {
			vm.write("function " + label + " " + nLocals + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeReturn(){
		try {
			vm.write("return\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
