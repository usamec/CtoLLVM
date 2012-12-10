package ctollvm;
import java.io.PrintStream;
import java.util.*;

public class ProgramNode implements PNode {
  private List<PNode> statements;

  public ProgramNode() {
    statements = new ArrayList<PNode>();
  }

  public void addItem(PNode node) {
    statements.add(node);
  }

  @Override
  public EvalResult produceOutput(PrintStream out) throws Exception {
    for (PNode n : statements)
      n.produceOutput(out);
    return new EvalResult();
  }
}
