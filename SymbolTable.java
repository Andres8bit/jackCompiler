import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
	private Map<String, Symbol> clas;
	private Map<String,Symbol> subroutine;
	int class_static_index;
	int class_field_index;
	int arg_index;
	int var_index;


	public SymbolTable(){
		clas = new HashMap<String,Symbol>();
		subroutine = new HashMap<String,Symbol>();
		class_static_index = 0;
		class_field_index = 0;
		arg_index = 0;
		var_index = 0;
	}

	public void define(String name,String type,String kind){
		String scope = kind.toUpperCase();
		Symbol var = new Symbol(name,type,scope,0);

		//class scope
		if(scope.contentEquals("STATIC") || scope.contentEquals("FIELD")){

			if(scope.contentEquals("STATIC")){
				var.setIndex(class_static_index);
				class_static_index++;
			}else{
				var.setIndex(class_field_index);
				class_field_index++;
			}

			System.out.println("is class scope:" + scope);
			if(!clas.containsKey(name)){//is new entry
				//System.out.println("is new entry name class:" + name);
				clas.put(name, var);

			}else{//is already in scope add??
				//System.out.println("already in scope:" + name);
			}
		}else{
			//is subroutine scope
			if(scope.contentEquals("ARG") || scope.contentEquals("VAR")){
				
				if(scope.contentEquals("ARG")){
					var.setIndex(arg_index);
					arg_index++;
				}else{
					var.setIndex(var_index);
					var_index++;
				}
				
				//System.out.println("is subroutine scope:" + scope);
				if(!subroutine.containsKey(name)){//new entry
					//System.out.println("is new entry name subroutine:" + name);
					subroutine.put(name, var);
				}else{//not new entry
					//System.out.println("is not new entry subroutine:" + name);

				}
			}else{
				//System.out.println("error unknown scope:" + scope);
			}

		}
		var.print();
	}


	public int varCount(String kind){
		String scope = kind.toUpperCase();

		if(is_class_scope(scope)){
			//System.out.println("calling class count check:" + kind);
			return clas.size();

		}else{
			//System.out.println("calling subroutine count check:" + kind);
			return subroutine.size();
		}	
	}

	public String kindOf(String name){
		if(clas.containsKey(name))
			return clas.get(name).kind();
		
		return subroutine.get(name).kind();
	}

	public String typeOf(String name){
		if(clas.containsKey(name))
			return clas.get(name).type();
		return subroutine.get(name).type();
	}

	public int indexOf(String name){
		if(clas.containsKey(name))
			return clas.get(name).index();
		return subroutine.get(name).index();
	}

	public void clear_subroutine(){
		subroutine.clear();
	}

	private boolean is_class_scope(String kind) {
		if(kind.contentEquals("STATIC") || kind.contentEquals("FIELD"))
			return true;
		return false;
	}
}