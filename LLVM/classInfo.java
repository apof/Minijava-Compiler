import java.util.HashMap;
import java.util.Map;

public class classInfo extends Info {

	public String []types;
	public String []names;
	public String []methods;
	public int member_num;
	public int method_number;
	public int offsets[];
	public int offsets_num;
	public int member_offset;
	public int method_offset;


	// to prosthesa gia na 3erw poses func syberilamvanomenwn kai aytwn twn yperklasewn h class
	public int all_func;

	public classInfo(){

		super();
	}

	public void set_Info(String []t,String []n,String nam,String par,String []meth,int off_vars,int off_meths){

		all_func = 0;

		super.set_name(nam);
		super.set_parent_name(par);

		if (t!=null){
		member_num = t.length;
		types = new String[member_num];
		names = new String[member_num];
		
		for(int i=0; i<member_num; i++){
			types[i] = t[i];
			names[i] = n[i];
		}
		}
		else{
		member_num = 0;
		types = null;
		names = null;
		}

		if(meth!=null){
		method_number = meth.length;
		methods = new String[method_number];

		for(int i=0; i<method_number; i++){
			methods[i] = meth[i];
		}
		}
		else{
			method_number = 0;
			methods = null;
		}

		offsets = null;
		int offsets_num = 0;


		if(method_number + member_num!=0)
		{
			offsets = new int[method_number + member_num];
			int index_table = 0;

			int index_offset = off_vars;
			for(int i = 0; i<member_num; i++)
			{
				offsets[index_table] = index_offset;
				if(types[i].equals("int"))
					index_offset += 4;
				else if (types[i].equals("boolean"))
					index_offset += 1;
				else if  (types[i].equals("int[]"))
					index_offset += 8;
				else
					index_offset += 8;

				index_table++;
			}

			member_offset = index_offset;

			index_offset = off_meths;
			for(int i = 0; i<method_number; i++)
			{
				String []tok = methods[i].split("\\.");
				if (tok.length == 2)
				{

					methods[i] = tok[1];
					offsets[index_table] = -1;
					index_table++;

				}
				else{
				offsets[index_table] = index_offset;
				if(!methods[i].equals("main"))
				index_offset += 8;
				index_table++;
				}
			}

			method_offset = index_offset;

		}
		else{
			method_offset = off_meths;
			member_offset = off_vars;
		}

			

	}

	public void print_info_class() {

		System.out.println("CLASS");
		super.print_info();
		if (names!=null){
			System.out.println("Types and data members:");
		for(int i=0; i<member_num; i++){
			System.out.println(types[i] + " " + names[i] + " offset " + offsets[i]);
		}
		}
		else System.out.println("No members in this class");

		int index = member_num;

		if (methods!=null){
			System.out.println("Methods:");
		for(int i=0; i<method_number; i++){
			if(offsets[index] == -1)
			System.out.println(methods[i] + " offset " + offsets[index] + " --> overridded method no offset");
			else
			System.out.println(methods[i] + " offset " + offsets[index]);
			index++;
		}
		}
		else System.out.println("No methods in this class");

	}


	public int return_func_vtable(HashMap<String, methodInfo> meth_map, String node_name,int pos,String[] result_table) {

		int func_number = 0;
	    String each_function = null;
    	String all_functions = null;
		int index = member_num;

      	if (methods!=null)													// ama h klash exei methodous
      	{
      		for(int j=0; j<method_number; j++)
      		{
      			each_function = null;
        		if(!methods[j].equals("main"))
        		{
          			each_function = "i8* bitcast ";

          			methodInfo method_node = meth_map.get(name + "." + methods[j]);

          			String ret_t = null;
          			String args = "(i8*,";

          			if(method_node.return_type.equals("int"))
            			ret_t = "i32";
          			else if(method_node.return_type.equals("int[]"))
            			ret_t = "i32*";
          			else if(method_node.return_type.equals("boolean"))
            			ret_t = "i1";
          			else
            			ret_t = "i8*";

          			if(method_node.arguments!=null)
          			{

          				for(int k = 0; k < method_node.arguments_num; k++)
          				{
            				if(method_node.arguments_type[k].equals("int"))
            					args += "i32,";
          					else if(method_node.arguments_type[k].equals("int[]"))
            					args += "i32*,";
          					else if(method_node.arguments_type[k].equals("boolean"))
            					args += "i1,";
          					else
            					args += "i8*,";
          				}

					}
					args = args.substring(0,args.length() - 1) + ")";
					

          			each_function = each_function + "(" + ret_t + " " + args +  "* @" + node_name + "." + method_node.name + " to i8*),";

				}

				if(!methods[j].equals("main"))
				{
          			result_table[pos] = each_function;
				}
				else
				{
					result_table[pos] = "$";
				}

				pos++;
				func_number++;

			//if (all_functions == null && each_function!=null)
			//	all_functions = each_function;
		  	//else if (each_function!=null && all_functions != null)
           	//	all_functions = all_functions + each_function;

          	index++;
          }

          //all_functions = all_functions.substring(0,all_functions.length() - 1) + "]";
      	}

      	return func_number;


      }


      public int return_func_number(HashMap<String, methodInfo> meth_map) {

		int func_number = 0;
		int index = member_num;

      	if (methods!=null)													
      	{
      	  return method_number;
      	}
      	return 0;
      }

      public int get_meths(HashMap<String, methodInfo> meth_map,int pos,String [] meths) {

		int index = member_num;

      	if (methods!=null)													
      	{
      		for(int j=0; j<method_number; j++)
      		{
          		methodInfo method_node = meth_map.get(name + "." + methods[j]);
          		meths[pos] = method_node.name;
          
          		pos++;
          	index++;
          }

      	}
      	return method_number;
      }





}


