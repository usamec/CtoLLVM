package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;

public class BreakStatementNode implements PNode {
  private Scope scope;

  public BreakStatementNode(Scope scope) {
    this.scope = scope;
  }
  
  @Override
  public EvalResult produceOutput(PrintStream out) throws Exception {
    String label = scope.getBreakLabel();
    if (label == null) {
      throw new Exception("Break not inside cycle or switch");
    }
    String labelafter = IdCounter.GetNewLabel();
    out.printf("br label %%%s\n", label);
    out.printf("%s:\n", labelafter);
    return null;
  }
}
