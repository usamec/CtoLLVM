package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;

public class DefaultNode implements PNode {
  private Scope scope;

  public DefaultNode(Scope scope) {
    this.scope = scope;
  }
  @Override
  public EvalResult produceOutput(PrintStream out) throws Exception {
    SwitchNode switchNode = scope.getSwitchNode();
    if (switchNode == null) {
      throw new Exception("Default outside switch");
    }
    String label = IdCounter.GetNewLabel();
    out.printf("br label %%%s\n", label);
    out.printf("%s:\n", label);

    switchNode.setDefaultLabel(label);
    return null;
  }
}
