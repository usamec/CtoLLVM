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

  // Toto je hovadina, ktoru som si povodne myslel, ze treba checkovat, ale pojde to i bez nej
/*  public boolean isReturnStatement() {
    return false;
  }*/

  public boolean isConstant() {
    return false;
  }
}
