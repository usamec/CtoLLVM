package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;

public class StructMemberNode implements PNode {
  private PNode child;
  private String member;

  public StructMemberNode(PNode child, String member) {
    this.child = child;
    this.member = member;
  }
  
  @Override
  public EvalResult produceOutput(PrintStream out) throws Exception {
    EvalResult c = child.produceOutput(out);

    if (!(c.type instanceof StructType)) {
      throw new Exception("pokus o . na nieco co nie je struct");
    }
    StructType t = (StructType) c.type;

    int index = t.getMemberIndex(member);
    Type mt = t.getMemberType(index);

    if (c.isLVal()) {
      String tmppointer = IdCounter.GetNewTmpVal();
      out.printf("%s = getelementptr %s* %s, i64 0, i32 %d\n",
          tmppointer, t.getRepresentation(), c.getIdentifierName(),
          index);

      if (mt.isArray()) {
        ArrayType at = (ArrayType) mt;
        Type pt = TypeSystem.getInstance().getPointerType(at.getPointerTo());
        EvalResult res = new EvalResult(pt);
        out.printf("%s = getelementptr %s* %s, i64 0, i64 0\n",
            res.getRepresentation(), at.getRepresentation(), tmppointer);
        return res;
      } else {
        EvalResult res = new EvalResult(mt, tmppointer);
        out.println(String.format("%s = load %s* %s", res.getRepresentation(),
              res.type.getRepresentation(), tmppointer));
        return res;
      }
    } else {
      // Toto je celkom v zadku pokial sa nam tu vyskytne pole
      if (mt.isArray()) {
        String tmpval = IdCounter.GetNewTmpVal();
        out.printf("%s = extractvalue %s %s, %d\n",
          tmpval, t.getRepresentation(), c.getRepresentation(), index);
        String tmpvalp = IdCounter.GetNewTmpVal();
        out.printf("%s = alloca %s", tmpvalp, mt.getRepresentation());
        out.printf("store %s %s, %s* %s\n", mt.getRepresentation(),
            tmpval, mt.getRepresentation(), tmpvalp);
        ArrayType at = (ArrayType) mt;
        Type pt = TypeSystem.getInstance().getPointerType(at.getPointerTo());
        EvalResult res = new EvalResult(pt);
        out.printf("%s = getelementptr %s* %s, i64 0, i64 0\n",
            res.getRepresentation(), at.getRepresentation(), tmpvalp);
        return res;
      } else {
        EvalResult res = new EvalResult(mt);
        out.printf("%s = extractvalue %s %s, %d\n", 
            res.getRepresentation(), t.getRepresentation(),
            c.getRepresentation(), index);
        return res;
      }
    }
  }
}
