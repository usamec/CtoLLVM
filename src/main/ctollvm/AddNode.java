package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;

public class AddNode implements PNode {
  private PNode lhs;
  private PNode rhs;

  public AddNode(PNode lhs, PNode rhs) {
    this.lhs = lhs;
    this.rhs = rhs;
  }

  static public EvalResult evaluateOperation(EvalResult l, EvalResult r, PrintStream out)
      throws Exception {
    // Najprv vyriesime scitanie typu pointer + integer
    if (r.type.isPointer() && l.type.isIntegral()) {
      EvalResult tmp = l;
      l = r;
      r = tmp;
    }

    if (l.type.isPointer() && r.type.isIntegral()) {
      EvalResult res = new EvalResult(l.type);
      out.printf("%s = getelementptr %s %s, %s %s\n",
          res.getRepresentation(), l.type.getRepresentation(),
          l.getRepresentation(), r.type.getRepresentation(), r.getRepresentation());
      return res;
    }

    if (l.type != r.type) {
      EvalResult l1 = TypeSystem.getInstance().unifyTypes(l, r, out);
      if (l1 == null) {
        EvalResult r1 = TypeSystem.getInstance().unifyTypes(r, l, out);
        if (r1 == null) {
          throw new Exception("scitanie nekompatibilnych typov");
        } else {
          r = r1;
        }
      } else {
        l = l1;
      }
    }

    EvalResult res = new EvalResult(l.type);

    if (l.type.isIntegral()) {
      out.println(String.format("%s = add %s %s, %s", 
          res.getRepresentation(), l.type.getRepresentation(),
          l.getRepresentation(), r.getRepresentation()));
    } else if (l.type.isDouble()) {
      out.println(String.format("%s = fadd %s %s, %s", 
          res.getRepresentation(), l.type.getRepresentation(),
          l.getRepresentation(), r.getRepresentation()));
    } else {
      throw new Exception("Wrong types for addition");
    }

    return res;
  }

  @Override
  public EvalResult produceOutput(PrintStream out) throws Exception {
    EvalResult l = lhs.produceOutput(out);
    EvalResult r = rhs.produceOutput(out);

    return evaluateOperation(l, r, out);
  }
}
