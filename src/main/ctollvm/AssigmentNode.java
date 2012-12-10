package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;

public class AssigmentNode implements PNode {
  private PNode lhs;
  private PNode rhs;

  public AssigmentNode(PNode lhs, PNode rhs) {
    this.lhs = lhs;
    this.rhs = rhs;
  }
  @Override
  public EvalResult produceOutput(PrintStream out) throws Exception {
    int id = IdCounter.GetNewId();
    EvalResult l = lhs.produceOutput(out);
    EvalResult r = rhs.produceOutput(out);
    if (!l.isLVal()) {
      throw new Exception("priradenie do nie lval");
    }
    if (l.type != r.type) {
      EvalResult r2 = TypeSystem.getInstance().convertTo(l.type, r, out);
      if (r2 == null) {
        throw new Exception("zle priradenie");
      }
      r = r2;
    }
    
    out.println(String.format("store %s %s, %s* %s", r.type.getRepresentation(),
        r.getRepresentation(),
        l.type.getRepresentation(), l.getIdentifierName()));

    // TODO: isto chcem vratit toto?
    return r;
  }
}
