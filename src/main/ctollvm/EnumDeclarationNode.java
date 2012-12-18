package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;
import java.lang.StringBuffer;
import java.util.*;

public class EnumDeclarationNode {
  Scope scope;
  String name;
  List<EnumVal> ids;
  boolean use = false;

  public EnumDeclarationNode(Scope scope) {
    this.scope = scope;
    this.name = "";
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setEnumList(List<EnumVal> ids) {
    this.ids = ids;
  }

  public Type processDeclaration(PrintStream out) throws Exception {
    int id = 0;
    for (EnumVal s : ids) {
      String name = s.name;
      if (scope.hasInCurrentScope(name)) {
        throw new Exception("Reuse of identifier");
      }
      Scope.Variable v = scope.addVariable(name, TypeSystem.getInstance().getType("int"));
      v.enumConst = true;
      if (s.exp != null) {
        EvalResult res = s.exp.produceOutput(out);
        if (!res.type.isIntegral())
          throw new Exception("Enum constant should be integer");
        if (!res.isConstant())
          throw new Exception("Enum constant should be constant");
        id = new Integer(res.getRepresentation());
      }
      v.enumVal = id;
      id++;
    }

    return TypeSystem.getInstance().getType("int");
  }
}
