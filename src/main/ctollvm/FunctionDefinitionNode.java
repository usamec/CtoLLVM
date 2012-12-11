package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;
import java.lang.StringBuffer;
import java.util.*;

public class FunctionDefinitionNode implements PNode {
  private String type;
  private String name;
  private Scope scope;
  private PNode bli;
  private List<FunctionParameterNode> parameters;
  private DeclarationProcessor declaration;

  public FunctionDefinitionNode(Scope scope) {
    this.scope = scope;
    this.type = "";
    this.name = "";
    parameters = new ArrayList<FunctionParameterNode>();
  }

  public void setFunctionDeclaration(DeclarationProcessor dec) {
    declaration = dec;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setBli(PNode bli) {
    this.bli = bli;
  }

  @Override
  public EvalResult produceOutput(PrintStream out) throws Exception {
    name = declaration.getName();
    if (!scope.parent().isGlobal()) {
      throw new Exception("Function cannot be defined in local scope");
    }
    TypeSystem typeSystem = TypeSystem.getInstance();
    if (!typeSystem.isValidType(type)) {
      throw new Exception(String.format("Invalid type %s", type));
    }    
    Type t = typeSystem.getType(type);
    Type fT = declaration.processTypeAll(t);
    if (!fT.isFunction()) {
      throw new Exception("Bad function definition");
    }
    FunctionType fType = (FunctionType) fT;
    Type retvalType = fType.returnValue;

    if (retvalType.isVoid())
      out.print(String.format("define void @%s(", name));
    else 
      out.print(String.format("define %s @%s(", retvalType.getRepresentation(), name));
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

    scope.setFunctionReturnType(retvalType);


    parameters = declaration.getFunctionParameters();
    
    boolean first = true;
    for (FunctionParameterNode n : parameters) {
      if (!first) out.print(", ");
      first = false;
      out.print(n.getRepresentation());
    }
   
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
    if (retvalType.isVoid()) {
      out.println("ret void");
    } else {
      String name1 = String.format("%%tmp.val.%d", IdCounter.GetNewId()); 
      out.printf("%s = alloca %s\n", name1, retvalType.getRepresentation());
      String name2 = String.format("%%tmp.val.%d", IdCounter.GetNewId()); 
      out.printf("%s = load %s* %s\n", name2, retvalType.getRepresentation(), name1);
      out.printf("ret %s %s\n", retvalType.getRepresentation(), name2);
    }
    
    out.println("}");

    return new EvalResult();
  }
}
