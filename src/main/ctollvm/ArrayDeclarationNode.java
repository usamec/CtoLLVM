package ctollvm;
import java.util.*;
import java.io.*;

public class ArrayDeclarationNode {
  List<PNode> sizes;
  String name;
  Scope scope;

  public ArrayDeclarationNode(Scope scope, String name) {
    this.scope = scope;
    this.name = name;
    sizes = new ArrayList<PNode>();
  }

  public void addSize(PNode node) {
    sizes.add(node);
  }

  public void produceOutput(String type, int pointerDepth, PrintStream out) throws Exception {
    if (scope.isGlobal()) {
      throw new Exception("Global variables not allowed yet");
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
      throw new Exception("Array of void is bad, and you should feel bad");
    }

    for (int i = sizes.size() - 1; i >= 0; i--) {
      EvalResult res = sizes.get(i).produceOutput(out);
      if (!res.type.isIntegral()) {
        throw new Exception("Array size should have integer type.");
      }
      if (!res.isConstant()) {
        throw new Exception("Array size should be constant.");
      }
      IntegerConstantEvalResult ee = (IntegerConstantEvalResult) res;
      Type t2 = TypeSystem.getInstance().getArrayType(t, ee.value);
      t = t2;
    }
    
    Scope.Variable v = scope.addVariable(name, t);
    out.println(String.format("%s = alloca %s", v.name, v.type.getRepresentation()));
  }
}
