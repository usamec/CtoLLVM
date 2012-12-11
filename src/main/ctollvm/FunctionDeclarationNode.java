package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;
import java.lang.StringBuffer;
import java.util.*;

public class FunctionDeclarationNode {
  protected String name;
  private Scope scope;
  protected List<FunctionParameterNode> parameters;
  boolean varArgs = false;

  public FunctionDeclarationNode(Scope scope) {
    this.scope = scope;
    this.name = "";
    parameters = new ArrayList<FunctionParameterNode>();
    varArgs = false;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setVarArgs() {
    varArgs = true;
  }

  public void addParameter(FunctionParameterNode node) {
    parameters.add(node);
  }

  public List<FunctionParameterNode> getParameters() {
    return parameters;
  }

  public String getName() {
    return name;
  }

  public void produceOutput(String type, int pointerDepth, PrintStream out) throws Exception {
    if (!scope.isGlobal()) {
      throw new Exception("Function cannot be declared in local scope");
    }
    TypeSystem typeSystem = TypeSystem.getInstance();
    if (!typeSystem.isValidType(type)) {
      throw new Exception(String.format("Invalid type %s", type));
    }    
    Type t = typeSystem.getType(type, pointerDepth);
    if (t.isVoid())
      out.print(String.format("declare void @%s(", name));
    else 
      out.print(String.format("declare %s @%s(", t.getRepresentation(), name));
    boolean first = true;
    List<Type> arguments = new ArrayList<Type>();
    HashSet<String> argument_names = new HashSet<String>();
    for (FunctionParameterNode n : parameters) {
      // TODO: check for distinct names
      if (!first) out.print(", ");
      first = false;
      out.print(n.getRepresentation());
      arguments.add(n.getType());
      if (argument_names.contains(n.getName())) {
        throw new Exception("Duplicate argument name");
      }
      argument_names.add(n.getName());
    }
    if (varArgs) {
      out.printf(", ...");
    }
    out.println(")");

    Type fType = typeSystem.getTypeForFunction(t, arguments, varArgs);
    Scope.Variable v = scope.findInScope(name);
    if (v != null) {
      if (v.type != fType) {
        throw new Exception("Incompatible declaration of function");
      }
    }
    scope.addVariable(name, fType);
  }
}
