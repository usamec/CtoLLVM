package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;

public class DereferenceNode implements PNode {
  private PNode child;

  public DereferenceNode(PNode child) {
    this.child = child;
  }
  
  @Override
  public EvalResult produceOutput(PrintStream out) throws Exception {
    int id = IdCounter.GetNewId();
    EvalResult c = child.produceOutput(out);

    if (!c.type.isPointer()) {
      throw new Exception("pokus o * na nieco co nie je pointer");
    }

    Type t2 = TypeSystem.getInstance().dereference(c.type);

    EvalResult res = new EvalResult(t2, c.getRepresentation());

    out.println(String.format("%s = load %s* %s", res.getRepresentation(),
          res.type.getRepresentation(), c.getRepresentation()));


    return res;
  }
}
