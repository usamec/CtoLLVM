package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;

public class UnaryNotNode implements PNode {
  private PNode child;

  public UnaryNotNode(PNode child) {
    this.child = child;
  }
  
  @Override
  public EvalResult produceOutput(PrintStream out) throws Exception {
    EvalResult result = child.produceOutput(out);

    Type rettype = TypeSystem.getInstance().getType("_Bool");
    if (result.type.isIntegral()) {
      EvalResult res = new EvalResult(rettype);
      out.printf("%s = icmp eq %s %s, 0\n", res.getRepresentation(),
          result.type.getRepresentation(), result.getRepresentation());
      return res;
    }
    if (result.type.isPointer()) {
      String tmpvar = IdCounter.GetNewTmpVal();
      EvalResult res = new EvalResult(rettype);
      out.printf("%s = ptrtoint %s %s to i64\n", tmpvar,
          result.type.getRepresentation(), result.getRepresentation());
      out.printf("%s = icmp eq i64 %s, 0\n",
          res.getRepresentation(), tmpvar);
      return res;
    }
    if (result.type.isDouble()) {
      EvalResult res = new EvalResult(rettype);
      out.printf("%s = fcmp oeq %s %s, 0.0\n", res.getRepresentation(),
          result.type.getRepresentation(), result.getRepresentation());
      return res;
    }
    throw new Exception("Unary ! on invalid type");
  }
}
