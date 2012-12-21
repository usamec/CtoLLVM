package ctollvm;

import java.io.PrintStream;
import java.lang.Exception;
import java.util.HashMap;

public class EqualityNode implements PNode {
	private PNode lhs;
	private PNode rhs;
	String equality;

	public EqualityNode(PNode lhs, PNode rhs, String equality) {
		this.lhs = lhs;
		this.rhs = rhs;
		this.equality = equality;
	}

	@Override
	public EvalResult produceOutput(PrintStream out) throws Exception {
		HashMap<String, String> integer_comparators = new HashMap<String, String>();
		HashMap<String, String> floating_comparators = new HashMap<String, String>();

		integer_comparators.put("==", "eq");
		integer_comparators.put("!=", "ne");
		floating_comparators.put("==", "oeq");
		floating_comparators.put("!=", "one");

		EvalResult l = lhs.produceOutput(out);
		EvalResult r = rhs.produceOutput(out);
                if (r.type.isPointer() && l.type.isIntegral()) {
                  EvalResult tmp = l;
                  l = r;
                  r = tmp;
                }

                if (l.type.isPointer() && r.type.isIntegral()) {
                  EvalResult l1 = TypeSystem.getInstance().convertTo(r.type, l, out);
                  if (l1 == null)
                    throw new Exception("Comparison of incompatible types");
                  l = l1;
                }

		if (l.type != r.type) {
			EvalResult l1 = TypeSystem.getInstance().unifyTypes(l, r, out);
			if (l1 == null) {
				EvalResult r1 = TypeSystem.getInstance().unifyTypes(r, l, out);
				if (r1 == null) {
					throw new Exception("Comparison of incompatible types");
				} else {
					r = r1;
				}
			} else {
				l = l1;
			}
		}

		EvalResult res = new EvalResult(TypeSystem.getInstance().getType(
				"_Bool"));

		if (l.type.isIntegral() || l.type.isPointer()) {
			out.println(String.format("%s = icmp %s %s %s, %s", res
					.getRepresentation(), integer_comparators.get(equality),
					l.type.getRepresentation(), l.getRepresentation(), r
							.getRepresentation()));

		} else if (l.type.isDouble()) {
			out.println(String.format("%s = fcmp %s %s %s, %s", res
					.getRepresentation(), floating_comparators.get(equality),
					l.type.getRepresentation(), l.getRepresentation(), r
							.getRepresentation()));
		} else {
			throw new Exception("Wrong types for comparation");
		}

		return res;
	}
}
