package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;

public class ReturnStatementNode implements PNode {
  private PNode expression;
  private Scope scope;

  public ReturnStatementNode(PNode expression, Scope scope) {
    this.expression = expression;
    this.scope = scope;
  }
  
  @Override
  public EvalResult produceOutput(PrintStream out) throws Exception {
    if (this.expression == null && !scope.getFunctionReturnType().isVoid()) {
      throw new Exception("Returning void from non-void function");
    }
    if (this.expression != null && scope.getFunctionReturnType().isVoid()) {
      throw new Exception("Returning non-void from void function");
    }
    String labelafter = IdCounter.GetNewLabel();
    if (this.expression == null) {
      out.printf("ret void\n");
      out.printf("%s:\n", labelafter);
      return null;
    }

    EvalResult res = this.expression.produceOutput(out);
    if (res.type != scope.getFunctionReturnType()) {
      EvalResult r2 = TypeSystem.getInstance().convertTo(scope.getFunctionReturnType(), 
          res, out);
      if (r2 == null) {
        throw new Exception("Wrong return value type");
      }
      res = r2;
    }
    out.printf("ret %s %s\n", res.type.getRepresentation(), res.getRepresentation());
    out.printf("%s:\n", labelafter);
    return null;
  }
}
