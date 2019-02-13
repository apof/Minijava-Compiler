import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

public class SymbolTable {


	public int error_flag = 0;
    
    private HashMap<String, methodInfo> method_map = new HashMap<String, methodInfo>();
    private HashMap<String, classInfo> class_map = new HashMap<String, classInfo>();

    public HashMap<String, methodInfo> get_method_map() {
         return method_map;
    }

    public HashMap<String, classInfo> get_class_map() {
         return class_map;
    }

    public void Display_class_map(){
      Set set = class_map.entrySet();
      Iterator iterator = set.iterator();
      while(iterator.hasNext()) {
         Map.Entry mentry = (Map.Entry)iterator.next();
         classInfo node = (classInfo)mentry.getValue();

         node.print_info_class();
	
      }
    }

    public void Display_method_map(){
      Set set = method_map.entrySet();
      Iterator iterator = set.iterator();
      while(iterator.hasNext()) {
         Map.Entry mentry = (Map.Entry)iterator.next();
         methodInfo node = (methodInfo)mentry.getValue();
         node.print_info_method();
      }
    }






}


