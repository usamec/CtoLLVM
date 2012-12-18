package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;
import java.lang.StringBuffer;
import java.util.*;

public class FunctionDeclarationProcessor extends DeclarationProcessor {
  boolean varArgs = false;

  public FunctionDeclarationProcessor() {
    super();
    varArgs = false;
  }

  public void setVarArgs() {
    varArgs = true;
  }

  public void addParameter(FunctionParameterNode node) {
    functionParameters.add(node);
  }

  public Type processType(Type type) throws Exception {
    if (initializer != null) {
      throw new Exception("Function shouldn't have initializer");
    }
    if (type.isArray()) {
      throw new Exception("Function cannot return an array");
    }
    if (type.isFunction()) {
      throw new Exception("Function cannot return a function");
    }
    List<Type> arguments = new ArrayList<Type>();
    HashSet<String> argument_names = new HashSet<String>();
    for (FunctionParameterNode n : functionParameters) {
      arguments.add(n.getType());
      if (argument_names.contains(n.getName())) {
        throw new Exception("Duplicate argument name");
      }
      argument_names.add(n.getName());
    }
    Type fType = TypeSystem.getInstance().getTypeForFunction(type, arguments, varArgs);
    return fType;
  }

  public void produceOutput(Type type, Scope scope, PrintStream out) throws Exception {
    Type fType = processType(type);
    if (child != null) {
      child.produceOutput(fType, scope, out);
      return;
    }

    if (!scope.isGlobal()) {
      throw new Exception("Function cannot be declared in local scope");
    }

    if (type.isVoid()) {
      out.printf("declare void @%s(", name);
    } else {
      out.printf("declare %s @%s(", type.getRepresentation(), name);
    }
    boolean first = true;
    for (FunctionParameterNode n : functionParameters) {
      if (!first) out.printf(", ");
      first = false;
      out.print(n.getRepresentation());
    }
    if (varArgs) {
      out.printf(", ...");
    }
    out.printf(")\n");
    Scope.Variable v = scope.findInScope(name);
    if (v != null) {
      if (v.type != fType) {
        throw new Exception("Incompatible declaration of function");
      }
    }
    scope.addVariable(name, fType);
  }
}
