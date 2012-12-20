package ctollvm;
import java.io.PrintStream;

public class FloatingConstantNode implements PNode {
  String value;
  String type;

  public FloatingConstantNode(String value) {
    value = value.toLowerCase();
    int l = value.indexOf('l');
    int f = value.indexOf('f');
    String value2;
    if (f+l == -2) {
      value2 = value;
    } else {
      int min = f;
      if (f == -1) min = l;
      if (min > l) min = l;
      if (l == -1) min = f;
      value2 = value.substring(0,min);
    }
    this.value = value2;
    type = "double";
    if (l > -1) type = "long double"; 
    if (f > -1) type = "float";
  }

  @Override
  public EvalResult produceOutput(PrintStream out) {
    return new FloatingConstantEvalResult(new Double(value),TypeSystem.getInstance().getType(type));
  }
}
