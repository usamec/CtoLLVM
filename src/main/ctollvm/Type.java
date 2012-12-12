package ctollvm;
public interface Type {
  public String getRepresentation();

  public String getCrepr();
  public boolean isIntegral();
  public boolean isDouble();
  public boolean isSigned();
  public int sizeof();
  public boolean isPointer();
  public boolean isVoid();
  public boolean isFunction();
  public boolean isBool();
  public boolean isArray(); 
  public boolean isTypedef(); 
  public boolean isIncomplete();
}
