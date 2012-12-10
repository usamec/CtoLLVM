package ctollvm;
import java.io.PrintStream;
import java.util.*;

public class BlockItemList implements PNode {
  private List<PNode> statements;

  public BlockItemList() {
    statements = new ArrayList<PNode>();
  }

  public void addBlockItem(PNode node) {
    statements.add(node);
  }

  @Override
  public EvalResult produceOutput(PrintStream out) throws Exception {
    int id = IdCounter.GetNewId();
    for (PNode n : statements)
      n.produceOutput(out);

    // TODO: ak bol posledny return, tak vyblublat
    return new EvalResult();
  }
}
