package ctollvm;
import java.io.PrintStream;

public interface PNode {
  public EvalResult produceOutput(PrintStream out) throws Exception;
}
