package ctollvm;
import java.io.*;
import java.lang.Exception;

public class TernaryNode implements PNode {
  private PNode cond;
  private PNode tr;
  private PNode fal;

  public TernaryNode(PNode cond, PNode tr, PNode fal) {
    this.cond = cond;
    this.tr = tr;
    this.fal = fal;
  }

  @Override
  public EvalResult produceOutput(PrintStream out) throws Exception {
    EvalResult exp = cond.produceOutput(out);
    if (exp.type != TypeSystem.getInstance().getType("_Bool")) {
      EvalResult exp2 = TypeSystem.getInstance().convertTo(
          TypeSystem.getInstance().getType("_Bool"), exp, out);
      if (exp2 == null)
        throw new Exception("Bad time in if expression.");
      exp = exp2;
    }
    ByteArrayOutputStream bat = new ByteArrayOutputStream();
    ByteArrayOutputStream baf = new ByteArrayOutputStream();
    PrintStream pst = new PrintStream(bat);
    PrintStream psf = new PrintStream(baf);

    EvalResult trr = tr.produceOutput(pst);
    EvalResult far = fal.produceOutput(psf);

    if (far.type.isPointer() && trr.type.isIntegral() && trr.isConstant()) {
      trr = TypeSystem.getInstance().convertTo(far.type, trr, pst);
    }
    if (trr.type.isPointer() && far.type.isIntegral() && far.isConstant()) {
      far = TypeSystem.getInstance().convertTo(trr.type, far, psf);
    }

    if (trr.type != far.type) {
      EvalResult trr1 = TypeSystem.getInstance().unifyTypes(trr, far, pst);
      if (trr1 == null) {
        EvalResult far1 = TypeSystem.getInstance().unifyTypes(far, trr, psf);
        if (far1 == null) {
          throw new Exception("ternary nekompatibilnych typov");
        } else {
          far = far1;
        }
      } else {
        trr = trr1;
      }
    }

    String labeltrue = IdCounter.GetNewLabel();
    String labeltrueend = IdCounter.GetNewLabel();
    String labelfalse = IdCounter.GetNewLabel();
    String labelfalseend = IdCounter.GetNewLabel();
    String labelafter = IdCounter.GetNewLabel();
    EvalResult res = new EvalResult(far.type);

    out.printf("br i1 %s, label %%%s, label %%%s\n", exp.getRepresentation(),
        labeltrue, labelfalse);

    out.printf("%s:\n", labeltrue);
    bat.writeTo(out);
    out.printf("br label %%%s\n", labeltrueend);
    out.printf("%s:\n", labeltrueend);
    out.printf("br label %%%s\n", labelafter);
    out.printf("%s:\n", labelfalse);
    baf.writeTo(out);
    out.printf("br label %%%s\n", labelfalseend);
    out.printf("%s:\n", labelfalseend);
    out.printf("br label %%%s\n", labelafter);
    out.printf("%s:\n", labelafter);
    out.printf("%s = phi %s [%s, %%%s], [%s, %%%s]\n",
      res.getRepresentation(), res.type.getRepresentation(),
      trr.getRepresentation(), labeltrueend, far.getRepresentation(),
      labelfalseend);

    return res;
  }
}
