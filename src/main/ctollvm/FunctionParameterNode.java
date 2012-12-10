package ctollvm;

public class FunctionParameterNode {
  private String type;
  private String name;
  private Scope scope;
  private int pointerDepth = 0;
  private Type tt = null;

  public void setName(String name) {
    this.name = name;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void incPointerDepth() {
    this.pointerDepth++;
  }

  public Type getType() throws Exception {
    if (tt == null) {
      TypeSystem typeSystem = TypeSystem.getInstance();
      if (!typeSystem.isValidType(type)) {
        throw new Exception(String.format("Invalid type %s", type));
      }
      tt = typeSystem.getType(type, pointerDepth);

      if (tt.isVoid()) {
        throw new Exception(String.format("Variable cannot have void type"));
      }
    }
    return tt;
  }

  public String getRepresentation() throws Exception {
    return String.format("%s %%%s", getType().getRepresentation(), name);
  }

  public String getName() {
    return name;
  }
}
