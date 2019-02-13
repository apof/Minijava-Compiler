public class Info {

	public String name;
	public String parent_name;

	public Info() {
		name = null;
		parent_name = null;
	}

	public void set_name(String arg){
		name = arg;
	}

	public void set_parent_name(String arg){
		parent_name = arg;
	}

	public void print_info(){

		System.out.println("Name and parent name: " + name + " " + parent_name);
	}
}



