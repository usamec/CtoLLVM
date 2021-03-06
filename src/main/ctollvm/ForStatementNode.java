package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;

public class ForStatementNode implements PNode {
  private PNode clause;
  private PNode expression2;
  private PNode expression3;
  private PNode statement;
  private Scope scope;

  public ForStatementNode(PNode clause,PNode expression2,PNode expression3, PNode statement, Scope scope) {
    this.clause = clause;
    this.expression2 = expression2;
    this.expression3 = expression3;
    this.statement = statement;
    this.scope = scope;
  }
   
  @Override
  public EvalResult produceOutput(PrintStream out) throws Exception {
    
    String labelcond = IdCounter.GetNewLabel();
    String labelstart = IdCounter.GetNewLabel();
    String labelafter = IdCounter.GetNewLabel();
    String labelthird = IdCounter.GetNewLabel();
    
//    out.printf("br label %%%s\n", labelinit);
//    out.printf("%s:\n", labelinit);
    if (clause!=null){
    	EvalResult exp3 = clause.produceOutput(out);
    }
    out.printf("br label %%%s\n", labelcond);
    out.printf("%s:\n", labelcond);

    if(expression2!=null){
    	EvalResult exp = expression2.produceOutput(out);
    
    	if (exp.type != TypeSystem.getInstance().getType("_Bool")) {
    		EvalResult exp2 = TypeSystem.getInstance().convertTo(
    				TypeSystem.getInstance().getType("_Bool"), exp, out);
    		if (exp2 == null)
    			throw new Exception("Bad type in for expression.");
    		exp = exp2;
    	}
        
        out.printf("br i1 %s, label %%%s, label %%%s\n", exp.getRepresentation(), 
        labelstart, labelafter);
    } else {
    	out.printf("br label %%%s\n", labelstart);
    }
    out.printf("%s:\n", labelstart);  
    scope.pushBreakLabel(labelafter);
    scope.pushContinueLabel(labelthird);
    statement.produceOutput(out);
   
    out.printf("br label %%%s\n", labelthird);
    out.printf("%s:\n", labelthird);
    if (expression3!=null){
    	EvalResult exp4 = expression3.produceOutput(out);    	       
    }  
    out.printf("br label %%%s\n", labelcond);
    out.printf("%s:\n", labelafter);
    scope.popBreakLabel();
    scope.popContinueLabel();
    return null;
  }
}
