package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;
import java.util.*;

public class FunctionCallNode implements PNode {
  private PNode functionName;
  private List<PNode> arguments; 
  private Scope scope;

  public FunctionCallNode(Scope scope) {
    this.scope = scope;
    arguments = new ArrayList<PNode>();
  }

  public void setFunctionName(PNode node) {
    functionName = node;
  }

  public void addArgument(PNode node) {
    arguments.add(node);
  }

  @Override
  public EvalResult produceOutput(PrintStream out) throws Exception {
    EvalResult fn = functionName.produceOutput(out);
    if (!fn.type.isFunction()) {
      throw new Exception("Call on non-function");
    }
    FunctionType ft = (FunctionType) fn.type;

    // TODO: argumenty typu ...

    if (ft.arguments.size() != arguments.size()) {
      throw new Exception("Bad number of arguments");
    }

    EvalResult res;
    List<EvalResult> args = new ArrayList<EvalResult>();
    for (int i = 0; i < arguments.size(); i++) {
      EvalResult ar = arguments.get(i).produceOutput(out);
      if (ar.type != ft.arguments.get(i)) {
        EvalResult ar2 = TypeSystem.getInstance().convertTo(ft.arguments.get(i), ar, out);
        if (ar2 == null) {
          throw new Exception("Incompatible arguments");
        }
        ar = ar2;
      }
      args.add(ar);
    }
    if (ft.returnValue.isVoid()) {
      res = new EvalResult();
      out.printf("call %s @%s(",
          ft.returnValue.getRepresentation(), fn.getIdentifierName());  
    } else {
      res = new EvalResult(ft.returnValue);
      out.printf("%s = call %s @%s(", res.getRepresentation(),
          ft.returnValue.getRepresentation(), fn.getIdentifierName());  
    }
    for (int i = 0; i < arguments.size(); i++) {
      EvalResult ar = args.get(i);
      if (i != 0) out.print(",");
      out.printf("%s %s", ar.type.getRepresentation(), ar.getRepresentation());
    }
    out.printf(")\n");
    return res;
  }
}
