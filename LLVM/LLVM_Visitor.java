import syntaxtree.*;
import visitor.GJDepthFirst;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

public class LLVM_Visitor extends GJDepthFirst<Object, Object>{

	public SymbolTable table;
	public int Label_counter;
	public int Var_counter;
	public File file;

	public LLVM_Visitor(SymbolTable my_table, String FileName) throws Exception {
		table = my_table;
		Label_counter = 0;
		Var_counter = 0;


    //create a directory for .ll
    String Dir_String = "LLVM";
    File theDir = new File(Dir_String);

    // if the directory does not exist, create it
    if (!theDir.exists()) {
    theDir.mkdir();
    }

		String file_name = "./" + Dir_String + "/" + FileName + ".ll";

		file = new File(file_name);


    // if file exists delete it		
		if (file.exists()) {
     file.delete();
     	}

    // and create a new
 		file.createNewFile();


		}


    /////////////////////////////////////// Vohthitikes Synarthseis /////////////////////////////////////////////////////////


    public String id_exist(String method,String id) throws Exception{

    HashMap<String, classInfo> classes = table.get_class_map();
    HashMap<String, methodInfo> methods = table.get_method_map();

    String found = "not_found";


    methodInfo method_node = methods.get(method);

    if (method_node!=null){

    for(int i = 0; i < method_node.var_num; i++)
    {
      if(method_node.names[i].equals(id)) return method_node.types[i] + " " + "method";
    }

    for(int i = 0; i < method_node.arguments_num; i++)
    {
      if(method_node.arguments[i].equals(id)) return method_node.arguments_type[i] + " " + "method_argument";
    }

    classInfo class_node = classes.get(method_node.parent_name);

    String s;

    do {

      for(int i = 0; i<class_node.member_num; i++)
      {
        if(class_node.names[i].equals(id)) return class_node.types[i] + " " + class_node.name + " " + class_node.offsets[i];
      }

      s = class_node.parent_name;
  
      if (s!=null){
        class_node = classes.get(s);
      }

    }while(s!=null);
  }

    return "not_found";


  }

  public int find_method_offset(String class_name, String method){

    int result = -1;

    HashMap<String, classInfo> classes = table.get_class_map();

    classInfo class_node = classes.get(class_name);

    String s;

    do {

      for(int j=0; j<class_node.method_number; j++)
      {
        if(class_node.methods[j].equals(method) && class_node.offsets[class_node.member_num + j]!=-1)
        {
          if(class_node.offsets[class_node.member_num + j] == 0)
            result = 0;
          else 
            result = class_node.offsets[class_node.member_num + j]/8;
        }
      }

      s = class_node.parent_name;
  
      if (s!=null){
        class_node = classes.get(s);
      }

    }while(s!=null);

    return result;

  }


  public String get_method_arguments(String class_name, String method,String []class_type){

    String args = null;

    HashMap<String, classInfo> classes = table.get_class_map();
    HashMap<String, methodInfo> meth_map = table.get_method_map();


    classInfo class_node = classes.get(class_name);

    String s;

    do {

      for(int j=0; j<class_node.method_number; j++)
      {

        if(class_node.methods[j].equals(method) && class_node.offsets[class_node.member_num + j]!=-1)
        {
          methodInfo method_node = meth_map.get(class_node.name + "." + class_node.methods[j]);

          if(method_node.return_type.equals("int"))
            args = "i32,";
          else if(method_node.return_type.equals("int[]"))
            args = "i32*,";
          else if(method_node.return_type.equals("boolean"))
            args = "i1,";
          else
          {
            class_type[0] = method_node.return_type;
            args = "i8*,";
          }


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
        }
      }

      s = class_node.parent_name;
  
      if (s!=null){
        class_node = classes.get(s);
      }

    }while(s!=null);

    if (args!=null)
      args = args.substring(0,args.length() - 1);

    return args;

  }


  public int class_size(String class_name) {

    HashMap<String, classInfo> classes = table.get_class_map();

    classInfo class_node = classes.get(class_name);

    String s;

    int size = 0;

    do {

      for(int i = 0; i<class_node.member_num; i++)
      {
          if(class_node.types[i].equals("int"))
            size+=4;
          else if(class_node.types[i].equals("int[]"))
            size+=8;
          else if (class_node.types[i].equals("boolean"))
            size+=1;
          else
            size+=8;
      }

      s = class_node.parent_name;
  
      if (s!=null){
        class_node = classes.get(s);
      }

    }while(s!=null);

    return size + 8;


  }


	public String getVar(){
		String ret_val = "%_" + Integer.toString(Var_counter);
		Var_counter++;
		return ret_val;
	}

	public String getLabel(){
		String ret_val = "Label_" + Integer.toString(Label_counter);
		Label_counter++;
		return ret_val;
	}

	public void emit(String str ) {

		BufferedWriter bw = null;
		FileWriter fw = null;

		try {
		fw = new FileWriter(file.getAbsoluteFile(), true);
		bw = new BufferedWriter(fw);
		bw.write(str);
		}
		catch(Exception ex)
		{
			System.out.println("Emit file exception");
		}
		finally {

			try {

				if (bw != null)
					bw.close();

				if (fw != null)
					fw.close();

			} catch (Exception ex) {

				ex.printStackTrace();

			}
		}
	}

  public void Vtable_create(){

    HashMap<String, classInfo> classes = table.get_class_map();
    HashMap<String, methodInfo> methods = table.get_method_map();

    Set set = classes.entrySet();
    Iterator iterator = set.iterator();

    while(iterator.hasNext()) {

    Map.Entry mentry = (Map.Entry)iterator.next();
    classInfo node = (classInfo)mentry.getValue();                // epilegw mia mia tis klaseis apo to map

    String vtable = null;
    vtable = "@." + node.name + "_vtable = global [";


    String s = null;
    classInfo class_node = classes.get(node.name);                // metraw poses yperklaseis exei h klash syberilamvanomenhs kai ths idias
    int number_of_classes = 0;
    do {

      number_of_classes += 1;

      s = class_node.parent_name;
  
      if (s!=null){
        class_node = classes.get(s);
      }

    }while(s!=null);

    classInfo []class_table = new classInfo[number_of_classes];

    s = null;
    class_node = classes.get(node.name);                // apothikeyw thn klash kai tis yperklaseis se ena pinaka
    number_of_classes = 0;
    do {

      class_table[number_of_classes] = class_node;

      number_of_classes += 1;

      s = class_node.parent_name;
  
      if (s!=null){
        class_node = classes.get(s);
      }

    }while(s!=null);




     int total_func_num = 0;
     for(int i = number_of_classes - 1; i>=0; i--)
     {
      int temp = 0;
      temp = class_table[i].return_func_number(methods);
      total_func_num += temp;
      }

      String []meths_array = new String[total_func_num];
      String []result_table = new String[total_func_num];
      int []last_occ = new int[total_func_num];
      int pos = 0;

      for(int i = number_of_classes - 1; i>=0; i--)
      {
      int p;
      p = class_table[i].get_meths(methods,pos,meths_array);
      pos += p;
      }


      if (total_func_num!=0 && total_func_num!=1)
      {
      for(int i = 0; i<meths_array.length; i++)
      {

        int po = -1;
        for(int j=0; j<meths_array.length; j++)
        {
          if (meths_array[j].equals(meths_array[i]) && j>i)
            po = j;

        }
        if(po != -1)
        {
          last_occ[i] = 0;
          last_occ[po] = 1;
        }
        else last_occ[i] = 1;
      }
      }
      else if (total_func_num == 1) last_occ[0] = 1;

      
      
    String all_funcs = "[ ";
    int pp = 0;

    for(int i = number_of_classes - 1; i>=0; i--)
    {
      int temp;
      temp = class_table[i].return_func_vtable(methods,class_table[i].name,pp,result_table);
      pp += temp;    
    }

    int total_funcs = 0;
    for(int l = 0; l<meths_array.length; l++)
    {
      if (last_occ[l] == 1)
      {
        if (!meths_array[l].equals("main"))
        {
         total_funcs++;
         all_funcs += result_table[l];
        }
      }
      else
      {
        //System.out.println("ok1");
        for(int m = 0; m<meths_array.length; m++)
        {
          if(m>l && last_occ[m]==1 && meths_array[m].equals(meths_array[l])  )
          {
            all_funcs += result_table[m];
            total_funcs++;
            last_occ[m]=0;
          }
        }
      }

    }



    node.all_func = total_funcs;
    classes.put(node.name,node);

    all_funcs = all_funcs.substring(0,all_funcs.length() - 1) + "]";

    vtable = vtable + Integer.toString(total_funcs) + " x i8*] " + all_funcs + "\n";

    emit(vtable);

  }


  }



  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////



  /**
    * f0 -> <IDENTIFIER>
    */
   public Object visit(Identifier n, Object argu) throws Exception {

       String id = n.f0.toString();


       if (argu!=null){                               // to argu periexei String ths morfhs simple_id class_name.method_name
                                                      // mporei sto telos na periexei kai ena not ama den theloume timh alla deikth
       String []tokk = null;                          // se periptwsh pou anaferomaste se int boolean data member yperklashs
       tokk = argu.toString().split("\\s");

       String typ = id_exist(tokk[1],id);             // gyrnaei to typo tou id kai ama vrethhke sth methodo h se klash yperklash


       if(typ.equals("not_found"))                    // edw anagnwrizei ws type se declarartion kapoia klash
          return "class_id " + id;                    // kaleitai mono sta declaration gia type me typo kapoia klash


       String []tok = null;
       tok = typ.split("\\s");
       typ = tok[0];

      // na ftia3w thn periptwsh o id na einai typou antikeimenou
      // na melethsw thn periptwhs int[] const

      String res = null;

      // ama to id einai topikh metavlhth (metavlhth orismenh sth methodo h orisma to gyrnaei me to onoma kai ton typo tou)

      if (tok[1].equals("method") || tok[1].equals("method_argument")) // periptwsh pou ena id einai topikh metavlhth synarthshs
      {

      if(typ.equals("int"))
      {
        return "int_var %_" + id;
      }
      else if (typ.equals("boolean"))
      {
        return "boolean_var %_" + id;
      }
      else if (typ.equals("int[]"))
      {
        if (tokk.length==3 )
        {
        if(tokk[2].equals("not2"))
        {

        String var = getVar();
        res = null;
        res = var + " = load i32*, i32** %_" + id + "\n";
        emit(res);
        return "int[]_var " + var;
        }
        else return "int[]_var %_" + id;
        }
        else return "int[]_var %_" + id;

      }
      else
      {
        if (tokk.length==3 )
        {
        if(tokk[2].equals("not2"))
        {

        String var = getVar();
        res = null;
        res = var + " = load i8*, i8** %_" + id + "\n";
        emit(res);
        return "obj " + var + " " + typ;
        }
        else return "obj %_" + id + " " + typ;
        }
        else return "obj %_" + id + " " + typ;
      }

      }
      // ama to id einai data member klashs h yperklashs prepei na to fortwsw apo to "this"
      else
      {
        String tt = null;
        String flg = null;
        String ttt = null;

        if(typ.equals("int"))
        {
          tt = "i32";
          flg = "int_const";
        }
        else if (typ.equals("boolean"))
        {
          tt = "i1";
          flg = "boolean_const";
        }
        else if (typ.equals("int[]"))
        {
          tt = "i32*"; 
          flg = "int[]_var";
        }
        else
        {
         tt = "i8*";
         flg = "obj";
         ttt = typ;
        }

        String var = getVar();
        int offset = Integer.parseInt(tok[2]);
        offset = offset + 8;
        String off_str = Integer.toString(offset);
        
        res = var + " = getelementptr i8, i8* %_this, i32 " + off_str + "\n";
        emit(res);
        String var2 = getVar();
        res = var2 + " = bitcast i8* " + var + " to " + tt + "*\n";
        emit(res);


        String var3 = null;

        if (tokk.length!=3 || (tokk.length==3)&&(tokk[2].equals("not2")))                                                         
        {
        if(tt.equals("i32"))
        {
          var3 = getVar();
          res = var3 + " = load i32, i32* " + var2 + "\n";
          emit(res);
          return flg + " " + var3;

        }
        else if(tt.equals("i1"))
        {
          var3 = getVar();
          res = var3 + " = load i1, i1* " + var2 + "\n";
          emit(res);
          return flg + " " + var3;
        }

        }

        if (tokk.length==3)                              
        {
        if (tokk[2].equals("not2"))
        {
        if(tt.equals("i32*"))
        {
          var3 = getVar();
          res = var3 + " = load i32*, i32** " + var2 + "\n";
          emit(res);
          return flg + " " + var3;
        }


        if(tt.equals("i8*"))
        {
          var3 = getVar();
          res = var3 + " = load i8*, i8** " + var2 + "\n";
          emit(res);
          return flg + " " + var3 + " " + ttt;
        }

        }
        }

        if (tt.equals("i8*"))
        {
          return flg + " " + var2 + " " + ttt;
        }
        else
          return flg + " " + var2;

      }

      }

       // oti kalw me null gyrnaei apla to id tou --> (Onomata klasewn, onomata Synarthsewn kai klhsewn synarthsewn,argument ths main)
       return "simple_id " + id;

   }


  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

    Vtable_create();

     emit("\n\n");

   	 emit("declare i8* @calloc(i32, i32)\ndeclare i32 @printf(i8*, ...)\ndeclare void @exit(i32)\n\n");

   	 emit("@_cint = constant [4 x i8] c"+"\""+"%d\\0a\\00"+"\""+"\n");
   	 emit("@_cOOB = constant [15 x i8] c"+"\""+"Out of bounds\\0a\\00"+"\""+"\n");

     emit("@.ret_val = constant [20 x i8] c"+"\""+"Returned value: %d\\0A\\00"+"\""+"\n\n");
   	 emit("define void @print_int(i32 %i) {\n%_str = bitcast [4 x i8]* @_cint to i8*\ncall i32 (i8*, ...) @printf(i8* %_str, i32 %i)\nret void\n}\n\n");
   	 emit("define void @throw_oob() { \n%_str = bitcast [15 x i8]* @_cOOB to i8*\ncall i32 (i8*, ...) @printf(i8* %_str)\ncall void @exit(i32 1)\nret void\n}\n\n");
   	 emit("define i32 @main() {\n");

      n.f0.accept(this, argu);

      String id  = n.f1.accept(this, null).toString();
      //String []tok =  null;
      //tok = id.split("\\s");
      //id = tok[1];

      n.f2.accept(this, argu);
      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      n.f5.accept(this, argu);
      n.f6.accept(this, argu);
      n.f7.accept(this, argu);
      n.f8.accept(this, argu);
      n.f9.accept(this, argu);
      n.f10.accept(this, argu);
      n.f11.accept(this, argu);
      n.f12.accept(this, argu);
      n.f13.accept(this, argu);

      emit("\n");

      n.f14.accept(this,id + "." + "main");

      emit("\n\n");

      n.f15.accept(this, id + "." + "main");

      n.f16.accept(this, argu);
      n.f17.accept(this, argu);

      emit("ret i32 0\n");

      emit("}\n\n");


      return null;
   }

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
   public Object visit(AssignmentStatement n, Object argu) throws Exception {
  
      String id  = n.f0.accept(this, argu.toString() + " not1").toString();
      String []tok =  null;
      tok = id.split("\\s");


      String reg = tok[1];

      n.f1.accept(this, argu);

      String val = n.f2.accept(this, argu.toString() + " not2").toString();

      tok =  null;
      tok = val.split("\\s");
      val = tok[0];

      if (val.equals("int_const"))
      {
        String res = "store i32 " + tok[1] + ", i32* " + reg + "\n";
        emit(res);  
      }
      else if (val.equals("int_var"))
      {
        String var = getVar();
        String res = var + " = load i32, i32* " + tok[1] + "\n";
        emit(res);
        res = "store i32 " + var + ", i32* " + reg + "\n";
        emit(res);

      }
      else if ((val.equals("boolean_const")))
      {
        String res = "store i1 " + tok[1] + ", i1* " + reg + "\n";
        emit(res);
      }
      else if ((val.equals("boolean_var"))) 
      {
        String var = getVar();
        String res = var + " = load i1, i1* " + tok[1] + "\n";
        emit(res);
        res = "store i1 " + var + ", i1* " + reg + "\n";
        emit(res);
      }
      else if ((val.equals("int[]_var"))) 
      {

        String res = null;
        res = "store i32* " + tok[1] + ", i32** " + reg + "\n";
        emit(res);

      }
      else if ((val.equals("obj")))
      {
        String res = null;
        res = "store i8* " + tok[1] + ", i8** " + reg + "\n";
        emit(res);
      }

      n.f3.accept(this, argu);

      emit("\n\n");


      return null;

   }

   /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
   public Object visit(VarDeclaration n, Object argu) throws Exception {

   	  String t = null;

      if (argu!=null){

      String typ = n.f0.accept(this, argu).toString();

      String id  = n.f1.accept(this, argu).toString();

      String []tok =  null;
      tok = id.split("\\s");
      id = tok[1];

      if (typ.equals("int"))
      	t = "i32";
      else if (typ.equals("boolean"))
      	t = "i1";
      else if (typ.equals("int[]"))
        t= "i32*";
      else
        t = "i8*";	

      emit(id + " = alloca " + t + "\n");

      n.f2.accept(this, argu);

      return null;
    }
    return null;
   }

   ///////////////////// Types //////////////////////////////////////////////////////////////////

   public Object visit(ArrayType n, Object argu) throws Exception {
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      return "int[]";
   }

   public Object visit(BooleanType n, Object argu) throws Exception {
      n.f0.accept(this, argu);
      return "boolean";
   }

   public Object visit(IntegerType n, Object argu) throws Exception {
      n.f0.accept(this, argu);
      return "int";
   }

   /////////////////////////////////////////////////////////////////////////////////////////////////

   //////////////////////////////////////Expressions////////////////////////////////////////////////
   
   public Object visit(PlusExpression n, Object argu) throws Exception {

      String val1 = n.f0.accept(this, argu).toString();
      n.f1.accept(this, argu);
      String val2 = n.f2.accept(this, argu).toString();

      String []tok1 =  null;
      String []tok2 = null;
      tok1 = val1.split("\\s");
      tok2 = val2.split("\\s");

      if (tok1[0].equals("int_const"))
      {
        val1 = tok1[1];  
      }
      else
      {
        String var = getVar();
        String res = var + " = load i32, i32* " + tok1[1] + "\n";
        emit(res);
        val1 = var;

      }

      if (tok2[0].equals("int_const"))
      {
        val2 = tok2[1];  
      }
      else
      {
        String var = getVar();
        String res = var + " = load i32, i32* " + tok2[1] + "\n";
        emit(res);
        val2 = var;

      }

      String plus_var = getVar();
      String plus_res = plus_var + " = add i32 " + val1 + ", " + val2 + "\n";

      emit(plus_res);

      emit("\n\n");


      return "int_const " + plus_var;


   }

   public Object visit(MinusExpression n, Object argu) throws Exception {
    
      String val1 = n.f0.accept(this, argu).toString();
      n.f1.accept(this, argu);
      String val2 = n.f2.accept(this, argu).toString();

      String []tok1 =  null;
      String []tok2 = null;
      tok1 = val1.split("\\s");
      tok2 = val2.split("\\s");

      if (tok1[0].equals("int_const"))
      {
        val1 = tok1[1];  
      }
      else
      {
        String var = getVar();
        String res = var + " = load i32, i32* " + tok1[1] + "\n";
        emit(res);
        val1 = var;

      }

      if (tok2[0].equals("int_const"))
      {
        val2 = tok2[1];  
      }
      else
      {
        String var = getVar();
        String res = var + " = load i32, i32* " + tok2[1] + "\n";
        emit(res);
        val2 = var;

      }

      String minus_var = getVar();
      String minus_res = minus_var + " = sub i32 " + val1 + ", " + val2 + "\n";

      emit(minus_res);

      emit("\n\n");


      return "int_const " + minus_var;

   }

   public Object visit(TimesExpression n, Object argu) throws Exception {
      
      String val1 = n.f0.accept(this, argu).toString();
      n.f1.accept(this, argu);
      String val2 = n.f2.accept(this, argu).toString();

      String []tok1 =  null;
      String []tok2 = null;
      tok1 = val1.split("\\s");
      tok2 = val2.split("\\s");

      if (tok1[0].equals("int_const"))
      {
        val1 = tok1[1];  
      }
      else
      {
        String var = getVar();
        String res = var + " = load i32, i32* " + tok1[1] + "\n";
        emit(res);
        val1 = var;

      }

      if (tok2[0].equals("int_const"))
      {
        val2 = tok2[1];  
      }
      else
      {
        String var = getVar();
        String res = var + " = load i32, i32* " + tok2[1] + "\n";
        emit(res);
        val2 = var;

      }

      String mul_var = getVar();
      String mul_res = mul_var + " = mul i32 " + val1 + ", " + val2 + "\n";

      emit(mul_res);

      emit("\n\n");


      return "int_const " + mul_var;
   }

   public Object visit(AndExpression n, Object argu) throws Exception {

      String val1 = n.f0.accept(this, argu).toString();
      n.f1.accept(this, argu);
      String val2 = n.f2.accept(this, argu).toString();

      String []tok1 =  null;
      String []tok2 = null;
      tok1 = val1.split("\\s");
      tok2 = val2.split("\\s");

      String var1 = null;
      String res  = null;
      String var2 = null;

      if(tok1[0].equals("boolean_var"))
      {

        var1 = getVar();
        res = var1 + " = load i1, i1* " + tok1[1] + "\n";
        emit(res);

      }
      else if (tok1[0].equals("boolean_const"))
      {

        var1 = tok1[1];

      }

      if(tok2[0].equals("boolean_var"))
      {

        var2 = getVar();
        res = var2 + " = load i1, i1* " + tok2[1] + "\n";
        emit(res);

      }
      else if (tok2[0].equals("boolean_const"))
      {

        var2 = tok2[1];

      }

      String and_var = getVar();
      String and_res = and_var + " = and i1 " + var1 + ", " + var2 + "\n";

      emit(and_res);

      emit("\n\n");


      return "boolean_const " + and_var;



   }

   public Object visit(CompareExpression n, Object argu) throws Exception {
      
      String val1 = n.f0.accept(this, argu).toString();
      n.f1.accept(this, argu);
      String val2 = n.f2.accept(this, argu).toString();

      String []tok1 =  null;
      String []tok2 = null;
      tok1 = val1.split("\\s");
      tok2 = val2.split("\\s");

      if (tok1[0].equals("int_const"))
      {
        val1 = tok1[1];  
      }
      else
      {
        String var = getVar();
        String res = var + " = load i32, i32* " + tok1[1] + "\n";
        emit(res);
        val1 = var;

      }

      if (tok2[0].equals("int_const"))
      {
        val2 = tok2[1];  
      }
      else
      {
        String var = getVar();
        String res = var + " = load i32, i32* " + tok2[1] + "\n";
        emit(res);
        val2 = var;

      }

      String comp_var = getVar();

      String comp_res = comp_var + " = icmp slt i32 " + val1 + ", " + val2 + "\n";

      emit(comp_res);

      emit("\n\n");


      return "boolean_const " + comp_var;  
   }

   //////////////////////////////////////////////////////////////////////////////////////////////////////////

   public Object visit(PrintStatement n, Object argu) throws Exception {
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      String res =  n.f2.accept(this, argu).toString();
      String []tok = null;
      tok = res.split("\\s");
      String r = null;

      String reg = getVar();
      
      if(tok[0].equals("int_var"))
      {
        String var1 = getVar();
        r = var1 + " = load i32, i32* " + tok[1] + "\n";
        emit(r);
        r = "call void (i32) @print_int(i32 " + var1 + ")\n";
        emit(r);
      }
      else if(tok[0].equals("int_const"))
      {
        r = "call void (i32) @print_int(i32 " + tok[1] + ")\n"; 
        emit(r);
      }
      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      emit("\n\n");
      return null;
   }


   ////////////////////////////////////Primary Expression/////////////////////////////////////////////////////

      /**
    * f0 -> <INTEGER_LITERAL>
    */
   public Object visit(IntegerLiteral n, Object argu) throws Exception {
      String val= n.f0.toString();
      String var = getVar();
      String res = var + " = " + "add i32 0," + val + "\n";
      emit(res);
      return "int_const " + var;

   }

   public Object visit(TrueLiteral n, Object argu) throws Exception {
      n.f0.accept(this, argu);
      String var = getVar();
      String res = var + " = " + "and i1 1, 1\n";
      emit(res);
      return "boolean_const " + var;
      
   }

   public Object visit(FalseLiteral n, Object argu) throws Exception {
      n.f0.accept(this, argu);
      String var = getVar();
      String res = var + " = " + "and i1 0, 0\n";
      emit(res);
      return "boolean_const " + var;
   }

   

   public Object visit(NotExpression n, Object argu) throws Exception {

      n.f0.accept(this, argu);

      String val = n.f1.accept(this, argu).toString();

      String []tok1 =  null;
      tok1 = val.split("\\s");

      String var1 = null;
      String res  = null;

      if(tok1[0].equals("boolean_var"))
      {

        var1 = getVar();
        res = var1 + " = load i1, i1* " + tok1[1] + "\n";
        emit(res);

      }
      else if (tok1[0].equals("boolean_const"))
      {

        var1 = tok1[1];

      }

      String var2 = getVar();
      res = var2 + " = sub i1 " + "1, " + var1 + "\n";
      emit(res);

      return "boolean_const " + var2;
   }

   public Object visit(BracketExpression n, Object argu) throws Exception {
      n.f0.accept(this, argu);
      String ret = n.f1.accept(this, argu).toString();
      n.f2.accept(this, argu);
      return ret;
   }

   ///////////////////////////////////////////////////////////////////////////////////////////////

   public Object visit(ArrayAllocationExpression n, Object argu) throws Exception {

      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);

      String val = n.f3.accept(this, argu).toString();

      n.f4.accept(this, argu);

      String []tok1 =  null;
      tok1 = val.split("\\s");

      String var1 = null;
      String res  = null;

      if(tok1[0].equals("int_var"))
      {

        var1 = getVar();
        res = var1 + " = load i32, i32* " + tok1[1] + "\n";
        emit(res);

      }
      else if (tok1[0].equals("int_const"))
      {

        var1 = tok1[1];

      }

      String var5 = getVar();
      res = var5 + " = icmp slt i32 " + "0, " + var1 + "\n";
      emit(res);
      String lb1 = getLabel();
      String lb2 = getLabel();
      String lb3 = getLabel();
      res = "br i1 " + var5 + ", label " + "%" + lb1 + ", label %" + lb2 + "\n"; 
      emit(res);

      emit(lb2 + ":" + "\n");
      emit("call void @throw_oob()\n");
      emit("br label %" + lb3 + "\n");

      emit(lb1 + ":" + "\n");
      String var2 = getVar();
      res = var2 + " = add i32 " + var1 +  ", 1\n";
      emit(res);
      String var3 = getVar();
      res = var3 + " = call i8* @calloc(i32 4, i32 " + var2 + ")\n";  
      emit(res);
      String var4 = getVar();
      res = var4 + " = bitcast i8* " + var3 + " to i32*\n";
      emit(res);
      res = "store i32 " + var1 + ", i32* " + var4 + "\n";
      emit(res);

      emit("br label %" + lb3 + "\n");
      emit(lb3 + ":" + "\n");

      return "int[]_var " + var4;
   }

   public Object visit(ArrayLookup n, Object argu) throws Exception {

      String s = argu.toString();

      String[] str = s.split("\\s");

      String new_argu = null;

      if (str.length == 3)
      {
        new_argu = str[0] + " " + str[1];

        argu = new_argu;
      }

      String id = n.f0.accept(this, argu).toString();
      n.f1.accept(this, argu);
      String val = n.f2.accept(this, argu).toString();
      n.f3.accept(this, argu);


      String []tok1 =  null;
      tok1 = val.split("\\s");

      String []tok2 =  null;
      tok2 = id.split("\\s");

      String var1 = null;
      String res  = null;

      if(tok1[0].equals("int_var"))
      {

        var1 = getVar();
        res = var1 + " = load i32, i32* " + tok1[1] + "\n";
        emit(res);

      }
      else if (tok1[0].equals("int_const"))
      {

        var1 = tok1[1];

      }

      String var2 = getVar();
      res = var2 + " = load i32*, i32** " + tok2[1] + "\n";
      emit(res);
      String var3 = getVar();
      res = var3 + " = load i32, i32* " + var2 + "\n";
      emit(res);
      String var4 = getVar();
      res = var4 + " = icmp ult i32 " + var1 + ", " + var3 + "\n";
      emit(res);
      String lb1 = getLabel();
      String lb2 = getLabel();
      String lb3 = getLabel();
      res = "br i1 " + var4 + ", label " + "%" + lb1 + ", label %" + lb2 + "\n"; 
      emit(res);

      emit(lb2 + ":" + "\n");
      emit("call void @throw_oob()\n");
      emit("br label %" + lb3 + "\n");
      emit(lb1 + ":" + "\n");
      String var5 = getVar();
      res = var5 + " = add i32 1, " + var1 + "\n";
      emit(res);
      String var6 = getVar();
      res = var6 + " = getelementptr i32, i32* " + var2 + ", i32 " + var5 + "\n";
      emit(res);
      String var7 = getVar();
      res = var7 + " = load i32, i32* " + var6 + "\n";
      emit(res);

      res = "br label %" + lb3 + "\n";
      emit(res);
      emit("\n\n" + lb3 + ":" + "\n"); 

      return "int_const " + var7;
    
   }

  
   public Object visit(ArrayAssignmentStatement n, Object argu) throws Exception {
  
      String id = n.f0.accept(this, argu).toString();
      n.f1.accept(this, argu);
      String val = n.f2.accept(this, argu).toString();
      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      String val2 = n.f5.accept(this, argu).toString();
      n.f6.accept(this, argu);


      String []tok1 =  null;
      tok1 = val.split("\\s");

      String []tok2 =  null;
      tok2 = id.split("\\s");

      String []tok3 =  null;
      tok3 = val2.split("\\s");

      String var1 = null;
      String var10 = null;
      String res  = null;

      if(tok1[0].equals("int_var"))
      {

        var1 = getVar();
        res = var1 + " = load i32, i32* " + tok1[1] + "\n";
        emit(res);

      }
      else if (tok1[0].equals("int_const"))
      {

        var1 = tok1[1];

      }

      if(tok3[0].equals("int_var"))
      {

        var10 = getVar();
        res = var10 + " = load i32, i32* " + tok3[1] + "\n";
        emit(res);

      }
      else if (tok3[0].equals("int_const"))
      {

        var10 = tok3[1];

      }

      String var2 = getVar();
      res = var2 + " = load i32*, i32** " + tok2[1] + "\n";
      emit(res);
      String var3 = getVar();
      res = var3 + " = load i32, i32* " + var2 + "\n";
      emit(res);
      String var4 = getVar();
      res = var4 + " = icmp ult i32 " + var1 + ", " + var3 + "\n";
      emit(res);
      String lb1 = getLabel();
      String lb2 = getLabel();
      String lb3 = getLabel();
      res = "br i1 " + var4 + ", label " + "%" + lb1 + ", label %" + lb2 + "\n"; 
      emit(res);

      emit(lb2 + ":" + "\n");
      emit("call void @throw_oob()\n");
      emit("br label %" + lb3 + "\n");

      emit(lb1 + ":" + "\n");
      String var5 = getVar();
      res = var5 + " = add i32 1, " + var1 + "\n";
      emit(res);
      String var6 = getVar();
      res = var6 + " = getelementptr i32, i32* " + var2 + ", i32 " + var5 + "\n";
      emit(res);
      String var7 = getVar();
      res = "store i32 " + var10 + ", i32* " + var6 + "\n";
      emit(res);
      emit("br label %" + lb3 + "\n");
      emit(lb3 + ":" + "\n");

      return null;


   }

   public Object visit(ArrayLength n, Object argu) throws Exception {
      
      String s = argu.toString();

      String[] str = s.split("\\s");

      String new_argu = null;

      if (str.length == 3)
      {
        new_argu = str[0] + " " + str[1];

        argu = new_argu;
      }

      String id = n.f0.accept(this, argu).toString();


      n.f1.accept(this, argu);
      n.f2.accept(this, argu);

      String []tok2 =  null;
      tok2 = id.split("\\s");

      String res  = null;

      String var2 = getVar();
      res = var2 + " = load i32*, i32** " + tok2[1] + "\n";
      emit(res);
      String var3 = getVar();
      res = var3 + " = load i32, i32* " + var2 + "\n";
      emit(res);

      return "int_const " + var3;

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

      String id = n.f1.accept(this, argu).toString();
      String []tok = null;
      tok = id.split("\\s");

      classInfo class_node = classes.get(tok[1]);

      int meth_number = class_node.all_func;

      int size = class_size(tok[1]);

      String var1 = getVar();
      String res = null;

      res = var1 + " = call i8* @calloc(i32 1, i32 " + size + ")\n";
      emit(res);
      String var2 = getVar();
      res = var2 + " = bitcast i8* " + var1 + " to i8***\n";
      emit(res);
      String var3 = getVar();
      res = var3 + " = getelementptr [" + meth_number + " x i8*], [" + meth_number + " x i8*]* @." + tok[1] + "_vtable, i32 0, i32 0\n";
      emit(res);
      String var4 = getVar();
      res = "store i8** " + var3 + ", i8*** " + var2 + "\n\n";
      emit(res); 

      n.f2.accept(this, argu);
      n.f3.accept(this, argu);


      return "obj " + var1 + " " + tok[1];
   }


   /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> ( VarDeclaration() )*
    * f4 -> ( MethodDeclaration() )*
    * f5 -> "}"
    */
   public Object visit(ClassDeclaration n, Object argu) throws Exception {
    
      n.f0.accept(this, argu);

      String class_name = n.f1.accept(this, null).toString();

      n.f2.accept(this, argu);

      n.f3.accept(this, null);

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
   public Object visit(ClassExtendsDeclaration n, Object argu) throws Exception {

      n.f0.accept(this, argu);

      String class_name = n.f1.accept(this, null).toString();

      n.f2.accept(this, argu);
      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      n.f5.accept(this, null);
      n.f6.accept(this, class_name);
      n.f7.accept(this, argu);

      return null;
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
    
      n.f0.accept(this, argu);

      String t = null;
      String []tok = null;

      String typ = n.f1.accept(this, null).toString();
      tok = typ.split("\\s");

      if(tok.length == 2)
        t = "i8*";
      else if (tok[0].equals("int"))
        t = "i32";
      else if (tok[0].equals("int[]"))
        t = "i32*";
      else if (tok[0].equals("boolean"))
        t = "i1";



      String name = n.f2.accept(this, null).toString();
      tok = null;
      tok = name.split("\\s");

      String []tok2 = null;
      String arg = argu.toString();
      tok2 = argu.toString().split("\\s");

      String method = tok2[1] + "." + tok[1];

      emit("define " + t + " @" + method + "(" );

      String arguments = "i8* %_this";

      HashMap<String, methodInfo> methods = table.get_method_map();

      methodInfo method_node = methods.get(method);

      for(int i = 0; i < method_node.arguments_num; i++)
      {
        arguments = arguments +  ", ";
        if(method_node.arguments_type[i].equals("int"))
          arguments = arguments + "i32 ";
        else if(method_node.arguments_type[i].equals("int[]"))
          arguments = arguments + "i32* ";
        else if(method_node.arguments_type[i].equals("boolean"))
          arguments = arguments + "i1 ";
        else
          arguments = arguments + "i8* ";

        arguments = arguments + "%."+method_node.arguments[i];
      }

      emit(arguments + ")  {\n");

      emit("\n");

      n.f3.accept(this, argu);
      n.f4.accept(this, null);
      n.f5.accept(this, argu);
      n.f6.accept(this, argu);

      for(int i = 0; i < method_node.arguments_num; i++)
      {
        
      String typp =  method_node.arguments_type[i];
      String id =  method_node.arguments[i];
      String res = null;

      if(typp.equals("int"))
        {
          res = "%_" + id + " =  alloca i32\n" ;
          emit(res);
          res = "store i32 %." + id + ", i32* " + "%_" + id + "\n";
          emit(res); 
        }
        else if (typp.equals("boolean"))
        {
          res = "%_" + id + " = alloca i1\n" ;
          emit(res);
          res = "store i1 %." + id + ", i1* " + "%_" + id + "\n";
          emit(res);
        }
        ///////////// Prosoxh edwwww //////////////////////////
        else if (typp.equals("int[]"))
        {
          res = "%_" + id + " = alloca i32*\n" ;
          emit(res);
          res = "store i32* %." + id + ", i32** " + "%_" + id + "\n";
          emit(res);
        }
        else
        {
          res = "%_" + id + " = alloca i8*\n" ;
          emit(res);
          res = "store i8* %." + id + ", i8** " + "%_" + id + "\n";
          emit(res);
        }
      }

      emit("\n\n");


      n.f7.accept(this, arg + "." + tok[1]);
      n.f8.accept(this, arg + "." + tok[1]);

      n.f9.accept(this, argu);

      String expr = n.f10.accept(this,arg + "." + tok[1] + " not2").toString();
      tok = expr.split("\\s");

      String ret_expr = null;
      String val = null;

      if (tok[0].equals("int_const"))
      {
        val = tok[1];
        ret_expr = "i32";
      }
      else if (tok[0].equals("int_var"))
      {
        String var = getVar();
        String res = var + " = load i32, i32* " + tok[1] + "\n";
        emit(res);
        val = var;
        ret_expr = "i32";

      }
      else if ((tok[0].equals("boolean_const")))
      {
        val = tok[1];
        ret_expr = "i1";
      }
      else if ((tok[0].equals("boolean_var"))) 
      {
        String var = getVar();
        String res = var + " = load i1, i1* " + tok[1] + "\n";
        emit(res);
        val = var;
        ret_expr = "i1";
      }
      else if ((tok[0].equals("int[]_var")))
      {
        val = tok[1];
        ret_expr = "i32*";
      }
      else if ((tok[0].equals("obj")))
      {
        val = tok[1];
        ret_expr = "i8*";

      }
      
      emit("ret " + ret_expr + " " + val + "\n}");



      n.f11.accept(this, argu);
      n.f12.accept(this, argu);

      return null;

   }

   public Object visit(ThisExpression n, Object argu) throws Exception {
      n.f0.accept(this, argu);

      String []tok =  null;
      tok = argu.toString().split("\\s");

      String []tokk =  null;
      tokk = tok[1].split("\\.");

      String res = null;

      return "obj " + "%_this" + " " + tokk[0] + " err";

   }


   ////////////////////////////////// Message Sent /////////////////////////////////////////////////////



   /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( ExpressionList() )?
    * f5 -> ")"
    */

   public Object visit(MessageSend n, Object argu) throws Exception {

      String []tok_not2 =  null;
      tok_not2 = argu.toString().split("\\s");

      String object;

      if (tok_not2.length != 3)
      object = n.f0.accept(this, argu + " not2").toString();
      else
      object = n.f0.accept(this, argu).toString(); 

      String []tok =  null;
      tok = object.toString().split("\\s");

      n.f1.accept(this, argu);

      String method = n.f2.accept(this, argu).toString();

      String []tokk =  null;
      tokk = method.split("\\s");

      String []class_type = new String[1];
      class_type[0] = null;

      int offset = find_method_offset(tok[2],tokk[1]);


      String var1 = getVar();
      String res = null;
      res = var1 + " = bitcast i8* " + tok[1] + " to i8***\n";
      emit(res);
      String var2 = getVar();
      res = var2 + " = load i8**, i8*** " + var1 + "\n";
      emit(res);
      String var3 = getVar();
      res = var3 + " = getelementptr i8*, i8** " + var2 + ", i32 " + offset + "\n";
      emit(res);
      String var4 = getVar();
      res = var4 + " = load i8*, i8** " + var3 + "\n";
      emit(res);


      String arg_t = get_method_arguments(tok[2],tokk[1],class_type);

      String []tokkk =  null;
      tokkk = arg_t.split("\\,");

      String var5 = getVar();
      res = var5 + " = bitcast i8* " + var4 +  " to ";
      emit(res);
      emit(tokkk[0] + "(i8*");
      if(tokkk.length!=1)
      {
        int i;
        for(i = 1; i<tokkk.length; i++)
        {
          emit(",");
          emit(tokkk[i]);
        }
      }
      emit(")*\n");

      n.f3.accept(this, argu);


      Object expr_list = null;

      if (tok_not2.length != 3)
      expr_list = n.f4.accept(this, argu + " not2");
      else
      expr_list = n.f4.accept(this, argu);

      String []tokkkk =  null;
      if(expr_list!=null)
      {
      tokkkk = expr_list.toString().split("\\s");
      }

      String var6 = getVar();

      emit(var6 + " = call " + tokkk[0] + " " + var5 + "(i8* " + tok[1]);

      if (tokkkk!=null)
      {
        int i;
        for(i = 0; i<tokkkk.length; i++)
        {
          emit(",");
          emit(tokkk[i+1] + " " + tokkkk[i]);
        }
      }

      emit(")\n");

      n.f5.accept(this, argu);

      String tt = null;
      if(tokkk[0].equals("i32"))
        tt = "int_const";
      else if(tokkk[0].equals("i1"))
        tt = "boolean_const";
      else if(tokkk[0].equals("i32*"))
        tt = "int[]_var";
      else
        return "obj" + " " + var6 + " " + class_type[0];

      return tt + " " + var6;
      
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
        String []tok =  null;
        tok = obj1.toString().split("\\s");

        String var = getVar();
        if(tok[0].equals("int_var"))
        {
          res = var + " = load i32, i32* " + tok[1] + "\n";
          emit(res);
          tok[1] = var;
        }
        else if (tok[0].equals("boolean_var"))
        {
          res = var + " = load i1, i1* " + tok[1] + "\n";
          emit(res);
          tok[1] = var;
        }

        res = tok[1];
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

      String expr = n.f1.accept(this, argu).toString();

      String []tok =  null;
      tok = expr.split("\\s");

      String var = getVar();
      String res = null;
      if(tok[0].equals("int_var"))
      {
        res = var + " = load i32, i32* " + tok[1] + "\n";
        emit(res);
        tok[1] = var;
      }
      else if (tok[0].equals("boolean_var"))
      {
        res = var + " = load i1, i1* " + tok[1] + "\n";
        emit(res);
        tok[1] = var;

      }

      return tok[1];

   }

   /////////////////////////////////////////////////////////////////////////////////////////////////////////////



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

      String expr = n.f2.accept(this, argu).toString();

      String []tok =  null;
      tok = expr.split("\\s");

      String val = null;

      if (tok[0].equals("boolean_const"))
      {
        val = tok[1];  
      }
      else
      {
        String var = getVar();
        String res = var + " = load i1, i1* " + tok[1] + "\n";
        emit(res);
        val = var;

      }

      String res = null;

      String lb0 = getLabel();
      String lb1 = getLabel();
      String lb2 = getLabel();
      res = "br i1 " + val + ", label %" + lb0 + ", label %" + lb1 + "\n"; 
      emit(res);
      emit(lb0 + ":\n\n");



      n.f3.accept(this, argu);
      n.f4.accept(this, argu);

      emit("br label %" + lb2 + "\n\n");

      emit(lb1 + ":\n\n");

      n.f5.accept(this, argu);

      n.f6.accept(this, argu);

      emit("br label %" + lb2 + "\n\n");

      emit(lb2 + ":\n\n");

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

      String lb0 = getLabel();
      String lb1 = getLabel();
      String lb2 = getLabel();

      String res = null;

      emit("br label %" + lb0 + "\n\n");

      emit(lb0 + ":\n\n");

      String expr = n.f2.accept(this, argu).toString();

      String []tok =  null;
      tok = expr.split("\\s");

      String val = null;

      if (tok[0].equals("boolean_const"))
      {
        val = tok[1];  
      }
      else
      {
        String var = getVar();
        res = null;
        res = var + " = load i1, i1* " + tok[1] + "\n";
        emit(res);
        val = var;

      }
      

      res = "br i1 " + val + ", label %" + lb1 + ", label %" + lb2 + "\n"; 
      emit(res);

      emit(lb1 + ":\n\n");

      n.f3.accept(this, argu);

      n.f4.accept(this, argu);

      emit("br label %" + lb0 + "\n\n");

      emit(lb2 + ":\n\n");

      return null;
   }





  
}