package ctollvm;
public class EvalResult {
  protected Type type = null;
  protected String name;
  protected String identifierName;
  protected boolean lVal = false;

  public EvalResult() {
  }

  public EvalResult(Type type) {
    this.type = type;
    name = IdCounter.GetNewTmpVal(); 
  }

  public EvalResult(Type type, String identifierName) {
    this(type);
    this.identifierName = identifierName;
    if (!type.isFunction())
      lVal = true;
  }

  public String getRepresentation() {
    return name;
  }

  public boolean isLVal() {
    return lVal;
  }

  public String getIdentifierName() {
    return identifierName;
  }

  public boolean isReturnStatement() {
    return false;
  }
}
