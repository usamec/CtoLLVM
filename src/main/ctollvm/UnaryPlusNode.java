package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;

public class UnaryPlusNode implements PNode {
  private PNode child;

  public UnaryPlusNode(PNode child) {
    this.child = child;
  }
  
  @Override
  public EvalResult produceOutput(PrintStream out) throws Exception {
    EvalResult c = child.produceOutput(out);

    if (!(c.type.isIntegral() || c.type.isDouble())) {
      throw new Exception("Unary+ on notarithmetic type");
    }

    if (c.type.isIntegral()) {
      c = TypeSystem.getInstance().promoteInteger(c, out);
    }    
    return c;
  }
}
