package ctollvm;

import java.io.PrintStream;
import java.lang.Exception;

public class ShiftOperationNode implements PNode {
	private PNode lhs;
	private PNode rhs;
	private String direction;

	public ShiftOperationNode(PNode lhs, PNode rhs, String direction) {
		this.lhs = lhs;
		this.rhs = rhs;
		this.direction = direction;

	}

	static public EvalResult evaluateOperation(EvalResult l, EvalResult r,
			PrintStream out, String direction) {
		EvalResult res = new EvalResult(l.type);
		if (direction.equals(">>")) {
			out.println(String.format("%s = lshr %s %s, %s", res
					.getRepresentation(), l.type.getRepresentation(), l
					.getRepresentation(), r.getRepresentation()));
		} else {

			out.println(String.format("%s = shl %s %s, %s", res
					.getRepresentation(), l.type.getRepresentation(), l
					.getRepresentation(), r.getRepresentation()));
		}

		return res;
	}

	@Override
	public EvalResult produceOutput(PrintStream out) throws Exception {
		EvalResult l = lhs.produceOutput(out);
		EvalResult r = rhs.produceOutput(out);

		return evaluateOperation(l, r, out, direction);

	}
}
