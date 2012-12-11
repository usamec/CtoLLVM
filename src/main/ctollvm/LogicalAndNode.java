package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;

public class LogicalAndNode implements PNode {
  private PNode lhs;
  private PNode rhs;

  public LogicalAndNode(PNode lhs, PNode rhs) {
    this.lhs = lhs;
    this.rhs = rhs;
  }

  @Override
  public EvalResult produceOutput(PrintStream out) throws Exception { 
    // Brace yourselves, jazda traktorom sa zacina

    String labell = IdCounter.GetNewLabel();
    String labell2 = IdCounter.GetNewLabel();
    String labelr = IdCounter.GetNewLabel();
    String labelr2 = IdCounter.GetNewLabel();
    String labelafter = IdCounter.GetNewLabel();
    out.printf("br label %%%s\n", labell);
    out.printf("%s:\n", labell);

    EvalResult l = lhs.produceOutput(out);

    if (l.type != TypeSystem.getInstance().getType("_Bool")) {
      EvalResult exp2 = TypeSystem.getInstance().convertTo(
          TypeSystem.getInstance().getType("_Bool"), l, out);
      if (exp2 == null)
        throw new Exception("Bad type in expression.");
      l = exp2;
    }
    out.printf("br label %%%s\n", labell2);
    out.printf("%s:\n", labell2);
    // Ak je l true, tak pokracuje vo vyhodnocovani, ak je false, tak skaceme rovno za to
    out.printf("br i1 %s, label %%%s, label %%%s\n",
        l.getRepresentation(), labelr, labelafter);

    out.printf("%s:\n", labelr);
    EvalResult r = rhs.produceOutput(out);
    if (r.type != TypeSystem.getInstance().getType("_Bool")) {
      EvalResult exp2 = TypeSystem.getInstance().convertTo(
          TypeSystem.getInstance().getType("_Bool"), r, out);
      if (exp2 == null)
        throw new Exception("Bad type in expression.");
      r = exp2;
    }
    out.printf("br label %%%s\n", labelr2);
    out.printf("%s:\n", labelr2);
    out.printf("br label %%%s\n", labelafter);
    out.printf("%s:\n", labelafter);
    EvalResult res = new EvalResult(l.type);
    out.printf("%s = phi i1 [0, %%%s], [%s, %%%s]\n",
      res.getRepresentation(), labell2, r.getRepresentation(), labelr2);
    return res;
  }
}
