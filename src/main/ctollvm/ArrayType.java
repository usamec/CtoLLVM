package ctollvm;
public class ArrayType implements Type, PointingType {
  Type pointerTo;
  int count;

  public Type getPointerTo() {
    return pointerTo;
  }

  public ArrayType(Type pointerTo, int count) {
    this.pointerTo = pointerTo;
    this.count = count;
  }

  public String getRepresentation() {
    return String.format("[%d x %s]", count, pointerTo.getRepresentation());
  }

  public String getCrepr() {
    return String.format("%s[%d]", pointerTo.getCrepr(), count);
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
    return count * pointerTo.sizeof();
  }

  public boolean isPointer() {
    return true;
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
    return true;
  }

  public boolean isTypedef() {
    return false;
  }

  public boolean isIncomplete() {
    return false;
  }
}
