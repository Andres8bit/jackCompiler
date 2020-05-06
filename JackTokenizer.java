import java.io.FileInputStream;
import java.util.regex.Pattern;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class JackTokenizer {
	Scanner reader;
	String token;
	String line;
	Map<String, TokenType> tokens;
	Map<String,KeyWord> keyWords;

	public JackTokenizer(String file){
		try{
			//System.out.println("infile name:" + file);
			reader = new Scanner(new FileInputStream(file));
			//System.out.println("errored:" + reader.ioException());
			init();
		}catch(FileNotFoundException e){
			System.out.println("file not found");
			e.printStackTrace();
		}
	}

	
	boolean hasMoreTokens(){
		return reader.hasNext();
	}

	public void advance(){

		if(line.isEmpty()){
			if(reader.hasNext()){
			line = reader.nextLine();
			while(line.startsWith("/") || line.length() == 0 || line.startsWith("* "))
				line = reader.nextLine();

			if(line.contains("//")){
				line = line.substring(0,line.lastIndexOf("//"));
				line = line.trim();
			}
			}
		}
		token = getNextToken();
		if(token.equals(""))
				advance();
		//System.out.println("token returning:" + token +"|");
		
	}

	private  String getNextToken() {
		int i = 0;
		char next;
		String temp;


		line = line.trim();
		if(!line.isEmpty() ){
			if(line.equals("\n") || line.equals(" ")){
				line = "";
				return line;
			}
			if(line.startsWith("\"")){
				int n = line.lastIndexOf('"');
				temp = line.substring(1,n);
				line = line.substring(n + 1);

				return temp;
			}
			next = line.charAt(i);
			while(  next != '(' && next != ')' && next != '{' && next != '}' && 
					next != '[' && next != ']' && next != '.' && next != ',' && 
					next != ';' && next != '+' && next != '-' && next != '*' &&
					next != '/' && next != '&' && next != '|' && next != '<' && 
					next != '>' && next != '=' && next != '~' && next != '\n'&&
					next != ' ' && i <line.length()-1){
				i++;
				next = line.charAt(i);
			}
			if(i == 0){
				temp = line.substring(0,1);
				line = line.substring(i + 1);
			}else{
				temp = line.substring(0,i);
				line = line.substring(i);
			}
			if(line.length() == 0)
				line = "";
			temp.trim();
			return temp; 
		}else{
			return line;
		}

	}

	public TokenType tokenType(){
		//System.out.println("token being checked:" + token);
		//checks the keyword map
		String temp = token.toUpperCase();
		if(keyWords.get(temp) != null)
			return TokenType.KEYWORD;

		if(temp.equals("TRUE") || temp.equals("FALSE") ||
		   temp.equals("NULL") || temp.equals("THIS"))
			return TokenType.KEYWORD_CONST;
		//searches for not digit, denoting identifier
		if(Pattern.matches("\\w*",token))
			return TokenType.IDENTIFIER;
		//searches for int. const.
		if(Pattern.matches("\\d*",token))
			return TokenType.INT_CONST;

		if(token.length() == 1)
			return TokenType.SYMBOL;

		return TokenType.STRING_CONST;		
	}

	public KeyWord keyWord(){
		String temp = token.toUpperCase();
		//		System.out.println("keyword:" + token);
		//		System.out.println("keyword type:" + keyWords.get(temp));

		return keyWords.get(temp);
	}

	public char symbol(){
		return token.charAt(0);
	}

	public String identifier(){
		return token;
	}

	public String stringVal(){
		return token.substring(1,token.lastIndexOf("\""));
	}

	public int inVal(){
		return Integer.parseInt(token);
	}

	private void init() {
		tokens = new HashMap<String,TokenType>();
		keyWords = new HashMap<String,KeyWord>();
		line = "";

		//filling token hashmap
		tokens.put("KEYWORD",TokenType.KEYWORD);
		tokens.put("SYMBOL", TokenType.SYMBOL);
		tokens.put("IDENTIFIER", TokenType.IDENTIFIER);
		tokens.put("INT_CONST", TokenType.INT_CONST);
		tokens.put("STRING_CONST", TokenType.STRING_CONST);
		//filling keywords hashmap
		keyWords.put("CLASS", KeyWord.CLASS);
		keyWords.put("METHOD",KeyWord.METHOD);
		keyWords.put("FUNCTION", KeyWord.FUNCTION);
		keyWords.put("CONSTRUCTOR", KeyWord.CONSTRUCTOR);
		keyWords.put("INT", KeyWord.INT);
		keyWords.put("BOOLEAN", KeyWord.BOOLEAN);
		keyWords.put("CHAR", KeyWord.CHAR);
		keyWords.put("VOID", KeyWord.VOID);
		keyWords.put("VAR", KeyWord.VAR);
		keyWords.put("STATIC", KeyWord.STATIC);
		keyWords.put("FIELD", KeyWord.FIELD);
		keyWords.put("LET", KeyWord.LET);
		keyWords.put("DO", KeyWord.DO);
		keyWords.put("IF", KeyWord.IF);
		keyWords.put("WHILE", KeyWord.WHILE);
		keyWords.put("RETURN", KeyWord.RETURN);
		keyWords.put("TRUE",KeyWord.TRUE);
		keyWords.put("FALSE", KeyWord.FALSE);
		keyWords.put("NULL", KeyWord.NULL);
		keyWords.put("THIS", KeyWord.THIS);
	}

	public  String lookAhead() {
		int i = 0;
		char next;
		String temp;


		String tempcpy = line.trim();
		if(!tempcpy.isEmpty()){
			if(tempcpy.startsWith("\"")){
				int n = tempcpy.lastIndexOf('"');
				temp = tempcpy.substring(1,n);
	//			line = line.substring(n + 1);

				return temp;
			}
			next = tempcpy.charAt(i);
			while(  next != '(' && next != ')' && next != '{' && next != '}' && 
					next != '[' && next != ']' && next != '.' && next != ',' && 
					next != ';' && next != '+' && next != '-' && next != '*' &&
					next != '/' && next != '&' && next != '|' && next != '<' && 
					next != '>' && next != '=' && next != '~' && next != '\n'&&
					next != ' ' && i < line.length()-1){
				next = tempcpy.charAt(i);
				i++;
			}
			if(i == 0){
				temp = tempcpy.substring(0,1);
	//			line = line.substring(i + 1);
			}else{
				temp = tempcpy.substring(0,i);
	//			line = line.substring(i);
			}
			if(tempcpy.length() == 0)
				tempcpy = "";
			temp.trim();
			return temp; 
		}else{
			return tempcpy;
		}

	}
}
