import syntaxtree.*;
import visitor.*;
import java.io.*;

import java.util.HashMap;
import java.util.Map;



class Main {
    public static void main (String [] args){

	FileInputStream fis = null;
	FileInputStream fis2 = null;

	if(args.length == 0) { System.out.println("Please give arguments"); return; }

	for(int i = 0; i<args.length; i++) {


	System.out.println("-----------> " + args[i]);

	try{
	    fis = new FileInputStream(args[i]);
	    MiniJavaParser parser = new MiniJavaParser(fis);
	    MyStorageVisitor visitor = new MyStorageVisitor();
	    Goal root = parser.Goal();

	    root.accept(visitor, null);

	    fis2 = new FileInputStream(args[i]);
	    MiniJavaParser parser2 = new MiniJavaParser(fis2);
	    //System.err.println("Program parsed successfully again.");
	    MyTypeCheckVisitor visitor2 = new MyTypeCheckVisitor(visitor.table);
	    Goal root2 = parser2.Goal();


	    root2.accept(visitor2, null);

	    System.out.println("----------------Display Offsets: -----------------------");

	    visitor.table.Display_class_map();

	    /*System.out.println("----------------Display Methods: -----------------------");

	    visitor.table.Display_method_map();*/



	}
	catch(Exception ex){

	    ex.printStackTrace();
	    //System.out.println("Error");
	}
    }
}
}
