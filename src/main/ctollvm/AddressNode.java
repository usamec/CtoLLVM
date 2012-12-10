package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;

public class AddressNode implements PNode {
  private PNode child;

  public AddressNode(PNode child) {
    this.child = child;
  }
  
  @Override
  public EvalResult produceOutput(PrintStream out) throws Exception {
    int id = IdCounter.GetNewId();
    EvalResult c = child.produceOutput(out);

    if (!c.isLVal()) {
      throw new Exception("pokus o & na nieco co nie je lval");
    }

    Type t2 = TypeSystem.getInstance().getPointerTo(c.type);

    EvalResult res = new EvalResult(t2);
    res.name = c.getIdentifierName();

    return res;
  }
}
