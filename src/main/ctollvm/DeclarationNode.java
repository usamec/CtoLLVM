package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;
import java.lang.StringBuffer;
import java.util.*;

public class DeclarationNode implements PNode {
  private List<DeclarationProcessor> declarators = null;
  private Scope scope;
  private String storageSpecifier;
  private List<String> typeSpecifiers;
  private String typedef;
  private boolean moreStorageSpecifiers;
  private StructDeclarationNode structDeclaration;
  private EnumDeclarationNode enumDeclaration;

  public DeclarationNode(Scope scope) {
    this.declarators = new ArrayList<DeclarationProcessor>();
    this.scope = scope;
    this.storageSpecifier = "";
    this.typeSpecifiers = new ArrayList<String>();
    this.moreStorageSpecifiers = false;
    this.structDeclaration = null;
    this.typedef = "";
  }

  public void setStruct(StructDeclarationNode structDeclaration) {
    this.structDeclaration = structDeclaration;
  }

  public void setEnum(EnumDeclarationNode enumDeclaration) {
    this.enumDeclaration = enumDeclaration;
  }

  public void addDeclarationProcessor(DeclarationProcessor node) {
    declarators.add(node);
  }

  public void setTypedef(String typedef) {
    this.typedef = typedef;
  }

  public void setStorageSpecifier(String storageSpecifier) {
    if (!this.storageSpecifier.equals("")) {
      moreStorageSpecifiers = true;
    }
    this.storageSpecifier = storageSpecifier;
  }

  public void addTypeSpecifier(String typeSpecifier) {
    typeSpecifiers.add(typeSpecifier);
  }

  public List<DeclaredVariable> getDeclaredVariables(PrintStream out) throws Exception {
    Type t = getDeclarationType(out);
    List<DeclaredVariable> retVal = new ArrayList<DeclaredVariable>();
    for (DeclarationProcessor n : declarators) {
      Type td = n.processTypeAll(t);
      String name = n.getName();
      retVal.add(new DeclaredVariable(td, name));
    }
    return retVal;
  }

  public Type getDeclarationType(PrintStream out) throws Exception {
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
      t = structDeclaration.processDeclaration(out);
    } else if (enumDeclaration != null) {
      t = enumDeclaration.processDeclaration(out);
    }
    return t;  
  }

  @Override
  public EvalResult produceOutput(PrintStream out) throws Exception {
    if (moreStorageSpecifiers) {
      throw new Exception("Declaration can have at most one storage specifier");
    }

    Type t = getDeclarationType(out);

    if (storageSpecifier == "typedef") {
      for (DeclarationProcessor n : declarators) {
        Type td = TypeSystem.getInstance().getTypedefTo(n.processTypeAll(t));
        String name = n.getName();

        if (scope.hasInCurrentScope(name)) {
          throw new Exception(String.format("Variable %s already declared", name));
        }

        Scope.Variable v = scope.addVariable(name, td);
      }
      return null;
    } else {
      for (DeclarationProcessor n : declarators) {
        n.produceOutput(t, scope, out);
      }
    }
    return null;
  }
}
