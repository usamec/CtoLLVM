package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;

public class EmptyStatementNode implements PNode {
  public EmptyStatementNode() {
  }

  @Override
  public EvalResult produceOutput(PrintStream out) throws Exception {
    return null;
  }
}
