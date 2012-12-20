package ctollvm;
public class IntegerConstantEvalResult extends EvalResult {
  long value = 0;

  public IntegerConstantEvalResult(long value, Type type) {
    this.value = value;
    this.type = type;
  }

  public String getRepresentation() {
    return Long.toString(value);
  }

  public boolean isLVal() {
    return false;
  }

  public boolean isConstant() {
    return true;
  }
}
