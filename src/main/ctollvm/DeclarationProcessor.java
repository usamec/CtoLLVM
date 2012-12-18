package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;
import java.lang.StringBuffer;
import java.util.*;

public class DeclarationProcessor {
  DeclarationProcessor child = null;
  protected String name = "";
  protected List<FunctionParameterNode> functionParameters;
  private boolean isDummy = false;
  protected PNode initializer;

  public DeclarationProcessor() {
    functionParameters = new ArrayList<FunctionParameterNode>();
  }

  public void setInitializer(PNode node) {
    initializer = node;
  }

  public List<FunctionParameterNode> getFunctionParameters() {
    if (child != null)
      return child.getFunctionParameters();
    return functionParameters;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isDummy() {
    if (child != null)
      return child.isDummy();
    return isDummy;
  }

  public void setDummyName() {
    isDummy = true;
    name = IdCounter.GetDummyParameterName();
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

/*    if (scope.isGlobal()) {
      throw new Exception("Global variables not allowed yet");
    }*/

    if (scope.hasInCurrentScope(name)) {
      throw new Exception(String.format("Variable %s already declared", name));
    }
    if (type.isVoid()) {
      throw new Exception(String.format("Variable cannot have void type"));
    }
    Scope.Variable v = scope.addVariable(name, type);
    if (scope.isGlobal()) {
      if (initializer != null) {
        EvalResult res = initializer.produceOutput(out);
        if (!res.isConstant()) {
          throw new Exception("Global variable must be initialized by constant");
        }
        if (v.type.isIntegral()) {
          if (res.type.isDouble()) {
            FloatingConstantEvalResult fr = (FloatingConstantEvalResult) res;
            res = new IntegerConstantEvalResult(new Double(fr.value).intValue(), v.type);
          } else {
            IntegerConstantNode ic = new IntegerConstantNode(res.getRepresentation());
            res = ic.produceOutput(out);
            res.type = v.type;
          }
        }
        if (v.type.isDouble()) {
          if (res.type.isDouble()) {
            res.type = v.type;
          } else {
            FloatingConstantNode ic = new FloatingConstantNode(res.getRepresentation());
            res = ic.produceOutput(out);
            res.type = v.type; 
          }
        }
        if (v.type != res.type) {
          throw new Exception("Wrong type in inicialization");
        }
        out.println(String.format("%s = global %s %s", v.name, v.type.getRepresentation(),
            res.getRepresentation()));
      } else {
        out.println(String.format("%s = global %s undef", v.name, v.type.getRepresentation()));
      }
    } else {
      out.println(String.format("%s = alloca %s", v.name, v.type.getRepresentation()));
      if (initializer != null) {
        EvalResult res = initializer.produceOutput(out);
        IdentifierNode idNode = new IdentifierNode(name, scope);
        AssigmentNode.evaluateOperation(idNode.produceOutput(out), res, out);
      }
    }
  }
}
