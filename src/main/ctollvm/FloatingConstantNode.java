package ctollvm;
import java.io.PrintStream;

public class FloatingConstantNode implements PNode {
  String value;

  public FloatingConstantNode(String value) {
    this.value = value;
  }

  @Override
  public EvalResult produceOutput(PrintStream out) {
    return new FloatingConstantEvalResult(new Double(value));
  }
}
