package ctollvm;
public class PointerType implements Type {
  Type pointerTo;

  public PointerType(Type pointerTo) {
    this.pointerTo = pointerTo;
  }

  public String getRepresentation() {
    return pointerTo.getRepresentation() + "*";
  }

  public String getCrepr() {
    return pointerTo.getCrepr() + "*";
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
    return 8;
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
}
