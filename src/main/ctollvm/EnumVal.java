package ctollvm;

public class EnumVal {
  public String name;
  public PNode exp;

  public EnumVal(String name) {
    exp = null;
    this.name = name;
  }

  public EnumVal(String name, PNode exp) {
    this.name = name;
    this.exp = exp;
  }
}
