package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;
import java.lang.StringBuffer;
import java.util.*;

public class FunctionDefinitionNode implements PNode {
  private String type;
  private String name;
  private Scope scope;
  private int pointerDepth = 0;
  private PNode bli;
  private List<FunctionParameterNode> parameters;
  private FunctionDeclarationNode declaration;

  public FunctionDefinitionNode(Scope scope) {
    this.scope = scope;
    this.pointerDepth = 0;
    this.type = "";
    this.name = "";
    parameters = new ArrayList<FunctionParameterNode>();
  }

  public void setFunctionDeclaration(FunctionDeclarationNode dec) {
    declaration = dec;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void incPointerDepth() {
    this.pointerDepth++;
  }

  public void setBli(PNode bli) {
    this.bli = bli;
  }

  @Override
  public EvalResult produceOutput(PrintStream out) throws Exception {
    name = declaration.getName();
    parameters = declaration.getParameters();
    if (!scope.parent().isGlobal()) {
      throw new Exception("Function cannot be defined in local scope");
    }

    TypeSystem typeSystem = TypeSystem.getInstance();
    if (!typeSystem.isValidType(type)) {
      throw new Exception(String.format("Invalid type %s", type));
    }    
    Type t = typeSystem.getType(type, pointerDepth);

    if (t.isVoid())
      out.print(String.format("define void @%s(", name));
    else 
      out.print(String.format("define %s @%s(", t.getRepresentation(), name));
    
    boolean first = true;
    List<Type> arguments = new ArrayList<Type>();
    for (FunctionParameterNode n : parameters) {
      if (!first) out.print(", ");
      first = false;
      out.print(n.getRepresentation());
      arguments.add(n.getType());
    }
    Type fType = typeSystem.getTypeForFunction(t, arguments);
    Scope.Variable vf = scope.parent().findInScope(name);
    if (vf != null) {
      if (vf.type != fType) {
        throw new Exception("Incompatible definition of function");
      }
      if (vf.defined) {
        throw new Exception("Redefinition of function");
      }
    }
    vf = scope.parent().addVariable(name, fType);
    vf.defined = true;

    scope.setFunctionReturnType(t);
   
    out.println(") {");

    for (FunctionParameterNode n: parameters) {
      if (scope.hasInCurrentScope(n.getName())) {
        throw new Exception(String.format("Variable %s already declared", name));
      }
      Scope.Variable v = scope.addVariable(n.getName(), n.getType());
      out.printf("%s = alloca %s\n", v.name, v.type.getRepresentation());
      out.printf("store %s, %s* %s\n", n.getRepresentation(), v.type.getRepresentation(),
                 v.name);
    }

    EvalResult res = bli.produceOutput(out);
    if (!res.isReturnStatement()) {
      if (t.isVoid()) {
        out.println("ret void");
      } else {
        String name1 = String.format("%%tmp.val.%d", IdCounter.GetNewId()); 
        out.printf("%s = alloca %s\n", name1, t.getRepresentation());
        String name2 = String.format("%%tmp.val.%d", IdCounter.GetNewId()); 
        out.printf("%s = load %s* %s\n", name2, t.getRepresentation(), name1);
        out.printf("ret %s %s\n", t.getRepresentation(), name2);
      }
    }
    out.println("}");

    return new EvalResult();
  }
}
