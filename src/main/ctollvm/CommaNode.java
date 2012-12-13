package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;

public class CommaNode implements PNode {
  private PNode l, r;

  public CommaNode(PNode l, PNode r) {
    this.l = l;
    this.r = r;
  }
  
  @Override
  public EvalResult produceOutput(PrintStream out) throws Exception {
    EvalResult lr = l.produceOutput(out);
    EvalResult rr = r.produceOutput(out);
    return rr;
  }
}
