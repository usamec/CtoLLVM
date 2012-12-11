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
    // TODO: suffixy
    // TODO: 0x, 0 zapisy
    return new IntegerConstantEvalResult(
        new Integer(value), TypeSystem.getInstance().getType("int"));
  }
}
