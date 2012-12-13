package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;

public class AssigmentOperationNode implements PNode {
  private PNode lhs;
  private PNode rhs;
  private String operation;

  public AssigmentOperationNode(PNode lhs, PNode rhs, String operation) {
    this.lhs = lhs;
    this.rhs = rhs;
    this.operation = operation;
  }

  @Override
  public EvalResult produceOutput(PrintStream out) throws Exception {
    EvalResult l = lhs.produceOutput(out);
    EvalResult r = rhs.produceOutput(out);
    if (operation.equals("+"))
      return AssigmentNode.evaluateOperation(l, AddNode.evaluateOperation(l, r, out), out);
    if (operation.equals("*"))
      return AssigmentNode.evaluateOperation(l, MulNode.evaluateOperation(l, r, out), out);
    if (operation.equals("/"))
      return AssigmentNode.evaluateOperation(l, DivNode.evaluateOperation(l, r, out), out);
    if (operation.equals("%"))
      return AssigmentNode.evaluateOperation(l, RemNode.evaluateOperation(l, r, out), out);
    return null;
  }
}
