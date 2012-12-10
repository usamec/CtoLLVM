package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;
import java.util.HashMap;

public class CompareNode implements PNode {
  private PNode lhs;
  private PNode rhs;
  private String comparation;

  public CompareNode(PNode lhs, PNode rhs, String comparation) {
    this.lhs = lhs;
    this.rhs = rhs;
    this.comparation = comparation;
  }
  @Override
  public EvalResult produceOutput(PrintStream out) throws Exception {
    HashMap<String, String> signed_comparators = new HashMap<String, String>();
    HashMap<String, String> unsigned_comparators = new HashMap<String, String>();
    HashMap<String, String> floating_comparators = new HashMap<String, String>();
    signed_comparators.put("<", "slt");
    signed_comparators.put("<=", "sle");
    signed_comparators.put(">", "sgt");
    signed_comparators.put(">=", "sge");

    unsigned_comparators.put("<", "ult");
    unsigned_comparators.put("<=", "ule");
    unsigned_comparators.put(">", "ugt");
    unsigned_comparators.put(">=", "uge");

    floating_comparators.put("<", "olt");
    floating_comparators.put("<=", "ole");
    floating_comparators.put(">", "ogt");
    floating_comparators.put(">=", "oge");
	
    EvalResult l = lhs.produceOutput(out);
    EvalResult r = rhs.produceOutput(out);

    if (l.type != r.type) {
      EvalResult l1 = TypeSystem.getInstance().unifyTypes(l, r, out);
      if (l1 == null) {
        EvalResult r1 = TypeSystem.getInstance().unifyTypes(r, l, out);
        if (r1 == null) {
          throw new Exception("Comparison of incompatible types");
        } else {
          r = r1;
        }
      } else {
        l = l1;
      }
    }

    EvalResult res = new EvalResult(TypeSystem.getInstance().getType("_Bool"));
    
    if (l.type.isIntegral() || l.type.isPointer()) {
      if (l.type.isSigned() && !l.type.isPointer()) {
    	out.println(String.format("%s = icmp %s %s %s, %s", 
    	    res.getRepresentation(),
    	    signed_comparators.get(comparation),
    	    l.type.getRepresentation(),
    		l.getRepresentation(), r.getRepresentation()));
      } else {
        out.println(String.format("%s = icmp %s %s %s, %s", 
            res.getRepresentation(),
            unsigned_comparators.get(comparation),
            l.type.getRepresentation(),
            l.getRepresentation(), r.getRepresentation()));
      }
    } else if (l.type.isDouble()) {
      out.println(String.format("%s = fcmp %s %s %s, %s", 
          res.getRepresentation(),
          floating_comparators.get(comparation),
          l.type.getRepresentation(),
          l.getRepresentation(), r.getRepresentation()));
    } else {
      throw new Exception("Wrong types for comparation");
    }

    return res;
  }
}
