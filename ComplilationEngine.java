import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

public class ComplilationEngine {
	private FileInputStream in;
	private FileOutputStream out;
	private FileWriter xml;
	private JackTokenizer token;
	private VMWriter vm;
	private String spacer;
	private SymbolTable table;
	private String className;
	private int arg_count;

	public ComplilationEngine(String inFile,String outFile){
		try {
			System.out.println("file:" + inFile);
			in = new FileInputStream (inFile);
			out = new FileOutputStream (outFile);
			xml= new FileWriter(outFile);
			token = new JackTokenizer(inFile.toString());
			spacer = "";
			table = new SymbolTable();
			vm = new VMWriter(inFile,outFile);
			className = "";
			arg_count = 0;
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}


	//Compiles a complete class, must be called directly after the constructor	
	//because a class is a nested structure this must be recursive
	public void compileClass(){
		String name = "";
		
		try {
			System.out.print("<class>\n");
			//indent();
			token.advance();
			writer("keyword");
			token.advance();
			writer("identifier");
			className = token.token;
			token.advance();
			writer("symbol");
			while(token.hasMoreTokens()){
				token.advance();
				if(token.token.equals("static") || token.token.equals("field")){
					compileClassVarDec();
				}else{
					compileSubroutine();
				}
			}
	//		back_tab();
			System.out.println("</class>");
			xml.close();
			vm.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//Compiles a static declaration or a field declaration called with type as token
	public void compileClassVarDec(){
		//try {
			String next = token.lookAhead();
			String type = "";
			Symbol var = new Symbol();
			
			System.out.print(spacer+ "<classVarDec>\n");
			//indent();
					writer("keyword");
			var.setKind(token.token);

			
			token.advance();
			writer("keyword");
			var.setType(token.token);
			
			token.advance();
			writer("identifier");
			var.setName(token.token);
			table.define(var.id(), var.type(),var.kind());

			next = token.lookAhead();
			if(next.equals(",")){
				compileMultVarDec(var.type());
			}
			token.advance();
			//writer("symbol");
			
			//back_tab();
			System.out.print(spacer+ " </classVarDec>\n");


	}

	//Compiles a complete method, function,or constructor
	public void compileSubroutine(){
		String next = token.lookAhead();
		String label ="";

		if(is_type(token.token) || token.keyWord() == KeyWord.FUNCTION 
				|| token.keyWord() == KeyWord.CONSTRUCTOR || token.keyWord() == KeyWord.METHOD
				|| token.keyWord() == KeyWord.VOID){
			//try {
				System.out.print(spacer + "<subroutine_dec>\n");
				//indent();
				writer("keyWord");
				
				label = token.token;
				table.define("this", className, "arg");
				token.advance();
				writer("keyword");
				token.advance();
				label +=  " " + token.token + ".init";
				writer("identifier");
				next = token.lookAhead();
				if(next.equals("(")){
					token.advance();
					//writer("symbol");
					token.advance();
					compileParameterList();
					
					System.out.println("arg cout:" + arg_count);
					System.out.println("label:" + label);
					vm.writeFunction(label, arg_count);
					//writer("symbol");
					next = token.lookAhead();
					if(next.equals("{")){
						token.advance();

						//writer("symbol");//writes '{'
						token.advance();
						compileSubroutineBody();
						token.advance();
						//writer("symbol");
						back_tab();
						System.out.println(spacer + "</subroutine_dec>");

						arg_count = 0;

					}else{
						System.out.println("Error syntax mistake missing { before subroutine body");
					}
				}
			//} catch (IOException e) {
//				e.printStackTrace();
//			}

		}

	}

	private void compileSubroutineBody() {
		while(token.keyWord() == KeyWord.VAR){
			compileVarDec();
			token.advance();
		}
		compileStatements();
		
	}

	private void compileStatements() {
//		try {
			System.out.print(spacer + "<statments>\n");
			//indent();

			while(!token.token.contentEquals("}")){
				KeyWord k = token.keyWord();
				switch(k){
				case LET:
					compileLet();
					break;
				case IF:
					compileIf();
					break;
				case WHILE:
					compileWhile();
					break;
				case DO:
					compileDo();
					break;
				case RETURN:
					compileReturn();
					break;
				default:
					System.out.println("ERORR: unkown statement call");		
					token.advance();
				}
			}
			//back_tab();
			System.out.print(spacer + "</statemtents>\n");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

	//Compiles a (possibly empty) parameter list, not including the closing ")" assumes "(" has been printed and is current token
	public void compileParameterList(){
	//	try {
		String name = "";
		String type = "arg";
		String kind = "";
			if(token.token.equals(")")){			
				System.out.print(spacer + 
						"<parameterList> </parameterList>\n");

			}else{
				System.out.print(spacer + "<parameterList>\n");
				indent();
				writer("keyword");
				type = token.token;
				token.advance();
				name = token.token;
				writer("identifier");
				table.define(name, type, kind);
				arg_count++;
				if(token.lookAhead().equals(","))
					compileMultParameters();
				back_tab();
				System.out.print( spacer + " /<ParameterList>");
			}			
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	//recursive call for multiple parameters
	private void compileMultParameters() {
		//write the next parameter
		//writer("symbol");
		token.advance();
		writer("keyword");
		token.advance();
		writer("identfier");
		arg_count++;

		//if more then recursive call 
		//else stop and return all the way back to top parameterlist call
		if(token.lookAhead().equals(","))
			compileMultParameters();
	}

	//compiles a variable declaration
	//nested structure bc it consits of three parts
	public void compileVarDec(){
//		try {
		String kind = "";
		String name = "";
		String type = "";
			System.out.print(spacer +"<VarDec>\n");
			indent();
			writer("keyWord");
			kind = token.token;
			token.advance();
			type = token.token;
			if(is_type(token.token)){
				writer("keyWord");
			}else{
				writer("identifier");
			}

			token.advance();
			name = token.token;
			table.define(name, type, kind);
			writer("identifier");
			// if multiple assignment call recursive varDec
			if(token.lookAhead().equals(",")){
				token.advance();
				//writer("symbol");
				compileMultVarDec(type);
			}
			token.advance();
			//writer("symbol");//writes ';'
			back_tab();
			System.out.print(spacer + " </VarDec>\n");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}


	private void compileMultVarDec(String type) {
		//recursive call for a multiple variable declaration statement.
		// i.e ,varName,varName.....
		token.advance();//move to identifier
		writer("identifer"); //write identifier
		table.define(token.token, type, "var");
		if(token.lookAhead().equals(",")){//if ', then recursive call
			token.advance();
			//writer("symbol");//write ,
			compileMultVarDec(type);
		}
	}

	//compiles a do statement.
	public void compileDo(){
		System.out.println(spacer + "<doStatement>");
		
		indent();
		writer("keword");
		token.advance();
		compileSubRoutineCall();
		
		if(!token.token.contentEquals(";")){
		//writer("symbol");
		token.advance();
		//writer("symbol");
		token.advance();
		}else
			//writer("symbol");
		if(token.token.contentEquals(";"))
			token.advance();

		back_tab();
		System.out.println(spacer + "</doStatement>");

	}

	//compiles a let statement.
	public void compileLet(){
//		try {
			System.out.print(spacer + "<letStatement>\n");
			indent();
			writer("keyword");
			token.advance();
			writer("identifier");
			if(token.lookAhead().equals("[")){
				compileArrayRefrence(0);
			}
			if(token.lookAhead().equals("=") || token.token.contentEquals("=")){
				token.advance();
				//writer("symbol");
				compileExpression();
				}	
			//}
			
			if(token.token.equals(";")){
				//writer("symbol");
				token.advance();
			}
			
			back_tab();
			System.out.print(spacer + "</letStatement>\n");
//			}

//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

	private void compileArrayRefrence(int iteration) {
		if(iteration < 2){
			token.advance();
			//writer("symbol");
			token.advance();
			compileExpression();
			token.advance();
			//writer("symbol");
			if(token.lookAhead().equals("["))
				compileArrayRefrence( iteration + 1 );
		}
	}

	//compiles a while statement.
	public void compileWhile(){
//		try {
			System.out.print(spacer + "<while_statement>\n");
			indent();
			writer("keyWord");
			token.advance();
			writer("symbol");
			token.advance();
			compileExpression();	
			writer("symbol");// ')'
			token.advance();

			writer("symbol"); // '{'
			token.advance();
			compileStatements();
			
		
			if(token.token.contentEquals("}")){
				writer("symbol");// '}'
			token.advance();

			}

			back_tab();
			System.out.print(spacer + "</while_statement>\n");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	//compiles a return statement.
	public void compileReturn(){
//		try {
			System.out.print(spacer + "<return_statement>\n");
			indent();
			writer("keyword");
			compileExpression();
			writer("symbol"); //write ;
			token.advance(); //move past ;
			vm.writeReturn();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	//compile if statement.
	public void compileIf(){
		//try {
			System.out.print(spacer + "<if_statement>\n");
			writer("keyword");
			token.advance();
			writer("symbol");//writes (
			token.advance();//move past (
			compileExpression();
			writer("symbol");//write )
			token.advance();
			writer("symbol");//write {
			compileStatements();
			token.advance();
			writer("symbol");
			back_tab();
			System.out.print(spacer + "</if_statemet>\n");			
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	//Compiles an expression with the term at the next advance".
	public void compileExpression(){
//		try {
		String op = "";
			System.out.print(spacer + "<expression>\n");
			indent();
//			if(is_op()){ //unary op
//			   op = "unary";
//			  token.advance();
//			  vm.writePush(table.kindOf(token.token), table.indexOf(token.token));
//			  vm.writeArithmentic(op);
//			}
			System.out.println("expression token:" + token.token);
			if(!token.token.equals(")") || !token.token.equals(";")){
				compileTerm();
				while(is_op()){
					writer("op");
					op = token.token;
					token.advance();
					if(token.lookAhead() == "(")
						token.advance();
					compileTerm();
					vm.writeArithmentic(op);
				}
			}
			back_tab();
			System.out.print(spacer + "</expression>\n");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

	}


	private void compileMultipleExpressions() {
		if(token.token.equals("(")){
			//writer("symbol");
			token.advance();
			compileExpression();
		}

	}

	//compile a term, this needs to look ahead 
	//at least once to tell if the term is a variable,array,or subroutine.
	public void compileTerm(){
//		try {
				if(is_op()){
					writer("op");
					token.advance();
					compileTerm();
				}else{
					System.out.print(spacer+ "<term>\n");
					//indent();

					if(token.token.equals("("))
						compileMultipleExpressions();

					if(is_const(token.token))
						compileConst();

					//back_tab();
					System.out.print(spacer+ "</term>\n");
					token.advance();
					//writer("symbol");
				}
			
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

	}


	private void compileIdentifier() {
		writer("identifier");
		System.out.println("compile token:" + token.token);
			
		if(token.lookAhead().contentEquals("["))
			compileArrayRefrence(0);
		if(token.lookAhead().contentEquals(".")){
			token.advance();
			writer("op");
			compileSubRoutineCall();
		}else{

			vm.writePush(table.kindOf(token.token),table.indexOf(token.token));
		}
	}

	private void compileConst() {
		//indent();
		System.out.println("const token:" + token.token);
		vm.writePush("constant", Integer.parseInt(token.token));
		writer(token.tokenType().toString().toLowerCase());
		back_tab();
	}

	//compiles a (possibly empty) comma-seperated list of expressions.
	public void compileExpressionList(){
		//try {
			System.out.print(spacer+ "<expressionList>\n");
			//indent();
			compileExpression();
			if(token.lookAhead().equals(",")){
				token.advance();
				writer("symbol");
				compileExpression();
			}
			//back_tab();
			System.out.print(spacer + "</expressionList>\n");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	private void compileSubRoutineCall() {
		String label = "call ";
		writer("identifier");
		label +=  className + "." + token.token; 
		token.advance();

		if(token.token.equals(".")){
			writer("symbol");
			token.advance();
			writer("identifier");
		}
		//write (expression list)
		token.advance();
		writer("symbol");
		token.advance();
		compileExpressionList();
		vm.writeCall(label, arg_count);
	}


	private boolean is_const(String token2) {
		if(token.tokenType() == TokenType.INT_CONST ||
				token.tokenType() == TokenType.STRING_CONST ||
				token.tokenType() == TokenType.KEYWORD_CONST ||
				Pattern.matches("\\d*", token.token))
			return true;
		return false;
	}

	private boolean is_type(String lookAhead) {

		if(token.keyWord() == KeyWord.INT || 
				token.keyWord() == KeyWord.BOOLEAN ||
				token.keyWord() == KeyWord.CHAR)
			return true;
		return false;
	}
	private boolean is_op() {
		if(token.token.equals("+") || token.token.equals("-") ||
				token.token.equals("*") || token.token.equals("/") ||
				token.token.equals("&") || token.token.equals("|") ||
				token.token.equals("<") || token.token.equals(">") ||
				token.token.equals("=") || token.token.equals("-") ||
				token.token.equals("~"))
			return true;
		return false;
	}
	//formatting private functions:
	private void writer(String tag){
		//try {
			System.out.print(spacer + "<"+ tag.toLowerCase() + "> " + token.token + " </" + tag +">\n");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	private void indent(){
		//spacer = spacer + '\t';
	}

	private void back_tab(){
		//spacer = spacer.substring(1);
	}
}
