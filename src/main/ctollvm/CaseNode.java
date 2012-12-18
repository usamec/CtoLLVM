package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;

public class CaseNode implements PNode {
  private Scope scope;
  private PNode exp;

  public CaseNode(PNode exp, Scope scope) {
    this.exp = exp;
    this.scope = scope;
  }
  @Override
  public EvalResult produceOutput(PrintStream out) throws Exception {
    SwitchNode switchNode = scope.getSwitchNode();
    if (switchNode == null) {
      throw new Exception("Default outside switch");
    }
    EvalResult res = exp.produceOutput(out);
    if (!res.type.isIntegral()) {
      throw new Exception("case constant should be integer");
    }
    if (!res.isConstant()) {
      throw new Exception("case constant should be constant");
    }
    
    String label = IdCounter.GetNewLabel();
    out.printf("br label %%%s\n", label);
    out.printf("%s:\n", label);

    switchNode.addLabel(res.getRepresentation(), label);
    return null;
  }
}
