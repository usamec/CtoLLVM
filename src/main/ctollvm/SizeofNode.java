package ctollvm;
import java.io.*;
import java.lang.Exception;

public class SizeofNode implements PNode {
  private PNode child = null;
  private TypeNameNode typeName = null;

  public SizeofNode(PNode child) {
    this.child = child;
  }
 
  public SizeofNode(TypeNameNode typeName) {
    this.typeName = typeName;
  }
 
  @Override
  public EvalResult produceOutput(PrintStream out) throws Exception {
    Type t;
    if (typeName != null) {
      t = typeName.getType();
    } else {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      PrintStream ps = new PrintStream(baos);

    
      EvalResult c = child.produceOutput(ps);
      t = c.type;
    }

    return new IntegerConstantEvalResult(t.sizeof(),
        TypeSystem.getInstance().getType("unsigned int"));
  }
}
