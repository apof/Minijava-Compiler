import syntaxtree.*;
import visitor.GJDepthFirst;
import java.util.HashMap;
import java.util.Map;

public class MyTypeCheckVisitor extends GJDepthFirst<Object, Object>{

	public SymbolTable table;

	public MyTypeCheckVisitor(SymbolTable stored_data) throws Exception {
		table = stored_data;
	}


  public void check_if_type_declared(String []t) throws Exception{

    HashMap<String, classInfo> classes = table.get_class_map();

    for(int i = 0; i<t.length; i++)
    {

      if (!t[i].equals("int") && !t[i].equals("boolean") && !t[i].equals("int[]"))
      {
        classInfo check_node = classes.get(t[i]);
        if (check_node==null)
        {
          System.out.println("Type: " + t[i] + " has not been declared");
          throw new Exception();
        }

      }
    }
  }


  public String id_exist(String method,String id) throws Exception{

    HashMap<String, classInfo> classes = table.get_class_map();
    HashMap<String, methodInfo> methods = table.get_method_map();

    String found = "not_found";

    //System.out.println(method);

    methodInfo method_node = methods.get(method);

    //method_node.print_info_method();

    if (method_node!=null){

    for(int i = 0; i < method_node.var_num; i++)
    {
      if(method_node.names[i].equals(id)) return method_node.types[i];
    }

    for(int i = 0; i < method_node.arguments_num; i++)
    {
      if(method_node.arguments[i].equals(id)) return method_node.arguments_type[i];
    }

    classInfo class_node = classes.get(method_node.parent_name);

    String s;

    do {

      for(int i = 0; i<class_node.member_num; i++)
      {
        if(class_node.names[i].equals(id)) return class_node.types[i];
      }

      s = class_node.parent_name;
  
      if (s!=null){
        class_node = classes.get(s);
      }

    }while(s!=null);
  }

    return "not_found";


  }

  public String check_superclass(String superclass, String subclass) throws Exception {

    if(subclass.equals(superclass)) return "yes";

    HashMap<String, classInfo> classes = table.get_class_map();

    classInfo class_node = classes.get(subclass);

    if(class_node!=null)
    {

    String s;

    s = class_node.parent_name;

    while(s!=null)
    {
      if (class_node.parent_name.equals(superclass))
        return "yes";

        class_node = classes.get(s);

        s = class_node.parent_name;

    }

  }
  else { return "no";}

    return "no";

  }

  public boolean is_basic_type(String a) throws Exception{

    if(!a.equals("int") && !a.equals("boolean") && !a.equals("int[]"))
      return false;
    else
      return true;

  }


  public String search_method(String where,String func_name,String name,String []arguments) throws Exception
  {

    if(is_basic_type(where)){ 
      System.out.println("Mehtod cannot be called in basic type(int,boolean,int[]) Identifier");
      throw new Exception();
    }

    HashMap<String, classInfo> classes = table.get_class_map();
    HashMap<String, methodInfo> methods = table.get_method_map();
   
    methodInfo method_node = methods.get(func_name);
    classInfo class_node = classes.get(method_node.parent_name);


    if("this".equals(where))
    {
      methodInfo node = methods.get(method_node.parent_name + "." + name);
      if(node == null)
      {
        System.out.println("Method " + method_node.parent_name + "." + name + " not found");
        throw new Exception();
      }
      else
      {
          int num;
          if (arguments == null)
            num = 0;
          else
            num = arguments.length;

          if (node.arguments_num != num)
          {
            System.out.println("Wrong number of arguments for " + method_node.parent_name + "." + name);
            throw new Exception();
          }
          else
          {
              for(int i = 0; i<node.arguments_num; i++)
              {
                if(!node.arguments_type[i].equals(arguments[i]))
                {

                if (arguments[i].equals("this"))
                {
                  if(check_superclass(node.arguments_type[i],method_node.parent_name).equals("no"))
                  {
                    System.out.println("(this. this) Invalid type of arguments for " + method_node.parent_name + "." + name);
                    throw new Exception();
                  }

                }
                else if(check_superclass(node.arguments_type[i],arguments[i]).equals("no"))
                { 
                 System.out.println("(this. arg)Invalid type of arguments for " + method_node.parent_name + "." + name);
                 throw new Exception();
                
                }


                }
              }

          }

          return node.return_type;

      }


    }
    else
    {
      
      methodInfo node = methods.get(where + "." + name);

         String p_name = where;

          while(p_name!=null){

          if(node!=null){

          int num;
          if (arguments == null)
            num = 0;
          else
            num = arguments.length;

          if (node.arguments_num != num)
          {
            System.out.println("Wrong number of arguments for " + p_name + "." + name);
            throw new Exception();
          }
          else
          {
            for(int i = 0; i<node.arguments_num; i++)
              {
                if(!node.arguments_type[i].equals(arguments[i]))
                {

                if (arguments[i].equals("this"))
                {
                  if(check_superclass(node.arguments_type[i],method_node.parent_name).equals("no"))
                  {

                    System.out.println("(this arg) Invalid type of arguments for " + p_name + "." + name);
                    throw new Exception();
                  }

                }
                else if(check_superclass(node.arguments_type[i],arguments[i]).equals("no"))
                { 
                 System.out.println("(obj arg)Invalid type of arguments for " + p_name + "." + name);
                 throw new Exception();
                
                }


                }
              }

              return node.return_type; 
          }

          }

          class_node = classes.get(p_name);
          p_name = class_node.parent_name;
          node = methods.get(p_name + "." + name);
        }
      }
    return "no";
  }


  //////////////////////////SXOLIA//////////////////////////////////////

  // # Oi typoi poy xrhsimpopoioume na exoun oristei ama den einai int,boolean,int[]
  // oi typoi aforoun data memebers,local vars methodwn orismata alla kai typous epistofhs methodwn


  //////////////////////////Visitors////////////////////////////////////


  /* f0 -> "class"
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

      n.f0.accept(this, argu);

      String name = n.f1.accept(this, null).toString();

      n.f2.accept(this, argu);
      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      n.f5.accept(this, argu);
      n.f6.accept(this, argu);
      n.f7.accept(this, argu);
      n.f8.accept(this, argu);
      n.f9.accept(this, argu);
      n.f10.accept(this, argu);

      n.f11.accept(this, null);

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
        String str = (n.f14.elementAt(i).accept(this,null)).toString();
        tok = str.split("\\s");
        names[i] = tok[1];
        types[i] = tok[0];
      }

      check_if_type_declared(types);
      }

      n.f15.accept(this, name + "." + "main");
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

      n.f0.accept(this, argu);
      String class_name = (n.f1.accept(this, argu)).toString();

      n.f2.accept(this, argu);

      if(n.f3.size()!=0)
      {
      int size = n.f3.size();
      String []tok =  null;
      String []names = new String[n.f3.size()];
      String []types = new String[n.f3.size()];
      //System.out.println("Data Members: ");
      for (int i=0; i<n.f3.size(); i++)
      {
        String str = (n.f3.elementAt(i).accept(this,argu)).toString();
        tok = str.split("\\s");
        names[i] = tok[1];
        types[i] = tok[0];
        //System.out.println(str);
      }

      check_if_type_declared(types);

      }


      n.f4.accept(this, class_name);
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

      n.f0.accept(this, argu);
      String class_name = (n.f1.accept(this, argu)).toString();
      n.f2.accept(this, argu);
      
      String ext_class_name = (n.f3.accept(this, argu)).toString();

      n.f4.accept(this, argu);
      

      if(n.f5.size()!=0)
      {
      int size = n.f5.size();
      String []names = new String[n.f5.size()];
      String []types = new String[n.f5.size()];
      for (int i=0; i<n.f5.size(); i++)
      {
        String str = (n.f5.elementAt(i).accept(this,argu)).toString();
        String []tok = str.split("\\s");
        names[i] = tok[1];
        types[i] = tok[0];
      }

      check_if_type_declared(types);
      }


      n.f6.accept(this, class_name);
      n.f7.accept(this, argu);
      return null;
   }

   /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
   public Object visit(VarDeclaration n, Object argu) throws Exception{
      Object declaration = n.f0.accept(this, argu) + " " + n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      return declaration ;
   }


   /**
    * f0 -> "int"
    * f1 -> "["
    * f2 -> "]"
    */
   public Object visit(ArrayType n, Object argu) throws Exception{

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
   public Object visit(BooleanType n, Object argu) throws Exception{
       String boolType = n.f0.toString();
       //System.out.println(boolType);
       return boolType;
   }

   /**
    * f0 -> "int"
    */
   public Object visit(IntegerType n, Object argu) throws Exception{
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

      n.f0.accept(this, null);
      String type = n.f1.accept(this, null).toString();

      String []t = new String[1];
      t[0] = type;
      check_if_type_declared(t);

      String name = n.f2.accept(this, null).toString();

      n.f3.accept(this, null);
      String arg_list = null; 
      String []arg_types = null;
      String []arg_names = null;
      Object obj = n.f4.accept(this, null);
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

        check_if_type_declared(arg_types);
  
      }
      n.f5.accept(this, null);
      n.f6.accept(this, null);
      String []names = null;
      String []types = null;
      if(n.f7.size()!=0)
      {
      int size = n.f7.size();
      names = new String[n.f7.size()];
      types = new String[n.f7.size()];
      for (int i=0; i<n.f7.size(); i++)
      {
        String str = (n.f7.elementAt(i).accept(this,null)).toString();
        String []tok = str.split("\\s");
        names[i] = tok[1];
        types[i] = tok[0];
        //System.out.println(str);
      }

      check_if_type_declared(types);

      }

      for (int i=0; i<n.f8.size(); i++)
      {
        n.f8.elementAt(i).accept(this, argu.toString() + "." + name);
      }

      n.f9.accept(this, null);

      String ret_val = n.f10.accept(this, argu.toString() + "." + name).toString();


      if(!ret_val.equals("this"))
      {
      if (!type.equals(ret_val) && check_superclass(type, ret_val).equals("no")) {
        System.out.println("(this or non user type)Incompatible method return type and return expression");
        throw new Exception();
      }
      }
      else
      {
        if(check_superclass(type, argu.toString()).equals("no"))
        {
          System.out.println("Incompatible method return type ,expected same class or superclass, or invalid assignment");
          throw new Exception();
        }
      }

      n.f11.accept(this, null);
      n.f12.accept(this, null);
      return null;
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

   ////////////////////////////////////////////////////////////////////////////////////////////////////////


   /**
    * f0 -> "{"
    * f1 -> ( Statement() )*
    * f2 -> "}"
    */
   public Object visit(Block n, Object argu) throws Exception {
      n.f0.accept(this, argu);

      for (int i=0; i<n.f1.size(); i++)
      { 
        n.f1.elementAt(i).accept(this,argu);
      }

      n.f2.accept(this, argu);
      return null;
   }

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
   public Object visit(AssignmentStatement n, Object argu) throws Exception {
      
      String type = n.f0.accept(this, argu).toString();

      n.f1.accept(this, argu);

      String res2 = n.f2.accept(this, argu).toString();

      if(is_basic_type(type) && is_basic_type(res2))
      {
          if (!res2.equals(type)){
            System.out.println("Expected Indentifier of " + res2 + " but is of type " + type);
            throw new Exception();}
      }
      else
      {
        
        String this_type = res2;

        if(res2.equals("this"))
        {

          
          String []res  = (argu.toString()).split("\\.");
          this_type = res[0];

        }
        if(check_superclass(type, this_type).equals("no")){
          System.out.println("Expected same class or superclass, or invalid assignment");
          throw new Exception();}
      }

      n.f3.accept(this, argu);
      return null;
   }

   /**
    * f0 -> Identifier()
    * f1 -> "["
    * f2 -> Expression()
    * f3 -> "]"
    * f4 -> "="
    * f5 -> Expression()
    * f6 -> ";"
    */
   public Object visit(ArrayAssignmentStatement n, Object argu) throws Exception {
      

      String type = n.f0.accept(this, argu).toString();

      if (!"int[]".equals(type)){ 
        System.out.println("Expected Indentifier of int[] type but is of type " + type);
        throw new Exception();}



      n.f1.accept(this, argu);

      String res2  = n.f2.accept(this, argu).toString();

      if (!"int".equals(res2)) {

        System.out.println("Invalid index of an array must be an int");
        throw new Exception();
        
      }

      n.f3.accept(this, argu);
      n.f4.accept(this, argu);

      String res3 = n.f5.accept(this, argu).toString();

      if (!res3.equals("int")) {

        System.out.println("Invalid type of expression: is " + res3 + " and must be " + type );
        throw new Exception();
        
      }

      n.f6.accept(this, argu);
      return null;
   }

   /**
    * f0 -> "if"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    * f5 -> "else"
    * f6 -> Statement()
    */
   public Object visit(IfStatement n, Object argu) throws Exception {
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);

      if (!(n.f2.accept(this, argu).toString()).equals("boolean")){
        System.out.println("Expression in if must be of boolean type");
        throw new Exception();}

      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      n.f5.accept(this, argu);
      n.f6.accept(this, argu);

      return null;
   }

   /**
    * f0 -> "while"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    */
   public Object visit(WhileStatement n, Object argu) throws Exception {
      
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);

      if (!(n.f2.accept(this, argu).toString()).equals("boolean")){
        System.out.println("Expression in while must be of boolean type");
        throw new Exception();}

      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      return null;
   }

   /**
    * f0 -> "System.out.println"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> ";"
    */
   public Object visit(PrintStatement n, Object argu) throws Exception {
      
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);

      if (!(n.f2.accept(this, argu).toString()).equals("int")){
        System.out.println("Expression Print must be of int type");
        throw new Exception();
      }
      
      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      return null;
   }



   /**
    * f0 -> Clause()
    * f1 -> "&&"
    * f2 -> Clause()
    */
   public Object visit(AndExpression n, Object argu) throws Exception {
      Object ob1 = n.f0.accept(this, argu).toString();
      n.f1.accept(this, argu);
      Object ob2  = n.f2.accept(this, argu).toString();
      //System.out.println(ob1 + " " + ob2);
      if(!ob1.equals("boolean") || !ob2.equals("boolean")){
        System.out.println("Clause must be between booleans");
        throw new Exception();}
      return "boolean";
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
   public Object visit(CompareExpression n, Object argu) throws Exception {
      Object ob1 = n.f0.accept(this, argu).toString();
      n.f1.accept(this, argu);
      Object ob2  = n.f2.accept(this, argu).toString();
      if(!ob1.equals("int") || !ob2.equals("int")){
        System.out.println(" < must be between int's");
        throw new Exception();}
      return "boolean";
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
   public Object visit(PlusExpression n, Object argu) throws Exception {
      Object ob1 = n.f0.accept(this, argu).toString();
      n.f1.accept(this, argu);
      Object ob2  = n.f2.accept(this, argu).toString();
      if(!ob1.equals("int") || !ob2.equals("int")){
        System.out.println("+ must be between int's");
        throw new Exception();}
      return "int";
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
   public Object visit(MinusExpression n, Object argu) throws Exception {
      Object ob1 = n.f0.accept(this, argu).toString();
      n.f1.accept(this, argu);
      Object ob2  = n.f2.accept(this, argu).toString();
      if(!ob1.equals("int") || !ob2.equals("int")){
        System.out.println("- must be between int's");
        throw new Exception();}
      return "int";
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
   public Object visit(TimesExpression n, Object argu) throws Exception {
      Object ob1 = n.f0.accept(this, argu).toString();
      n.f1.accept(this, argu);
      Object ob2  = n.f2.accept(this, argu).toString();
      if(!ob1.equals("int") || !ob2.equals("int")){
        System.out.println("* must be between int's");
        throw new Exception();}
      return "int";
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
   public Object visit(ArrayLookup n, Object argu) throws Exception {
      
      String type = n.f0.accept(this, argu).toString();

          if (!"int[]".equals(type)){
            System.out.println("Expected Indentifier of int[] but is of type " + type);
            throw new Exception();}

      n.f1.accept(this, argu);

      String res2 = n.f2.accept(this, argu).toString();

          if (!"int".equals(res2)) {
            System.out.println("Expected Indentifier of int type as an index but is of type " + res2);
            throw new Exception();}
      

      n.f3.accept(this, argu);

      return "int";
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    */
   public Object visit(ArrayLength n, Object argu) throws Exception {
      
      String res = n.f0.accept(this, argu).toString();

          if (!"int[]".equals(res)){ 
            System.out.println("Expected Indentifier of int[] type before .length but is of type " + res);
            throw new Exception();}

      n.f1.accept(this, argu);
      n.f2.accept(this, argu);

      return "int";
   }

   /**
    * f0 -> IntegerLiteral()
    *       | TrueLiteral()
    *       | FalseLiteral()
    *       | Identifier()
    *       | ThisExpression()
    *       | ArrayAllocationExpression()
    *       | AllocationExpression()
    *       | BracketExpression()
    */
   public Object visit(PrimaryExpression n, Object argu) throws Exception {
      return n.f0.accept(this, argu);
   }

   /**
    * f0 -> "true"
    */
   public Object  visit(TrueLiteral n, Object argu) throws Exception {
      n.f0.accept(this, argu);
      return "boolean";
   }

   /**
    * f0 -> "false"
    */
   public Object visit(FalseLiteral n, Object argu) throws Exception {
      n.f0.accept(this, argu);
      return "boolean";
   }

   /**
    * f0 -> <INTEGER_LITERAL>
    */
   public Object visit(IntegerLiteral n, Object argu) throws Exception {
      n.f0.accept(this, argu);
      return "int";
   }

   /**
    * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
   public Object visit(ArrayAllocationExpression n, Object argu) throws Exception {
  
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      if(!(n.f3.accept(this, argu).toString()).equals("int")){
        System.out.println("Index of an array must be int");
        throw new Exception();}
      n.f4.accept(this, argu);
      return "int[]";
   }

   /**
    * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    */
   public Object visit(AllocationExpression n, Object argu) throws Exception {
      
      HashMap<String, classInfo> classes = table.get_class_map();

      n.f0.accept(this, argu);

      String id = n.f1.accept(this, null).toString();

      classInfo check_node = classes.get(id);
      if (check_node==null)
      {
        System.out.println("Identifier " + id + " does not name a type");
        throw new Exception();
      }

      n.f2.accept(this, argu);
      n.f3.accept(this, argu);

      return id;
   }

   /**
    * f0 -> "!"
    * f1 -> Clause()
    */
   public Object visit(NotExpression n, Object argu) throws Exception {
      n.f0.accept(this, argu);

      //System.out.println("Not Clause");
      String res = n.f1.accept(this, argu).toString();
      if(!res.equals("boolean")){
        System.out.println("Expected boolean after !");
        throw new Exception();}

      return "boolean";
      
   }

   /**
    * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
   public Object visit(BracketExpression n, Object argu) throws Exception {
      n.f0.accept(this, argu);
      Object ret = n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      return ret;
   }


   /**
    * f0 -> <IDENTIFIER>
    */
   public Object visit(Identifier n, Object argu) throws Exception {


      String id  = n.f0.toString();

      if(argu == null)
      {
        return id;
      }
      else
      { 
      String type = id_exist(argu.toString(),id);
      if("not_found".equals(type)){
        System.out.println("Identifier(id check) not found");
        throw new Exception();
      }
      return type;
      }

   }

   /**
    * f0 -> "this"
    */
   public Object visit(ThisExpression n, Object argu) throws Exception {
      n.f0.accept(this, argu);
      return "this";
   }


   ////////////////////////////////Visitors for method calls////////////////////////////////

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( ExpressionList() )?
    * f5 -> ")"
    */
   public Object visit(MessageSend n, Object argu) throws Exception {

      String ex = n.f0.accept(this, argu).toString();

      n.f1.accept(this, argu);

      String id = n.f2.accept(this, null).toString();

      n.f3.accept(this, argu);

      String arg_list = null;
      String []list = null;
      Object obj = n.f4.accept(this, argu);
      if (obj!=null)
      {
        arg_list = obj.toString();
        String []tok = arg_list.split("\\s");
        list = new String[(tok.length)];
        for(int i = 0; i<tok.length; i++){
          list[i] = tok[i];
        } 
      }

      /////////////

      String method_ret = search_method(ex,argu.toString(),id,list);

      n.f5.accept(this, argu);

      return method_ret;
   }

   /**
    * f0 -> Expression()
    * f1 -> ExpressionTail()
    */
   public Object visit(ExpressionList n, Object argu) throws Exception{
      String res = null;
      Object obj1 = n.f0.accept(this, argu);
      Object obj2 = n.f1.accept(this, argu);
      
      if(obj1!=null)
      {
        res = obj1.toString();
        if(obj2!=null)
        {
          res = res + " ";
          res += obj2.toString();
        }
      }
      return res;
   }

   /**
    * f0 -> ( ExpressionTerm() )*
    */
   public Object visit(ExpressionTail n, Object argu) throws Exception{


      String result = null;
      if (n.f0.size()!=0)
      {
        for(int i=0; i<n.f0.size(); i++)
        {
          String str = (n.f0.elementAt(i).accept(this,argu)).toString();
          if (i==0) result = str;
          else
            result = result + " " + str;
        }
      }
      return result; 
   }

   /**
    * f0 -> ","
    * f1 -> Expression()
    */
   public Object visit(ExpressionTerm n, Object argu) throws Exception{
      n.f0.accept(this, argu);
      return n.f1.accept(this, argu);
   }

}
