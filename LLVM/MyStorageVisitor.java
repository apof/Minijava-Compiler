import syntaxtree.*;
import visitor.GJDepthFirst;
import java.util.HashMap;
import java.util.Map;

public class MyStorageVisitor extends GJDepthFirst<Object, Object>{

	public SymbolTable table;

	public MyStorageVisitor() throws Exception {
		table = new SymbolTable();
	}


    public void already_defined(String[]local_vars,String[]args) throws Exception{
      for(int i = 0; i<local_vars.length; i++)
      {
        int n;
        if (args == null)
          n = 0;
        else
          n = args.length;
        for(int j=0; j<n; j++)
        {
          if ( local_vars[i].equals(args[j]))  {
            System.out.println("Local variable: " + local_vars[i] + " is already defined");
            throw new Exception();

          }
        }
      }
    }

    public String same_overrided_method(methodInfo node,String ext_class ) throws Exception
    {
      HashMap<String, methodInfo> methods = table.get_method_map();

      methodInfo check_node = methods.get(ext_class + "." + node.name);

      int flag = 0;

      if (check_node!=null)
        {
          flag = 1;
          
          if (check_node.arguments_num == node.arguments_num)
          {
            if(!check_node.return_type.equals(node.return_type)){
            System.out.println("Inherited Method " + node.parent_name + "." + node.name + " has not the same return type with method " + ext_class + "." + node.name);
            throw new Exception();
            }
            else{
              for(int i = 0; i<check_node.arguments_num; i++)
              {
                if(!check_node.arguments_type[i].equals(node.arguments_type[i])){
                System.out.println("Inherited Method " + node.parent_name + "." + node.name + " has incompatible arguments with method " + ext_class + "." + node.name);
                throw new Exception();
                }

              }
            }
          }
          else{
            System.out.println("Inherited Method " + node.parent_name + "." + node.name + " has not the same number of arguments with method " + ext_class + "." + node.name);
            throw new Exception();
          }

        }

        if(flag == 1)
        return node.name;
        else
        return null;

    }


//////////////////////////Visitors/////////////////////////////////////////////////////////////////
/**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> "public"
    * f4 -> "static"
    * f5 -> "void"
    * f6 -> "main"
    * f7 -> "("
    * f8 -> "String"
    * f9 -> "["
    * f10 -> "]"
    * f11 -> Identifier()
    * f12 -> ")"
    * f13 -> "{"
    * f14 -> ( VarDeclaration() )*
    * f15 -> ( Statement() )*
    * f16 -> "}"
    * f17 -> "}"
    */


   public Object visit(MainClass n, Object argu) throws Exception {

      HashMap<String, classInfo> classes = table.get_class_map();
      HashMap<String, methodInfo> methods = table.get_method_map();



      n.f0.accept(this, argu);

      String name = n.f1.accept(this, argu).toString();

      n.f2.accept(this, argu);
      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      n.f5.accept(this, argu);
      n.f6.accept(this, argu);
      n.f7.accept(this, argu);
      n.f8.accept(this, argu);
      n.f9.accept(this, argu);
      n.f10.accept(this, argu);

      String []ids = new String[1];
      String []types2 = new String[1]; 

      String id = n.f11.accept(this, argu).toString();

      ids[0] = id;
      types2[0] = "String[]";

      n.f12.accept(this, argu);
      n.f13.accept(this, argu);
      

      String []names = null;
      String []types = null;
      if(n.f14.size()!=0)
      {
      String []tok =  null;
      names = new String[n.f14.size()];
      types = new String[n.f14.size()];
      
      for (int i=0; i<n.f14.size(); i++)
      {
        String str = (n.f14.elementAt(i).accept(this,argu)).toString();
        tok = str.split("\\s");
        names[i] = tok[1];
        types[i] = tok[0];

      }
      }

      String []special_main = new String[1];
      special_main[0] = "main";

      classInfo info_node1 = new classInfo();
      info_node1.set_Info(null,null,name,null,special_main,0,0);
      classes.put(name,info_node1);

      methodInfo info_node2 = new methodInfo();
      info_node2.set_Info(types,names,types2,ids,"main",name,"null");
      methods.put(name + "." + "main",info_node2);

      n.f15.accept(this, argu);
      n.f16.accept(this, argu);
      n.f17.accept(this, argu);
      return null;
   }




   /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> ( VarDeclaration() )*
    * f4 -> ( MethodDeclaration() )*
    * f5 -> "}"
    */
   public Object visit(ClassDeclaration n, Object argu) throws Exception{

   	  HashMap<String, classInfo> classes = table.get_class_map();

      //System.out.println("ClassDeclaration");
      n.f0.accept(this, argu);
      String class_name = (n.f1.accept(this, argu)).toString();

      classInfo check_node = classes.get(class_name);
      if (check_node!=null)
      {
        System.out.println("Multiple definitions of class: " + class_name);
        throw new Exception();
      }


      
      n.f2.accept(this, argu);



      String []names = null;
      String []types = null;

      if(n.f3.size()!=0)
      {
      int size = n.f3.size();
      String []tok =  null;
      names = new String[n.f3.size()];
      types = new String[n.f3.size()];
      //System.out.println("Data Members: ");
      for (int i=0; i<n.f3.size(); i++)
      {
      	String str = (n.f3.elementAt(i).accept(this,argu)).toString();
      	tok = str.split("\\s");
      	names[i] = tok[1];
      	types[i] = tok[0];
      	//System.out.println(str);

      }


      for (int i=1; i<size; i++)
      {
        //System.out.println("i: " + i);
        for(int j=0; j<=i-1; j++)
        {
          int fl = i - 1;
          //System.out.println("j: " + j + "-" + fl);
          if (names[i].equals(names[j])){
            System.out.println("Multiple definition of data memeber: " + names[j] + " in class: " + class_name);
            throw new Exception();}
        }
      }

  	  }

      classInfo info_node1 = new classInfo();
      info_node1.set_Info(types,names,class_name,null,null,0,0);
      classes.put(class_name,info_node1);


      int meth_num = n.f4.size();
      String []meths = null;

      if (meth_num!=0){

      meths = new String[meth_num];

      for (int i=0; i<n.f4.size(); i++)
      {
        
        String str = (n.f4.elementAt(i).accept(this,class_name)).toString();
        meths[i] = str;
      }
      }

      info_node1.set_Info(types,names,class_name,null,meths,0,0);
      classes.put(class_name,info_node1);

      
      n.f5.accept(this, argu);
      return null;
   }

   /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "extends"
    * f3 -> Identifier()
    * f4 -> "{"
    * f5 -> ( VarDeclaration() )*
    * f6 -> ( MethodDeclaration() )*
    * f7 -> "}"
    */
   public Object visit(ClassExtendsDeclaration n, Object argu) throws Exception{

   	  HashMap<String, classInfo> classes = table.get_class_map();

      //System.out.println("Class Extends Declaration");
      n.f0.accept(this, argu);
      String class_name = (n.f1.accept(this, argu)).toString();
      n.f2.accept(this, argu);

      classInfo check_node = classes.get(class_name);
      if (check_node!=null)
      {
        System.out.println("Multiple definitions of class: " + class_name);
        throw new Exception();
      }
      
      String ext_class_name = (n.f3.accept(this, argu)).toString();

      classInfo check_node2 = classes.get(ext_class_name);
      if (check_node2==null)
      {
        System.out.println("class " + ext_class_name + " has not been declared (cannot be extended)");
        throw new Exception();
      }

      //String arg = class_name + " extends " +  ext_class_name;
      //System.out.println(arg);
      n.f4.accept(this, argu);

      String []names = null;
      String []types = null;

      if(n.f5.size()!=0)
      {
      int size = n.f5.size();
      names = new String[n.f5.size()];
      types = new String[n.f5.size()];
      for (int i=0; i<n.f5.size(); i++)
      {
      	String str = (n.f5.elementAt(i).accept(this,argu)).toString();
      	String []tok = str.split("\\s");
      	names[i] = tok[1];
      	types[i] = tok[0];

      }

      for (int i=1; i<size; i++)
      {
        //System.out.println("i: " + i);
        for(int j=0; j<=i-1; j++)
        {
          int fl = i - 1;
          //System.out.println("j: " + j + "-" + fl);
          if (names[i].equals(names[j])){
            System.out.println("Multiple definition of data memeber: " + names[j] + " in class: " + class_name);
            throw new Exception();}
        }
      }

  	  }


      classInfo info_node1 = new classInfo();
      classInfo nod = new classInfo();
      nod = classes.get(ext_class_name);

      info_node1.set_Info(types,names,class_name,ext_class_name,null,nod.member_offset,nod.method_offset);
      classes.put(class_name,info_node1);


      int meth_num = n.f6.size();
      String []meths = null;

      if (meth_num!=0){

      meths = new String[meth_num];

      for (int i=0; i<n.f6.size(); i++)
      {
        String str = (n.f6.elementAt(i).accept(this,class_name)).toString();

        meths[i] = str;
      }
      }


      info_node1.set_Info(types,names,class_name,ext_class_name,meths,nod.member_offset,nod.method_offset);
      classes.put(class_name,info_node1);


      
      n.f7.accept(this, argu);
      return null;
   }

   /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
   public Object visit(VarDeclaration n, Object argu) throws Exception {
      Object declaration = n.f0.accept(this, argu) + " " + n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      return declaration ;
   }


   /**
    * f0 -> <IDENTIFIER>
    */
   public Object visit(Identifier n, Object argu) throws Exception {
       String id = n.f0.toString();
       //System.out.println(id);
       return id;

   }

   /**
    * f0 -> "int"
    * f1 -> "["
    * f2 -> "]"
    */
   public Object visit(ArrayType n, Object argu) throws Exception {

      String int_type = n.f0.toString();
      String brack1 = n.f1.toString();
      String brack2 = n.f2.toString();
      String return_string = int_type + brack1 + brack2;
      //System.out.println(return_string);
      return return_string;
   }

   /**
    * f0 -> "boolean"
    */
   public Object visit(BooleanType n, Object argu) throws Exception {
       String boolType = n.f0.toString();
       //System.out.println(boolType);
       return boolType;
   }

   /**
    * f0 -> "int"
    */
   public Object visit(IntegerType n, Object argu) throws Exception {
    	String intType = n.f0.toString();
    	//System.out.println(intType);
    	return intType;
   }
    
   /**
    * f0 -> "public"
    * f1 -> Type()
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( FormalParameterList() )?
    * f5 -> ")"
    * f6 -> "{"
    * f7 -> ( VarDeclaration() )*
    * f8 -> ( Statement() )*
    * f9 -> "return"
    * f10 -> Expression()
    * f11 -> ";"
    * f12 -> "}"
    */
   public Object visit(MethodDeclaration n, Object argu) throws Exception {

   	  HashMap<String, methodInfo> methods = table.get_method_map();

      n.f0.accept(this, argu);
      String type = n.f1.accept(this, argu).toString();

      String name = n.f2.accept(this, argu).toString();

      methodInfo check_node = methods.get(argu + "." + name);
      if (check_node!=null)
      {
        System.out.println("Multiple definitions of method: " + argu + "." + name );
        throw new Exception();
      }

      n.f3.accept(this, argu);
      String arg_list = null; 
      String []arg_types = null;
      String []arg_names = null;
      Object obj = n.f4.accept(this, argu);
      if (obj!=null)
      {
      	arg_list = obj.toString();
      	String []tok = arg_list.split("\\s");
      	arg_types = new String[(tok.length)/2];
      	arg_names = new String[(tok.length)/2];
        int pos = 0;
        //System.out.println("Argument list: ");
      	for(int i = 0; i<tok.length; i = i + 2){
      		arg_types[pos] = tok[i];
      		//System.out.println(arg_types[pos]);
      		arg_names[pos] = tok[i+1];
      		//System.out.println(arg_names[pos]);
          pos++;

      	} 

        for (int i=1; i<arg_names.length; i++)
        {

        //System.out.println("i: " + i);
        for(int j=0; j<=i-1; j++)
        {
          int fl = i - 1;
          //System.out.println("j: " + j + "-" + fl);
          if (arg_names[i].equals(arg_names[j])){
            System.out.println("Multiple argument: " + arg_names[j] + " in method: " + name);
            throw new Exception();
            }
        }
      }
      }
      n.f5.accept(this, argu);
      n.f6.accept(this, argu);
      String []names = null;
      String []types = null;
      //System.out.println("Variable list: ");
      if(n.f7.size()!=0)
      {
      int size = n.f7.size();
      names = new String[n.f7.size()];
      types = new String[n.f7.size()];
      for (int i=0; i<n.f7.size(); i++)
      {
      	String str = (n.f7.elementAt(i).accept(this,argu)).toString();
      	String []tok = str.split("\\s");
      	names[i] = tok[1];
      	types[i] = tok[0];
      	//System.out.println(str);
      }


      already_defined(names,arg_names);

  	  

      for (int i=1; i<size; i++)
      {
        //System.out.println("i: " + i);
        for(int j=0; j<=i-1; j++)
        {
          int fl = i - 1;
          //System.out.println("j: " + j + "-" + fl);
          if (names[i].equals(names[j])){
            System.out.println("Multiple definition of variable: " + names[j] + " in method: " + name + " in class: " + argu);
            throw new Exception();
        }
        }
      }
      }


      methodInfo info_node1 = new methodInfo();
      //System.out.println("ok");
      //System.out.println(argu.toString());
      info_node1.set_Info(types,names,arg_types,arg_names,name,argu.toString(),type);

      HashMap<String, classInfo> classes = table.get_class_map();

      classInfo node = classes.get(argu.toString());


      String over_meth = null;

      String res_over = null;

      if (node!=null)
      {
        String p_name = node.parent_name;
        while(p_name!=null)
        {
          over_meth = same_overrided_method(info_node1,node.parent_name);
          if(over_meth!=null)
          {
            res_over = over_meth;
          }

          node = classes.get(p_name);
          p_name=node.parent_name;
        }
      }      
      //info_node1.print_info_method();
      methods.put(argu + "." + name,info_node1);


      n.f8.accept(this, argu);
      n.f9.accept(this, argu);
      n.f10.accept(this, argu);
      n.f11.accept(this, argu);
      n.f12.accept(this, argu);

      if(res_over==null)
      {
      return name;
      }
      else
      {
       return "over." + name;
      }
   }

   /**
    * f0 -> FormalParameter()
    * f1 -> FormalParameterTail()
    */
   public Object visit(FormalParameterList n, Object argu) throws Exception {

   		String res = null;
   		Object obj1 = n.f0.accept(this, argu);
   		Object obj2 = n.f1.accept(this, argu);
   		if (obj1!=null){
   			res = obj1.toString();
   			if(obj2!=null){
          res = res + " ";
   				res += obj2.toString();
   			}

   		}
   		return res;
   }

   /**
    * f0 -> Type()
    * f1 -> Identifier()
    */
   public Object visit(FormalParameter n, Object argu) throws Exception {
      
      String type = n.f0.accept(this, argu).toString();
      String id = n.f1.accept(this, argu).toString();
      String ret = type + " " + id;
      return ret; 
   }

   /**
    * f0 -> ( FormalParameterTerm() )*
    */
   public Object visit(FormalParameterTail n, Object argu) throws Exception {


    String result = null;

   	if(n.f0.size()!=0)
      {
      for (int i=0; i<n.f0.size(); i++)
      { 
      	String str = (n.f0.elementAt(i).accept(this,argu)).toString();
        if(i==0) result = str;
        else
      	result = result + " " + str;
      }

      return result;
  	  }

  	  return result;
   }


   /**
    * f0 -> ","
    * f1 -> FormalParameter()
    */
   public Object visit(FormalParameterTerm n, Object argu) throws Exception {
      n.f0.accept(this, argu);
      return n.f1.accept(this, argu);
      
   }

   

}


