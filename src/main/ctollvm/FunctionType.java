package ctollvm;
import java.util.*;

public class FunctionType implements Type {
  public Type returnValue;
  public List<Type> arguments;

  public FunctionType(Type returnValue, List<Type> arguments) {
    this.returnValue = returnValue;
    this.arguments = arguments;
  }

  public String getRepresentation() {
    return "";
  }

  static public String buildCrepr(Type returnValue, List<Type> arguments) {
    StringBuffer buf = new StringBuffer(returnValue.getCrepr());
    buf.append('(');
    if (arguments != null) {
      boolean first = false;
      for (Type t : arguments) {
        if (!first) {
          buf.append(',');
        }
        first = false;
        buf.append(t.getCrepr());
      }
    }
    buf.append(')');
    return buf.toString();
  }

  public String getCrepr() {
    return buildCrepr(returnValue, arguments);
  }

  public boolean isIntegral() {
    return false;
  }

  public boolean isDouble() {
    return false;
  }
  
  public boolean isSigned() {
    return false;
  }
  
  public int sizeof() {
    return 0;
  }

  public boolean isPointer() {
    return false;
  }

  public boolean isVoid() {
    return false;
  }

  public boolean isFunction() {
    return true;
  }

  public boolean isBool() {
    return false;
  }
}