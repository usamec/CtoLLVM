package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;

public class RemNode implements PNode {
  private PNode lhs;
  private PNode rhs;

  public RemNode(PNode lhs, PNode rhs) {
    this.lhs = lhs;
    this.rhs = rhs;
  }

  public static EvalResult evaluateOperation(EvalResult l, EvalResult r, PrintStream out)
      throws Exception {
    if (l.type != r.type) {
      EvalResult l1 = TypeSystem.getInstance().unifyTypes(l, r, out);
      if (l1 == null) {
        EvalResult r1 = TypeSystem.getInstance().unifyTypes(r, l, out);
        if (r1 == null) {
          throw new Exception("zvysok nekompatibilnych typov");
        } else {
          r = r1;
        }
      } else {
        l = l1;
      }
    }

    EvalResult res = new EvalResult(l.type);

    if (l.type.isIntegral()) {
      // TODO: unsigned operation
      if (l.type.isSigned()) {
        out.println(String.format("%s = srem %s %s, %s", 
            res.getRepresentation(), l.type.getRepresentation(),
            l.getRepresentation(), r.getRepresentation()));
      } else {
        out.println(String.format("%s = urem %s %s, %s", 
            res.getRepresentation(), l.type.getRepresentation(),
            l.getRepresentation(), r.getRepresentation()));
      }
    } else {
      throw new Exception("Wrong types for remainder");
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
