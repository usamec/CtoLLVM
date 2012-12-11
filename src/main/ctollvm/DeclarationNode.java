package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;
import java.lang.StringBuffer;
import java.util.*;

public class DeclarationNode implements PNode {
  private String type;
  private List<DeclaratorNode> declarators = null;

  public DeclarationNode() {
    this.type = "";
    this.declarators = new ArrayList<DeclaratorNode>();
  }

  public void setType(String type) {
    this.type = type;
  }

  public void addDeclaratorNode(DeclaratorNode node) {
    declarators.add(node);
  }

  @Override
  public EvalResult produceOutput(PrintStream out) throws Exception {
    for (DeclaratorNode n : declarators) {
      n.setType(type);
      n.produceOutput(out);
    }
    return null;
  }
}
