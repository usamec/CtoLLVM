package ctollvm;
import java.io.*;
import java.lang.Exception;
import java.lang.StringBuffer;
import java.util.*;

public class ArrayDeclarationProcessor extends DeclarationProcessor {
  PNode expression = null;

  public ArrayDeclarationProcessor() {
    super();
  }

  public void setExpression(PNode expression) {
    this.expression = expression;
  }

  public Type processType(Type type) throws Exception {
    if (type.isFunction()) {
      throw new Exception("Array of functions is forbidden");
    }
    if (type.isIncomplete() || type.isVoid()) {
      throw new Exception("Array of incomplete type or void is forbidden");
    }

    // Vyrobime si dummy outputstream - vysledok tohoto ma byt konstanta
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(baos);
    EvalResult res = expression.produceOutput(ps);

    if (!res.type.isIntegral()) {
      throw new Exception("Array size should have integer type.");
    }
    if (!res.isConstant()) {
      throw new Exception("Array size should be constant.");
    }
    IntegerConstantEvalResult ee = (IntegerConstantEvalResult) res;
    Type t2 = TypeSystem.getInstance().getArrayType(type, ee.value);

    return t2;
  }
}
