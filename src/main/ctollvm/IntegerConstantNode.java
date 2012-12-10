package ctollvm;
import java.io.PrintStream;

public class IntegerConstantNode implements PNode {
  String value;

  public IntegerConstantNode(String value) {
    this.value = value;
  }

  @Override
  public EvalResult produceOutput(PrintStream out) {
    int id = IdCounter.GetNewId();
    return new IntegerConstantEvalResult(new Integer(value));
  }
}
