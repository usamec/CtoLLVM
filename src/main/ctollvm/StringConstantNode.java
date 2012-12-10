package ctollvm;
import java.io.PrintStream;

public class StringConstantNode implements PNode {
  String value;

  public StringConstantNode(String value) {
    this.value = value;
  }

  @Override
  public EvalResult produceOutput(PrintStream out) {
    int length = value.getBytes().length+1;
    String llvmvalue = Util.escapeStringForLLVM(value);

    String tmpval = IdCounter.GetNewTmpVal();
    out.printf("%s = alloca [%d x i8]\n", tmpval, length);
    out.printf("store [%d x i8] c\"%s\", [%d x i8]* %s\n",
        length, llvmvalue, length, tmpval);
    Type t = TypeSystem.getInstance().getType("char", 1);
    EvalResult res = new EvalResult(t);
    out.printf("%s = getelementptr [%d x i8]* %s, i64 0, i64 0\n",
        res.getRepresentation(), length, tmpval);
    return res;
  }
}
