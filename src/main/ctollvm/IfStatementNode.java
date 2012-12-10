package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;

public class IfStatementNode implements PNode {
  private PNode expression;
  private PNode ifbranch;
  private PNode elsebranch;

  public IfStatementNode(PNode expression, PNode ifbranch, PNode elsebranch) {
    this.expression = expression;
    this.ifbranch = ifbranch;
    this.elsebranch = elsebranch;
  }
  @Override
  public EvalResult produceOutput(PrintStream out) throws Exception {
    EvalResult exp = expression.produceOutput(out);
    
    if (exp.type != TypeSystem.getInstance().getType("_Bool")) {
      throw new Exception("Non bool comparisons are not supported yet.");
    }
    
    String labelif = IdCounter.GetNewLabel();
    String labelelse = IdCounter.GetNewLabel();
    String labelafter = IdCounter.GetNewLabel();
    
    out.printf("br i1 %s, label %%%s, label %%%s\n", exp.getRepresentation(), labelif, labelelse);
    out.printf("%s:\n", labelif);
    ifbranch.produceOutput(out);
    out.printf("br label %%%s\n", labelafter);
    out.printf("%s:\n", labelelse);
    if (elsebranch != null)
      elsebranch.produceOutput(out);
    out.printf("br label %%%s\n", labelafter);
    out.printf("%s:\n", labelafter);
    return null;
  }
}
