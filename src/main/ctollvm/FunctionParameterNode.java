package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;
import java.lang.StringBuffer;
import java.util.*;

public class FunctionParameterNode {
  private DeclarationProcessor declaration = null;
  private String type;
  private Scope scope;
  private List<String> typeSpecifiers;
  private String typedef;
  private boolean moreStorageSpecifiers;

  public FunctionParameterNode(Scope scope) {
    this.scope = scope;
    this.typeSpecifiers = new ArrayList<String>();
    this.moreStorageSpecifiers = false;
  }

  public void setDeclaration(DeclarationProcessor dec) {
    declaration = dec;
  }

  public void setTypedef(String typedef) {
    this.typedef = typedef;
  }

  public void setStorageSpecifier(String storageSpecifier) {
    this.moreStorageSpecifiers = true;
  }

  public void addTypeSpecifier(String typeSpecifier) {
    typeSpecifiers.add(typeSpecifier);
  }

  public Type getType() throws Exception {
    if (moreStorageSpecifiers) {
      throw new Exception("Storage specifier in function parameter");
    }
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
    }
    // TODO: ak je typ array, tak zmenit na pointer
    return declaration.processTypeAll(t);
  }

  public String getRepresentation() throws Exception {
    return String.format("%s %%%s", getType().getRepresentation(), getName());
  }

  public String getName() {
    return declaration.getName();
  }
}
