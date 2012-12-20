package ctollvm;
import java.io.PrintStream;

public class IntegerConstantNode implements PNode {
  String value;
  String type;
  Boolean unsigned;
  Integer base;

  public IntegerConstantNode(String value) {
    value = value.toLowerCase();
    int ll = value.indexOf("ll");
    int l = value.indexOf('l');
    int u = value.indexOf('u');
    if (u > -1) {
      this.unsigned = true;
    } else {
      this.unsigned = false;
    }
    this.type = "int";
    if (l > -1) this.type = "long";
    if (ll > -1) this.type = "long long";
    String value2;
    if (u+l == -2) {
      value2 = value;
    } else {
      int min = u;
      if (u == -1) min = l;
      if (min > l) min = l;
      if (l == -1) min = u;
      value2 = value.substring(0,min);
    }
    this.value = value2;
    this.base = 10;
    if (value2.length() > 1) {
      if (value2.indexOf('0') == 0) {
        this.base = 8;
        this.value = value2.substring(1);
      }
      if ((value2.indexOf('x') == 1) || (value2.indexOf('X') == 1)) {
        this.base = 16;
        this.value = value2.substring(2);
      }
    }
    if (this.unsigned) this.type = "unsigned "+this.type;
  }

  @Override
  public EvalResult produceOutput(PrintStream out) {
    int id = IdCounter.GetNewId();
    long value2 = Long.parseLong(value, base);
    return new IntegerConstantEvalResult(
        value2, TypeSystem.getInstance().getType(type));
  }
}
