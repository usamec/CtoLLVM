package ctollvm;
import java.io.PrintStream;

public class CharacterConstantNode implements PNode {
  String value;

  public CharacterConstantNode(String value) {
    this.value = value;
  }

  @Override
  public EvalResult produceOutput(PrintStream out) {
    byte bytes[] = value.getBytes();
    return new IntegerConstantEvalResult(
        bytes[0], TypeSystem.getInstance().getType("char"));
  }
}
