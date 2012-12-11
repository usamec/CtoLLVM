package ctollvm;

public class FunctionParameterNode {
  private DeclarationProcessor declaration = null;
  private String type;

  public FunctionParameterNode() {
  }

  public void setDeclaration(DeclarationProcessor dec) {
    declaration = dec;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Type getType() throws Exception {
    TypeSystem typeSystem = TypeSystem.getInstance();
    if (!typeSystem.isValidType(type)) {
      throw new Exception(String.format("Invalid type %s", type));
    }    
    Type t = typeSystem.getType(type);
    // TODO: ak je typ array, tak zmenit na pointer
    return declaration.processTypeAll(t);
  }

  public String getRepresentation() throws Exception {
    return String.format("%s %%%s", getType().getRepresentation(), getName());
  }

  public String getName() {
    return declaration.getName();
  }
}
