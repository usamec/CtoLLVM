package ctollvm;

import java.io.PrintStream;
import java.lang.Exception;

public class ExclusiveOrNode implements PNode {
	private PNode lhs;
	private PNode rhs;

	public ExclusiveOrNode(PNode lhs, PNode rhs) {
		this.lhs = lhs;
		this.rhs = rhs;
	}

	static public EvalResult evaluateOperation(EvalResult l, EvalResult r,
			PrintStream out) {
		EvalResult res = new EvalResult(l.type);

		out.println(String.format("%s = xor %s %s, %s",
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