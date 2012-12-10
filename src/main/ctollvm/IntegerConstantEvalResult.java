package ctollvm;
public class IntegerConstantEvalResult extends EvalResult {
  int value = 0;

  public IntegerConstantEvalResult(int value, Type type) {
    this.value = value;
    this.type = type;
  }

  public String getRepresentation() {
    return Integer.toString(value);
  }

  public boolean isLVal() {
    return false;
  }
}
