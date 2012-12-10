package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;

public class IdentifierNode implements PNode {
  String name;
  private Scope scope;

  public IdentifierNode(String name, Scope scope) {
    this.name = name;
    this.scope = scope;
  }

  @Override
  public EvalResult produceOutput(PrintStream out) throws Exception {
    int id = IdCounter.GetNewId();
    Scope.Variable v = scope.findInScope(name);
    if (v == null) {
      throw new Exception("neznama premenna");
    }
    EvalResult res = new EvalResult(v.type, v.name);
    if (!v.type.isFunction()) {
      out.println(String.format("%s = load %s* %s", res.getRepresentation(),
            v.type.getRepresentation(), v.name));
    }
    return res;
  }
}
