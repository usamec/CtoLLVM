package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;
import java.lang.StringBuffer;
import java.util.*;

public class TypeNameNode {
  private DeclarationProcessor declaration = null;
  private Scope scope;
  private List<String> typeSpecifiers;
  private String typedef;
  private StructDeclarationNode structDeclaration;

  public TypeNameNode(Scope scope) {
    this.scope = scope;
    this.typeSpecifiers = new ArrayList<String>();
    this.structDeclaration = null;
    this.typedef = "";
  }

  public void setStruct(StructDeclarationNode structDeclaration) {
    this.structDeclaration = structDeclaration;
  }

  public void setDeclaration(DeclarationProcessor dec) {
    declaration = dec;
  }

  public void setTypedef(String typedef) {
    this.typedef = typedef;
  }

  public void addTypeSpecifier(String typeSpecifier) {
    typeSpecifiers.add(typeSpecifier);
  }

  public Type getType() throws Exception {
    TypeSystem typeSystem = TypeSystem.getInstance();
    Type t = null;
    if (typeSpecifiers.size() > 0) {
      if (!typeSystem.isValidType(typeSpecifiers)) {
        throw new Exception(String.format("Invalid type"));
      }
    
      t = typeSystem.getType(typeSpecifiers);
    } else if (!typedef.equals("")) {
      Scope.Variable v = scope.findInScope(typedef);
      if (v == null) {
        throw new Exception("Unknown identifier");
      }
      if (!v.type.isTypedef()) {
        throw new Exception(String.format("%s is not type", typedef));
      }
      TypedefType tt = (TypedefType) v.type;
      t = tt.getTypeTo();
    } else if (structDeclaration != null) {
      t = structDeclaration.processDeclaration(null);
    } 
    t = declaration.processTypeAll(t);
    if (t.isArray()) {
      ArrayType at = (ArrayType) t;
      t = TypeSystem.getInstance().getPointerType(at.getPointerTo());
    }
    return t;
  }
}
