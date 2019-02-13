public class methodInfo extends Info {

	public String return_type;
	public String []arguments;
	public String []arguments_type;
	public String []types;
	public String []names;
	int arguments_num;
	int var_num;
	

	public methodInfo(){

		super();
	}

	public void set_Info(String []t,String []n,String []arg_t,String []arg_n, String nam,String par,String ret_type) {

		super.set_name(nam);
		super.set_parent_name(par);
		
		return_type = ret_type;
		
		
		if (t!=null){

		var_num = t.length;
		types = new String[var_num];
		names = new String[var_num];

		for(int i=0; i<var_num; i++){
			types[i] = t[i];
			names[i] = n[i];
		}
		}
		else {
			var_num = 0;
			types = null;
			names = null;
		}


		if (arg_t!=null){

		arguments_num = arg_t.length;
		arguments = new String[arguments_num];
		arguments_type = new String[arguments_num];

		for(int i=0; i<arguments_num; i++){
			arguments[i] = arg_n[i];
			arguments_type[i] = arg_t[i];
		}
		}
		else{
			arguments_num = 0;
			arg_t = null;
			arg_n = null;
		}

	}

	public void print_info_method() {

		System.out.println("METHOD");
		super.print_info();
		if (types!=null){
		System.out.println("Types and members:");
		for(int i=0; i<var_num; i++){
			System.out.println(types[i] + " " + names[i]);
		}
		}
		else{ System.out.println("Method has no memebers");}

		if (arguments!=null){
		System.out.println("Types and arguments:");
		for(int i=0; i<arguments_num; i++){
			System.out.println(arguments[i] + " " + arguments_type[i]);
		}
		}
		else { System.out.println("Method has no arguments");}

		System.out.println("return type:");
		System.out.println(return_type);


	}

}
