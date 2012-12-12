package ctollvm;
import java.util.*;

public class FunctionType implements Type {
  public Type returnValue;
  public List<Type> arguments;
  public boolean varArgs;

  public FunctionType(Type returnValue, List<Type> arguments) {
    this.returnValue = returnValue;
    this.arguments = arguments;
    this.varArgs = false;
  }

  public FunctionType(Type returnValue, List<Type> arguments, boolean varArgs) {
    this.returnValue = returnValue;
    this.arguments = arguments;
    this.varArgs = varArgs;
  }

  public String getRepresentation() {
    StringBuffer buf = new StringBuffer(returnValue.getRepresentation());
    buf.append(" (");
    if (arguments != null) {
      boolean first = true;
      for (Type t : arguments) {
        if (!first) {
          buf.append(',');
        }
        first = false;
        buf.append(t.getRepresentation());
      }
    }
    if (varArgs) {
      buf.append(", ...");
    }
    buf.append(')');
    return buf.toString();
  }

  static public String buildCrepr(Type returnValue, List<Type> arguments) {
    return buildCrepr(returnValue, arguments, false);
  }

  static public String buildCrepr(Type returnValue, List<Type> arguments, boolean varArgs) {
    StringBuffer buf = new StringBuffer(returnValue.getCrepr());
    buf.append('(');
    if (arguments != null) {
      boolean first = true;
      for (Type t : arguments) {
        if (!first) {
          buf.append(',');
        }
        first = false;
        buf.append(t.getCrepr());
      }
    }
    if (varArgs) {
      buf.append(", ...");
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
  public boolean isArray() {
    return false;
  }

  public boolean isTypedef() {
    return false;
  }

}
