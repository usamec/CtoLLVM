package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;

public class AndNode implements PNode {
  private PNode lhs;
  private PNode rhs;

  public AndNode(PNode lhs, PNode rhs) {
    this.lhs = lhs;
    this.rhs = rhs;
  }

  @Override
  public EvalResult produceOutput(PrintStream out) throws Exception { 
    EvalResult l = lhs.produceOutput(out);
	  EvalResult r = rhs.produceOutput(out);	  
	  EvalResult res = new EvalResult(l.type);
	  
	  out.println(String.format("%s = and %s %s, %s", res.getRepresentation(), l.type.getRepresentation(),l.getRepresentation(), r.getRepresentation()));
	
    return res;
  }
}
