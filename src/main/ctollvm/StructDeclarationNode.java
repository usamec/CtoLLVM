package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;
import java.lang.StringBuffer;
import java.util.*;

public class StructDeclarationNode {
  Scope scope;
  String name;
  List<DeclarationNode> declarations;

  public StructDeclarationNode(Scope scope) {
    this.scope = scope;
    this.name = "";
    this.declarations = new ArrayList<DeclarationNode>();
  }

  public void setName(String name) {
    this.name = name;
  }

  public void addDeclaration(DeclarationNode node) {
    declarations.add(node);
  }

  public Type processDeclaration(PrintStream out) throws Exception {
    if (!scope.isGlobal()) {
      throw new Exception("Structs only allowed in global scope");
    }

    StructType t = new StructType(IdCounter.GetNewStructName());
    if (!name.equals("")) {
      if (scope.hasInCurrentScope(name)) {
        throw new Exception(String.format("Variable %s already declared", name));
      }
      scope.addVariable(t.getCrepr(), t);
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
     
    return null;
  }
}
