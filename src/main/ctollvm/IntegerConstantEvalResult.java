package ctollvm;
public class IntegerConstantEvalResult extends EvalResult {
  int value = 0;

  public IntegerConstantEvalResult(int value) {
    this.value = value;
    this.type = TypeSystem.getInstance().getType("int");
  }

  public String getRepresentation() {
    return Integer.toString(value);
  }

  public boolean isLVal() {
    return false;
  }
}
