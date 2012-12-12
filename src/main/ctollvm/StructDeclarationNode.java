package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;
import java.lang.StringBuffer;
import java.util.*;

public class StructDeclarationNode {
  Scope scope;
  String name;
  List<DeclarationNode> declarations;
  boolean use = false;

  public StructDeclarationNode(Scope scope) {
    this.scope = scope;
    this.name = "";
    this.declarations = new ArrayList<DeclarationNode>();
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setUse() {
    use = true;
  }

  public void addDeclaration(DeclarationNode node) {
    declarations.add(node);
  }

  public Type processUse() {
    String structName = "struct "+name;
    if (scope.hasInScope(structName)) {
      return scope.findInScope(structName).type;
    } else {
      Type t = new StructType(IdCounter.GetNewStructName());
      scope.addVariable(structName, t);
      return t;
    }
  }

  public Type processDeclaration(PrintStream out) throws Exception {
    if (use) {
      return processUse();
    }
    if (!scope.isGlobal()) {
      throw new Exception("Structs only allowed in global scope");
    }

    StructType t;
    if (name.equals("")) {
      t = new StructType(IdCounter.GetNewStructName());
    } else {
      String structName = "struct "+name;
      if (scope.hasInCurrentScope(structName)) {
        t = (StructType) scope.findInCurrentScope(structName).type;
        if (!t.isIncomplete())
          throw new Exception(String.format("Variable %s already declared", name));
      } else {
        t = new StructType(IdCounter.GetNewStructName());
        scope.addVariable(structName, t);
      }
    }

    List<DeclaredVariable> declaredVariables = 
        new ArrayList<DeclaredVariable>();

    for (DeclarationNode n : declarations) {
      declaredVariables.addAll(n.getDeclaredVariables(out));
    }
    
    HashSet<String> member_names = new HashSet<String>();
    for (DeclaredVariable d : declaredVariables) {
      if (member_names.contains(d.name)) {
        throw new Exception("Duplicate member in struct");
      }
      if (d.type.isIncomplete() || d.type.isVoid()) {
        throw new Exception("Incomplete type in struct");
      }
      
      member_names.add(d.name);
    }

    t.setDeclaredVariables(declaredVariables);
    out.printf("%s = type %s\n", t.getRepresentation(),
               t.getTypeRepresentation());
     
    return t;
  }
}
