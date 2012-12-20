package ctollvm;
public class FloatingConstantEvalResult extends EvalResult {
  double value = 0;

  public FloatingConstantEvalResult(double value, Type type) {
    this.value = value;
    this.type = type;
  }

  public String getRepresentation() {
    return String.format("0x%16x", Double.doubleToRawLongBits(value));
  }

  public boolean isLVal() {
    return false;
  }

  public boolean isConstant() {
    return true;
  }
}
