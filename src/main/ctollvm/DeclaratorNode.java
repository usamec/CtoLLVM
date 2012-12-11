package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;
import java.lang.StringBuffer;

public class DeclaratorNode implements PNode {
  private String type;
  private String name;
  private Scope scope;
  private int pointerDepth = 0;
  private FunctionDeclarationNode functionDeclaration = null;

  public DeclaratorNode(String type, String name, Scope scope) {
    this.type = type;
    this.name = name;
    this.scope = scope;
  }

  public DeclaratorNode(Scope scope) {
    this.scope = scope;
    this.pointerDepth = 0;
    this.type = "";
    this.name = "";
  }

  public void setFunctionDeclaration(FunctionDeclarationNode fd) {
    functionDeclaration = fd;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void incPointerDepth() {
    this.pointerDepth++;
  }

  @Override
  public EvalResult produceOutput(PrintStream out) throws Exception {
    if (functionDeclaration != null) {
      functionDeclaration.produceOutput(type, pointerDepth, out);
      return new EvalResult();
    } else {
      if (scope.isGlobal()) {
        throw new Exception("Global variables not allowed");
      }
      if (scope.hasInCurrentScope(name)) {
        throw new Exception(String.format("Variable %s already declared", name));
      }

      TypeSystem typeSystem = TypeSystem.getInstance();
      if (!typeSystem.isValidType(type)) {
        throw new Exception(String.format("Invalid type %s", type));
      }

      
      Type t = typeSystem.getType(type, pointerDepth);

      if (t.isVoid()) {
        throw new Exception(String.format("Variable cannot have void type"));
      }

      Scope.Variable v = scope.addVariable(name, t);

      out.println(String.format("%s = alloca %s", v.name, v.type.getRepresentation()));
      return new EvalResult();
    }
  }
}
