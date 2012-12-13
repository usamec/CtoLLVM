package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;

public class UnaryNegNode implements PNode {
  private PNode child;

  public UnaryNegNode(PNode child) {
    this.child = child;
  }
  
  @Override
  public EvalResult produceOutput(PrintStream out) throws Exception {
    EvalResult c = child.produceOutput(out);

    if (!(c.type.isIntegral())) {
      throw new Exception("Unary~ on noninteger type");
    }

    c = TypeSystem.getInstance().promoteInteger(c, out);

    EvalResult res = new EvalResult(c.type);
    out.printf("%s = xor %s %s, -1\n", res.getRepresentation(), res.type.getRepresentation(),
        c.getRepresentation());
    return res;
  }
}
