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
    if (v.enumConst) {
      return new IntegerConstantEvalResult(v.enumVal, TypeSystem.getInstance().getType("int"));   
    }
    if (v.type.isArray()) {
      ArrayType at = (ArrayType) v.type;
      Type pt = TypeSystem.getInstance().getPointerType(at.getPointerTo());
      EvalResult res = new EvalResult(pt);
      out.printf("%s = getelementptr %s* %s, i64 0, i64 0\n",
          res.getRepresentation(), at.getRepresentation(), v.name);
      return res;
    } else {
      EvalResult res = new EvalResult(v.type, v.name);
      if (!v.type.isFunction()) {
        out.println(String.format("%s = load %s* %s", res.getRepresentation(),
              v.type.getRepresentation(), v.name));
      }
      return res;
    }
  }
}
