package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;
import java.lang.StringBuffer;
import java.util.*;

public class DeclarationNode implements PNode {
  private String type;
  private List<DeclarationProcessor> declarators = null;
  private Scope scope;
  private String storageSpecifier;
  private List<String> typeSpecifiers;

  public DeclarationNode(Scope scope) {
    this.type = "";
    this.declarators = new ArrayList<DeclarationProcessor>();
    this.scope = scope;
    this.storageSpecifier = "";
    this.typeSpecifiers = new ArrayList<String>();
  }

  public void setType(String type) {
    this.type = type;
  }

  public void addDeclarationProcessor(DeclarationProcessor node) {
    declarators.add(node);
  }

  @Override
  public EvalResult produceOutput(PrintStream out) throws Exception {
    TypeSystem typeSystem = TypeSystem.getInstance();
    if (!typeSystem.isValidType(type)) {
      throw new Exception(String.format("Invalid type %s", type));
    }
    
    Type t = typeSystem.getType(type);
    for (DeclarationProcessor n : declarators) {
      n.produceOutput(t, scope, out);
    }
    return null;
  }
}
