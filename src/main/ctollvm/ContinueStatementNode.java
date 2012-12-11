package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;

public class ContinueStatementNode implements PNode {
  private Scope scope;

  public ContinueStatementNode(Scope scope) {
    this.scope = scope;
  }
  
  @Override
  public EvalResult produceOutput(PrintStream out) throws Exception {
    String label = scope.getContinueLabel();
    if (label == null) {
      throw new Exception("Continue not inside cycle");
    }
    String labelafter = IdCounter.GetNewLabel();
    out.printf("br label %%%s\n", label);
    out.printf("%s:\n", labelafter);
    return null;
  }
}
