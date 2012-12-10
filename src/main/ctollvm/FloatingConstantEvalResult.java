package ctollvm;
public class FloatingConstantEvalResult extends EvalResult {
  double value = 0;

  public FloatingConstantEvalResult(double value) {
    this.value = value;
    this.type = TypeSystem.getInstance().getType("double");
  }

  public String getRepresentation() {
    return String.format("0x%16x", Double.doubleToRawLongBits(value));
  }

  public boolean isLVal() {
    return false;
  }
}
