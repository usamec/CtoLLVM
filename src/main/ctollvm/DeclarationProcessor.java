package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;
import java.lang.StringBuffer;
import java.util.*;

public class DeclarationProcessor {
  DeclarationProcessor child = null;
  protected String name = "";
  protected List<FunctionParameterNode> functionParameters;

  public DeclarationProcessor() {
    functionParameters = new ArrayList<FunctionParameterNode>();
  }

  public List<FunctionParameterNode> getFunctionParameters() {
    if (child != null)
      return child.getFunctionParameters();
    return functionParameters;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setChild(DeclarationProcessor child) {
    this.child = child;
  }

  public String getName() {
    if (child != null)
      return child.getName();
    return name;
  }

  public Type processType(Type type) throws Exception {
    return type;
  }

  public Type processTypeAll(Type type) throws Exception {
    Type t = processType(type);
    if (child == null)
      return t;
    return child.processTypeAll(t);
  }

  public void produceOutput(Type type, Scope scope, PrintStream out) throws Exception {
    type = processType(type);

    if (child != null) {
      child.produceOutput(type, scope, out);
      return;
    }

    if (scope.isGlobal()) {
      throw new Exception("Global variables not allowed yet");
    }

    if (scope.hasInCurrentScope(name)) {
      throw new Exception(String.format("Variable %s already declared", name));
    }
    if (type.isVoid()) {
      throw new Exception(String.format("Variable cannot have void type"));
    }
    Scope.Variable v = scope.addVariable(name, type);
    out.println(String.format("%s = alloca %s", v.name, v.type.getRepresentation()));
  }
}
