package ctollvm;
public class PrimitiveType implements Type {
  private String repr;
  private String crepr;
  private boolean integral;
  private boolean doubl;
  private int size;
  private boolean signed;
  private boolean isVoid;
  private boolean isBool;
  
  public PrimitiveType(String repr, boolean integral, boolean doubl, int size,
                       boolean signed, String crepr, boolean isVoid, boolean isBool) {
    this.repr = repr;
    this.integral = integral;
    this.doubl = doubl;
    this.size = size;
    this.signed = signed;
    this.crepr = crepr;
    this.isVoid = isVoid;
    this.isBool = isBool;
  }

  public String getCrepr() {
    return this.crepr;
  }

  public String getRepresentation() {
    return repr;
  }

  public boolean isIntegral() {
    return integral; 
  }

  public boolean isDouble() {
    return doubl; 
  }

  public boolean isSigned() {
    return signed; 
  }

  public int sizeof() {
    return size;
  }

  public boolean isPointer() {
    return false;
  }

  public boolean isVoid() {
    return isVoid;
  }

  public boolean isFunction() {
    return false;
  }

  public boolean isBool() {
    return isBool;
  }
}
