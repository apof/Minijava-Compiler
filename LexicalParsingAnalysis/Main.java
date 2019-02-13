import java.util.*;

public class Main {
public static String reta(){
return "a";
}
public static String fun(String a){
return "yesss";
}
public static String test1(String x,String y,String z){
return (x.equals("What"))?(y.equals("What"))?fun((z.equals("ga"))?(z.equals("ga"))?"ga":"no":"no"):"no":(z.equals("ga"))?"ga":"no";
}

public static void main(String[] args) {

System.out.println(test1("What",("a".equals(reta()))?("a".equals(reta()))?"What":reta():"a","g" + reta()));
}
}

