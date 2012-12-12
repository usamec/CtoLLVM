package ctollvm;

import java.io.PrintStream;
import java.lang.Exception;

public class DoStatementNode implements PNode {
	private PNode expression;
	private PNode statement;
	private Scope scope;

	public DoStatementNode(PNode expression, PNode statement, Scope scope) {
		this.expression = expression;
		this.statement = statement;
		this.scope = scope;
	}

	@Override
	public EvalResult produceOutput(PrintStream out) throws Exception {
		String labelcond = IdCounter.GetNewLabel();
		String labelstart = IdCounter.GetNewLabel();
		String labelafter = IdCounter.GetNewLabel();
		out.printf("br label %%%s\n", labelstart);
		
		out.printf("%s:\n", labelstart);
		scope.pushBreakLabel(labelafter);
		scope.pushContinueLabel(labelcond);
		statement.produceOutput(out);
		out.printf("br label %%%s\n", labelcond);
		
		out.printf("%s:\n", labelcond);
		EvalResult exp = expression.produceOutput(out);
		if (exp.type != TypeSystem.getInstance().getType("_Bool")) {
			EvalResult exp2 = TypeSystem.getInstance().convertTo(
					TypeSystem.getInstance().getType("_Bool"), exp, out);
			if (exp2 == null)
				throw new Exception("Bad type in do while expression.");
			exp = exp2;
		}
		out.printf("br i1 %s, label %%%s, label %%%s\n", exp
				.getRepresentation(), labelstart, labelafter);
		
		out.printf("%s:\n", labelafter);
		scope.popBreakLabel();
		scope.popContinueLabel();
		return null;
	}
}
