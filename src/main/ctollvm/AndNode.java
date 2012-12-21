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

	static public EvalResult evaluateOperation(EvalResult l, EvalResult r,
			PrintStream out) throws Exception {
          if (l.type != r.type) {
            EvalResult l1 = TypeSystem.getInstance().unifyTypes(l, r, out);
            if (l1 == null) {
              EvalResult r1 = TypeSystem.getInstance().unifyTypes(r, l, out);
              if (r1 == null) {
                throw new Exception("shift nekompatibilnych typov");
              } else {
                r = r1;
              }
            } else {
              l = l1;
            }
          }
          if (!l.type.isIntegral())
            throw new Exception("And on nonintegral type");
		EvalResult res = new EvalResult(l.type);

		out.println(String.format("%s = and %s %s, %s",
				res.getRepresentation(), l.type.getRepresentation(), l
						.getRepresentation(), r.getRepresentation()));

		return res;
	}

	@Override
	public EvalResult produceOutput(PrintStream out) throws Exception {
		EvalResult l = lhs.produceOutput(out);
		EvalResult r = rhs.produceOutput(out);

		return evaluateOperation(l, r, out);
	}
}
