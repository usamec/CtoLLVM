package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;

public class MinusNode implements PNode {
  private PNode lhs;
  private PNode rhs;

  public MinusNode(PNode lhs, PNode rhs) {
    this.lhs = lhs;
    this.rhs = rhs;
  }

  static public EvalResult evaluateOperation(EvalResult l, EvalResult r, PrintStream out)
      throws Exception {
    // Najprv vyriesime odcitanie typu pointer + integer
    if (l.type.isPointer() && r.type.isIntegral()) {
      String tmpval = IdCounter.GetNewTmpVal();
      out.printf("%s = sub %s 0, %s\n", tmpval, r.type.getRepresentation(),
          r.getRepresentation());
      EvalResult res = new EvalResult(l.type);
      out.printf("%s = getelementptr %s %s, %s %s\n",
          res.getRepresentation(), l.type.getRepresentation(),
          l.getRepresentation(), r.type.getRepresentation(), tmpval);
      return res;
    }

    if (l.type.isPointer() && r.type.isPointer()) {
      PointerType lt = (PointerType) l.type;
      PointerType rt = (PointerType) r.type;
      Type li = lt.getPointerTo();
      Type ri = rt.getPointerTo();
      if (li != ri) {
        throw new Exception("Subtraction of pointers to different things");
      }
      TypeSystem typeSystem = TypeSystem.getInstance();
      EvalResult lnum = typeSystem.convertTo(typeSystem.getType("long long"), l, out);
      EvalResult rnum = typeSystem.convertTo(typeSystem.getType("long long"), r, out);
      String tmpval = IdCounter.GetNewTmpVal();
      out.printf("%s = sub i64 %s, %s\n", tmpval, 
          lnum.getRepresentation(), rnum.getRepresentation());
      EvalResult res = new EvalResult(typeSystem.getType("long long"));
      out.printf("%s = sdiv i64 %s, %d\n", res.getRepresentation(), tmpval, li.sizeof());
      return res;
    }

    if (l.type != r.type) {
      EvalResult l1 = TypeSystem.getInstance().unifyTypes(l, r, out);
      if (l1 == null) {
        EvalResult r1 = TypeSystem.getInstance().unifyTypes(r, l, out);
        if (r1 == null) {
          throw new Exception("odscitanie nekompatibilnych typov");
        } else {
          r = r1;
        }
      } else {
        l = l1;
      }
    }

    EvalResult res = new EvalResult(l.type);

    if (l.type.isIntegral()) {
      out.println(String.format("%s = sub %s %s, %s", 
          res.getRepresentation(), l.type.getRepresentation(),
          l.getRepresentation(), r.getRepresentation()));
    } else if (l.type.isDouble()) {
      out.println(String.format("%s = fsub %s %s, %s", 
          res.getRepresentation(), l.type.getRepresentation(),
          l.getRepresentation(), r.getRepresentation()));
    } else {
      throw new Exception("Wrong types for subtraction");
    }

    return res;
  }

  @Override
  public EvalResult produceOutput(PrintStream out) throws Exception {
    EvalResult l = lhs.produceOutput(out);
    EvalResult r = rhs.produceOutput(out);

    return evaluateOperation(l, r, out);
  }
}
