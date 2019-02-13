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

	public classInfo(){

		super();
	}

	public void set_Info(String []t,String []n,String nam,String par,String []meth,int off_vars,int off_meths){

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



}


