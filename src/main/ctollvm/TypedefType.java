package ctollvm;
public class TypedefType implements Type {
  Type typeTo;

  public Type getTypeTo() {
    return typeTo;
  }

  public TypedefType(Type typeTo) {
    this.typeTo = typeTo;
  }

  public String getRepresentation() {
    return "";
  }

  public String getCrepr() {
    return "typedef " + typeTo.getCrepr();
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
    return false;
  }

  public boolean isBool() {
    return false;
  }

  public boolean isArray() {
    return false;
  }

  public boolean isTypedef() {
    return true;
  }
}
