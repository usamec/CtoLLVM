package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;
import java.util.*;

public class SwitchNode implements PNode {
  private PNode expression;
  private PNode statement;
  private Scope scope;
  private String defaultLabel;
  private Map<String, String> caseLabels;

  public SwitchNode(PNode expression, PNode statement, Scope scope) {
    this.expression = expression;
    this.statement = statement;
    this.scope = scope;
    this.defaultLabel = null;
    this.caseLabels = new HashMap<String, String>();
  }

  public void setDefaultLabel(String label) {
    defaultLabel = label;
  }

  public void addLabel(String constant, String label) throws Exception {
    if (caseLabels.containsKey(constant)) 
      throw new Exception("Duplicate case value");
    caseLabels.put(constant, label);
  }
  @Override
  public EvalResult produceOutput(PrintStream out) throws Exception {
    scope.pushSwitchNode(this);
    EvalResult exp = expression.produceOutput(out);
    String endLabel = IdCounter.GetNewLabel();
    String decisionLabel = IdCounter.GetNewLabel();
    String dummyLabel = IdCounter.GetNewLabel();
    scope.pushBreakLabel(endLabel);
  
    if (!exp.type.isIntegral())
      throw new Exception("Switch allowed only for integer types");

    exp = TypeSystem.getInstance().promoteInteger(exp, out);

    out.printf("br label %%%s\n", decisionLabel);
    out.printf("%s:\n", dummyLabel);

    statement.produceOutput(out);
    out.printf("br label %%%s\n", endLabel);

    out.printf("%s:\n", decisionLabel);

    if (defaultLabel == null)
      defaultLabel = endLabel;

    out.printf("switch %s %s, label %%%s [",
        exp.type.getRepresentation(), exp.getRepresentation(), defaultLabel);

    for(Map.Entry<String, String> entry : caseLabels.entrySet()) {
      out.printf(" %s %s, label %%%s ", exp.type.getRepresentation(),
          entry.getKey(), entry.getValue());
    }
    out.printf("]\n");
    
    out.printf("%s:\n", endLabel);
/*    if (exp.type != TypeSystem.getInstance().getType("_Bool")) {
      EvalResult exp2 = TypeSystem.getInstance().convertTo(
          TypeSystem.getInstance().getType("_Bool"), exp, out);
      if (exp2 == null)
        throw new Exception("Bad time in if expression.");
      exp = exp2;
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
    out.printf("%s:\n", labelafter);*/
    scope.popSwitchNode();
    return null;
  }
}
