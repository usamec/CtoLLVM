package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;

public class CastNode implements PNode {
  private PNode child;
  private TypeNameNode typeName;

  public CastNode(PNode child, TypeNameNode typeName) {
    this.child = child;
    this.typeName = typeName;
  }
  
  @Override
  public EvalResult produceOutput(PrintStream out) throws Exception {
    EvalResult c = child.produceOutput(out);
    Type t2 = typeName.getType();
    if (c.type == t2)
      return c;
    return TypeSystem.getInstance().convertTo(t2, c, out);
  }
}
