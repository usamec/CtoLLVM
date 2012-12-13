package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;

public class UnaryMinusNode implements PNode {
  private PNode child;

  public UnaryMinusNode(PNode child) {
    this.child = child;
  }
  
  @Override
  public EvalResult produceOutput(PrintStream out) throws Exception {
    EvalResult c = child.produceOutput(out);

    if (!(c.type.isIntegral() || c.type.isDouble())) {
      throw new Exception("Unary- on notarithmetic type");
    }

    if (c.type.isIntegral()) {
      c = TypeSystem.getInstance().promoteInteger(c, out);
    }
    EvalResult res = new EvalResult(c.type);

    if (c.type.isIntegral()) {
      out.printf("%s = sub %s 0, %s\n", res.getRepresentation(),
          c.type.getRepresentation(), c.getRepresentation());
    } else {
      out.printf("%s = fsub %s 0.0, %s\n", res.getRepresentation(),
          c.type.getRepresentation(), c.getRepresentation());
    }
    return res;
  }
}
