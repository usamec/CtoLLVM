package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;

public class PostfixMinusNode implements PNode {
  private PNode child;

  public PostfixMinusNode(PNode child) {
    this.child = child;
  }
  
  @Override
  public EvalResult produceOutput(PrintStream out) throws Exception {
    EvalResult c = child.produceOutput(out);

    PNode ass = new AssigmentOperationNode(child, new IntegerConstantNode("1"), "-");
    ass.produceOutput(out);
    return c;
  }
}
