all: compile

compile:
	java -jar ../jtb132di.jar -te minijava.jj
	java -jar ../javacc5.jar minijava-jtb.jj
	javac Main.java
	javac Info.java
	javac classInfo.java
	javac methodInfo.java
	javac SymbolTable.java
	javac MyStorageVisitor.java
	javac LLVM_Visitor.java
	
clean:
	rm -f *.class *~
