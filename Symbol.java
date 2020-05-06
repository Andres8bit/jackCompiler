
public class Symbol {
	private String name;
	private String type;
	private String kind;
	private int index;
	
	public Symbol(){
		name = "";
		type = "";
		kind = "";
		index = 0;
	}
	
	public Symbol(String identifier,String typeOf,String scope,int offset){
		name = identifier;
		type = typeOf;
		kind = scope;
		index = offset;
	}
	
	public String id(){
		return name;
	}
	
	public String type(){
		return type;
	}
	
	public String kind(){
		return kind;
	}
	
	public int index(){
		return index;
	}

	public void setIndex(int offset) {
		index = offset;	
	}
	
	public void setKind(String type){
		kind = type;
	}
	
	public void setName(String identifier){
		name = identifier;
	}
	
	public void setType(String typeOf){
		type = typeOf;
	}
	
	public void print(){
		System.out.println("Name:" + name +", Type:" + type + ", Kind:" + kind + ", #:" + index);
	}
}
